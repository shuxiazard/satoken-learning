package com.shuxia.satoken.stp;

/**
 * Sa-Token 权限认证工具类
 * @author shuxia
 * @date 11/16/2022
 */
public class StpUtil {
    private StpUtil(){}

    /**
     * 账号类型
     */
    public static final String TYPE="login";

    public static StpLogic stpLogic=new StpLogic(TYPE);
    /**
     * 获取当前 StpLogic 的账号类型
     * @return See Note
     */
    public static String getLoginType() {
        return stpLogic.getLoginType();
    }
    // ------------------- 登录相关操作 -------------------

    // --- 登录

    /**
     * 会话登录
     * @param id 账号id，建议的类型：（long | int | String）
     */
    public static void login(Object id) {
        stpLogic.login(id);
    }

    /**
     * 检查是否登录
     */
    public static void checkLogin(){
       stpLogic.checkLogin();
    }

    /**
     * 踢人下线
     */
    public static void kickout(Object loginId){
        stpLogic.kickout(loginId);
    }

    /**
     * 踢人下线 根据token
     * @param token
     */
    public static void kickoutByToken(String token){
        stpLogic.kickoutByTokenValue(token);
    }

    /**
     * 封禁账号
     * @param loginId
     * @param time 单位秒
     */
    public static void disable(Object loginId,long time){
        stpLogic.disable(loginId,time);
    }

    /**
     * 封禁账号
     * @param loginId
     * @param service
     * @param time 单位秒
     */
    public static void disable(Object loginId,String service,long time){
        stpLogic.disable(loginId,service,time);
    }

    /**
     * 账号是否封禁
     * @param loginId
     */
    public static boolean isDisable(Object loginId){
        return stpLogic.isDisable(loginId);
    }
    public static boolean isDisable(Object loginId,String service){
      return stpLogic.isDisable(loginId,service);
    }

    /**
     * 获取封禁时间
     * @param loginId
     * @return
     */
    public static long getDisableTime(Object loginId){
      return  stpLogic.getDisableTime(loginId);
    }

    public static void uniteDisable(Object loginId){
        stpLogic.uniteDisable(loginId);
    }
}
