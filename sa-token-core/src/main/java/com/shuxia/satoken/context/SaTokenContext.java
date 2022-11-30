package com.shuxia.satoken.context;

import com.shuxia.satoken.context.model.SaStorage;

/**
 *  Sa-Token 上下文处理器
 * @author shuxia
 * @date 11/30/2022
 */
public interface SaTokenContext {
    /**
     * 获取当前请求的 [存储器] 对象
     *
     * @return see note
     */
     SaStorage getStorage();

   default boolean isValid(){
       return false;
   }
}
