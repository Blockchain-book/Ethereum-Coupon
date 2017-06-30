package com.block.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.block.coupon.mapper.SettlementOperationMapper;
import com.block.coupon.po.BankStaffCustom;
import com.block.coupon.po.MerchantRegister;
import com.block.coupon.service.BankService;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by flynn on 17-3-3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
public class BankServiceImplTest {

    private Logger logger = Logger.getLogger(BankServiceImplTest.class);

    @Autowired
    private BankService bankService;

    @Autowired
    private SettlementOperationMapper settlementOperationMapper;



    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void test() throws Exception {
        logger.info(this.settlementOperationMapper.querySettlomentByMerchantId("id_1"));
    }

    /**
     * Method: insertBankStaff(BankStaffCustom bankStaffCustom)
     */
    @Test
    public void testInsertBankStaff() throws Exception {
        BankStaffCustom bankStaffCustom = new BankStaffCustom();
        bankStaffCustom.setPublicKey("test_publicKey_2");
        bankStaffCustom.setPassword("123456");
        bankStaffCustom.setPosition("1");
        bankStaffCustom.setAccount("zhangsan");
        String resuletcode = this.bankService.insertBankStaff(bankStaffCustom);
        logger.info(resuletcode);
    }

    /**
     * Method: queryBankStaffByAccount(String account)
     */
    @Test
    public void testQueryBankStaffByAccount() throws Exception {
        BankStaffCustom bankStaffCustom = this.bankService.queryBankStaffByAccount("kk");
        logger.info(JSON.toJSONString(bankStaffCustom));
    }

    /**
     * Method: testLogin()
     */
    @Test
    public void testLogin() throws Exception {
        String resultCode_1 = this.bankService.login("zhangsan", "1234568").get("resultCode");
        String resultCode_2 = this.bankService.login("zhangsan", "123456").get("resultCode");
        String resultCode_3 = this.bankService.login("wangwu", "1234568").get("resultCode");
        logger.info(resultCode_1 + ", " + resultCode_2 + ", " + resultCode_3);
    }

}
