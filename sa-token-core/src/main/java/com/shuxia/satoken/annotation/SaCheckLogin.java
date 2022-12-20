package com.shuxia.satoken.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 登录鉴权
 * @author shuxia
 * @date 12/20/2022
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface SaCheckLogin {
    /**
     * 账号体系标识
     * @return
     */
    String type() default "";
}
