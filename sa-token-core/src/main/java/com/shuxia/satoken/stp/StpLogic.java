package com.shuxia.satoken.stp;


import cn.hutool.core.util.StrUtil;
import com.shuxia.satoken.SaManager;
import com.shuxia.satoken.config.SaCookieConfig;
import com.shuxia.satoken.config.SaTokenConfig;
import com.shuxia.satoken.context.SaTokenContext;
import com.shuxia.satoken.context.model.SaHolder;
import com.shuxia.satoken.context.model.SaRequest;
import com.shuxia.satoken.context.model.SaStorage;
import com.shuxia.satoken.dao.SatoKenDao;
import com.shuxia.satoken.exception.DisableServiceException;
import com.shuxia.satoken.exception.NotLoginException;
import com.shuxia.satoken.exception.SaTokenException;
import com.shuxia.satoken.listener.SaTokenEventCenter;
import com.shuxia.satoken.session.SaSession;
import com.shuxia.satoken.session.TokenSign;
import com.shuxia.satoken.strategy.SaStrategy;
import com.shuxia.satoken.util.SaFoxUtil;
import com.shuxia.satoken.util.SaTokenConsts;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Sa-Token 权限认证，逻辑实现类
 *
 * @author shuxia
 * @date 11/16/2022
 */
public class StpLogic {
    /**
     * 账号类型，多账号体系时以此值区分，比如：login、user、admin
     */
    public String loginType;

    /**
     * 初始化StpLogic, 并指定账号类型
     *
     * @param loginType 账号体系标识
     */
    public StpLogic(String loginType) {
        this.loginType = loginType;
    }

    /**
     * 获取当前 StpLogic 的账号类型
     *
     * @return See Note
     */
    public String getLoginType() {
        return loginType;
    }

    /**
     * 设置当前账号类型
     *
     * @param loginType loginType
     * @return 对象自身
     */
    public StpLogic setLoginType(String loginType) {
        this.loginType = loginType;
        return this;
    }

    // region -------------------- 会话注销 ----------------------

    public void logout(){
        // 如果连 Token 都没有，那么无需执行任何操作
        String tokenValue = getTokenValue();
        if(SaFoxUtil.isEmpty(tokenValue)) {
            return;
        }

        // 从当前 [Storage存储器] 里删除 Token
        SaHolder.getStorage().delete(splicingKeyJustCreateSave());

        // 清除当前上下文的 [临时有效期check标记]
        SaHolder.getStorage().delete(SaTokenConsts.TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY);

        // 清除这个 Token 的相关信息
        logoutByToken(tokenValue);
    }


    /**
     * 会话注销 根据id
     * @param loginId
     */
    public void logout(Object loginId){
        logout(loginId,null);
    }

    /**
     * 会话注销 根据id和设备
     * @param loginId
     * @param service
     */
    public void logout(Object loginId,String service){
            //获取session
        SaSession session = getSessionByLoginId(loginId, false);
        if (session!=null){
            for (TokenSign tokenSign : session.tokenSignListCopyByDevice(service)) {
                String token = tokenSign.getValue();
                clearLastActivity(token);
                session.removeTokenSign(token);
                deleteTokenToIdMapping(token);
                deleteTokenToSession(token);
                // $$ 发布事件：指定账号注销
                SaTokenEventCenter.doLogout(loginType, loginId, token);
            }
            session.logoutByTokenSignCountToZero();
        }
    }

    /**
     * 会话注销 根据token
     * @param tokenValue
     */
    public void logoutByToken(String tokenValue){
        clearLastActivity(tokenValue);
        deleteTokenToSession(tokenValue);

        //获取登录id
        String loginId = getLoginIdNoHandle(tokenValue);
        //验证id有效性
        if (!isValidLoginId(loginId)){
            return;
        }
        //删除token-id映射
        deleteTokenToIdMapping(tokenValue);

        //删除session
        SaSession session = getSessionByLoginId(loginId, false);
        if (session!=null){
            session.removeTokenSign(tokenValue);
            session.logoutByTokenSignCountToZero();
        }
    }

    // region -------------------- 账号封禁 ----------------------

