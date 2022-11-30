package com.shuxia.satoken.strategy;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.shuxia.satoken.SaManager;
import com.shuxia.satoken.session.SaSession;
import com.shuxia.satoken.util.SaFoxUtil;
import com.shuxia.satoken.util.SaTokenConsts;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author shuxia
 * @date 11/16/2022
 */
public class SaStrategy {
    private SaStrategy() {
    }


    /**
     * 获取 SaStrategy 对象的单例引用
     */
    public static final SaStrategy me = new SaStrategy();

    public Function<String, SaSession> createSession =(sessionId) -> new SaSession(sessionId);


    /**
     * 创建 Token 的策略
     * <p> 参数 [账号id, 账号类型]
     * BiFunction
     */
  public BiFunction<Object ,String,String> creatToken =(loginId, loginType) ->{
    //根据tokenStyle生成不同风格的token
        String tokenStyle = SaManager.getConfig().getTokenStyle();
        // uuid
        if(SaTokenConsts.TOKEN_STYLE_UUID.equals(tokenStyle)) {
            return UUID.randomUUID().toString();
        }
        // 简单uuid (不带下划线)
        if(SaTokenConsts.TOKEN_STYLE_SIMPLE_UUID.equals(tokenStyle)) {
            return UUID.randomUUID().toString().replaceAll("-", "");
        }
        // 32位随机字符串
        if(SaTokenConsts.TOKEN_STYLE_RANDOM_32.equals(tokenStyle)) {
            return RandomUtil.randomString(32);
        }
        // 64位随机字符串
        if(SaTokenConsts.TOKEN_STYLE_RANDOM_64.equals(tokenStyle)) {
            return RandomUtil.randomString(64);
        }
        // 128位随机字符串
        if(SaTokenConsts.TOKEN_STYLE_RANDOM_128.equals(tokenStyle)) {
            return RandomUtil.randomString(128);
        }
        // tik风格 (2_14_16)
        if(SaTokenConsts.TOKEN_STYLE_TIK.equals(tokenStyle)) {
            return RandomUtil.randomString(2) + "_" + RandomUtil.randomString(14)+ "_" + RandomUtil.randomString(16) + "__";
        }
        // 默认，还是uuid
        return UUID.randomUUID().toString();
    };
}
