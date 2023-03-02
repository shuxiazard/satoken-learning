package com.shuxia.satoken.context.model;

import com.shuxia.satoken.exception.SaTokenException;
import com.shuxia.satoken.util.SaFoxUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author shuxia
 * @date 3/1/2023
 */
@Getter
@Setter
public class SaCookie {
    /**
     * 写入响应头时使用的key
     */
    public static final String HEADER_NAME = "Set-Cookie";

    /**
     * 名称
     */
    private String name;

    /**
     * 值
     */
    private String value;

    /**
     * 有效时长 （单位：秒），-1代表为临时Cookie 浏览器关闭后自动删除
     */
    private int maxAge = -1;

    /**
     * 域
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

    public SaCookie(){};

    public SaCookie(String name,String value){
        this.name =name;
        this.value =value;
    }

    /**
     * 转换为响应头 Set-Cookie 参数需要的值
     * @return /
     */
    public String toHeaderValue() {

        if(SaFoxUtil.isEmpty(name)) {
            throw new SaTokenException("name不能为空");
        }
        if(value != null && value.contains(";")) {
            throw new SaTokenException("无效Value：" + value);
        }

        // Set-Cookie: name=value; Max-Age=100000; Expires=Tue, 05-Oct-2021 20:28:17 GMT; Domain=localhost; Path=/; Secure; HttpOnly; SameSite=Lax

        StringBuffer sb = new StringBuffer();
        sb.append(name).append("=").append(value);

        if(maxAge >= 0) {
            sb.append("; Max-Age=").append(maxAge);
            String expires;
            if(maxAge == 0) {
                expires = Instant.EPOCH.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME);
            } else {
                expires = OffsetDateTime.now().plusSeconds(maxAge).format(DateTimeFormatter.RFC_1123_DATE_TIME);
            }
            sb.append("; Expires=").append(expires);
        }
        if(!SaFoxUtil.isEmpty(domain)) {
            sb.append("; Domain=").append(domain);
        }
        if(!SaFoxUtil.isEmpty(path)) {
            sb.append("; Path=").append(path);
        }
        if(secure) {
            sb.append("; Secure");
        }
        if(httpOnly) {
            sb.append("; HttpOnly");
        }
        if(!SaFoxUtil.isEmpty(sameSite)) {
            sb.append("; sameSite=").append(sameSite);
        }

        return sb.toString();
    }



}
