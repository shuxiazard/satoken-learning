package com.shuxia.satoken.spring.demo;

import com.shuxia.satoken.annotation.SaCheckPermission;
import com.shuxia.satoken.annotation.SaMode;
import org.springframework.stereotype.Component;

/**
 * @author shuxia
 * @date 12/20/2022
 */
@Component
public class CheckAnnotation {

    @SaCheckPermission(value = {"a","bb"})
    public void checkPermission(){
        System.out.println("check ok");
    }
    @SaCheckPermission(value = {"a","bb"},mode = SaMode.OR)
    public void checkPermission2(){
        System.out.println("check ok");
    }
    @SaCheckPermission(value = {"a,b"},mode = SaMode.OR,orRole = {"a,a"})
    public void checkPermission3(){
        System.out.println("check ok");
    }
}
