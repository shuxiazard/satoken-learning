package com.shuxia.satoken.listener;

import com.shuxia.satoken.stp.SaLoginModel;

/**
 * @author shuxia
 * @date 11/30/2022
 */
public interface SaTokenListener {
    void doLogin(String loginType, Object id, String token, SaLoginModel loginModel);

    void doLogout(String loginType, Object id, String token);

    void doLogoutSession(String id);

    void doReplaced(String loginType, Object id, String value);

    void doKickOut(String loginType, String loginId, String tokenValue);

    void doDisable(String loginType, Object loginId, String service, int level, long time);

    void doUntieDisable(String loginType, Object loginId, String service);
}
