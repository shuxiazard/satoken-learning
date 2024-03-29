package com.shuxia.satoken.context.model;

import cn.hutool.extra.servlet.JakartaServletUtil;
import com.shuxia.satoken.SaManager;
import com.shuxia.satoken.session.SaSession;

/**
 *  上下文持有类
 * @author shuxia
 * @date 11/30/2022
 */
public class SaHolder {


    /**
     * 获取请求存储器
     * @return
     */
    public static SaStorage getStorage(){
        return SaManager.getSaTokenContextOrSecond().getStorage();
    }

    /**
     * 获取request
     * @return
     */
    public static SaRequest getRequest(){
        return SaManager.getSaTokenContextOrSecond().getRequest();
    }

    public static SaResponse getResponse() {
        return SaManager.getSaTokenContextOrSecond().getRespone();
    }
}
