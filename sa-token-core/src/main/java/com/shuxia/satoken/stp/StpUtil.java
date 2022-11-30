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

}
