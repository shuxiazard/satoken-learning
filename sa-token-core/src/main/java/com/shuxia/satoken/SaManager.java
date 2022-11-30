package com.shuxia.satoken;

import com.shuxia.satoken.config.SaTokenConfig;
import com.shuxia.satoken.context.SaTokenContext;
import com.shuxia.satoken.context.SaTokenContextDefaultImpl;
import com.shuxia.satoken.context.model.SaStorage;
import com.shuxia.satoken.dao.SaTokenDaoDefaultImpl;
import com.shuxia.satoken.dao.SatoKenDao;
import com.shuxia.satoken.stp.StpUtil;
import com.sun.xml.internal.fastinfoset.stax.StAXManager;

/**
 * @author shuxia
 * @date 11/16/2022
 */
public class SaManager {
    /**
     * 配置文件 Bean
     */
    public volatile static SaTokenConfig config;
    public static SaTokenConfig getConfig() {
        //TODO 手动获取config
        return config;
    }

    public static void setConfig(SaTokenConfig saTokenConfig) {
   SaManager.config=saTokenConfig;
        StpUtil.getLoginType();
    }


    /**
     * 持久化 Bean
     */
    private volatile static SatoKenDao saTokenDao;
    public static void setSaTokenDao(SatoKenDao saTokenDao){
        if ((SaManager.saTokenDao instanceof SaTokenDaoDefaultImpl)){
            //TODO 清楚定时任务
        }
        SaManager.saTokenDao=saTokenDao;

    }
    public static SatoKenDao getSaTokenDao() {
        if (saTokenDao ==null){
            synchronized (SaManager.class){
                if (saTokenDao ==null){
                    setSaTokenDao(new SaTokenDaoDefaultImpl());
                }
            }
        }
        return saTokenDao;
    }

    /**
     * 上下文
     */
    private volatile static SaTokenContext saTokenContext;
    public static void setSaTokenContext(SaTokenContext saTokenContext){
            SaManager.saTokenContext=saTokenContext;
        }
    public static SaTokenContext getSaTokenContext(){return saTokenContext;}



    /**
     * 获取一个可用的SaTokenContext
     * @return /
     */
    public static SaTokenContext getSaTokenContextOrSecond() {
        if (saTokenContext !=null && saTokenContext.isValid()){
            return saTokenContext;
        }
        //返回默认上下文处理器
        return SaTokenContextDefaultImpl.defaultContext;
    }

    public static void setSaTokenSecondContext(SaTokenContext saTokenContext) {
    }
}
