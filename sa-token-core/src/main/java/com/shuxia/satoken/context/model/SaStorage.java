package com.shuxia.satoken.context.model;

/**
 * @author shuxia
 * @date 11/30/2022
 */
public interface SaStorage {
    /**
     * 获取底层源对象
     * @return see note
     */
     Object getSource();

    // ---- 实现接口存取值方法

    /** 取值 */
     Object get(String key);

    /** 写值 */
     SaStorage set(String key, Object value);

    /** 删值 */
     SaStorage delete(String key);

}
