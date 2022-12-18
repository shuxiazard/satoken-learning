package com.shuxia.satoken.exception;

import com.shuxia.satoken.stp.StpLogic;
import com.shuxia.satoken.stp.StpUtil;

import java.util.List;

/**
 * @author shuxia
 * @date 12/18/2022
 */
public class NotPermissionException extends SaTokenException {
    /** 权限码 */
    private String permission;

    /**
     * @return 获得具体缺少的权限码
     */
    public String getPermission() {
        return permission;
    }

    /**
     * 账号类型
     */
    private String loginType;

    /**
     * 获得账号类型
     *
     * @return 账号类型
     */
    public String getLoginType() {
        return loginType;
    }

    public NotPermissionException(String permission) {
        this(permission, StpUtil.getLoginType());
    }

    public NotPermissionException(String permission, String loginType) {
        super("无此权限：" + permission);
        this.permission = permission;
        this.loginType = loginType;
    }
}
