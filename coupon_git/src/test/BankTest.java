import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.codehaus.jettison.json.JSONObject;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import com.block.coupon.controller.BankController;
import com.block.coupon.mapper.BankMapper;
import com.block.coupon.po.BankStaffCustom;
import com.block.coupon.service.BankService;
import com.block.coupon.sha3.Sha3;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


public class BankTest extends SpringTest{
	/**
	 * @author thomas
	 * 测试登录功能
	 */
	@Autowired
	private BankService bankService;
	private BankMapper bankMapper;


	private String account_right;
	private String password_right;
	private String account_wrong;
	private String password_wrong;
	
	
	@Before
	public void setup(){
		super.setup();
		ApplicationContext ac = new FileSystemXmlApplicationContext("classpath:spring/applicationContext.xml");
		bankMapper = ac.getBean(BankMapper.class);

		account_right = "thomas";
		password_right = "123";
		account_wrong = "thomas";
		password_wrong = "123456";
	}
//	测试登录controller
	@Test
	public void testBankLoginController() {
		//可用账户正常登陆
		try {
			MvcResult result = mockMvc.perform(post("/bank/login.action").
					param("account", account_right).param("password", password_right))
					.andExpect(handler().handlerType(BankController.class))
					.andExpect(handler().methodName("login"))
					.andExpect(status().isOk())
					.andDo(print()).andReturn();
			JSONObject jsonResult = new JSONObject(result.getResponse().getContentAsString());
			assertEquals(jsonResult.get("resultCode"),"1");
			assertEquals(jsonResult.get("position"),"1");
			assertEquals(jsonResult.get("id"),"29e5c25f-7c4f-4a17-b6e7-9d25424d0991");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//可用账户密码错误登录
		try{
			MvcResult result = mockMvc.perform(post("/bank/login.action").param("account", account_wrong)
					.param("password", password_wrong))
					.andExpect(status().isOk())
					.andExpect(handler().handlerType(BankController.class))
					.andExpect(handler().methodName("login"))
					.andReturn();
			JSONObject json_result = new JSONObject(result.getResponse().getContentAsString());
			assertEquals(json_result.get("resultCode"),"0");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	测试登录的service
	@Test
	public void testBankLoginService() throws Exception{
		//正常登录
		Map<String, String> result = bankService.login(account_right, password_right);
		assertEquals(result.get("resultCode"),"1");
		assertEquals(result.get("position"),"1");
		assertEquals(result.get("id"),"29e5c25f-7c4f-4a17-b6e7-9d25424d0991");
		
		//密码错误登录
		Map<String, String> result1 = bankService.login(account_wrong, password_wrong);
		assertEquals(result1.get("resultCode"),"0");
	}
	
//	测试登录的Mapper
	@Test
	public void testBankLoginMapper() throws Exception{
		BankStaffCustom bsc = bankMapper.queryBankStaffByAccount(account_right);
		assertNotNull(bsc.getPassword());
		assertEquals(Sha3.sha3(password_right+bsc.getSalt()),bsc.getPassword());
		assertNotNull(bsc.getPublicKey());
	}
}
