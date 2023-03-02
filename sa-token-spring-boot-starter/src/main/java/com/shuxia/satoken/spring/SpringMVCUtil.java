package com.shuxia.satoken.spring;


import com.shuxia.satoken.context.SaTokenContext;
import com.shuxia.satoken.context.model.SaStorage;
import com.shuxia.satoken.exception.SaTokenException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;

/**
 * SpringMVC相关操作
 * @author shuxia
 * @date 11/30/2022
 */
public class SpringMVCUtil {

    private SpringMVCUtil(){}

    /**
     * 获取request
     * @return
     */
    public static HttpServletRequest getRequest(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (requestAttributes ==null){
            throw new SaTokenException("无法获取上下文");
        }
        return requestAttributes.getRequest();
    }
    public static SaStorage getSaStorage(){
        return new SaTokenContextForSpring().getStorage();
    }

    /**
     * 判断当前是否处于 Web 上下文中
     * @return
     */
    public static boolean isWeb() {
        return RequestContextHolder.getRequestAttributes() !=null;
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes==null){
            throw new SaTokenException("非web 无法获取Response");
        }
        return requestAttributes.getResponse();
    }
}
