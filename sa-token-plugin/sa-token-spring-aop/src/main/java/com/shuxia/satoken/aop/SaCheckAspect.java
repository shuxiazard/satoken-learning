package com.shuxia.satoken.aop;

import com.shuxia.satoken.annotation.SaIgnore;
import com.shuxia.satoken.strategy.SaStrategy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author shuxia
 * @date 12/20/2022
 */
@Aspect
@Component
public class SaCheckAspect {
    public SaCheckAspect(){}

    /**
     * 定义AOP签名 (切入所有使用sa-token鉴权注解的方法)
     */
    public static final String POINTCUT_SIGN =
            "@within(com.shuxia.satoken.annotation.SaCheckLogin) || @annotation(com.shuxia.satoken.annotation.SaCheckLogin)";


    @Pointcut(POINTCUT_SIGN)
    public void pointcut(){}

    @Around("pointcut()")
    public Object arround(ProceedingJoinPoint joinPoint)throws Throwable{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        //如果标注@SaIgnore,忽略鉴权
       if (SaStrategy.me.isAnnotationPresent.apply(method, SaIgnore.class)){
           //do something
       }else{
           //注解鉴权
           SaStrategy.me.checkMethodAnnotation.accept(method);
       }
       try {
           return joinPoint.proceed();
       }catch (Throwable e){
           throw e;
       }
    }
}
