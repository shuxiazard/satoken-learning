import com.shuxia.satoken.SaManager;
import com.shuxia.satoken.context.SaTokenContext;
import com.shuxia.satoken.spring.App;
import com.shuxia.satoken.spring.SaTokenContextForSpring;
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

      //  StpUtil.login(100);
        StpUtil.checkLogin();
    }
}
