package com.shuxia.satoken.bean;

import com.shuxia.satoken.SaManager;
import com.shuxia.satoken.config.SaTokenConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shuxia
 * @date 11/16/2022
 */
@Component
public class SaBeanInject {

    /**
     * 注入SaTokenConfig
     * @param saTokenConfig
     */
    @Autowired
    public void setConfig(SaTokenConfig saTokenConfig){
        SaManager.setConfig(saTokenConfig);
    }
}
