package com.block.coupon.controller;

import com.block.coupon.po.MerchantRegisterCustom;
import com.block.coupon.po.SettlementApplication;
import com.block.coupon.service.BankService;
import com.block.coupon.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = "/main")
public class MainController {

    @Autowired
    private BankService bankService;


    /**
     * 跳转到登录界面
     * @return
     */
    @RequestMapping("/login")
    public String toBankStaffRegister() {
        return "/bank/login";
    }

    /**
     * 跳转到银行职员初审界面
     * @return
     */
    @RequestMapping("/toFirstTrial")
    public String toFirstTrial(Map<String, Object> map) throws Exception {

        return "/bank/firstTrial";

    }

    /**
     * 跳转到复审界面
     */
    @RequestMapping("/toReCheck")
    public String toReCheck(Map<String, Object> map) throws Exception {

        return "/bank/recheck";
    }

    /**
     * 跳转到消费者中心
     * @return
     */
    @RequestMapping("/toConsumerCenter")
    public String toConsumerCenter(Map<String, Object> map) {
        return "/bank/consumer";
    }

    /**
     * 跳转到消费者中心
     * @return
     */
    @RequestMapping("/toCouponCenter")
    public String toCouponCenter(Map<String, Object> map) {
        return "/bank/coupon";
    }
}
