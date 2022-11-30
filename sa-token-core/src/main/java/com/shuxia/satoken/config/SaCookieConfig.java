package com.shuxia.satoken.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Sa-Token Cookie写入 相关配置
 * @author shuxia
 * @date 11/16/2022
 */

@Data
@Accessors(chain = true)
public class SaCookieConfig {
    /**
     * 域（写入Cookie时显式指定的作用域, 常用于单点登录二级域名共享Cookie的场景）
     */
    private String domain;

    /**
     * 路径
     */
    private String path;

    /**
     * 是否只在 https 协议下有效
     */
    private Boolean secure = false;

    /**
     * 是否禁止 js 操作 Cookie
     */
    private Boolean httpOnly = false;

    /**
     * 第三方限制级别（Strict=完全禁止，Lax=部分允许，None=不限制）
     */
    private String sameSite;

}
