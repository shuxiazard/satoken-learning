package com.shuxia.satoken.spring;

import com.shuxia.satoken.annotation.SaCheckLogin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author shuxia
 * @date 11/30/2022
 */
@SpringBootApplication(scanBasePackages = "com.shuxia.satoken")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }
}
