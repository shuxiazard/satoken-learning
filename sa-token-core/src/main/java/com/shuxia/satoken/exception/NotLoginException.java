package com.shuxia.satoken.exception;

import com.shuxia.satoken.util.SaFoxUtil;
import lombok.Getter;
import sun.awt.HKSCS;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * @author shuxia
 * @date 12/1/2022
 */
public class NotLoginException extends RuntimeException{

    /** 表示token已被顶下线 */
    public static final String BE_REPLACED = "-4";
    public static final String BE_REPLACED_MESSAGE = "Token已被顶下线";

    /** 表示未提供token */
    public static final String NOT_TOKEN = "-1";
    public static final String NOT_TOKEN_MESSAGE = "未能读取到有效Token";

    /** 表示token无效 */
    public static final String INVALID_TOKEN = "-2";
    public static final String INVALID_TOKEN_MESSAGE = "Token无效";

    /** 表示token已过期 */
    public static final String TOKEN_TIMEOUT = "-3";
    public static final String TOKEN_TIMEOUT_MESSAGE = "Token已过期";

    /** 表示token已被踢下线 */
    public static final String KICK_OUT = "-5";
    public static final String KICK_OUT_MESSAGE = "Token已被踢下线";

    /** 默认的提示语 */
    public static final String DEFAULT_MESSAGE = "当前会话未登录";

    /** 无效登录id*/
    public static final List<String> ABNORMAL_LIST = Arrays.asList(NOT_TOKEN,INVALID_TOKEN, TOKEN_TIMEOUT, BE_REPLACED, KICK_OUT);


    /**
     * 异常类型
     */
    @Getter
    private String type;


    /**
     * 账号类型
     */
    @Getter
    private String loginType;


    /**
     * 构造方法创建一个
     * @param message 异常消息
     * @param loginType 账号类型
     * @param type 类型
     */
    public NotLoginException(String message, String loginType, String type) {
        super(message);
        this.loginType = loginType;
        this.type = type;
    }


    /**
     * 静态方法构建一个NotLoginException
     * @param loginType 账号类型
     * @param type 账号类型
     * @return 构建完毕的异常对象
     */
    public static NotLoginException newInstance(String loginType, String type) {
        return newInstance(loginType, type, null);
    }

    /**
     * 静态方法构建一个NotLoginException
     * @param loginType 账号类型
     * @param type 账号类型
     * @param token 引起异常的Token值
     * @return 构建完毕的异常对象
     */
    public static NotLoginException newInstance(String loginType, String type, String token) {
        String message = null;
        if(NOT_TOKEN.equals(type)) {
            message = NOT_TOKEN_MESSAGE;
        }

        else {
            message = DEFAULT_MESSAGE;
        }
        if(SaFoxUtil.isEmpty(token) == false) {
            message = message + "：" + token;
        }
        return new NotLoginException(message, loginType, type);
    }

}
