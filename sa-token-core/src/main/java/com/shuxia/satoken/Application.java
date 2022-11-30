package com.shuxia.satoken;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author shuxia
 * @date 11/30/2022
 */
@SpringBootApplication(scanBasePackages = "com.shuxia.satoken")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
