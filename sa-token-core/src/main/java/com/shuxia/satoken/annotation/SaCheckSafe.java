package com.shuxia.satoken.annotation;

import com.shuxia.satoken.util.SaTokenConsts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shuxia
 * @date 1/24/2023
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface SaCheckSafe {

    String type() default "";

    String value() default SaTokenConsts.DEFAULT_SAFE_AUTH_SERVICE;
}
