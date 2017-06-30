package com.block.coupon.service.impl;

import java.text.ParseException;
import java.util.*;

import com.block.coupon.mapper.MerchantMapper;
import com.block.coupon.po.*;
import net.sf.json.JSONObject;

import org.apache.tools.ant.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.block.coupon.mapper.ConsumerMapper;
import com.block.coupon.mapper.CouponMapper;
import com.block.coupon.po.*;
import com.block.coupon.rpc.Web3;
import com.block.coupon.service.ConsumerService;
import com.block.coupon.sha3.Sha3;
import com.block.coupon.util.DateUtil;
import com.block.coupon.util.Encryption;
import com.block.coupon.util.ReadAccount;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConsumerServiceImpl implements ConsumerService{
	
	@Autowired
	private ConsumerMapper consumerMapper;
	
	@Autowired
	private CouponMapper couponMapper;

	@Autowired
	private MerchantMapper merchantMapper;

//	注册消费者
	public String consumerRegister(ConsumerCustom consumerCustom) throws Exception {
		// TODO Auto-generated method stub
		try {
			ConsumerCustom cc=consumerMapper.queryConsumerInfoByAccount(consumerCustom.getAccount());
			if(cc!=null){
				return "0";
			}
			consumerCustom.setId(UUID.randomUUID().toString());
			consumerCustom.setSalt(Encryption.createSalt());
			consumerCustom.setMark("1");
			consumerCustom.setPassword(Sha3.sha3(consumerCustom.getPassword()+consumerCustom.getSalt()));

			String consumerAccount=ReadAccount.getAccount("consumerAccount2");
			consumerCustom.setPublicKey(consumerAccount);
			//===郭威更新，创建消费者合约
			String functionName = "eth_compileSolidity";
			Boolean flag = null;
			String object = null;
			String contractCode = ReadAccount.getAccount("code");
			String[] content={contractCode};
			String result=Web3.universalCall(functionName, object, content, flag);
			JSONObject js=JSONObject.fromObject(result);
			JSONObject multi=(JSONObject)js.get("<stdin>:Consumer");
			String code=multi.getString("code");
			String functionName2 = "eth_sendTransaction";
			Boolean flag2 = null;
			String object2 = "{\"from\": \""+consumerAccount+"\", \"gas\": \""+"0x470000"+"\", \"data\": \""+code+"\",\"arg0\":\"" + ReadAccount.getAccount("bankAccount") + "\"}";
			String[] content2 = null;
			String txHash=Web3.universalCall(functionName2, object2, content2, flag2);
			String functionName3 = "eth_getTransactionReceipt";
			String[] content3 = {txHash};
			String finish="null";
			while(finish.equals("null")){
				finish = Web3.universalCall(functionName3, null, content3, null);
			}
			JSONObject tsxInfo=JSONObject.fromObject(finish);
			String contractAddress=tsxInfo.getString("contractAddress");
			consumerCustom.setContractAddress(contractAddress);
			consumerMapper.insertConsumer(consumerCustom);
			
			return "1";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "0";
		}
	}

//	登录
	public Map<String, String> consumerLogin(String account,String password) throws Exception {
		ConsumerCustom mc=consumerMapper.queryConsumerInfoByAccount(account);
		Map<String, String> m=new HashMap<String, String>();
		if(mc!=null){
			String encryptionPasswd=Sha3.sha3(password+mc.getSalt());
			if(encryptionPasswd.equals(mc.getPassword())){
				try {
					m.put("resultCode", "1");
					m.put("id", mc.getId());
					String timeStamp=DateUtil.getCurrentTime();
					String token=Sha3.sha3(account+password+timeStamp);
					m.put("token", token);
					mc.setToken(token);
					consumerMapper.updateToken(mc);
					return m;
				} catch (Exception e) {
					// 如果token指令更新失败
					m.put("resultCode", "-1");
					return m;
				}
			}else{
				m.put("resultCode", "0");
				return m;
			}
		}else{
			m.put("resultCode", "0");
			return m;
		}
	}

	public List<ConsumerCoupon> consumerQueryUnusedCoupons(String consumerId) {
		return consumerMapper.consumerQueryUnusedCoupons(consumerId);
	}
     
// 插入优惠券申请
	public String insertCouponApplication(CouponApplicationCustom couponApplicationCustom) throws Exception {
		// TODO Auto-generated method stub
		try {
			couponApplicationCustom.setId(UUID.randomUUID().toString());
			couponApplicationCustom.setConsumptionTime(DateUtil.getCurrentTime());
			couponMapper.insertCouponApplication(couponApplicationCustom);
			return "1";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "0";
		}
	}
	
//	插入优惠券支付申请
	public String insertCouponPayApp(String merchantId, String consumerId, String couponIds, Integer consumeValue) throws Exception {
		// TODO Auto-generated method stub
		try {
			String[] strs=couponIds.split(",");
			String applicationCode=UUID.randomUUID().toString();
			CouponCustom cc=new CouponCustom();
			for(String couponId:strs){
				CouponPayApplicationCustom cpac=new CouponPayApplicationCustom();
				cpac.setId(UUID.randomUUID().toString());
				cpac.setMerchantId(merchantId);
				cpac.setConsumerId(consumerId);
				cpac.setCouponId(couponId);
				cpac.setApplicationTime(DateUtil.getCurrentTime());
				cpac.setApplicationCode(applicationCode);
				cpac.setConsumeValue(consumeValue);
				couponMapper.insertCouponPayApp(cpac);
				couponMapper.updateCouponInUse(couponId);
			}
			return "1";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "0";
		}
	}

	@Override
	public List<CouponDetail> queryAvailableCoupons(AvailableQuery aq) throws Exception {
		List<CouponDetail> items = new ArrayList<CouponDetail>();
		String consumerPK = consumerMapper.queryConsumerPK(aq.getConsumerId());
		String consumerAddr = consumerMapper.queryConsumerConAddr(aq.getConsumerId());
		String curDate = DateUtil.getCurrentDate();
		String addrsResult = Web3.getValueMap("Consumer.getCoupons", null, consumerPK, consumerAddr);
		List<String> addrs = Web3.decodeReturnValue("address[]", addrsResult);
		for (String s : addrs) {
			Integer status = Integer.parseInt(Web3.decodeReturnValue("uint",
					Web3.getValueMap("Coupon.getState", null, consumerPK, s)).get(0));
			if (status == 2 && !couponMapper.queryStateByConAddr(s).equals("3")) {
				Integer reach = Integer.parseInt(Web3.decodeReturnValue("uint",
						Web3.getValueMap("Coupon.getLimit",null,consumerPK,s)).get(0));
				if(reach<=aq.getComsuptionAmount()) {
					CouponDetail cd = new CouponDetail();
					cd.setCouponId(couponMapper.queryCouponIdByAddr(s));
					cd.setValue(Integer.parseInt(Web3.decodeReturnValue("uint",
							Web3.getValueMap("Coupon.getValue", null, consumerPK, s)).get(0)));
					cd.setConsumptionDate(Web3.decodeReturnValue("bytes32",
							Web3.getValueMap("Coupon.getObtainDate", null, consumerPK, s)).get(0));
					cd.setStartDate(Web3.decodeReturnValue("bytes32",
							Web3.getValueMap("Coupon.getStartDate", null, consumerPK, s)).get(0));
					cd.setEndDate(Web3.decodeReturnValue("bytes32",
							Web3.getValueMap("Coupon.getEndDate", null, consumerPK, s)).get(0));
					cd.setMerchantName(merchantMapper.queryNameByAddr(Web3.decodeReturnValue("address",
							Web3.getValueMap("Coupon.getGranter", null, consumerPK, s)).get(0)));
					cd.setConsumptionValue(Integer.parseInt(Web3.decodeReturnValue("uint",
							Web3.getValueMap("Coupon.getObtainValue", null, consumerPK, s)).get(0)));
					items.add(cd);
				}
			}
		}
		return items;
	}


	@Override
	public String presentCoupon(String from, String to, String couponId) {
		//===郭威更新，优惠券转赠
		String[] couponsId = couponId.split(",");
		int length = couponsId.length;
		try {
			String targetId = consumerMapper.queryConsumerId(to);
			String targetConAddr = consumerMapper.queryConsumerConAddr(targetId);
			String senderConAddr = consumerMapper.queryConsumerConAddr(from);
			String senderPK = consumerMapper.queryConsumerPK(from);
			if(targetConAddr != null && senderConAddr != null
					&& senderPK != null) {
				for (String cid : couponsId) {
					String couponConAddr = couponMapper.queryConponConAddr(cid);
					String functionName = "Consumer.transfer";
					String[] content = {targetConAddr, couponConAddr};
					String txHash = Web3.sendTransactionMap(functionName, content, senderPK, senderConAddr);
					String finish = "null";
					String[] content1 = {txHash};
					while (finish.equals("null")) {
						finish = Web3.universalCall("eth_getTransactionReceipt", null, content1, null);
					}
				}
				for (int i = 0; i < length; i++) {
					UpdateCouponOwner updateCouponOwner = new UpdateCouponOwner();
					updateCouponOwner.setCouponsId(couponsId[i]);
					updateCouponOwner.setTargetId(targetId);
					couponMapper.presentCoupon(updateCouponOwner);
					//添加转赠的数据库记录
					CouponMigration cm = new CouponMigration();
					cm.setId(UUID.randomUUID().toString());
					cm.setOperationDate(DateUtil.getCurrentDate());
					cm.setCouponId(couponsId[0]);
					cm.setFromId(from);
					cm.setToId(to);
					couponMapper.insertCouponMigration(cm);
				}

				return "1";
			}else{
				return "-1";
			}
		}catch (Exception e){
			return "0";
		}
		//===更新结束
	}

	@Override
	public List<ConsumerCoupon> queryConsumerCouponByAddress(String consumerId) throws Exception {
		String consumerPK = consumerMapper.queryConsumerConAddr(consumerId);
		String consumerConAddr = consumerMapper.queryConsumerConAddr(consumerId);
		List<ConsumerCoupon> result_list = new ArrayList<ConsumerCoupon>();
		String funNameAllCoupons = "Consumer.getCoupons";
		String conAddrs_json = Web3.getValueMap(funNameAllCoupons, null, consumerPK, consumerConAddr);
		List<String> conAddrs = Web3.decodeReturnValue("address[]", conAddrs_json);
		for(String couponAddr:conAddrs){
			String funNameCouponTemp = "Coupon.getState";
			String stateJson = Web3.getValueMap(funNameCouponTemp, null, consumerPK, couponAddr);
			List<String> state = Web3.decodeReturnValue("uint", stateJson);
			if(state!=null && state.size()!=0 && state.get(0).equals("2")){
				if(!couponMapper.queryStateByConAddr(couponAddr).equals("3")) {
					String getMerchantName = "Coupon.getGranter";
					String merchantConAddr_json = Web3.getValueMap(getMerchantName, null, consumerPK, couponAddr);
					List<String> merchantConAddr = Web3.decodeReturnValue("address", merchantConAddr_json);
					String merchantName = merchantMapper.queryNameByAddr(merchantConAddr.get(0));
					String getObtainValue = "Coupon.getObtainValue";
					String obtainValueJson = Web3.getValueMap(getObtainValue, null, consumerPK, couponAddr);
					List<String> value = Web3.decodeReturnValue("uint", obtainValueJson);
					List<ConsumerCoupon> cc = consumerMapper.queryConsumerCouponByAddress(couponAddr);
					cc.get(0).setMerchantName(merchantName);
					cc.get(0).setObtainValue(Integer.parseInt(value.get(0)));
					result_list.addAll(cc);
				}
			}
		}
		return result_list;
	}
//	消费者修改密码
	@Override
	public String modifyPassword(ModifyPassVo mpv) throws Exception {
		ConsumerCustom cc=consumerMapper.queryConsumerInfoByAccount(mpv.getAccount());
		String salt= cc.getSalt();
		//判断旧密码是否正确
		if (Sha3.sha3(mpv.getOldPassword()+salt).equals(cc.getPassword())){
			//		用sha3加密算法对前端传递过来的代码再次进行加密
			mpv.setNewPassword(Sha3.sha3(mpv.getNewPassword()+salt));
			consumerMapper.modifyPassword(mpv);
			return "1";
		}else {
			return "0";
		}
	}

	@Override
	public CouponSummary CalculateCouponSummary(String consumerId) throws Exception {
		//今天的日期
		String curDate = DateUtil.getCurrentDate();
		//暂存各个种类的总钱数
		Integer outDate = new Integer(0);
		Integer almostOutDate = new Integer(0);
		Integer unused = new Integer(0);
		Integer totalValue = new Integer(0);
		//完全从区块链上查询优惠券的所有信息
		String consumerAddress = consumerMapper.queryConsumerConAddr(consumerId);
		String consumerAccount = consumerMapper.queryConsumerPK(consumerId);
		String result = Web3.getValueMap("Consumer.getCoupons",null,consumerAccount,consumerAddress);
		List<String> addresses = Web3.decodeReturnValue("address[]",result);
		for(String s:addresses){
			String tempValueResult = Web3.getValueMap("Coupon.getValue",null,consumerAccount,s);
			Integer tempValue = Integer.parseInt(Web3.decodeReturnValue("uint",tempValueResult).get(0));
			String tempStateResult = Web3.getValueMap("Coupon.getState",null,consumerAccount,s);
			Integer tempState = Integer.parseInt(Web3.decodeReturnValue("uint",tempStateResult).get(0));
			if(tempState == 2){//在消费者手中，还未消费
				String endDateResult = Web3.getValueMap("Coupon.getEndDate",null,consumerAccount,s);
				String endDate = Web3.decodeReturnValue("bytes32",endDateResult).get(0);
				if(compareDate(endDate,curDate)<=2 && compareDate(endDate,curDate)>=0){
					almostOutDate += tempValue;
				}else if(compareDate(endDate,curDate)<0){
					outDate += tempValue;
				}else{
					unused += tempValue;
				}
				totalValue += tempValue;
			}
		}
		return new CouponSummary(((double)(outDate*100))/((double)totalValue),
				((double)(almostOutDate*100))/((double)totalValue),
				((double)(unused*100))/((double)totalValue),
				totalValue);
	}
	private long compareDate(String s1, String s2) throws ParseException {
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date1 = format.parse(s1);
		java.util.Date date2 = format.parse(s2);
		long day=(date1.getTime()-date2.getTime())/(24*60*60*1000);
		return day;
	}

	@Override
	public Map<String,List<CouponDetail>> queryAllCouponDetails(String consumerId) throws Exception {
		List<CouponDetail> outDate = new ArrayList<CouponDetail>();
		List<CouponDetail> almostOutDate = new ArrayList<CouponDetail>();
		List<CouponDetail> unused = new ArrayList<CouponDetail>();
		Map<String, List<CouponDetail>> result = new HashMap<String, List<CouponDetail>>();
		String consumerPK = consumerMapper.queryConsumerPK(consumerId);
		String consumerAddr = consumerMapper.queryConsumerConAddr(consumerId);
		String curDate = DateUtil.getCurrentDate();
		String addrsResult = Web3.getValueMap("Consumer.getCoupons", null, consumerPK, consumerAddr);
		List<String> addrs = Web3.decodeReturnValue("address[]", addrsResult);
		for (String s : addrs) {
			Integer status = Integer.parseInt(Web3.decodeReturnValue("uint",
					Web3.getValueMap("Coupon.getState", null, consumerPK, s)).get(0));
			if (status == 2) {
				CouponDetail cd = new CouponDetail();
				cd.setCouponId(couponMapper.queryCouponIdByAddr(s));
				cd.setValue(Integer.parseInt(Web3.decodeReturnValue("uint",
						Web3.getValueMap("Coupon.getValue", null, consumerPK, s)).get(0)));
				cd.setConsumptionDate(Web3.decodeReturnValue("bytes32",
						Web3.getValueMap("Coupon.getObtainDate", null, consumerPK, s)).get(0));
				cd.setStartDate(Web3.decodeReturnValue("bytes32",
						Web3.getValueMap("Coupon.getStartDate", null, consumerPK, s)).get(0));
				cd.setEndDate(Web3.decodeReturnValue("bytes32",
						Web3.getValueMap("Coupon.getEndDate", null, consumerPK, s)).get(0));
				cd.setMerchantName(merchantMapper.queryNameByAddr(Web3.decodeReturnValue("address",
						Web3.getValueMap("Coupon.getGranter", null, consumerPK, s)).get(0)));
//				cd.setConsumptionValue(couponMapper.queryConsumptionValueByAddr(s));
				cd.setConsumptionValue(Integer.parseInt(Web3.decodeReturnValue("uint",
						Web3.getValueMap("Coupon.getObtainValue", null, consumerPK, s)).get(0)));
				if (compareDate(cd.getEndDate(), curDate) <= 2 && compareDate(cd.getEndDate(), curDate) >= 0) {
					almostOutDate.add(cd);
				} else if (compareDate(cd.getEndDate(), curDate) < 0) {
					outDate.add(cd);
				} else {
					unused.add(cd);
				}
			}
		}
		result.put("almostOutDate", almostOutDate);
		result.put("outDate", outDate);
		result.put("unused", unused);
		return result;
	}
}
