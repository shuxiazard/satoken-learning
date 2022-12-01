package com.shuxia.satoken.listener;

import com.shuxia.satoken.stp.SaLoginModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 事件中心 事件发布器
 * @author shuxia
 * @date 11/30/2022
 */
public class SaTokenEventCenter  {
    // --------- 注册侦听器
    private static List<SaTokenListener> listenerList = new ArrayList<>();

    static {
        //默认添加控制台日志监听器
        listenerList.add(new SaTokenListenerForConsolePrint());
    }
    /**
     *  每次登录触发
     * @param loginType
     * @param id
     * @param token
     * @param loginModel
     */
    public static void doLogin(String loginType, Object id, String token, SaLoginModel loginModel) {
        for (SaTokenListener listener : listenerList) {
            listener.doLogin(loginType,id,token,loginModel);
        }
    }

    public static void doLogout(String loginType, Object id, String token) {
        for (SaTokenListener listener : listenerList) {
            listener.doLogout(loginType,id,token);
        }
    }

    public static void doLogoutSession(String id) {
        for (SaTokenListener listener : listenerList) {
            listener.doLogoutSession(id);
        }
    }

    public static void doReplaced(String loginType, Object id, String value) {
        for (SaTokenListener listener : listenerList) {
            listener.doReplaced(loginType,id,value);
        }
    }
}
