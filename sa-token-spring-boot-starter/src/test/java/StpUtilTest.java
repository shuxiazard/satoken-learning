import com.shuxia.satoken.SaManager;
import com.shuxia.satoken.annotation.SaCheckLogin;
import com.shuxia.satoken.context.SaTokenContext;
import com.shuxia.satoken.dao.SatoKenDao;
import com.shuxia.satoken.spring.App;
import com.shuxia.satoken.spring.SaTokenContextForSpring;
import com.shuxia.satoken.spring.demo.CheckAnnotation;
import com.shuxia.satoken.stp.StpInterface;
import com.shuxia.satoken.stp.StpLogic;
import com.shuxia.satoken.stp.StpUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author shuxia
 * @date 11/30/2022
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class StpUtilTest {


    @Test
    public void test(){

      StpUtil.login(100);
     // StpUtil.checkLogin();
        StpLogic stpLogic = new StpLogic("login");
        SatoKenDao saTokenDao = stpLogic.getSaTokenDao();

        StpUtil.kickout(100);
        StpUtil.checkLogin();
    }

    @Test
    public void testDisable(){
        StpUtil.disable(100,1000);
        System.out.println(StpUtil.isDisable(100));
        System.out.println(StpUtil.getDisableTime(100));
        StpUtil.uniteDisable(100);
        System.out.println(StpUtil.isDisable(100));
    }


    @Test
    public void testPermission(){
        StpUtil.login(100);
        System.out.println("权限相关:   "+StpUtil.getPermissionList());
        System.out.println(StpUtil.getPermissionList(100));
        System.out.println("是否有a权限：  "+StpUtil.hasPermission("a"));
        System.out.println("是否有d权限：  "+StpUtil.hasPermission("d"));
        System.out.println("是否有a,b权限：  "+StpUtil.hasPermissionAnd("a", "b"));
        System.out.println("是否有a,d权限：  "+StpUtil.hasPermissionAnd("a", "d"));
        System.out.println("是否有a，d任意权限：  "+StpUtil.hasPermissionOr("a", "d"));
        System.out.println("是否有c，d任意权限：  "+StpUtil.hasPermissionOr("c", "d"));

    }

    @Autowired
    CheckAnnotation checkAnnotation;
    @Test
    public void testCheck(){
        StpUtil.login(100);
        checkAnnotation.checkRole();
    }
}
