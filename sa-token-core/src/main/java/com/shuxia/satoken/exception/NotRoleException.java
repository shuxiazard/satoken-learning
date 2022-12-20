package com.shuxia.satoken.exception;

import com.shuxia.satoken.stp.StpUtil;

/**
 * @author shuxia
 * @date 12/20/2022
 */
public class NotRoleException extends SaTokenException{
    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 8243974276159004739L;

    /** 角色标识 */
    private String role;

    /**
     * @return 获得角色标识
     */
    public String getRole() {
        return role;
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

    public NotRoleException(String role) {
        this(role, StpUtil.getLoginType());
    }

    public NotRoleException(String role, String loginType) {
        super("无此角色：" + role);
        this.role = role;
        this.loginType = loginType;
    }
}
