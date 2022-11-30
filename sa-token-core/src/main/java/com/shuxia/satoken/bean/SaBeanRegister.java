package com.shuxia.satoken.bean;

import com.shuxia.satoken.config.SaTokenConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 注册Sa-Token所需要的Bean
 * @author shuxia
 * @date 11/16/2022
 */

@Configuration
public class SaBeanRegister {
    /**
     * 获取配置Bean
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "sa-token")
    public SaTokenConfig getSaTokenConfig(){
        return new SaTokenConfig();
    }

}
