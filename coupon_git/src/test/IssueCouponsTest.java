import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alibaba.fastjson.JSONObject;
import com.block.coupon.mapper.CouponMapper;
import com.block.coupon.mapper.MerchantMapper;
import com.block.coupon.service.MerchantService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.block.coupon.controller.MerchantController;
import com.block.coupon.po.CouponRulerCustom;


public class IssueCouponsTest extends SpringTest {

	private CouponRulerCustom couponRulerCustom;
    @Autowired
	private MerchantService merchantService;
    private CouponMapper couponMapper;
    private MerchantMapper merchantMapper;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        ApplicationContext ac = new FileSystemXmlApplicationContext("classpath:spring/applicationContext.xml");
        couponMapper = ac.getBean(CouponMapper.class);
        merchantMapper = ac.getBean(MerchantMapper.class);
		couponRulerCustom = new CouponRulerCustom();
        couponRulerCustom.setId("w1");
        couponRulerCustom.setMerchantId("mem1");
        couponRulerCustom.setReach(100);
        couponRulerCustom.setGive(10);
        couponRulerCustom.setIsAccumulation("1");
        couponRulerCustom.setCapping(100);
        couponRulerCustom.setTotalAmount(2000);
        couponRulerCustom.setIssueDate("2016-12-17");
        couponRulerCustom.setValidStartDate("2016-12-15");
        couponRulerCustom.setValidEndDate("2016-12-18");
        couponRulerCustom.setStatus("1");
	}
	//优惠卷发行测试controller
	@Test
	public void testPublishController() {
		try {		  
			String requestJson = JSONObject.toJSONString(couponRulerCustom);
			String responseString = mockMvc.perform( post("/merchant/issueCoupons.action")
					.contentType(MediaType.APPLICATION_JSON).content(requestJson))
					.andExpect(handler().handlerType(MerchantController.class))
					.andExpect(handler().methodName("issueCoupons"))
					.andExpect(status().isOk())
					.andReturn()
					.getResponse().getContentAsString();   //将相应的数据转换为字符串
			        System.out.println("--------返回的json = " + responseString);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
    //优惠卷发行测试service

    @Test
    public void testPublishService() throws Exception{
	    int balance_before = merchantMapper.querySettlementBalance(couponRulerCustom.getMerchantId());
        String resultCode = merchantService.insertCouponRuler(couponRulerCustom);
        String rulerId = couponRulerCustom.getId();
        assertEquals(rulerId, merchantMapper.queryConCurrentCouponRulerId(couponRulerCustom.getMerchantId()));
        int balance_after = merchantMapper.querySettlementBalance(couponRulerCustom.getMerchantId());
        assertEquals(balance_before, balance_after + couponRulerCustom.getTotalAmount());
        assertEquals(resultCode,"0");
    }
    //优惠卷发行测试mapper

    @Test
    public void testPublishMapper() throws Exception{
        couponMapper.insertCouponRuler(couponRulerCustom);
        assertNotNull(couponMapper.queryCouponRulerInfo(couponRulerCustom.getId()));
    }

}