    /**
     * 封禁账号 根据id
     *
     * @param loginId 账号id
     * @param time    封禁天数 （单位秒）
     */
    public void disable(Object loginId, long time) {
        disableLevel(loginId, SaTokenConsts.DEFAULT_DISABLE_SERVICE, SaTokenConsts.DEFAULT_DISABLE_LEVEL, time);
    }

    /**
     * 封禁账号 根据id
     *
     * @param loginId 账号id
     * @param service 封禁服务
     * @param time    封禁天数 （单位秒）
     *
     */
    public void disable(Object loginId, String service,long time) {
        disableLevel(loginId, service, SaTokenConsts.DEFAULT_DISABLE_LEVEL, time);
    }

    /**
     * 账号是否封禁
     * @param loginId 账号id
     * @return /
     */
    public boolean isDisable(Object loginId){
       return isDisableLevel(loginId,SaTokenConsts.DEFAULT_DISABLE_SERVICE,SaTokenConsts.DEFAULT_DISABLE_LEVEL);
    }

    /**
     * 账号是否封禁
     * @param loginId
     * @param service
     * @return
     */
    public boolean isDisable(Object loginId,String service){
        return isDisableLevel(loginId,service,SaTokenConsts.DEFAULT_DISABLE_LEVEL);
    }

    /**
     * 账号是否封禁 抛出异常
     * @param loginId
     * @return /
     */
    public void checkDisable(Object loginId){
        checkDisableLevel(loginId,SaTokenConsts.DEFAULT_DISABLE_SERVICE,SaTokenConsts.DEFAULT_DISABLE_LEVEL);
    }
    /**
     * 判断：指定账号是否已被封禁到指定等级
     *
     * @param loginId 指定账号id
     * @param level 指定封禁等级
     * @return /
     */
    public boolean isDisableLevel(Object loginId, int level) {
        return isDisableLevel(loginId, SaTokenConsts.DEFAULT_DISABLE_SERVICE, level);
    }

    /**
     * 判断：指定账号的指定服务，是否已被封禁到指定等级
     * @param loginId
     * @param service
     * @param level
     * @return
     */
    public boolean isDisableLevel(Object loginId, String service, int level) {
        //检查是否被封禁
       int disableLevel= getDisableLevel(loginId,service);
      if (disableLevel ==SaTokenConsts.NOT_DISABLE_LEVEL){
          return false;
      }
      return disableLevel >=level;
    }

    /**
     * 获取封禁等级
     * @param loginId
     * @param service
     * @return 未被封禁则返回-2
     */
    public int getDisableLevel(Object loginId, String service) {
        String value = getSaTokenDao().get(splicingKeyDisable(loginId, service));
        if (SaFoxUtil.isEmpty(value)){
            return SaTokenConsts.NOT_DISABLE_LEVEL;
        }
        return Integer.parseInt(value);
    }

    /**
     * 判断账号是否封禁到指定等级
     *
     * @param loginId 账号id
     * @return
     */
    public void checkDisableLevel(Object loginId,int level) {
      checkDisableLevel(loginId, SaTokenConsts.DEFAULT_DISABLE_SERVICE, level);
    }

    /**
     * @param loginId
     * @param service
     * @param level
     */
    public void checkDisableLevel(Object loginId, String service, int level) {
        // 检查是否被封禁
        String value = getSaTokenDao().get(splicingKeyDisable(loginId, service));
        if (SaFoxUtil.isEmpty(value)) {
            return;
        }
        //检查封禁等级是否达到指定等级
        Integer disableLevel = Integer.getInteger(value);
        if (disableLevel >= level) {
            throw new DisableServiceException(loginType, loginId, service, disableLevel, level, getDisableTime(loginId, service));
        }
    }

    /**
     * 获取封禁时间
     *
     * @param loginId
     * @param service
     * @return
     */
    public long getDisableTime(Object loginId, String service) {
        return getSaTokenDao().getTimeout(splicingKeyDisable(loginId, service));
    }

    public long getDisableTime(Object loginId) {
        return getSaTokenDao().getTimeout(splicingKeyDisable(loginId, SaTokenConsts.DEFAULT_DISABLE_SERVICE));
    }

