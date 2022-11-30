package com.shuxia.satoken.stp;

import com.shuxia.satoken.SaManager;
import com.shuxia.satoken.config.SaTokenConfig;
import com.shuxia.satoken.util.SaTokenConsts;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 调用 `StpUtil.login()` 时的 [配置参数 Model ]
 * @author shuxia
 * @date 11/16/2022
 */
@Data
@Accessors(chain = true)
public class SaLoginModel {
    /**
     * 此次登录的客户端设备类型
     */
    public String device;

    /**
     * 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
     */
    public Boolean isLastingCookie = true;

    /**
     * 指定此次登录token的有效期, 单位:秒 （如未指定，自动取全局配置的timeout值）
     */
    public Long timeout;

    /**
     * 扩展信息（只在jwt模式下生效）
     */
    public Map<String, Object> extraData;

    /**
     * 预定Token（预定本次登录生成的Token值）
     */
    public String token;

    /** 是否在登录后将 Token 写入到响应头 */
    private Boolean isWriteHeader;

    public SaLoginModel build(){
        return build(SaManager.getConfig());
    }


    public String getDeviceOrDefault() {
        if (device ==null){
             return SaTokenConsts.DEFAULT_LOGIN_DEVICE;
        }
        return device;
    }
    /**
     * @return timeout 值 （如果此配置项尚未配置，则取全局配置的值）
     */
    public Long getTimeoutOrGlobalConfig() {
        if(timeout == null) {
            timeout = SaManager.getConfig().getTimeout();
        }
        return timeout;
    }

    /**
     * 构建对象，初始化默认值
     * @param config 配置对象
     * @return 对象自身
     */
    public SaLoginModel build(SaTokenConfig config){
        if(timeout == null) {
            timeout = config.getTimeout();
        }
        if(isWriteHeader == null) {
            isWriteHeader = config.getIsWriteHeader();
        }
        return this;
    }

}
