package com.block.coupon.mapper;

import com.block.coupon.po.BankStaffCustom;
import com.block.coupon.po.CouponConsumeInfo;
import com.block.coupon.po.CouponMes;

import java.util.List;

public interface BankMapper {
	
//	根据银行职员id查询其信息
	BankStaffCustom  queryBankStaffById(String id)throws Exception;
	

//	注册员工
	void insertBankStaff(BankStaffCustom bankStaffCustom)throws Exception;
	
//	根据员工账户查询其信息
	BankStaffCustom queryBankStaffByAccount(String account)throws Exception;

//	冻结异常账户
    void freezeExceptionAccount(String account);

//	解冻异常账户
	void thawExceptionAccount(String account);

	List<CouponMes> queryAllCouponMes() throws Exception;

	CouponConsumeInfo queryConsumeMerchant(String couponId)throws Exception;

	void updateOnline(BankStaffCustom bsc) throws Exception;
}
