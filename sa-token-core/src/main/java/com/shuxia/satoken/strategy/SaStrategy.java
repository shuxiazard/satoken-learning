package com.shuxia.satoken.strategy;

import cn.hutool.core.util.RandomUtil;
import com.shuxia.satoken.SaManager;
import com.shuxia.satoken.annotation.SaCheckLogin;
import com.shuxia.satoken.annotation.SaCheckPermission;
import com.shuxia.satoken.annotation.SaCheckRole;
import com.shuxia.satoken.session.SaSession;
import com.shuxia.satoken.util.SaTokenConsts;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

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

    public Function<String, SaSession> createSession = SaSession::new;


    //region -------------------注解鉴权相关-----------------
    /**
     * 判断Method或其所属的Class是否包含指定注解
     */
    public BiFunction<Method,Class<? extends Annotation>,Boolean> isAnnotationPresent =((method, aClass) ->
            me.getAnnotation.apply(method,aClass) !=null ||
                    me.getAnnotation.apply(method.getDeclaringClass(),aClass)!=null);
    /**
     * 从元素（类，方法---所有实现AnnotatedElement类）上获取注解
     */
    public BiFunction<AnnotatedElement,Class< ? extends Annotation>,Annotation> getAnnotation=(AnnotatedElement::getAnnotation);
    /**
     * 对一个 [Method] 对象进行注解校验
     */
   public Consumer<Method> checkMethodAnnotation=(method -> {
       //检验Class上的注解
       me.checkElementAnnotation.accept(method.getDeclaringClass());
       //Method上的注解
       me.checkElementAnnotation.accept(method);
   });
    /**
     * 对一个 [Method] 对象进行注解校验 （注解鉴权内部实现）
     */
   public Consumer<AnnotatedElement> checkElementAnnotation =(element->{
       //检验@SaCheckLogin 注解
       SaCheckLogin checkLogin = (SaCheckLogin) SaStrategy.me.getAnnotation.apply(element, SaCheckLogin.class);
       if (checkLogin!=null){
           SaManager.getStpLogic().checkByAnnotation(checkLogin);
       }
       //检验@SaCheckPermission
        SaCheckPermission saCheckPermission = (SaCheckPermission) SaStrategy.me.getAnnotation.apply(element, SaCheckPermission.class);
       if (saCheckPermission!=null){
           SaManager.getStpLogic().checkByAnnotation(saCheckPermission);
       }
       // //检验@SaCheckPermission
        SaCheckRole saCheckRole = (SaCheckRole) SaStrategy.me.getAnnotation.apply(element, SaCheckRole.class);
       if (saCheckRole!=null){
           SaManager.getStpLogic().checkByAnnotation(saCheckRole);
       }


    });
    //endregion

    /**
     * 判断集合是否包含指定元素
     */
    public BiFunction<List<String>,String,Boolean> hasElement =(list,element)->{
        if (CollectionUtils.isEmpty(list)){
            return false;
        }
        if (list.contains(element)){
            return true;
        }
        //模糊查询
        for (String s : list) {
            if (!s.contains("*")){
                return s.equals(element);
            }
            return Pattern.matches(s.replaceAll("\\*",".*"),element);
        }
        return false;
    };

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
