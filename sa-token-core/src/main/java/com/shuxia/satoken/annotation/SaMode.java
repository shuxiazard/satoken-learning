package com.shuxia.satoken.annotation;

/**
 * 注解鉴权的验证模式
 * @author shuxia
 * @date 12/20/2022
 */
public enum SaMode {
    /**
     * 必须具有所有的元素
     */
    AND,

    /**
     * 只需具有其中一个元素
     */
    OR
}
