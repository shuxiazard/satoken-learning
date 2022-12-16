package com.shuxia.satoken.listener;

import com.shuxia.satoken.SaManager;
import com.shuxia.satoken.dao.SatoKenDao;
import com.shuxia.satoken.stp.SaLoginModel;
import com.shuxia.satoken.util.SaFoxUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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
     * 每次被顶下线时触发
     */
    @Override
    public void doReplaced(String loginType, Object id, String value) {
        println("账号[" + id + "]被顶下线 (Token=" + value + ")");
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

    /**
     *  踢人下线
     * @param loginType
     * @param loginId
     * @param tokenValue
     */
    @Override
    public void doKickOut(String loginType, String loginId, String tokenValue) {
        println("账号[" + loginId + "]被踢下线 (Token=" + tokenValue + ")");
    }

    @Override
    public void doDisable(String loginType, Object loginId, String service, int level, long time) {
       //获取当前时间戳 utc通用时间
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis() + time * 1000);
        //特定时区
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        println("账号[" + loginId + "] " + service + " 服务被封禁，封禁等级=" + level + " (解封时间: " + zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ")");
    }

    @Override
    public void doUntieDisable(String loginType, Object loginId, String service) {
        println("账号[" + loginId + "] " + service + " 服务被解除封禁");
    }
}
