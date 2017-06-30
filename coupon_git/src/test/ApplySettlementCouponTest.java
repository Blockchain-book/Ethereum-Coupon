import com.block.coupon.controller.MerchantController;
import com.block.coupon.mapper.SettlementOperationMapper;
import com.block.coupon.po.SettlementOperationCustom;
import com.block.coupon.service.MerchantService;
import com.block.coupon.util.JSONUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.test.web.servlet.MvcResult;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by ziqiji on 17/2/28.
 * 测试结算卷申请
 */
public class ApplySettlementCouponTest extends SpringTest {

    private SettlementOperationCustom settlementOperationCustom;
    private SettlementOperationCustom wrongSettlementOperationCustom;
    private MerchantService merchantService;
    private SettlementOperationMapper settlementOperationMapper;

    @Before
    public void setup() {
        super.setup();

        settlementOperationCustom = new SettlementOperationCustom();
        settlementOperationCustom.setBankStaffId("bank111");
        settlementOperationCustom.setMerchantId("merchant111");
        settlementOperationCustom.setOperationAmount(1000);
        settlementOperationCustom.setOperationDate("2017-1-20");
        settlementOperationCustom.setOperationType("1");

        wrongSettlementOperationCustom = new SettlementOperationCustom();
        wrongSettlementOperationCustom.setBankStaffId("bank111");
        wrongSettlementOperationCustom.setMerchantId("merchant111");
        wrongSettlementOperationCustom.setOperationAmount(10000);
        wrongSettlementOperationCustom.setOperationDate("2017-1-20");
        wrongSettlementOperationCustom.setOperationType("1");

        ApplicationContext applicationContext = new FileSystemXmlApplicationContext("classpath:spring/applicationContext.xml");
        merchantService = applicationContext.getBean(MerchantService.class);
        settlementOperationMapper = applicationContext.getBean(SettlementOperationMapper.class);
    }

    //测试controller
    //可正常申请结算卷
    @Test
    public void testApplySettlementCouponController(){
        try {
            //将对象转换成json类型
            String soc = JSONUtils.objTojson(settlementOperationCustom);
            System.out.println(soc+"\n\n\n\n\n\n\n\n\n");

            MvcResult result = mockMvc.perform(post("/merchant/applySettlementCoupon.action")
                    .contentType(MediaType.APPLICATION_JSON).content(soc))
                    .andExpect(handler().handlerType(MerchantController.class))
                    .andExpect(handler().methodName("applySettlementCoupon"))
                    .andExpect(status().isOk())
                    .andReturn();
            JSONObject json_result = new JSONObject(result.getResponse().getContentAsString());
            assertEquals(json_result.get("resultCode"),"1");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    //测试controller
    //不能正常申请结算卷
    @Test
    public void testWrongApplySettlementCouponController(){
        try {
            //将对象转换成json类型
            String soc = JSONUtils.objTojson(wrongSettlementOperationCustom);
            System.out.println(soc+"\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

            MvcResult result = mockMvc.perform(post("/merchant/applySettlementCoupon.action")
                    .contentType(MediaType.APPLICATION_JSON).content(soc))
                    .andExpect(handler().handlerType(MerchantController.class))
                    .andExpect(handler().methodName("applySettlementCoupon"))
                    .andExpect(status().isOk())
                    .andReturn();
            JSONObject json_result = new JSONObject(result.getResponse().getContentAsString());
            assertEquals(json_result.get("resultCode"),"0");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    //测试service(可正常进行结算卷的申请)
    @Test
    public void testApplySettlementCouponService(){
        try {
            String result = merchantService.insertSettlementOperation(settlementOperationCustom);
            assertEquals(result,"1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //测试service(不可正常进行结算卷的申请)
    @Test
    public void testWrongApplySettlementCouponService(){
        try {
            String result = merchantService.insertSettlementOperation(wrongSettlementOperationCustom);
            assertEquals(result,"0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    //测试mapper(可正常进行结算卷的申请)
    @Test
    public void testApplySettlementCouponMapper(){
        try {
            String result = settlementOperationMapper.insertSettlementOperation(settlementOperationCustom);
            assertEquals(result,"1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //测试mapper(不可正常进行结算卷的申请)
    @Test
    public void testWrongApplySettlementCouponMapper(){
        try {
            String result = settlementOperationMapper.insertSettlementOperation(wrongSettlementOperationCustom);
            assertEquals(result,"0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    **/
}
