package com.shuxia.satoken.exception;

/**
 * @author shuxia
 * @date 12/1/2022
 */
public class NotLoginException extends RuntimeException{

    /** 表示token已被顶下线 */
    public static final String BE_REPLACED = "-4";
    public static final String BE_REPLACED_MESSAGE = "Token已被顶下线";
}