    /**
     * 封禁：指定账号，并指定封禁等级
     * @param loginId 指定账号id
     * @param level 指定封禁等级
     * @param time 封禁时间, 单位: 秒 （-1=永久封禁）
     */
    public void disableLevel(Object loginId, int level, long time) {
        disableLevel(loginId, SaTokenConsts.DEFAULT_DISABLE_SERVICE, level, time);
    }

    /**
     * @param loginId 账号id
     * @param service 封禁服务类型
     * @param level   封禁等级
     * @param time    封禁时间 unit:second
     */
    public void disableLevel(Object loginId, String service, int level, long time) {
        // 空值检查
        if (SaFoxUtil.isEmpty(loginId)) {
            throw new SaTokenException("请提供要封禁的账号");
        }
        if (SaFoxUtil.isEmpty(service)) {
            throw new SaTokenException("请提供要封禁的服务");
        }
        if (level < SaTokenConsts.MIN_DISABLE_LEVEL) {
            throw new SaTokenException("封禁等级不可以小于最小值：" + SaTokenConsts.MIN_DISABLE_LEVEL);
        }
        //标记封禁
        getSaTokenDao().set(splicingKeyDisable(loginId, service), String.valueOf(level), time);
        SaTokenEventCenter.doDisable(loginType, loginId, service, level, time);
    }

    /**
     * 解封账号
     * @param loginId
     */
    public void uniteDisable(Object loginId){
        uniteDisable(loginId,SaTokenConsts.DEFAULT_DISABLE_SERVICE);
    }

    /**
     * 解封账号
     * @param loginId
     * @param services
     */
    public void uniteDisable(Object loginId,String...services){
        // 空值检查
        if(SaFoxUtil.isEmpty(loginId)) {
            throw new SaTokenException("请提供要解禁的账号");
        }
        if(services == null || services.length == 0) {
            throw new SaTokenException("请提供要解禁的服务");
        }
        for (String service : services) {
            getSaTokenDao().delete(splicingKeyDisable(loginId,service));
            SaTokenEventCenter.doUntieDisable(loginType, loginId, service);
        }

    }

    /**
     * 拼接封禁key
     *
     * @param loginId 登录id
     * @param service 封禁类型
     * @return
     */
    public String splicingKeyDisable(Object loginId, String service) {
        return getConfig().getTokenName() + ":" + loginType + ":disable:" + service + ":" + loginId;
    }
    //endregion

    // region ------------------踢人下线 ------------------------

    /**
     * 踢人下线  根据登录id
     *
     * @param loginId
     */
    public void kickout(Object loginId) {
        kickout(loginId, null);
    }

    /**
     * 踢人下线，根据登录id和设备
     *
     * @param loginId
     * @param device
     */
    public void kickout(Object loginId, String device) {
        //获取登录id的session
        SaSession session = getSessionByLoginId(loginId, false);
        if (session != null) {
            for (TokenSign tokenSign : session.tokenSignListCopyByDevice(device)) {
                String value = tokenSign.getValue();
                session.removeTokenSign(value);
                clearLastActivity(value);
                updateTokenToIdMapping(value, NotLoginException.KICK_OUT);
            }
            //注销session
            session.logoutByTokenSignCountToZero();
        }
    }

    /**
     * 踢人下线  根据token
     *
     * @param tokenValue
     */
    public void kickoutByTokenValue(String tokenValue) {
        clearLastActivity(tokenValue);
        //获取登录id
        String loginId = getLoginIdNoHandle(tokenValue);
        if (!isValidLoginId(loginId)) {
            return;
        }
        //标记token状态
        updateTokenToIdMapping(tokenValue, NotLoginException.KICK_OUT_MESSAGE);
        SaTokenEventCenter.doKickOut(loginType, loginId, tokenValue);
        //session移除token
        SaSession session = getSessionByLoginId(loginId, false);
        if (session != null) {
            session.removeTokenSign(tokenValue);
            session.logoutByTokenSignCountToZero();
        }
    }
    //endregion

