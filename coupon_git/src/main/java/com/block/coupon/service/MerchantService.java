package com.block.coupon.service;

import com.block.coupon.po.*;

import java.util.List;
import java.util.Map;

public interface MerchantService {

	//	结算券申请
	String insertSettlementOperation(SettlementOperationCustom settlementOperationCustom)throws Exception;

	//	查询商户结算券余额
	Integer querySettlementBalance(String merchantId)throws Exception;

	//	商户注册
	String insertMerchant(MerchantRegisterCustom merchantRegisterCustom)throws Exception;

    //	商户登录
	Map<String, String> login(String account, String password)throws Exception;

	//	添加优惠券发行规则
	String insertCouponRuler(CouponRulerCustom couponRulerCustom)throws Exception;

	//添加bill账单
	String insertBill(BillInfo billInfo)throws Exception;

	//	商户查询优惠券状态
	List<CouponInfo> queryCouponStatus(QueryCouponStatus queryCouponStatus)throws Exception;

	List<BillInfo> queryMerchantBill(QueryMerchantBill queryMerchantBill)throws Exception;
	//	查询已使用和未使用优惠券总额
	CouponCountInfo queryTotalCoupons(String id)throws Exception;

	Integer queryCouponsUnsed(String id)throws Exception;

	//查询账户余额

	Integer queryAccountBalance(String id)throws Exception;
	//	查询优惠券申请信息
	List<CouponApplicationCustom> queryCouponApplication(String id)throws Exception;

	//	计算消费者某笔消费获取的优惠券额度
	Map<String, Integer> getCouponValue(String merchantId, String consumptionValue)throws Exception;

	//	优惠券申请处理
	String dealCouponApplicaiton(CouponApplicationCustom cac)throws Exception;

	//	 查询优惠券支付申请
	List<CouponPayApplicationCustom> queryCouponPayApp(String id)throws Exception;

	//	 优惠券支付支付确认
	//	 原来传入两个参数，第一个参数为id，用于设置所有涉及的coupon的ownerId
	String dealCouponPayApp(String id, String applicationCode)throws Exception;

	//	终止优惠券发行
	String terminateIssue(String merchant_id);

	//	优惠券流水记录
	List<SettlementRecord> querySettlementRecord(QuerySettlementCustom qsc) throws Exception;

	// 查看商户账号已绑定的银行账户
	String selectBankAccount(String merchantNumber)throws Exception;

	//	商户修改密码
	String modifyPassword(ModifyPassVo mpv) throws Exception;

	//	查询当前优惠券发行规则
	CouponRuler queryCouponRulerByMerchantId(String merchantId) throws Exception;


	// encapsulate terminate issue controller
	TerminateIssueEncapsulation terminateIssueDataEncapsulate(String merchantId) throws Exception;

	//查询商户基本信息
	MerchantAccountSetting queryMerchantInfo(String merchantId) throws Exception;
	//查询商户信息
	Merchant queryMerchantInfoByAccount(String account) throws Exception;
	//重交商户简介
	String insertMerchantIntro(MerchantSeting merchantSeting)throws Exception;

	//	查询距离最近的三个用户
	List<NearbyMerchantItem> fetchNearby(Location location) throws Exception;
	List<NearbyMerchantItem> calculateNearbyThree(List<NearbyMerchantDist> merchantDists, Location location) throws Exception;

    String queryMerchantNameById(String merchantId);
}
