package com.shuxia.satoken.context;

import com.shuxia.satoken.context.model.SaStorage;
import com.shuxia.satoken.exception.SaTokenException;

/**
 * @author shuxia
 * @date 11/30/2022
 */
public class SaTokenContextDefaultImpl implements SaTokenContext{
    /**
     * 默认上下文处理器
     */
    public static SaTokenContextDefaultImpl defaultContext =new SaTokenContextDefaultImpl();

    /**
     * 默认的错误提示语
     */
    public static final String ERROR_MESSAGE = "未初始化任何有效上下文处理器";


    @Override
    public SaStorage getStorage() {
        throw new SaTokenException(ERROR_MESSAGE);
    }


}
