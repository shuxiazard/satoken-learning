package com.shuxia.satoken.servlet.model;

import com.shuxia.satoken.context.model.SaRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author shuxia
 * @date 12/1/2022
 */
public class SaRequestForServlet implements SaRequest {

    protected HttpServletRequest request;
    public SaRequestForServlet(HttpServletRequest request){this.request=request;}

    @Override
    public Object getSource() {
        return null;
    }

    @Override
    public String getParam(String name) {
        return null;
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public String getCookieValue(String name) {
        return null;
    }

    @Override
    public String getRequestPath() {
        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String getMethod() {
        return null;
    }
}
