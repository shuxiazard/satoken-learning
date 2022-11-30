package com.shuxia.satoken.session;

import cn.hutool.core.util.StrUtil;
import com.shuxia.satoken.SaManager;
import com.shuxia.satoken.dao.SatoKenDao;
import com.shuxia.satoken.listener.SaTokenEventCenter;
import com.shuxia.satoken.util.SaFoxUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * @author shuxia
 * @date 11/27/2022
 */
@Data
@Accessors(chain = true)
public class SaSession implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 在 Session 上存储用户对象时建议使用的key
     */
    public static final String USER = "USER";

    /**
     * 在 Session 上存储角色时建议使用的key
     */
    public static final String ROLE_LIST = "ROLE_LIST";

    /**
     * 在 Session 上存储权限时建议使用的key
     */
    public static final String PERMISSION_LIST = "PERMISSION_LIST";

    /** 此 Session 的 id */
    private String id;



    //-----------------------构造器

    public SaSession(){}
    public SaSession(String loginId){
        this.id=loginId;
    }

    // ----------------------- TokenSign 相关

    /**
     * 此 Session 绑定的 Token 签名列表
     */
    private List<TokenSign> tokenSignList = new Vector<>();




    /**
     * 修改此Session的最小剩余存活时间 (只有在 Session 的过期时间低于指定的 minTimeout 时才会进行修改)
     * @param minTimeout 过期时间 (单位: 秒)
     */
    public void updateMinTimeout(Long minTimeout) {
        long min =trans(minTimeout);
        long cur =trans(getTimeout());
        if (cur <min){
            updateTimeout(minTimeout);
        }
    }

    public void updateTimeout(Long minTimeout) {
        SaManager.getSaTokenDao().updateSessionTimeout(minTimeout,this.id);
    }

    private Long getTimeout() {
        return SaManager.getSaTokenDao().getSessionTimeout(this.id);
    }

    private long trans(Long value) {
        return value == SatoKenDao.NEVER_EXPIRE?Long.MAX_VALUE:value;
    }

    /**
     * 添加token签名
     * @param token
     * @param device
     */
    public void addTokenSign(String token, String device) {
        addTokenSign(new TokenSign(token,device));
    }

    /**
     * 添加token签名
     * @param tokenSign
     */
    public void addTokenSign(TokenSign tokenSign) {
       //如果存在则返回
        if(getTokenSign(tokenSign.getValue())!=null){
            return;
        }
        //更新
        tokenSignList.add(tokenSign);
        update();

    }

    /**
     * 更新session
     */
    public void update() {
        SaManager.getSaTokenDao().updateSession(this);
    }

    /**
     * 查找token
     * @param value
     * @return
     */
    public TokenSign getTokenSign(String value) {
       return tokenSignListCopy().stream()
                .filter(tokenSign -> tokenSign.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }

    /**
     * 返回token签名副本
     * @return
     */
    public List<TokenSign> tokenSignListCopy(){
        return new ArrayList<>(tokenSignList);
    }

    /**
     * 根据设备获取token签名列表副本
     * @param device
     * @return
     */
    public List<TokenSign> tokenSignListCopyByDevice(String device) {
        List<TokenSign> tokenSigns = tokenSignListCopy();
        if (device ==null){
            return tokenSigns;
        }
       return tokenSigns.stream()
                .filter(tokenSign -> tokenSign.getDevice().equals(device))
                .collect(Collectors.toList());
    }

    /**
     * 清除token
     * @param token
     */
    public void removeTokenSign(String token) {
        TokenSign tokenSign = getTokenSign(token);
        if (tokenSignList.remove(tokenSign)){
            update();
        }
    }

    /**
     * tokensign为零，注销session
     */
    public void logoutByTokenSignCountToZero() {
        if (tokenSignList.size()==0){
            logout();
        }
    }

    /**
     * 注销
     */
    public void logout() {
        //删除session
        SaManager.getSaTokenDao().deleteSession(this.id);
        SaTokenEventCenter.doLogoutSession(id);
    }
}
