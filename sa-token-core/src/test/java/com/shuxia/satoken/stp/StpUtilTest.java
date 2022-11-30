package com.shuxia.satoken.stp;


import com.shuxia.satoken.Application;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)

class StpUtilTest {


    @Test
    public void testLogin() {
        StpUtil.login(100);
    }
}