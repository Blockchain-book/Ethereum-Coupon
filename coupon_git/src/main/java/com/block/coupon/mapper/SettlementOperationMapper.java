package com.block.coupon.mapper;

import com.block.coupon.po.SettlementApplication;
import com.block.coupon.po.SettlementOperationCustom;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SettlementOperationMapper {
	//	结算券申请初审列表查询
	List<SettlementApplication> querySFCList()throws Exception;

	//	结算券申请复审列表查询
	List<SettlementApplication> querySSCList()throws Exception;


	//	结算券提现初审列表查询
	List<SettlementApplication> querySWDFList()throws Exception;

	//	结算券提现复审列表查询
	List<SettlementApplication> querySWDSList()throws Exception;

	//	更新结算券申请初审结果
	void updateSettlementFirstCheck(SettlementOperationCustom settlementOperationCustom)throws Exception;

	//	更新结算券申复审结果
	void updateSettlementSecondCheck(SettlementOperationCustom settlementOperationCustom)throws Exception;

	//	结算券操作
	void insertSettlementOperation(SettlementOperationCustom settlementOperationCustom)throws Exception;

	SettlementOperationCustom querySettlomentByMerchantId(@Param("merchantId") String merchantId);

    SettlementOperationCustom querySettlementById(String id);
}
