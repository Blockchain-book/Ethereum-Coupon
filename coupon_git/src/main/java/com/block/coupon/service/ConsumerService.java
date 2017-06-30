package com.block.coupon.service;

import com.block.coupon.po.*;

import java.util.List;
import java.util.Map;

public interface ConsumerService {

//	注册消费者
	String consumerRegister(ConsumerCustom consumerCustom)throws Exception;

	Map<String, String> consumerLogin(String account,String password) throws Exception;

//	消费者修改密码
	String modifyPassword(ModifyPassVo mpv) throws Exception;


//	插入优惠券申请
	String insertCouponApplication(CouponApplicationCustom couponApplicationCustom)throws Exception;
	
//	添加优惠券支付申请
	String insertCouponPayApp(String merchantId, String consumerId, String couponIds, Integer consumeValue)throws Exception;

//	查询可用的优惠券
	List<CouponDetail> queryAvailableCoupons(AvailableQuery aq) throws Exception;

//	转赠优惠券
    String presentCoupon(String from, String to, String couponId);

	List<ConsumerCoupon> queryConsumerCouponByAddress(String consumerId) throws Exception;

//	获取消费者所有的优惠券并分类
	CouponSummary CalculateCouponSummary(String consumerId) throws Exception;

//	获取消费者所有优惠券的细节信息
	Map<String,List<CouponDetail>> queryAllCouponDetails(String consumerId) throws Exception;
}
