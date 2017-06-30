package com.block.coupon.controller;

import com.block.coupon.po.*;
import com.block.coupon.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value="/consumer")
public class ConsumerController {

	@Autowired
	private ConsumerService consumerService;

	//	消费者注册
	@RequestMapping(value="/register.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, String> register(@RequestBody ConsumerCustom consumerCustom)throws Exception{
		String resultCode=consumerService.consumerRegister(consumerCustom);
		Map<String, String> m=new HashMap<String, String>();
		m.put("resultCode", resultCode);
		return m;
	}


	//	消费者登录
	@RequestMapping(value="/login.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, String> login(String account, String password)throws Exception{
		return consumerService.consumerLogin(account, password);
	}

	//	消费者优惠券查询
	@RequestMapping(value="/queryUnusedCoupons.action",method=RequestMethod.POST)
	public @ResponseBody List<ConsumerCoupon> queryUnusedCoupons(String id)throws Exception{
		return consumerService.queryConsumerCouponByAddress(id);
	}

	//	插如优惠券申请记录
	@RequestMapping(value="/applyCoupon.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, String> applyCoupon(@RequestBody CouponApplicationCustom couponApplicationCustom)throws Exception{
		String resultCode=consumerService.insertCouponApplication(couponApplicationCustom);
		Map<String, String> m=new HashMap<String, String>();
		m.put("resultCode", resultCode);
		return m;

	}


	//	插如优惠券支付申请记录
	@RequestMapping(value="/applyUseCoupon.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, String> applyUseCoupon(String  merchantId,String consumerId,String couponIds, String consumeValue)throws Exception{
		String resultCode=consumerService.insertCouponPayApp(merchantId, consumerId, couponIds, Integer.parseInt(consumeValue));
		Map<String, String> m=new HashMap<String, String>();
		m.put("resultCode", resultCode);
		return m;
	}

	//	转赠优惠券
	@RequestMapping(value="/presentCoupon.action",method=RequestMethod.POST)
	public @ResponseBody Map<String,String> presentCoupon(String from,String to,String couponsId){
		String resultCode=consumerService.presentCoupon(from,to,couponsId);
		Map<String,String> m = new HashMap();
		m.put("resultCode",resultCode);
		return m;
	}

	//	查询可用的优惠券
	@RequestMapping(value="/queryAvalibleCoupons.action",method=RequestMethod.POST)
	public @ResponseBody List<CouponDetail> queryAvailableCoupons(@RequestBody AvailableQuery aq) throws Exception{
		List<CouponDetail> couponItems = consumerService.queryAvailableCoupons(aq);
		return couponItems;
	}

	//	修改账户密码
	@RequestMapping(value="/modifyPassword.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, String> modifyPassword(@RequestBody ModifyPassVo mpv) throws Exception{
		String resultCode = consumerService.modifyPassword(mpv);
		Map<String, String> result_map  = new HashMap<String, String>();
		result_map.put("resultCode", resultCode);
		return result_map;
	}

//	获取优惠券统计信息
	@RequestMapping(value="/queryCouponStatus.action",method=RequestMethod.POST)
	public @ResponseBody CouponSummary CalculateCouponSummary(String consumerId) throws Exception{
		CouponSummary couponSummary= consumerService.CalculateCouponSummary(consumerId);
		return couponSummary;
	}

//	获取消费者所有优惠券的信息
	@RequestMapping(value="/queryAllCouponDetails.action",method=RequestMethod.POST)
	public @ResponseBody Map<String,List<CouponDetail>> queryAllCouponDetails(String consumerId) throws Exception{
		Map<String,List<CouponDetail>> details = consumerService.queryAllCouponDetails(consumerId);
		return details;
	}
}
