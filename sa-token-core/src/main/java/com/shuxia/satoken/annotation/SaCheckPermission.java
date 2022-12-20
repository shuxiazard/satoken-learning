package com.shuxia.satoken.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shuxia
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface SaCheckPermission {
    /**
     * 需要校验的权限
     * @return /
     */
    String[] value() default {};

    /**
     * 验证模式：AND | OR，默认AND
     * @return 验证模式
     */
    SaMode mode() default SaMode.AND;

    /**
     * 在权限认证不通过时的次要选择，两者只要其一认证成功即可通过校验
     * @return
     */
    String[] orRole() default {};
}
