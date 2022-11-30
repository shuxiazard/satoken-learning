package com.shuxia.satoken.spring;

import com.shuxia.satoken.context.SaTokenContext;
import com.shuxia.satoken.context.model.SaStorage;
import com.shuxia.satoken.servlet.model.SaStorageForServlet;

import javax.swing.*;

/**
 * Sa-Token 上下文处理器 [ SpringMVC版本实现 ]
 * @author shuxia
 * @date 11/30/2022
 */
public class SaTokenContextForSpring implements SaTokenContext {
    @Override
    public SaStorage getStorage() {
     return new SaStorageForServlet(SpringMVCUtil.getRequest());
    }

    @Override
    public boolean isValid() {
        return SpringMVCUtil.isWeb();
    }
}