    /**
     * 验证id有效性
     *
     * @param loginId
     * @return
     */
    public boolean isValidLoginId(String loginId) {
        return loginId != null && !NotLoginException.ABNORMAL_LIST.contains(loginId.toString());
    }


    /**
     * 会话登录
     *
     * @param id 账号id
     */
    public void login(Object id) {
        login(id, new SaLoginModel());
    }

    /**
     * 会话登录，并指定所有登录参数Model
     *
     * @param id         登录id，建议的类型：（long | int | String）
     * @param loginModel 此次登录的参数Model
     */
    public void login(Object id, SaLoginModel loginModel) {
        //1.创建会话
        String token = createLoginSession(id, loginModel);
        //2.设置token
        setTokenValue(token, loginModel);

    }

    /**
     * 设置token
     *
     * @param token
     * @param loginModel
     */
    public void setTokenValue(String token, SaLoginModel loginModel) {
        if (StrUtil.isEmpty(token)) {
            return;
        }
        //保存token
        setTokenValueToStorage(token);

    }

    /**
     * 保存token
     *
     * @param token
     */
    public void setTokenValueToStorage(String token) {
        //获取存储器
        SaStorage storage = SaHolder.getStorage();

        //拼接token
        String tokenPrefix = getConfig().getTokenPrefix();
        if (StrUtil.isNotEmpty(tokenPrefix)) {
            storage.set(splicingKeyJustCreateSave(), tokenPrefix + SaTokenConsts.TOKEN_CONNECTOR_CHAT + token);
        } else {
            storage.set(splicingKeyJustCreateSave(), token);
        }

        //无前缀
        storage.set(SaTokenConsts.JUST_CREATED_NOT_PREFIX, token);
    }

    /**
     * 如果token为本次请求新创建的，则以此字符串为key存储在当前request中
     *
     * @return key
     */
    public String splicingKeyJustCreateSave() {
        return SaTokenConsts.JUST_CREATED;
    }

    /**
     * 创建指定账号id的登录会话
     *
     * @param id         登录id，建议的类型：（long | int | String）
     * @param loginModel 此次登录的参数Model
     * @return 返回会话令牌
     */
    public String createLoginSession(Object id, SaLoginModel loginModel) {
        // 非空检查
        SaTokenException.throwByNull(id, "id不能为空");

        // 初始化loginModel
        SaTokenConfig config = getConfig();
        loginModel.build(config);

        //分配token
        String token = distUsableToken(id, loginModel);

        //获取user-session
        SaSession session = getSessionByLoginId(id, true);
        session.updateMinTimeout(loginModel.getTimeout());
        SatoKenDao saTokenDao = getSaTokenDao();

        //记录token签名
        session.addTokenSign(token, loginModel.getDeviceOrDefault());

        //保存token
        saveTokenToIdMapping(token, id, loginModel.getTimeout());

        //token最后操作时间
        setLastActivityToNow(token);

        //打印日志
        SaTokenEventCenter.doLogin(loginType, id, token, loginModel);

        //检查账号会话数量是否超过最大值
        if (config.getMaxLoginCount() != -1) {
            logoutByMaxLoginCount(id, session, null, config.getMaxLoginCount());
        }

        return token;

    }

    /**
     * 会话注销
     *
     * @param id            登录id
     * @param session
     * @param device        设备名
     * @param maxLoginCount 最大登录数
     */
    public void logoutByMaxLoginCount(Object id, SaSession session, String device, int maxLoginCount) {
        if (session == null) {
            session = getSessionByLoginId(id, false);
            if (session == null) {
                return;
            }
        }
        List<TokenSign> list = session.tokenSignListCopyByDevice(device);
        for (int i = 0; i < list.size() - maxLoginCount; i++) {
            //清除token 最后活跃时间
            String token = list.get(i).getValue();
            session.removeTokenSign(token);
            clearLastActivity(token);

            //删除token--id映射 、token- session
            deleteTokenToIdMapping(token);
            deleteTokenToSession(token);

            //注销事件打印
            SaTokenEventCenter.doLogout(loginType, id, token);
        }
        session.logoutByTokenSignCountToZero();
    }

