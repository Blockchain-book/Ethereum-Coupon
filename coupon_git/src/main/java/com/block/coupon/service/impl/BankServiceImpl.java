package com.block.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.block.coupon.mapper.*;
import com.block.coupon.po.*;
import com.block.coupon.rpc.Web3;
import com.block.coupon.service.BankService;
import com.block.coupon.service.ConsumerService;
import com.block.coupon.sha3.Sha3;
import com.block.coupon.util.DateUtil;
import com.block.coupon.util.Encryption;
import com.block.coupon.util.ReadAccount;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class BankServiceImpl implements BankService {

	@Autowired
	private SettlementOperationMapper settlementOperationMapper;

	@Autowired
	private MerchantMapper merchantMapper;

	@Autowired
	private BankMapper bankMapper;

	@Autowired
	private ConsumerMapper consumerMapper;

	@Autowired
	private ConsumerService consumerService;

	@Autowired
	private CouponMapper couponMapper;

//	查询结算券申请列表
	public  List<SettlementApplication> querySettlementApplicationList(String id) throws Exception {
		// TODO Auto-generated method stub
		BankStaffCustom bankStaffCustom=bankMapper.queryBankStaffById(id);
		if(bankStaffCustom!=null){
			if(bankStaffCustom.getPosition().equals("1")) {
				return settlementOperationMapper.querySFCList();
			}else{
				return settlementOperationMapper.querySSCList();
			}
		} else{
			return null;
		}
	}

//	查询结算券提现列表
	public List<SettlementApplication> querySettlementWdList(String id) throws Exception {
		// TODO Auto-generated method stub
		BankStaffCustom bankStaffCustom=bankMapper.queryBankStaffById(id);
		if(bankStaffCustom!=null){
			if(bankStaffCustom.getPosition().equals("1")) {
//				返回提现初审列表
				return settlementOperationMapper.querySWDFList();
			} else {
//				返回提现复审列表
				return settlementOperationMapper.querySWDSList();
			}
		}
		return null;
	}

/**
 * 结算券初审结果更新
 * 因为结算券申请初审和结算券提现初审执行的操作是一样的
 * 所以，这里就统一使用了一个方法
 */
	public String updateSettlementFirstCheck(SettlementOperationCustom settlementOperationCustom) throws Exception {
		// TODO Auto-generated method stub
		settlementOperationCustom.setOperationTime(DateUtil.getCurrentTime());
		try {
			settlementOperationMapper.updateSettlementFirstCheck(settlementOperationCustom);
			return "1";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "0";
		}
	}

/**
 * 结算券复审之后，商户表中的结算券余额需要同时更新，
 * isAdd为true,则执行的操作是结算券申请，商户的结算券余额增加
 *  isAdd为false，则执行的操作是结算券提现，商户的结算券余额减少
 */
	public String updateSettlementSecondCheck(SettlementOperationCustom soc,boolean isAdd, String status) throws Exception {
		// TODO Auto-generated method stub
		soc.setRecheckTime(DateUtil.getCurrentTime());
		try {
			settlementOperationMapper.updateSettlementSecondCheck(soc);
			if(status.equals("pass_apply_second") || status.equals("pass_withdraw_second")) {
				MerchantCustom secMc = merchantMapper.queryPKAndConAddr(soc.getMerchantId());
				//===郭威更新，从区块链上读取结算券余额
				String settlementResult = Web3.getValueMap("Merchant.getSettlementBalance", null, secMc.getPublicKey(), secMc.getContractAddress());
				Integer settlementBalance =
						Integer.parseInt(Web3.decodeReturnValue("uint", settlementResult).get(0));
				//===更新结束
				MerchantCustom mc = new MerchantCustom();
				mc.setId(soc.getMerchantId());
				if (isAdd) {
					mc.setSettlementBalance(settlementBalance + soc.getOperationAmount());
				} else {
					mc.setSettlementBalance(settlementBalance - soc.getOperationAmount());
				}
//				更新商户结算券余额
				merchantMapper.updateSettlementBalance(mc);

				//===郭威更新，从区块链上读取数据，同意优惠券申请
				if (isAdd) {
					String funcName = "Bank.approve";
					String[] content = {"1234567", secMc.getContractAddress(), soc.getOperationAmount() + ""};
					String txHash = Web3.sendTransactionMap(funcName, content, ReadAccount.getAccount("bankAccount"), ReadAccount.getAccount("bankContractAddress"));
					String finish = "null";
					String[] content1 = {txHash};
					while (finish.equals("null")) {
						finish = Web3.universalCall("eth_getTransactionReceipt", null, content1, null);
					}
				} else {
					String funcName = "Bank.approveWithdraw";
					String[] content = {secMc.getContractAddress()};
					String txHash = Web3.sendTransactionMap(funcName, content, ReadAccount.getAccount("bankAccount"), ReadAccount.getAccount("bankContractAddress"));
					String finish = "null";
					String[] content1 = {txHash};
					while (finish.equals("null")) {
						finish = Web3.universalCall("eth_getTransactionReceipt", null, content1, null);
					}
				}
			}
			//===更新结束
			return "1";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "0";
		}
	}



//	查询注册信息尚未审核的商户信息
	public List<MerchantRegisterCustom> queryUncheckMerchant() throws Exception {
		// TODO Auto-generated method stub
		return merchantMapper.queryUncheckMerchant();
	}

//	员工注册
	public String insertBankStaff(BankStaffCustom bankStaffCustom) throws Exception {
		// TODO Auto-generated method stub
		try {
			BankStaffCustom bsc=bankMapper.queryBankStaffByAccount(bankStaffCustom.getAccount());

			if(bsc!=null){
				System.out.println("用户已经存在");
				return "0";//用户已经存在
			}
			bankStaffCustom.setId(UUID.randomUUID().toString());
			bankStaffCustom.setSalt(Encryption.createSalt());
			bankStaffCustom.setPassword(Sha3.sha3(bankStaffCustom.getPassword()+bankStaffCustom.getSalt()));
			bankMapper.insertBankStaff(bankStaffCustom);
			return "1";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("注册错误");
			return "0";
		}
	}

    /**
     * 员工登录
     * @param account
     * @param password
     * @return  0：不存在此用户   1：密码错误  2：验证成功
     */
    public Map<String, String> login(String account, String password) throws Exception {
        BankStaffCustom bsc= this.bankMapper.queryBankStaffByAccount(account);
        System.out.println(JSON.toJSON(bsc));
        Map<String, String> m = new HashMap();
        if(bsc==null){
            m.put("resultCode", "0");       // 不存在此用户
            return m;
        }else{
            // 验证成功
            if((Sha3.sha3(password+bsc.getSalt())).equals(bsc.getPassword())){
                m.put("resultCode", "2");
                m.put("position", bsc.getPosition());
                m.put("id", bsc.getId());
                return m;
            }else{
                m.put("resultCode", "1");   // 密码错误
                return m;
            }
        }
    }

//	冻结异常账户
	public Map<String, String> freezeExceptionAccount(String account) {
		Map<String,String> m = new HashMap<>();
		try{
			bankMapper.freezeExceptionAccount(account);
			//郭威更新，调用区块链方法冻结账户
			String funcName = "Consumer.freezeConsumer";
			String consumerConAddr = consumerMapper.queryConsumerConAddr(consumerMapper.queryConsumerId(account));
			String bankAccount = ReadAccount.getAccount("bankAccount");
			String txHash = Web3.sendTransactionMap(funcName,null,bankAccount,consumerConAddr);
			String[] content = {txHash};
			String finish = "null";
			while(finish.equals("null")){
				finish = Web3.universalCall("eth_getTransactionReceipt",null,content,null);
			}
			//更新结束
			m.put("resultCode","1");
		}catch (Exception e){
			e.printStackTrace();
			m.put("resultCode","0");
		}
		return m;
	}

//	解冻异常账户
	@Override
	public Map<String, String> thawExceptionAccount(String account) {
		Map<String,String> m = new HashMap<>();
		try{
			bankMapper.thawExceptionAccount(account);
			//郭威更新，调用区块链方法冻结账户
			String funcName = "Consumer.thawConsumer";
			String consumerConAddr = consumerMapper.queryConsumerConAddr(consumerMapper.queryConsumerId(account));
			String bankAccount = ReadAccount.getAccount("bankAccount");
			String txHash = Web3.sendTransactionMap(funcName,null,bankAccount,consumerConAddr);
			String[] content = {txHash};
			String finish = "null";
			while(finish.equals("null")){
				finish = Web3.universalCall("eth_getTransactionReceipt",null,content,null);
			}
			//更新结束
			m.put("resultCode","1");
		}catch (Exception e){
			e.printStackTrace();
			m.put("resultCode","0");
		}
		return m;
	}

	// 通过账户名查询银行员工
	@Override
	public BankStaffCustom queryBankStaffByAccount(String account) throws Exception {
		return this.bankMapper.queryBankStaffByAccount(account);
	}

    /**
     * 查询结算券申请初审列表
     * @return
     */
    @Override
    public List<SettlementApplication> querySFCList() throws Exception {
        return this.settlementOperationMapper.querySFCList();
    }

    /**
     * 查询结算券申请复审列表
     * @return
     */
    @Override
    public List<SettlementApplication> querySSCList() throws Exception {
        return this.settlementOperationMapper.querySSCList();
    }

    /**
     * 结算券提现初审列表
     * @return
     */
    @Override
    public List<SettlementApplication> querySWDFList() throws Exception {
        return this.settlementOperationMapper.querySWDFList();
    }

    /**
     * 结算券提现复审列表
     * @return
     */
    @Override
    public List<SettlementApplication> querySWDSList() throws Exception {
        return this.settlementOperationMapper.querySWDSList();
    }

    /**
     * 查询已注册还未审核的商户
     * @return
     */
    @Override
    public List<MerchantRegisterCustom> queryUncheckedMerchant() throws Exception {
        return this.merchantMapper.queryUncheckMerchant();
    }

	@Override
	public void updateStatus(String status, String id) throws Exception {
		MerchantCustom mc = new MerchantCustom();
		mc.setId(id);
		//===郭威更新，创建商户
		String bankAccount=ReadAccount.getAccount("bankAccount");
		String bankContractAddress=ReadAccount.getAccount("bankContractAddress");
		String merchantAccount=ReadAccount.getAccount("merchantAccount2");
		mc.setPublicKey(merchantAccount);

		String functionName = "Bank.createMerchant";
		String[] content = {merchantAccount};
		String txHash = Web3.sendTransactionMap(functionName, content, bankAccount, bankContractAddress);
		String[] content1 = {txHash};
		String jsonResult = "null";
		while(jsonResult.equals("null")) {
			jsonResult = Web3.universalCall("eth_getTransactionReceipt", null, content1, null);
		}

		String[] param1 = {merchantAccount};
		String resultAddress = Web3.getValueMap("Bank.getCorrespondingMerchant",param1, bankAccount, bankContractAddress);
		String merchantContractAddress = Web3.decodeReturnValue("address", resultAddress).get(0);
		mc.setContractAddress(merchantContractAddress);
		//===更新结束
		this.merchantMapper.updateAddrPk(mc);
		MerchantStatus ms = new MerchantStatus(id,status);
		this.merchantMapper.updateStatus(ms);
	}


	@Override
	public Map<String, Object> merchantCheck(String id, String status, String operationOpinion, String bankStaffId) throws Exception {
		Map<String, Object> map = new HashMap<>();
    	SettlementOperationCustom settlementOperationCustom = this.settlementOperationMapper.querySettlementById(id);
		System.out.println(settlementOperationCustom);
		List<SettlementApplication> list_settlement = new ArrayList<>();
    	// 根据id查询银行员工是初审还是复审
    	String bankStaffType = this.bankMapper.queryBankStaffById(bankStaffId).getPosition();
    	if(bankStaffType.equals("1")) {
			// 初审
			if(
					status.equals("pass_apply_first") || status.equals("refuse_apply_first") ||
					status.equals("pass_withdraw_first") || status.equals("refuse_withdraw_first")) {
				if (status.equals("pass_apply_first") || status.equals("pass_withdraw_first"))
					settlementOperationCustom.setCheckStatus("2");
				else if (status.equals("refuse_apply_first") || status.equals("refuse_withdraw_first"))
					settlementOperationCustom.setCheckStatus("0");
				settlementOperationCustom.setBankStaffId(bankStaffId);
				settlementOperationCustom.setFirstEncryptStr("FirstEncryptStr");
				settlementOperationCustom.setOperationTime(DateUtil.getCurrentTime());
				settlementOperationCustom.setOperatorOpinion(operationOpinion);
				updateSettlementFirstCheck(settlementOperationCustom);
				// 获取新的list
				if(status.equals("pass_apply_first") || status.equals("refuse_apply_first"))
					// 申请初审列表
					list_settlement = this.settlementOperationMapper.querySFCList();
				else if(status.equals("pass_withdraw_first") || status.equals("refuse_withdraw_first"))
					// 提现初审列表
					list_settlement = this.settlementOperationMapper.querySWDFList();
			}
		}else if(bankStaffType.equals("2")) {
    		// 复审
			if (
					status.equals("pass_apply_second") || status.equals("refuse_apply_second") ||
					status.equals("pass_withdraw_second") || status.equals("refuse_withdraw_second")) {
				if (status.equals("pass_apply_second") || status.equals("pass_withdraw_second"))
					settlementOperationCustom.setRecheckStatus("2");
				else if (status.equals("refuse_apply_second") || status.equals("refuse_withdraw_second"))
					settlementOperationCustom.setRecheckStatus("0");
				settlementOperationCustom.setRecheckId(bankStaffId);
				settlementOperationCustom.setSecondEncryptStr("secondEncryptStr");
				settlementOperationCustom.setRecheckTime(DateUtil.getCurrentTime());
				settlementOperationCustom.setRecheckOpinion(operationOpinion);
				if(status.equals("pass_apply_second") || status.equals("refuse_apply_second")) {
					updateSettlementSecondCheck(settlementOperationCustom, true, status);
					list_settlement = this.settlementOperationMapper.querySSCList();
				}else if(settlementOperationCustom.getOperationType().equals("2")){
					updateSettlementSecondCheck(settlementOperationCustom, false, status);
					list_settlement = this.settlementOperationMapper.querySWDSList();
				}
			}
		}
		map.put("list", list_settlement);
		return map;
	}

	public List<ConsumerInfo> queryConsumerList() throws Exception{
    	List<ConsumerInfo> consumerInfoList = consumerMapper.queryAllConsumerInfo();
    	if(consumerInfoList != null && consumerInfoList.size()!=0){
    		for(ConsumerInfo c:consumerInfoList){
    			Integer totalOwn = consumerService.CalculateCouponSummary(c.getConsumerId()).getTotalValue();
    			if(totalOwn == null){
					c.setTotalOwn(0);
				}else {
					c.setTotalOwn(totalOwn);
				}
				Integer totalConsume = couponMapper.queryTotalConsumeCoupon(c.getConsumerId());
				if( totalConsume == null){
					c.setTotalConsume(0);
				}else{
					c.setTotalConsume(totalConsume);
				}
				Integer totalSendOut = couponMapper.queryTotalTransferCoupon(c.getConsumerId());
				if(totalSendOut == null) {
					c.setTotalSendOut(0);
				}else{
					c.setTotalSendOut(totalSendOut);
				}
			}
		}
    	return consumerInfoList;
	}

	@Override
	public List<ConsumerInfo> queryAbnormalList() throws Exception {
		List<ConsumerInfo> abnormalConsumerInfoList = consumerMapper.queryAbnormalConsumerInfo();
		if(abnormalConsumerInfoList != null && abnormalConsumerInfoList.size()!=0){
			for(ConsumerInfo c:abnormalConsumerInfoList){
				Integer totalOwn = consumerService.CalculateCouponSummary(c.getConsumerId()).getTotalValue();
				if(totalOwn == null){
					c.setTotalOwn(0);
				}else {
					c.setTotalOwn(totalOwn);
				}
				Integer totalConsume = couponMapper.queryTotalConsumeCoupon(c.getConsumerId());
				if( totalConsume == null){
					c.setTotalConsume(0);
				}else{
					c.setTotalConsume(totalConsume);
				}
				Integer totalSendOut = couponMapper.queryTotalTransferCoupon(c.getConsumerId());
				if(totalSendOut == null) {
					c.setTotalSendOut(0);
				}else{
					c.setTotalSendOut(totalSendOut);
				}
			}
		}
		return abnormalConsumerInfoList;
	}

	@Override
	public String markAbnormal(String consumerId){
    	try {
			consumerMapper.markAbnormal(consumerId);
			return "1";
		}catch(Exception e){
    		e.printStackTrace();
    		return "0";
		}
	}

	@Override
	public String markNormal(String consumerId) throws Exception {
		try {
			consumerMapper.markNormal(consumerId);
			return "1";
		}catch(Exception e){
			e.printStackTrace();
			return "0";
		}
	}

	@Override
	public List<ConsumerInfo> queryFrozenConsumer() {
		try {
			List<ConsumerInfo> frozenConsumer = consumerMapper.queryFrozenConsumerInfo();
			if(frozenConsumer != null && frozenConsumer.size()!=0){
				for(ConsumerInfo c:frozenConsumer){
    			Integer totalOwn = consumerService.CalculateCouponSummary(c.getConsumerId()).getTotalValue();
    			c.setTotalOwn(totalOwn);
					Integer totalConsume = couponMapper.queryTotalConsumeCoupon(c.getConsumerId());
					if( totalConsume == null){
						c.setTotalConsume(0);
					}else{
						c.setTotalConsume(totalConsume);
					}
					Integer totalSendOut = couponMapper.queryTotalTransferCoupon(c.getConsumerId());
					if(totalSendOut == null) {
						c.setTotalSendOut(0);
					}else{
						c.setTotalSendOut(totalSendOut);
					}
				}
			}
			return frozenConsumer;
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public  Map<String,List<CouponMes>> queryAllCouponMes() throws Exception{
		List<CouponMes> mesList = new ArrayList<>();
		mesList = bankMapper.queryAllCouponMes();
		if(mesList.size()!=0){
			for(CouponMes cme:mesList){
				switch(cme.getStatus()){
					case "0"://刚发放:
						break;
					case "2"://未使用
						cme.setOwnerAccount(consumerMapper.queryAccountById(cme.getOwnerId()));
						break;
					case "3"://未使用，等待审批
						cme.setOwnerAccount(consumerMapper.queryAccountById(cme.getOwnerId()));
						break;
					case "1"://已使用
						cme.setOwnerAccount(consumerMapper.queryAccountById(cme.getOwnerId()));
						CouponConsumeInfo cci = bankMapper.queryConsumeMerchant(cme.getId());
						cme.setConsumeMerchant(cci.getConsumeMerchant());
						cme.setConsumptionDate(cci.getConsumeTime());
						break;
				}
			}
		}

		Map<String,List<CouponMes>> map = new HashMap<>();
		map.put("couponMes",mesList);
		return map;
	}

	@Override
	public List<ConsumerInfo> searchConsumer(String account, String status) throws Exception {
		List<ConsumerInfo> searchResult = consumerMapper.querySearchResult(account);
		if(searchResult != null && searchResult.size() != 0){
			switch (status) {
				case "1":
					if(!searchResult.get(0).getMark().equals("1") ||
							!searchResult.get(0).getIsFrozen().equals("0")){
						searchResult.remove(0);
					}					break;
				case "2":
					if(!searchResult.get(0).getMark().equals("0") ||
							!searchResult.get(0).getIsFrozen().equals("0")){
						searchResult.remove(0);
					}
					break;
				case "3":
					if(!searchResult.get(0).getIsFrozen().equals("1")){
						searchResult.remove(0);
					}
					break;
			}
			if(searchResult.size()!=0){
				ConsumerInfo c = searchResult.get(0);
				Integer totalOwn = consumerService.CalculateCouponSummary(c.getConsumerId()).getTotalValue();
				c.setTotalOwn(totalOwn);
				Integer totalConsume = couponMapper.queryTotalConsumeCoupon(c.getConsumerId());
				if( totalConsume == null){
					c.setTotalConsume(0);
				}else{
					c.setTotalConsume(totalConsume);
				}
				Integer totalSendOut = couponMapper.queryTotalTransferCoupon(c.getConsumerId());
				if(totalSendOut == null) {
					c.setTotalSendOut(0);
				}else{
					c.setTotalSendOut(totalSendOut);
				}
				searchResult.remove(0);
				searchResult.add(c);
			}
		}
		return searchResult;
	}

	@Override
	public Map<String, List<CouponMes>> searchCoupon(String merchantName) throws Exception {
		List<CouponMes> list1 = new ArrayList<>();
		if(merchantName.equals("") || merchantName==null) {
			List<CouponMes> list = queryAllCouponMes().get("couponMes");
			if (list != null && list.size() != 0) {
				for (CouponMes cme : list) {
					if (merchantName.equals(cme.getName()) || merchantName.equals(cme.getConsumeMerchant())) {
						list1.add(cme);
					}
				}
			}
		}
		Map<String, List<CouponMes>> result = new HashMap<String, List<CouponMes>>();
		result.put("list",list1);
		return result;
	}

	@Override
	public void updateOnline(BankStaffCustom bsc) throws Exception {
		bankMapper.updateOnline(bsc);
	}
}