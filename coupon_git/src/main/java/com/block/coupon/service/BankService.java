package com.block.coupon.service;

import com.block.coupon.po.*;

import java.util.List;
import java.util.Map;

public interface BankService {

//	结算券申请列表查询
	List<SettlementApplication> querySettlementApplicationList(String id)throws Exception;
	
//	结算券提现申请列表查询
	List<SettlementApplication> querySettlementWdList(String id)throws Exception;
	
//	更新结算券初审结果
	String updateSettlementFirstCheck(SettlementOperationCustom settlementOperationCustom)throws Exception;
	
//	更新结算券复审结果
	String updateSettlementSecondCheck(SettlementOperationCustom settlementOperationCustoms,boolean idAdd, String status)throws Exception;
	
//	查询注册信息尚未审核的商户
	List<MerchantRegisterCustom> queryUncheckMerchant()throws Exception;

//	注册员工
	String insertBankStaff(BankStaffCustom bankStaffCustom)throws Exception;
	
//	员工登录
	Map<String, String> login(String account,String password)throws Exception;

//	冻结异常账户
    Map<String,String> freezeExceptionAccount(String account);

//	解冻异常账户
	Map<String,String> thawExceptionAccount(String account);

//  通过账户名查询银行员工
	BankStaffCustom queryBankStaffByAccount(String account) throws Exception;

    List<SettlementApplication> querySFCList() throws Exception;

    List<SettlementApplication> querySSCList() throws Exception;

    List<SettlementApplication> querySWDFList() throws Exception;

    List<SettlementApplication> querySWDSList() throws Exception;

    List<MerchantRegisterCustom> queryUncheckedMerchant() throws Exception;

	// 更新商户的staus更新为“1”
	void updateStaus(String status, String id) throws Exception;

    Map<String, Object> merchantCheck(String id, String status, String operationOpinion, String id1) throws Exception;

	List<ConsumerInfo> queryConsumerList()throws Exception;

	List<ConsumerInfo> queryAbnormalList() throws Exception;

	String markAbnormal(String consumerId) throws Exception;

	String markNormal(String consumerId) throws Exception;

    List<ConsumerInfo> queryFrozenConsumer() throws Exception;

    List<ConsumerInfo> searchConsumer(String account, String status) throws Exception;

	Map<String,List<CouponMes>> queryAllCouponMes() throws Exception;

	Map<String,List<CouponMes>> searchCoupon(String merchantName) throws Exception;

	void updateOnline(BankStaffCustom bsc) throws Exception;
}
