package com.block.coupon.mapper;

import com.block.coupon.po.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MerchantMapper {

//	查询商户结算券余额
	Integer querySettlementBalance(String merchantId)throws Exception;
	
//	更新商户结算券余额
	void updateSettlementBalance(MerchantCustom merchantCustom)throws Exception;
	
//	商户注册
	void insertRegisterApp(MerchantRegisterCustom merchantRegisterCustom)throws Exception;
	
//	根据商户用户名查询商户信息
	MerchantCustom queryMerchantInfoByAccount(String account)throws Exception;
	
//	根据商户用户名查询商户信息
	MerchantRegisterCustom queryMerchantRegInfoByAccount(String account)throws Exception;
	
//	更新商户的token
	void updateToken(MerchantCustom merchantCustom)throws Exception;

	Integer queryAccountBalance(String id) throws Exception;

	//	查询商户最近的发行规则id
	String queryConCurrentCouponRulerId(String merchantId)throws Exception;
	
//	更新商户的当前发行规则id
	void updateConCurrentCouponId(CouponRulerCustom couponRulerCustom)throws Exception;
	
//	查询注册信息尚未审核的商户
	List<MerchantRegisterCustom> queryUncheckMerchant()throws Exception;

//  添加账单
	void insertBill(BillInfo billInfo)throws Exception;

//	查询商户的公钥和合约地址
	MerchantCustom queryPKAndConAddr(String id)throws Exception;

//	查询结算券流水
	List<SettlementRecord> querySettlementRecord(QuerySettlementCustom qsc) throws Exception;

//	商户修改密码
	void modifyPassword(ModifyPassVo mpv) throws Exception;

	//查询bill
	List<BillInfo> queryMerchantBill(QueryMerchantBill queryMerchantBill)throws Exception;

	//  查看商户账号已绑定的银行账户
	String selectBankAccount(String merchantNumber)throws Exception;

	void updateStaus(MerchantStatus ms) throws Exception;

	MerchantRegisterCustom queryMerchantRegisterById(@Param("id") String id) throws Exception;

	MerchantAccountSetting queryMerchantInfoById(String id) throws Exception;
	//  重新提交商户简介
	void insertMerchantIntro(MerchantSeting merchantSeting)throws Exception;

	List<NearbyMerchantDist> queryMerchantDistItems() throws Exception;

	void updateAddrPk(MerchantCustom mc) throws Exception;

	String queryNameByAddr(String merchantAddress) throws Exception;

	String queryMerchantNameById(String merchantId) throws Exception;
}
