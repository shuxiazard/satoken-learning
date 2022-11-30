package com.shuxia.satoken.spring;

import com.shuxia.satoken.SaManager;
import com.shuxia.satoken.config.SaTokenConfig;
import com.shuxia.satoken.context.SaTokenContext;
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

    /**
     * 注入上下文bean
     */
    @Autowired(required = false)
    public void setContext(SaTokenContext saTokenContext){SaManager.setSaTokenContext(saTokenContext);}
}
