import com.block.coupon.controller.MerchantController;
import com.block.coupon.service.MerchantService;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by ziqiji on 17/3/1.
 * 用户名格式使用手机端js确定，密码位数限制也由手机端确定
 */
public class MerchantAccessTest extends SpringTest{
    private String account;
    private String password;

    private MerchantService merchantService;

    @Before
    public void setup(){
        super.setup();
        ApplicationContext applicationContext = new FileSystemXmlApplicationContext("classpath:spring/applicationContext.xml");
        merchantService = applicationContext.getBean(MerchantService.class);
    }

    //用户名密码正确-controller
    @Test
    public void testLoginController(){
        try {
            account = "merchant_1";
            password = "111111";

            MvcResult result = mockMvc.perform(post("/merchant/login.action")
                    .param("account",account).param("password",password))
                    .andExpect(handler().handlerType(MerchantController.class))
                    .andExpect(handler().methodName("login"))
                    .andExpect(status().isOk())
                    .andReturn();
            JSONObject json_result = new JSONObject(result.getResponse().getContentAsString());
            assertEquals(json_result.get("resultCode"),"1");
            assertEquals(json_result.get("id"),"111");
            assertEquals(json_result.get("token"),"29e5c25f-7c4f-4a17-b6e7-9d25424d0991");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    //用户名或密码错误-controller
    @Test
    public void testWrongLoginController(){
        try {
            account = "merchant_1";
            password = "111";

            MvcResult result = mockMvc.perform(post("/merchant/login.action")
                    .param("account",account).param("password",password))
                    .andExpect(handler().handlerType(MerchantController.class))
                    .andExpect(handler().methodName("login"))
                    .andExpect(status().isOk())
                    .andReturn();
            JSONObject json_result = new JSONObject(result.getResponse().getContentAsString());
            assertEquals(json_result.get("resultCode"),"0");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    //用户名密码正确_service
    @Test
    public void testLoginService(){
        account = "merchant_1";
        password = "111111";

        try {
            Map<String,String> result = merchantService.login(account,password);
            assertEquals(result.get("resultCode"),"1");
            assertEquals(result.get("id"),"111");
            assertEquals(result.get("token"),"29e5c25f-7c4f-4a17-b6e7-9d25424d0991");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //用户名或密码不正确
    @Test
    public void testWrongLoginService(){
        account = "merchant_1";
        password = "111";

        try {
            Map<String,String> result = merchantService.login(account,password);
            assertEquals(result.get("resultCode"),"0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
