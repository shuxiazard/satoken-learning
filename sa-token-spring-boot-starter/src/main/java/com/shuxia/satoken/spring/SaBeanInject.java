package com.shuxia.satoken.spring;

import com.shuxia.satoken.SaManager;
import com.shuxia.satoken.config.SaTokenConfig;
import com.shuxia.satoken.context.SaTokenContext;
import com.shuxia.satoken.dao.SatoKenDao;
import com.shuxia.satoken.stp.StpInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shuxia
 * @date 11/16/2022
 */
@Component
public class SaBeanInject {


    /**
     * 注入持久化Bean （redis 等）
     * @param satoKenDao
     */
    @Autowired(required = false)
    public void setSaToken(SatoKenDao satoKenDao){SaManager.setSaTokenDao(satoKenDao);}

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

    /**
     * 注入权限认证Bean
     *
     * @param stpInterface StpInterface对象
     */
    @Autowired(required = false)
    public void setStpInterface(StpInterface stpInterface){SaManager.setStpInterface(stpInterface);}
}
