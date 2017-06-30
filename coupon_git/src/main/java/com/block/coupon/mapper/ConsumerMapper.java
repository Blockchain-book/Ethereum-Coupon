package com.block.coupon.mapper;

import java.util.List;

import com.block.coupon.po.ConsumerCoupon;
import com.block.coupon.po.ConsumerCustom;
import com.block.coupon.po.ConsumerInfo;
import com.block.coupon.po.ModifyPassVo;

import java.util.List;

public interface ConsumerMapper {

//	消费者注册
	void insertConsumer(ConsumerCustom consumerCustom)throws Exception;
	
//	获取消费者合约地址
	String queryConsumerConAddr(String id)throws Exception;

	ConsumerCustom queryConsumerInfoByAccount(String account);

	void updateToken(ConsumerCustom mc);

	List<ConsumerCoupon> consumerQueryUnusedCoupons(String consumerId);

	String queryConsumerId(String account);

	String queryConsumerPK(String consumerId);

	List<ConsumerCoupon> queryConsumerCouponByAddress(String conAddr);

//	消费者修改密码
	void modifyPassword(ModifyPassVo mpv) throws Exception;

	List<ConsumerInfo> queryAllConsumerInfo() throws Exception;

	List<ConsumerInfo> queryAbnormalConsumerInfo() throws Exception;

	void markAbnormal(String consumerId) throws Exception;

	void markNormal(String consumerId) throws Exception;

	List<ConsumerInfo> queryFrozenConsumerInfo() throws Exception;

	List<ConsumerInfo> querySearchResult(String account) throws Exception;

	String queryAccountById(String id) throws Exception;
}
