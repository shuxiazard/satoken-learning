package com.shuxia.satoken.stp;

import cn.hutool.core.stream.StreamUtil;
import cn.hutool.core.util.StrUtil;
import com.shuxia.satoken.SaManager;
import com.shuxia.satoken.config.SaTokenConfig;
import com.shuxia.satoken.context.SaTokenContext;
import com.shuxia.satoken.context.model.SaHolder;
import com.shuxia.satoken.context.model.SaStorage;
import com.shuxia.satoken.dao.SatoKenDao;
import com.shuxia.satoken.exception.SaTokenException;
import com.shuxia.satoken.listener.SaTokenEventCenter;
import com.shuxia.satoken.session.SaSession;
import com.shuxia.satoken.session.TokenSign;
import com.shuxia.satoken.strategy.SaStrategy;
import com.shuxia.satoken.util.SaFoxUtil;
import com.shuxia.satoken.util.SaTokenConsts;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Sa-Token 权限认证，逻辑实现类
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
     * @param loginType 账号体系标识
     */
    public StpLogic(String loginType) {
        this.loginType = loginType;
    }

    /**
     * 获取当前 StpLogic 的账号类型
     * @return See Note
     */
    public String getLoginType() {
        return loginType;
    }

    /**
     * 设置当前账号类型
     * @param loginType loginType
     * @return 对象自身
     */
    public StpLogic setLoginType(String loginType) {
        this.loginType = loginType;
        return this;
    }

    /**
     * 会话登录
     * @param id 账号id
     */
    public void login(Object id) {
        login(id,new SaLoginModel());
    }
    /**
     * 会话登录，并指定所有登录参数Model
     * @param id 登录id，建议的类型：（long | int | String）
     * @param loginModel 此次登录的参数Model
     */
    public void login(Object id, SaLoginModel loginModel) {
    //1.创建会话
           String token =createLoginSession(id,loginModel);
        //2.设置token
        setTokenValue(token,loginModel);

    }

    /**
     * 设置token
     * @param token
     * @param loginModel
     */
    public void setTokenValue(String token, SaLoginModel loginModel) {
        if (StrUtil.isEmpty(token)){
            return;
        }
        //保存token
        setTokenValueToStorage(token);

    }

    /**
     * 保存token
     * @param token
     */
    public void setTokenValueToStorage(String token) {
        //获取存储器
      SaStorage storage= SaHolder.getStorage();

      //拼接token
        String tokenPrefix = getConfig().getTokenPrefix();
        if (StrUtil.isNotEmpty(tokenPrefix)){
            storage.set(splicingKeyJustCreateSave(),tokenPrefix+ SaTokenConsts.TOKEN_CONNECTOR_CHAT+token);
        }else {
            storage.set(splicingKeyJustCreateSave(),token);
        }

        //无前缀
        storage.set(SaTokenConsts.JUST_CREATED_NOT_PREFIX,token);
    }

    /**
     * 如果token为本次请求新创建的，则以此字符串为key存储在当前request中
     * @return key
     */
    public String splicingKeyJustCreateSave() {
        return SaTokenConsts.JUST_CREATED;
    }

    /**
     * 创建指定账号id的登录会话
     * @param id 登录id，建议的类型：（long | int | String）
     * @param loginModel 此次登录的参数Model
     * @return 返回会话令牌
     */
    public String createLoginSession(Object id, SaLoginModel loginModel) {
        // 非空检查
        SaTokenException.throwByNull(id,"id不能为空");

        // 初始化loginModel
        SaTokenConfig config = getConfig();
        loginModel.build(config);

        //分配token
        String token =distUsableToken(id,loginModel);

        //获取user-session
        SaSession session =getSessionByLoginId(id,true);
        session.updateMinTimeout(loginModel.getTimeout());

        //记录token签名
        session.addTokenSign(token,loginModel.getDeviceOrDefault());

        //保存token
        saveTokenToIdMapping(token,id,loginModel.getTimeout());

        //token最后操作时间
        setLastActivityToNow(token);

        //打印日志
        SaTokenEventCenter.doLogin(loginType,id,token,loginModel);

        //检查账号会话数量是否超过最大值
        if (config.getMaxLoginCount()!= -1){
            logoutByMaxLoginCount(id, session, null, config.getMaxLoginCount());
        }

        return token;

    }

    /**
     * 会话注销
     * @param id 登录id
     * @param session
     * @param device 设备名
     * @param maxLoginCount 最大登录数
     */
    public void logoutByMaxLoginCount(Object id, SaSession session, String device, int maxLoginCount) {
        if (session ==null){
            session =getSessionByLoginId(id,false);
            if (session==null){
                return;
            }
        }
        List<TokenSign> list= session.tokenSignListCopyByDevice(device);
        for (int i = 0; i < list.size()-maxLoginCount; i++) {
            //清除token 最后活跃时间
            String token = list.get(i).getValue();
            session.removeTokenSign(token);
            clearLastActivity(token);

            //删除token--id映射 、token- session
            deleteTokenToIdMapping(token);
            deleteTokenToSession(token);

            //注销事件打印
            SaTokenEventCenter.doLogout(loginType,id,token);
        }
        session.logoutByTokenSignCountToZero();
    }

    /**
     * 删除token- session
     * @param token
     */

    public void deleteTokenToSession(String token) {
        getSaTokenDao().delete(splicingKeySession(token));
    }

    /**
     * 删除token--id映射
     * @param token
     */
    public void deleteTokenToIdMapping(String token) {
        getSaTokenDao().delete(splicingKeyToken(token));
    }

    /**
     * 清除 最后活跃时间
     * @param token
     */
    public void clearLastActivity(String token) {
        // 如果token == null 或者 设置了[永不过期], 则立即返回
        if(token == null || !isOpenActivityCheck()) {
            return;
        }
        getSaTokenDao().delete(splicingKeyLastActivityTime(token));
    }

    /**
     * token最后操作时间
     * @param token
     */
    private void setLastActivityToNow(String token) {
        if (token ==null || !isOpenActivityCheck()) {
            return;
        }
        getSaTokenDao().set(splicingKeyLastActivityTime(token),String.valueOf(System.currentTimeMillis()),getConfig().getTimeout());
    }

    /**
     *  拼接key  token'最后操作时间
     * @param token
     * @return
     */
    public String splicingKeyLastActivityTime(String token) {
        return getConfig().getTokenName() + ":" + loginType + ":last-activity:" + token;

    }

    /**
     * 返回全局配置是否开启了Token 活跃校验
     * @return /
     */
    public boolean isOpenActivityCheck() {
        return getConfig().getActivityTimeout() != SatoKenDao.NEVER_EXPIRE;
    }

    /**
     * 存储 Token-Id 映射
     * @param token
     * @param loginId
     * @param timeout
     */
    private void saveTokenToIdMapping(String token, Object loginId, Long timeout) {
            getSaTokenDao().set(splicingKeyToken(token),String.valueOf(loginId),timeout);
    }

    /**
     * 拼接key
     * @param token
     * @return
     */
    public String splicingKeyToken(String token) {
        return  getConfig().getTokenName()+":"+loginType +":token:"+token;
    }

    /**
     * 获取指定账号id的User-Session，如果Session尚未创建，isCreate==true则新建并返回
     * @param loginId
     * @param isCreate
     * @return SaSession
     */
    public SaSession getSessionByLoginId(Object loginId, boolean isCreate) {
        return getSessionByLoginId(splicingKeySession(loginId),isCreate);
    }

    /**
     * 获取指定账号id的User-Session，如果Session尚未创建，isCreate==true则新建并返回
     * @param loginId
     * @param isCreate
     * @return SaSession
     */
    public SaSession getSessionByLoginId(String loginId, boolean isCreate) {
        SaSession session = getSaTokenDao().getSession(loginId);
        //创建条件： 为空 && isCreate ==true
        if (session ==null && isCreate){
           session  = SaStrategy.me.createSession.apply(loginId);
           getSaTokenDao().getSession(session,getConfig().getTimeout());
        }
        return session;
    }

    /**
     * 返回持久化对象
     * @return
     */
    public SatoKenDao getSaTokenDao() {
        return SaManager.getSaTokenDao();
    }


    /**
     *  拼接key
     * @param loginId
     * @return
     */
    public String splicingKeySession(Object loginId) {
        return getConfig().getTokenName()+":"+loginType+":session:"+loginId;
    }

    /**
     * 分配token
     * @param id 登录id
     * @param loginModel 登录参数model
     * @return
     */
    protected String distUsableToken(Object id, SaLoginModel loginModel) {
        //TODO 顶人下线
        //如果调用者自定义token
        if (SaFoxUtil.isNotEmpty(loginModel.getToken())){
             return loginModel.getToken();
        }

        //新建token
        return createTokenValue(id, loginModel.getDeviceOrDefault(), loginModel.getTimeout(), loginModel.getExtraData());

    }

    /**
     * 创建一个TokenValue
     * @param loginId loginId
     * @param device 设备类型
     * @param timeout 过期时间
     * @param extraData 扩展信息
     * @return 生成的tokenValue
     */
    public String createTokenValue(Object loginId, Object device, Long timeout, Map<String, Object> extraData) {
        return SaStrategy.me.creatToken.apply(loginId,loginType);
    }


    /**
     * 返回全局配置对象
     * @return /
     */
    public SaTokenConfig getConfig() {
        return SaManager.getConfig();
    }
}