    /**
     * 删除token- session
     *
     * @param token
     */

    public void deleteTokenToSession(String token) {
        getSaTokenDao().delete(splicingKeySession(token));
    }

    /**
     * 删除token--id映射
     *
     * @param token
     */
    public void deleteTokenToIdMapping(String token) {
        getSaTokenDao().delete(splicingKeyToken(token));
    }

    /**
     * 清除 最后活跃时间
     *
     * @param token
     */
    public void clearLastActivity(String token) {
        // 如果token == null 或者 设置了[永不过期], 则立即返回
        if (token == null || !isOpenActivityCheck()) {
            return;
        }
        getSaTokenDao().delete(splicingKeyLastActivityTime(token));
    }

    /**
     * token最后操作时间
     *
     * @param token
     */
    private void setLastActivityToNow(String token) {
        if (token == null || !isOpenActivityCheck()) {
            return;
        }
        getSaTokenDao().set(splicingKeyLastActivityTime(token), String.valueOf(System.currentTimeMillis()), getConfig().getTimeout());
    }

    /**
     * 拼接key  token'最后操作时间
     *
     * @param token
     * @return
     */
    public String splicingKeyLastActivityTime(String token) {
        return getConfig().getTokenName() + ":" + loginType + ":last-activity:" + token;

    }

    /**
     * 返回全局配置是否开启了Token 活跃校验
     *
     * @return /
     */
    public boolean isOpenActivityCheck() {
        return getConfig().getActivityTimeout() != SatoKenDao.NEVER_EXPIRE;
    }

    /**
     * 存储 Token-Id 映射
     *
     * @param token
     * @param loginId
     * @param timeout
     */
    private void saveTokenToIdMapping(String token, Object loginId, Long timeout) {
        getSaTokenDao().set(splicingKeyToken(token), String.valueOf(loginId), timeout);
    }

    /**
     * 拼接key
     *
     * @param token
     * @return
     */
    public String splicingKeyToken(String token) {
        return getConfig().getTokenName() + ":" + loginType + ":token:" + token;
    }

    /**
     * 获取指定账号id的User-Session，如果Session尚未创建，isCreate==true则新建并返回
     *
     * @param loginId
     * @param isCreate
     * @return SaSession
     */
    public SaSession getSessionByLoginId(Object loginId, boolean isCreate) {
        return getSessionByLoginId(splicingKeySession(loginId), isCreate);
    }

    /**
     * 获取指定账号id的User-Session，如果Session尚未创建，isCreate==true则新建并返回
     *
     * @param loginId
     * @param isCreate
     * @return SaSession
     */
    public SaSession getSessionByLoginId(String loginId, boolean isCreate) {
        SaSession session = getSaTokenDao().getSession(loginId);
        //创建条件： 为空 && isCreate ==true
        if (session == null && isCreate) {
            session = SaStrategy.me.createSession.apply(loginId);
            getSaTokenDao().setSession(session, getConfig().getTimeout());
        }
        return session;
    }

    /**
     * 返回持久化对象
     *
     * @return
     */
    public SatoKenDao getSaTokenDao() {
        return SaManager.getSaTokenDao();
    }


    /**
     * 拼接key
     *
     * @param loginId
     * @return
     */
    public String splicingKeySession(Object loginId) {
        return getConfig().getTokenName() + ":" + loginType + ":session:" + loginId;
    }

    /**
     * 分配token
     *
     * @param id         登录id
     * @param loginModel 登录参数model
     * @return
     */
    protected String distUsableToken(Object id, SaLoginModel loginModel) {
        ///获取全局配置
        Boolean isConcurrent = getConfig().getIsConcurrent();
        if (!isConcurrent) {
            //顶人下线
            replaced(id, loginModel.getDevice());
        } else {
            //复用旧token
            if (getConfigOfIsShare()) {
                String token = getTokenValueByLoginId(id, loginModel.getDevice());
                if (StrUtil.isNotEmpty(token)) {
                    return token;
                }
            }
        }
        //如果调用者自定义token
        if (SaFoxUtil.isNotEmpty(loginModel.getToken())) {
            return loginModel.getToken();
        }

        //新建token
        return createTokenValue(id, loginModel.getDeviceOrDefault(), loginModel.getTimeout(), loginModel.getExtraData());

    }

