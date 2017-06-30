package com.block.coupon.mapper;

import java.util.List;

import com.block.coupon.po.*;

public interface CouponMapper {
	
//	添加优惠券发行规则
	void insertCouponRuler(CouponRulerCustom couponRulerCustom)throws Exception;
	
//	根据优惠券发行规则id，查询规则详细信息
	CouponRulerCustom queryCouponRulerInfo(String id)throws Exception;
	
//	插入优惠券
	void insertCoupon(CouponCustom couponCustom)throws Exception;
	
//	商户查询优惠券状态
	List<CouponInfo> queryCouponStatus(QueryCouponStatus queryCouponStatus)throws Exception;

//	查询优惠券申请信息
	List<CouponApplicationCustom> queryCouponApplication(String id)throws Exception;
	
//	更新优惠券申请状态
	void updateCouponAppStatus(CouponApplicationCustom cac)throws Exception;
	
	
//	更新优惠券信息
	 void updateCouponInfo(CouponCustom couponCustom)throws Exception;
	 
//	 查询优惠券支付申请
	 List<CouponPayApplicationCustom> queryCouponPayApp(String id)throws Exception;
	 
//	 查询商户尚未发放的优惠券
	List<CouponCustom> queryUnIssuedCoupons(String id)throws Exception;
	
//	更新优惠券支付申请状态
	void updateCouponPayApp(String applicationCode)throws Exception;
	
//	根据优惠券支付申请码查询消费者id和优惠券id
	List<CouponPayApplicationCustom> queryIdByAppCode(String applicationCode)throws Exception;
	
//	根据优惠券id查询优惠券合约地址
	String queryConponConAddr(String id)throws Exception;
	
//	插入优惠券申请
	void insertCouponApplication(CouponApplicationCustom couponApplicationCustom)throws Exception;
	
//	添加优惠券支付申请
	 void insertCouponPayApp(CouponPayApplicationCustom cpac)throws Exception;
	 
//	 将优惠券的状态改为申请使用中
	 void updateCouponInUse(String id)throws Exception;

	//	商户终止发行时，删除所有未发行的优惠券(guowei)
	void updateUnIssuedCoupon(String id);

	//	查询未发行的优惠券的总额(guowei)
	int queryTotalUnIssuedCouponValue(String couponRulerId);

	//	修改优惠券规则中的优惠券总额(guowei)
	void updateCouponRulerInfo(CouponRuler cr);

	//	通过MerchantId查询优惠券发行规则
	CouponRuler queryCouponRulerByMerchantId(String merchantId);

	//	转赠优惠券
	void presentCoupon(UpdateCouponOwner updateCouponOwner);

	//修改支付后优惠券状态
	void updatePayCouponStatus(String couponId);

//	根据消费者合约地址查询Id
	String queryCouponIdByAddr(String contractAddress);

//	插入优惠券转赠记录
	void insertCouponMigration(CouponMigration cm);

//	查询消费者的所有已支付的优惠券总额
	Integer queryTotalConsumeCoupon(String consumerId);

	//	查询消费者的所有已支付的优惠券总额
	Integer queryTotalTransferCoupon(String consumerId);

	String queryStateByConAddr(String couponAddress);
}
