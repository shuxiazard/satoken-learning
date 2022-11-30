package com.shuxia.satoken.util;

/**
 * @author shuxia
 * @date 11/16/2022
 */
public class SaFoxUtil {

    /**
     * 指定元素是否为null或者空字符串
     * @param str 指定元素
     * @return 是否为null或者空字符串
     */
    public static boolean isEmpty(Object str){
        return str == null ||"".equals(str);
    }


    /**
     * 指定元素是否不为 (null或者空字符串)
     * @param str 指定元素
     * @return 是否为null或者空字符串
     */
    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }
}
