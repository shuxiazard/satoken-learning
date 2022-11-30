package com.shuxia.satoken.listener;

import com.shuxia.satoken.SaManager;
import com.shuxia.satoken.dao.SatoKenDao;
import com.shuxia.satoken.stp.SaLoginModel;

/**
 * 控制台log打印
 * @author shuxia
 * @date 11/30/2022
 */
public class SaTokenListenerForConsolePrint implements SaTokenListener {
    /**
     * 每次登录时触发
     */
    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
        println("账号[" + loginId + "]登录成功");
    }

    /**
     * 每次注销时触发
     */
    @Override
    public void doLogout(String loginType, Object id, String token) {
        println("账号[" + id + "]注销成功 (Token=" + token + ")");
    }
    /**
     * 每次注销Session时触发
     * @param id SessionId
     */
    @Override
    public void doLogoutSession(String id) {
        println("Session[" + id + "]注销成功");
    }

    /**
     * 日志输出的前缀
     */
    public static final String LOG_PREFIX = "SaLog -->: ";
    /**
     * 打印指定字符串
     * @param str 字符串
     */
    public void println(String str) {
        if(SaManager.getConfig().getIsLog()) {
            System.out.println(LOG_PREFIX + str);
        }
    }
}
