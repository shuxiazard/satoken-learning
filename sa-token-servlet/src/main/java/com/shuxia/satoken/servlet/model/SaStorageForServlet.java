package com.shuxia.satoken.servlet.model;

import com.shuxia.satoken.context.model.SaStorage;

import javax.servlet.http.HttpServletRequest;

/**
 * @author shuxia
 * @date 11/30/2022
 */
public class SaStorageForServlet implements SaStorage {
    /**
     * request对象
     */
    protected HttpServletRequest httpServletRequest;

    public SaStorageForServlet(HttpServletRequest httpServletRequest){this.httpServletRequest=httpServletRequest;}

    @Override
    public Object getSource() {
        return httpServletRequest;
    }

    /**
     * 在request获取值
     * @param key
     * @return
     */
    @Override
    public Object get(String key) {
        return httpServletRequest.getAttribute(key);
    }

    /**
     * 在request设置值
     * @param key
     * @param value
     * @return
     */
    @Override
    public SaStorage set(String key, Object value) {
       httpServletRequest.setAttribute(key,value);
       return this;
    }

    @Override
    public SaStorage delete(String key) {
        httpServletRequest.removeAttribute(key);
        return this;
    }
}
