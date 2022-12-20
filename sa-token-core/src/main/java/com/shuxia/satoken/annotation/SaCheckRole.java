package com.shuxia.satoken.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shuxia
 * @date 12/20/2022
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface SaCheckRole {
    String[] value() default {};

    SaMode mode() default SaMode.AND;

    String[] orPermission() default {};
}
