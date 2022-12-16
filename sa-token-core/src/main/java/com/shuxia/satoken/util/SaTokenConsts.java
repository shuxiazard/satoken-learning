package com.shuxia.satoken.util;

/**
 * @author shuxia
 * @date 11/16/2022
 */
public interface SaTokenConsts {
     String DEFAULT_LOGIN_DEVICE = "default-device";


     // =================== token-style 相关 ===================

     /**
      * Token风格: uuid
      */
      String TOKEN_STYLE_UUID = "uuid";

     /**
      * Token风格: 简单uuid (不带下划线)
      */
      String TOKEN_STYLE_SIMPLE_UUID = "simple-uuid";

     /**
      * Token风格: 32位随机字符串
      */
      String TOKEN_STYLE_RANDOM_32 = "random-32";

     /**
      * Token风格: 64位随机字符串
      */
     String TOKEN_STYLE_RANDOM_64 = "random-64";

     /**
      * Token风格: 128位随机字符串
      */
      String TOKEN_STYLE_RANDOM_128 = "random-128";

     /**
      * Token风格: tik风格 (2_14_16)
      */
     String TOKEN_STYLE_TIK = "tik";

    /**
     * 连接Token前缀和Token值的字符
     */
   String TOKEN_CONNECTOR_CHAT  = " ";


    /**
     * 常量key标记: 如果token为本次请求新创建的，则以此字符串为key存储在当前request中
     */
    public static final String JUST_CREATED = "JUST_CREATED_";

    /**
     * 常量key标记: 如果token为本次请求新创建的，则以此字符串为key存储在当前request中（不拼接前缀，纯Token）
     */
    public static final String JUST_CREATED_NOT_PREFIX = "JUST_CREATED_NOT_PREFIX_";


    /**
     * 常量key标记: 在封禁账号时，默认封禁的服务类型
     */
    public static final String DEFAULT_DISABLE_SERVICE = "login";

    /**
     * 常量key标记: 在封禁账号时，默认封禁的等级
     */
    public static final int DEFAULT_DISABLE_LEVEL = 1;

    /**
     * 常量key标记: 在封禁账号时，可使用的最小封禁级别
     */
    public static final int MIN_DISABLE_LEVEL = 1;

    /**
     * 常量key标记: 账号封禁级别，表示未被封禁
     */
    public static final int NOT_DISABLE_LEVEL = -2;

}