    /**
     * 根据loginId获取token
     *
     * @param id
     * @param device
     * @return
     */
    public String getTokenValueByLoginId(Object id, String device) {
        List<String> tokenList = getTokenValueListByLoginId(id, device);
        return tokenList.size() == 0 ? null : tokenList.get(tokenList.size() - 1);
    }

    /**
     * 获取指定id的token集合
     *
     * @param id
     * @param device
     * @return
     */
    public List<String> getTokenValueListByLoginId(Object id, String device) {
        SaSession session = getSessionByLoginId(id, false);
        if (session == null) {
            return Collections.emptyList();
        }
        return session.tokenSignListCopy().stream()
                .filter(tokenSign -> tokenSign.getDevice().equals(device))
                .map(TokenSign::getValue)
                .collect(Collectors.toList());
    }

    /**
     * 返回isShare属性
     *
     * @return
     */
    public boolean getConfigOfIsShare() {
        return getConfig().getIsShare();
    }

    /**
     * 顶人下线
     *
     * @param id
     * @param device
     */
    public void replaced(Object id, String device) {
        SaSession session = getSessionByLoginId(id, false);
        if (session != null) {
            //通过设备名查找tokensign
            session.tokenSignListCopyByDevice(device).stream()
                    .peek(tokenSign -> {
                        //清除 tokensign ，最后活跃时间
                        String value = tokenSign.getValue();
                        session.removeTokenSign(value);
                        clearLastActivity(value);
                        //标记token状态
                        updateTokenToIdMapping(value, NotLoginException.BE_REPLACED);
                        //打印
                        SaTokenEventCenter.doReplaced(loginType, id, value);
                    }).close();

        }

    }

    /**
     * 更新 token-id映射
     *
     * @param value
     * @param loginId
     */
    public void updateTokenToIdMapping(String value, Object loginId) {
        SaTokenException.throwBy(SaFoxUtil.isEmpty(loginId), "loginId不能为空");
        getSaTokenDao().update(splicingKeyToken(value), loginId.toString());
    }

    /**
     * 创建一个TokenValue
     *
     * @param loginId   loginId
     * @param device    设备类型
     * @param timeout   过期时间
     * @param extraData 扩展信息
     * @return 生成的tokenValue
     */
    public String createTokenValue(Object loginId, Object device, Long timeout, Map<String, Object> extraData) {
        return SaStrategy.me.creatToken.apply(loginId, loginType);
    }


    /**
     * 返回全局配置对象
     *
     * @return /
     */
    public SaTokenConfig getConfig() {
        return SaManager.getConfig();
    }

    /**
     * 检查登录
     */
    public void checkLogin() {
        getLoginId();
    }

    /**
     * 获取当前会话id
     *
     * @return
     */
    public Object getLoginId() {

        //获取token
        String token = getTokenValue();
        if (token == null) {
            throw NotLoginException.newInstance(loginType, NotLoginException.NOT_TOKEN);
        }

        //查找对应loginId
        String loginId = getLoginIdNoHandle(token);
        if (loginId == null) {
            throw NotLoginException.newInstance(loginType, NotLoginException.INVALID_TOKEN);
        }
        // 如果是已经过期，则抛出：已经过期
        if (loginId.equals(NotLoginException.TOKEN_TIMEOUT)) {
            throw NotLoginException.newInstance(loginType, NotLoginException.TOKEN_TIMEOUT, token);
        }
        // 如果是已经被顶替下去了, 则抛出：已被顶下线
        if (loginId.equals(NotLoginException.BE_REPLACED)) {
            throw NotLoginException.newInstance(loginType, NotLoginException.BE_REPLACED, token);
        }
        // 如果是已经被踢下线了, 则抛出：已被踢下线
        if (loginId.equals(NotLoginException.KICK_OUT)) {
            throw NotLoginException.newInstance(loginType, NotLoginException.KICK_OUT, token);
        }

        //检查是否临期
        checkActivityTimeout(token);

        //是否自动续期
        if (getConfig().getAutoRenew()) {
            updateLastActivityToNow(token);
        }
        return loginId;
    }

