import com.shuxia.satoken.SaManager;
import com.shuxia.satoken.context.SaTokenContext;
import com.shuxia.satoken.dao.SatoKenDao;
import com.shuxia.satoken.spring.App;
import com.shuxia.satoken.spring.SaTokenContextForSpring;
import com.shuxia.satoken.stp.StpLogic;
import com.shuxia.satoken.stp.StpUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

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
}