    /**
     * 更好token过期时间
     *
     * @param token
     */
    public void updateLastActivityToNow(String token) {
        // 如果token == null 或者 设置了[永不过期], 则立即返回
        if (token == null || !isOpenActivityCheck()) {
            return;
        }
        getSaTokenDao().update(splicingKeyLastActivityTime(token), String.valueOf(System.currentTimeMillis()));
    }

    /**
     * /检查是否临期
     *
     * @param token
     */
    public void checkActivityTimeout(String token) {
        if (token == null || !isOpenActivityCheck()) {
            return;
        }

        //获取剩余时间
        long timeout = getTokenActivityTimeoutByToken(token);

        // -1 代表此token已经被设置永不过期，无须继续验证
        if (timeout == SatoKenDao.NEVER_EXPIRE) {
            return;
        }
        // -2 代表已过期，抛出异常
        if (timeout == SatoKenDao.NOT_VALUE_EXPIRE) {
            throw NotLoginException.newInstance(loginType, NotLoginException.TOKEN_TIMEOUT, token);
        }
    }

    /**
     * 获取剩余时间
     *
     * @param token
     * @return
     */
    public long getTokenActivityTimeoutByToken(String token) {
        // 如果token为null , 则返回 -2
        if (token == null) {
            return SatoKenDao.NOT_VALUE_EXPIRE;
        }
        // 如果设置了永不过期, 则返回 -1
        if (!isOpenActivityCheck()) {
            return SatoKenDao.NEVER_EXPIRE;
        }
        //token最后活跃时间
        String keyLastActivityTime = splicingKeyLastActivityTime(token);
        String lastActivityTimeValue = getSaTokenDao().get(keyLastActivityTime);
        if (lastActivityTimeValue == null) {
            return SatoKenDao.NOT_VALUE_EXPIRE;
        }

        long lastActivityTime = Long.parseLong(lastActivityTimeValue);
        long apartSecond = (System.currentTimeMillis() - lastActivityTime) / 1000;
        long timeout = getConfig().getActivityTimeout() - apartSecond;
        if (timeout < 0) {
            return SatoKenDao.NOT_VALUE_EXPIRE;
        }
        return timeout;

    }

    /**
     * 通过token查找loginId
     *
     * @param token
     * @return
     */
    public String getLoginIdNoHandle(String token) {
        return getSaTokenDao().get(splicingKeyToken(token));
    }

    /**
     * 获取token
     *
     * @return
     */
    public String getTokenValue() {
        //获取token
        String token = getTokenValueNotCut();


        //如果有前缀
        String tokenPrefix = getConfig().getTokenPrefix();
        if (StrUtil.isNotEmpty(tokenPrefix)) {
            if (StrUtil.isEmpty(token) || !token.startsWith(tokenPrefix + SaTokenConsts.TOKEN_CONNECTOR_CHAT)) {
                token = null;
            } else {
                token = token.substring(tokenPrefix.length() + SaTokenConsts.TOKEN_CONNECTOR_CHAT.length());
            }
        }
        return token;
    }

    /**
     * 获取token 有自定义前缀
     *
     * @return
     */
    public String getTokenValueNotCut() {
        String token = null;
        SaStorage storage = SaHolder.getStorage();
        SaRequest request = SaHolder.getRequest();
        SaTokenConfig config = getConfig();
        String tokenName = config.getTokenName();

        //从存储器拿取
        if (storage.get(splicingKeyJustCreateSave()) != null) {
            token = String.valueOf(storage.get(splicingKeyJustCreateSave()));
        }
        //从请求体拿取
        if (token == null && config.getIsReadBody()) {
            token = request.getParam(tokenName);
        }
        //从请求头拿
        if (token == null && config.getIsReadHeader()) {
            token = request.getHeader(tokenName);
        }
        //cookie
        if (token == null && config.getIsReadCookie()) {
            token = request.getCookieValue(tokenName);
        }
        return token;
    }
}
