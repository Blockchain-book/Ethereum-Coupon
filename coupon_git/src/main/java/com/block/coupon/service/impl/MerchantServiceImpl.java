package com.block.coupon.service.impl;

import com.block.coupon.mapper.ConsumerMapper;
import com.block.coupon.mapper.CouponMapper;
import com.block.coupon.mapper.MerchantMapper;
import com.block.coupon.mapper.SettlementOperationMapper;
import com.block.coupon.po.*;
import com.block.coupon.rpc.Web3;
import com.block.coupon.service.MerchantService;
import com.block.coupon.sha3.Sha3;
import com.block.coupon.util.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

public class MerchantServiceImpl implements MerchantService{


	@Autowired
	private MerchantMapper merchantMapper;
	
	@Autowired
	private SettlementOperationMapper settlementOperationMapper;
	
	@Autowired
	private CouponMapper couponMapper;
	
	@Autowired
	private ConsumerMapper consumerMapper;

//	插入结算券申请
	public String insertSettlementOperation(SettlementOperationCustom soc) throws Exception {
		// TODO Auto-generated method stub
		soc.setId(UUID.randomUUID().toString());
		soc.setOperationDate(DateUtil.getCurrentDate());
			
		try {
			settlementOperationMapper.insertSettlementOperation(soc);
			return "1";
		} catch (Exception e) {
			// TODO: handle exception
			return "0";
		}
	}

//	查询结算券余额
	public Integer querySettlementBalance(String merchantId) throws Exception {
		// TODO Auto-generated method stub
		MerchantCustom mc = merchantMapper.queryPKAndConAddr(merchantId);
		String settlementResult = Web3.getValueMap("Merchant.getSettlementBalance",null, mc.getPublicKey(), mc.getContractAddress());
		Integer settlementBalance = Integer.parseInt(Web3.decodeReturnValue("uint",settlementResult).get(0));
		return settlementBalance;

	}
	public Integer queryCouponsUnsed(String id) throws Exception {
		// TODO Auto-generated method stub
		MerchantCustom mc = merchantMapper.queryPKAndConAddr(id);
		String unusedCoupons = Web3.getValueMap("Merchant.getUnusedCoupons",null, mc.getPublicKey(), mc.getContractAddress());
		List<String> movedCoupon = Web3.decodeReturnValue("address[]",unusedCoupons);
		return movedCoupon.size();
	}

	public Integer queryAccountBalance(String id) throws Exception {
		// TODO Auto-generated method stub
		return merchantMapper.queryAccountBalance(id);

	}

//	商户注册
	public String insertMerchant(MerchantRegisterCustom merchantRegisterCustom) throws Exception {
		// TODO Auto-generated method stub
//		查询该用户是否已经存在
		MerchantRegisterCustom mrc=merchantMapper.queryMerchantRegInfoByAccount(merchantRegisterCustom.getAccount());
		//已存在审核通过的账户
		if(mrc!=null&&mrc.getStatus().equals("1")){
			return "3";
		}
		//该account账户正在审核中
		if(mrc!=null&&mrc.getStatus().equals("0")){
			return "2";
		}
		merchantRegisterCustom.setId(UUID.randomUUID().toString());
		String salt= Encryption.createSalt();
		merchantRegisterCustom.setSalt(salt);
//		用sha3加密算法对前端传递过来的代码再次进行加密
		merchantRegisterCustom.setPassword(Sha3.sha3(merchantRegisterCustom.getPassword()+salt));
		MerchantCustom merchantCustom = new MerchantCustom();
	
		
		try {
			merchantMapper.insertRegisterApp(merchantRegisterCustom);
			return "1";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "0";
		}
	}

/**
 * 商户登录
 * 登录成功，返回商户id，生成token，并将token写入商户表
 */
	public Map<String, String> login(String account, String password) throws Exception {
		// TODO Auto-generated method stub
		Map<String, String> m=new HashMap<String, String>();
		MerchantCustom mc=merchantMapper.queryMerchantInfoByAccount(account);
		if(mc!=null && mc.getStatus().equals("0")){
			m.put("resultCode","-3");
			return m;
		}
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
					merchantMapper.updateToken(mc);
					String couponRulerId = mc.getConCouponRulerId();
					CouponRuler cr = couponMapper.queryCouponRulerInfo(couponRulerId);
					if(cr!=null){
						m.put("reach",String.valueOf(cr.getReach()));
						m.put("give",String.valueOf(cr.getGive()));
					}
					return m;
				} catch (Exception e) {
					// 如果token指令更新失败
					m.put("resultCode", "-1");
					return m;
				}
			}else{
				m.put("resultCode", "－2");
				return m;
			}
		}else{
			m.put("resultCode", "0");
			return m;
		}
	}
	
	
/**
 * 插入优惠券发行规则
 * @param couponRulerCustom
 * @return
 */
	public String insertCr(CouponRulerCustom couponRulerCustom){
		couponRulerCustom.setId(UUID.randomUUID().toString());
		couponRulerCustom.setIssueDate(DateUtil.getCurrentDate());
		try {

			/**
			 * 创建所有的智能合约,并将每一份合约写入数据库
			 * 
			 */
//			计算发行的优惠券张数
			int num=couponRulerCustom.getTotalAmount()/couponRulerCustom.getGive();
			MerchantCustom mc=merchantMapper.queryPKAndConAddr(couponRulerCustom.getMerchantId());
//			查询结算券余额，判断剩下的结算券是否足够发行优惠券
			int settlementBalance=merchantMapper.querySettlementBalance(couponRulerCustom.getMerchantId());
			if(settlementBalance<couponRulerCustom.getTotalAmount()){
				return "0";//结算券余额不够
			}
			//发行优惠券，并返回所有新的优惠券地址
			String funcName = "Merchant.issueCoupon";
			String[] content = {couponRulerCustom.getGive()+"",couponRulerCustom.getReach()+"",num+"",couponRulerCustom.getValidStartDate(),couponRulerCustom.getValidEndDate()};
			String txHash = Web3.sendTransactionMap(funcName, content, mc.getPublicKey(), mc.getContractAddress());
			String finish = "null";
			String[] content1 = {txHash};
			while(finish.equals("null")){
				finish = Web3.universalCall("eth_getTransactionReceipt",null, content1, null);
			}
			String issues = Web3.getValueMap("Merchant.getNotGivenCoupons",null, mc.getPublicKey(), mc.getContractAddress());
			List<String> coupons = Web3.decodeReturnValue("address[]",issues);
			for(String c:coupons){
				CouponCustom cc=new CouponCustom();
				cc.setId(UUID.randomUUID().toString());
				cc.setRulerId(couponRulerCustom.getId());
				cc.setContractAddress(c);
				cc.setOwnerId(couponRulerCustom.getMerchantId());
				cc.setCouponValue(couponRulerCustom.getGive());
				couponMapper.insertCoupon(cc);
			}
			String settlementResult = Web3.getValueMap("Merchant.getSettlementBalance",null, mc.getPublicKey(), mc.getContractAddress());
			Integer settlementBalanceNew = Integer.parseInt(Web3.decodeReturnValue("uint",settlementResult).get(0));

			
//			向优惠券规则表中插入一条记录
			couponMapper.insertCouponRuler(couponRulerCustom);
//			更新商户表中当前优惠券发行规则id
			merchantMapper.updateConCurrentCouponId(couponRulerCustom);
//        更新结算券余额
			MerchantCustom newMc=new MerchantCustom();
			newMc.setId(couponRulerCustom.getMerchantId());
			settlementBalance=settlementBalanceNew;
			newMc.setSettlementBalance(settlementBalance);
			
			merchantMapper.updateSettlementBalance(newMc);
			return couponRulerCustom.getId();
		} catch (Exception e) {
			// TODO: handle exception
			return "0";
		}
	}

//	商户发行优惠券规则
	public String insertCouponRuler(CouponRulerCustom couponRulerCustom) throws Exception {
		// TODO Auto-generated method stub
//		根据商户id，查询商户最近的发行规则id
		String couponRulerId=merchantMapper.queryConCurrentCouponRulerId(couponRulerCustom.getMerchantId());
		
//     已经没有当前优惠券
		if(couponRulerId==null||couponRulerId.equals("")){
			return insertCr(couponRulerCustom);
		}else{
//			根据商户最近的发行规则id，查询发行规则信息
			CouponRulerCustom crc=couponMapper.queryCouponRulerInfo(couponRulerId);
			if(DateUtil.getCurrentDate().compareTo(crc.getValidEndDate())>0
					|| "0".equals(crc.getStatus())){
//				已经过了最后一次发行优惠券的有效期，可以发行新的优惠券
				return insertCr(couponRulerCustom);
			}else{
				return "0";
			}	
		}
	}
//	添加bill账单
	public String insertBill(BillInfo billInfo)throws Exception{
		BillInfo b = new BillInfo();
		b.setId(UUID.randomUUID().toString());
		b.setOperationDate(DateUtil.getCurrentDate());
		b.setMerchantId(billInfo.getMerchantId());
		b.setType(billInfo.getType());
		b.setValue(billInfo.getValue());
		merchantMapper.insertBill(b);
		return "1";
	}

//	查询商户优惠券状态
	public List<CouponInfo> queryCouponStatus(QueryCouponStatus queryCouponStatus) throws Exception {
		// TODO Auto-generated method stub
		String couponRulerId=merchantMapper.queryConCurrentCouponRulerId(queryCouponStatus.getId());
		queryCouponStatus.setConCurrentCouponRulerId(couponRulerId);
		return couponMapper.queryCouponStatus(queryCouponStatus);
	}

	//查询bill
	public List<BillInfo> queryMerchantBill(QueryMerchantBill queryMerchantBill)throws Exception {
		// TODO Auto-generated method stub
		return merchantMapper.queryMerchantBill(queryMerchantBill);
	}
//	查询已使用和未使用优惠券总额
	public CouponCountInfo queryTotalCoupons(String id)throws Exception{
		//从区块链中读取
		MerchantCustom mc = merchantMapper.queryPKAndConAddr(id);
		String resultUnused = Web3.getValueMap("Merchant.getUnusedCoupons",null,mc.getPublicKey(),mc.getContractAddress());
		int unused = Web3.decodeReturnValue("address[]",resultUnused).size();
		String resultUsed = Web3.getValueMap("Merchant.getUsedCoupons",null,mc.getPublicKey(),mc.getContractAddress());
		int used = Web3.decodeReturnValue("address[]",resultUsed).size();
		CouponCountInfo cc = new CouponCountInfo();
		cc.setTotalUsedCount(used);
		cc.setTotalUnusedCount(unused);
		return cc;
		//更新结束
	}
//	查询优惠券申请
	public List<CouponApplicationCustom> queryCouponApplication(String id) throws Exception {
		// TODO Auto-generated method stub
		
		return couponMapper.queryCouponApplication(id);
	}
	

//	获取优惠券额度
	public Map<String, Integer> getCouponValue(String merchantId, String consumptionValue) throws Exception {
		// TODO Auto-generated method stub
//		根据商户id，查询当前优惠券规则id，计算应该发行的优惠券数目
		String rulerId=merchantMapper.queryConCurrentCouponRulerId(merchantId);
		CouponRulerCustom crc=couponMapper.queryCouponRulerInfo(rulerId);
		int cv=(int)Float.parseFloat(consumptionValue);
		
//		计算优惠券张数
	    int couponNum = cv/crc.getReach();
	    int couponValue=couponNum*crc.getGive();
	    
	    Map<String, Integer> m=new HashMap<String, Integer>();
		if(couponValue>crc.getCapping()){
		    m.put("couponValue", crc.getCapping());
		    m.put("couponNum", crc.getCapping()/crc.getReach());
			return m;
		}else{
		    m.put("couponValue", couponValue);
		    m.put("couponNum",couponNum);
			return m;
		}
	}
//===============================================================================================
//	处理优惠券申请
	private String generatemark(String uuid){
		String[] parts = uuid.split("-");
		String result = "";
		for(int i=0;i<parts.length;i++){
			result += parts[i];
		}
		return result;
	}

	public String dealCouponApplicaiton(CouponApplicationCustom cac) throws Exception {
		// TODO Auto-generated method stub
//		不管商户是否拒绝优惠券申请，更新优惠券申请表状态
		if(cac.getStatus().equals("0")){
			try {
				couponMapper.updateCouponAppStatus(cac);
				return "1";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return "0";
			}
		}else{
			try {
//				判断商户剩余的优惠券是否足够支持消费者获取的优惠券
				List<CouponCustom> list=couponMapper.queryUnIssuedCoupons(cac.getMerchantId());
				if(list.size()<cac.getCouponNum()){
					return "-1";  //优惠券不够用了
				}
				String markCode=UUID.randomUUID().toString();
//				查询商户的公钥和合约地址
				MerchantCustom mc=new MerchantCustom();
				mc=merchantMapper.queryPKAndConAddr(cac.getMerchantId());
				
//			发放优惠券
//			发放优惠券,消费者合约地址来自于数据库
//			从区块链获得发放的优惠券信息
				String consumerContractAddress=consumerMapper.queryConsumerConAddr(cac.getConsumerId());
				String funcName = "Merchant.grant";
				String[] content = {consumerContractAddress,cac.getCouponNum()+"",DateUtil.getCurrentDate(),generatemark(cac.getId()),cac.getConsumptionValue()};
				String txHash = Web3.sendTransactionMap(funcName, content, mc.getPublicKey(), mc.getContractAddress());
				String finish = "null";
				String[] content1 = {txHash};
				while(finish.equals("null")){
					finish = Web3.universalCall("eth_getTransactionReceipt",null, content1, null);
				}
				String[] content2 = {generatemark(cac.getId())};
				String result = Web3.getValueMap("Merchant.getCorrespondingGrant",content2,mc.getPublicKey(),mc.getContractAddress());
				List<String> couponConAddres = Web3.decodeReturnValue("address[]",result);
				for(int i=0;i<couponConAddres.size();i++){
					CouponCustom cc=new CouponCustom();
					cc.setConsumptionDate(DateUtil.getCurrentDate());
					cc.setConsumptionValue(cac.getConsumptionValue());
					cc.setContractAddress(couponConAddres.get(i));
					cc.setMarkCode(markCode);
					cc.setStatus("2");
					cc.setOwnerId(cac.getConsumerId());
					couponMapper.updateCouponInfo(cc);
				}
				//更新结束
//				改变优惠券申请表状态
				couponMapper.updateCouponAppStatus(cac);
				return "1";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return "0";
			}
		}

	}
	
//	查询优惠券支付申请
	public List<CouponPayApplicationCustom> queryCouponPayApp(String id) throws Exception {
		// TODO Auto-generated method stub
		List<CouponPayApplicationCustom> l = couponMapper.queryCouponPayApp(id);
		return l;
	}
	
//	优惠券支付申请确认
	public String dealCouponPayApp(String id, String applicationCode) throws Exception {
		// TODO Auto-generated method stub
		try {
			couponMapper.updateCouponPayApp(applicationCode);
//		根据优惠券支付申请码查询消费者id和优惠券id
			List<CouponPayApplicationCustom> list=couponMapper.queryIdByAppCode(applicationCode);
			String consumerId=list.get(0).getConsumerId();
//		查询消费者合约地址
			String consumerContractAddress=consumerMapper.queryConsumerConAddr(consumerId);
			//确认优惠券支付
			String couponContractAddress = couponMapper.queryConponConAddr(list.get(0).getCouponId());
			Integer couponValue = list.get(0).getCouponValue();
			String consumeValue = list.get(0).getConsumeValue()+"";
			String consumeDate = list.get(0).getApplicationTime();
			MerchantCustom mc = merchantMapper.queryPKAndConAddr(id);
			String funcName = "Merchant.confirmCouponPay";
			String[] content = {consumeValue, consumeDate, couponContractAddress, consumerContractAddress};
			String txHash = Web3.sendTransactionMap(funcName, content, mc.getPublicKey(), mc.getContractAddress());
			String[] content1 = {txHash};
			String finish = "null";
			while(finish.equals("null")){
				finish = Web3.universalCall("eth_getTransactionReceipt",null,content1, null);
			}
			//修改支付后优惠券状态
			couponMapper.updatePayCouponStatus(list.get(0).getCouponId());
			//更新结束
			
//			   查询和更新商户结算券余额
			int settlementBalance=merchantMapper.querySettlementBalance(id);
			settlementBalance=settlementBalance+couponValue;
			MerchantCustom merchantCustom=new MerchantCustom();
			merchantCustom.setId(id);
			merchantCustom.setSettlementBalance(settlementBalance);
			merchantMapper.updateSettlementBalance(merchantCustom);
			return "1";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "0";
		}
	}

	@Override
	public String terminateIssue(String couponRulerId) {
		try {
			CouponRuler cr = couponMapper.queryCouponRulerInfo(couponRulerId);
			String merchant_id = cr.getMerchantId();
			MerchantCustom mc = merchantMapper.queryPKAndConAddr(merchant_id);
			//修改合约名字
			String txHash1 = Web3.sendTransactionMap("Merchant.terminateCoupon", null, mc.getPublicKey(), mc.getContractAddress());
			//更新结束
			String[] content1 = {txHash1};
			String finish = "null";
			while(finish.equals("null")){
				finish = Web3.universalCall("eth_getTransactionReceipt", null, content1, null);
			}
			int unissued_value = couponMapper.queryTotalUnIssuedCouponValue(couponRulerId);
			cr.setWithdrawAmount(unissued_value);
			cr.setStatus("0");
			cr.setWithdrawDate((new SimpleDateFormat("yyyy-MM-dd")).format(new Date()));
			couponMapper.updateUnIssuedCoupon(couponRulerId);
			couponMapper.updateCouponRulerInfo(cr);
			return "1";
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
	}

	//结算券流水
	@Override
	public List<SettlementRecord> querySettlementRecord(QuerySettlementCustom qsc) throws Exception {
		return merchantMapper.querySettlementRecord(qsc);
	}

//	商户修改密码
	@Override
	public String modifyPassword(ModifyPassVo mpv) throws Exception {
		try {
			MerchantRegisterCustom mrc=merchantMapper.queryMerchantRegInfoByAccount(mpv.getAccount());
			String salt= mrc.getSalt();
//		用sha3加密算法对前端传递过来的代码再次进行加密
			if (Sha3.sha3(mpv.getOldPassword()+salt).equals(mrc.getPassword())){
				mpv.setNewPassword(Sha3.sha3(mpv.getNewPassword()+salt));
				merchantMapper.modifyPassword(mpv);
				return "1";
			}else {
				return "0";
			}
		}catch (Exception e){
			return "0";
		}
	}


	//	查看商户账号已绑定的银行账户
	public String selectBankAccount(String merchantNumber) throws Exception {
		// TODO Auto-generated method stub
		return merchantMapper.selectBankAccount(merchantNumber);
	}



//	encapsulate terminate issue controller
	@Override
	public TerminateIssueEncapsulation terminateIssueDataEncapsulate(String merchantId) throws Exception {
		TerminateIssueEncapsulation tie = new TerminateIssueEncapsulation();
		// ruler info
		CouponRuler cr = queryCouponRulerByMerchantId(merchantId);
		tie.setUpTo(cr.getReach());
		tie.setReturnValue(cr.getGive());
		tie.setAddUp(cr.getIsAccumulation());
		tie.setTotal(cr.getTotalAmount());
		tie.setMost(cr.getCapping());
		tie.setFrom(cr.getValidStartDate());
		tie.setTo(cr.getValidEndDate());
		tie.setCouponRulerId(cr.getId());

		// calculate items
		CouponCountInfo cci = queryTotalCoupons(merchantId);
		tie.setNotUse(cci.getTotalUnusedCount()*cr.getGive());
		tie.setTotalSend(cci.getTotalUsedCount()*cr.getGive()+cci.getTotalUnusedCount()*cr.getGive());
		tie.setNotSend(tie.getTotal()-tie.getTotalSend());
		tie.setCallBack(tie.getNotSend()+tie.getNotUse());

		return tie;
	}

	@Override
	public MerchantAccountSetting queryMerchantInfo(String merchantId) throws Exception {
		return merchantMapper.queryMerchantInfoById(merchantId);
	}

	//	查询优惠券发行规则
	@Override
	public CouponRuler queryCouponRulerByMerchantId(String merchantId) throws Exception {
		return couponMapper.queryCouponRulerByMerchantId(merchantId);
	}

	//查询商户名称和简介
	@Override
	public Merchant queryMerchantInfoByAccount(String id)throws Exception{
		Merchant merchant = merchantMapper.queryMerchantInfoByAccount(id);
		return merchant;
	}

	//	重交商户简介
	public String insertMerchantIntro(MerchantSeting merchantSeting) throws Exception {
		// TODO Auto-generated method stub
		try {
			merchantMapper.insertMerchantIntro(merchantSeting);
			return "1";
		} catch (Exception e) {
			// TODO: handle exception
			return "0";
		}
	}

	//	查询距离最近的三个商家
	@Override
	public List<NearbyMerchantItem> fetchNearby(Location location) throws Exception {
		List<NearbyMerchantDist> merchantDists = merchantMapper.queryMerchantDistItems();
		List<NearbyMerchantItem> result_list = calculateNearbyThree(merchantDists, location);
		return result_list;
	}

	public List<NearbyMerchantItem> calculateNearbyThree(List<NearbyMerchantDist> merchantDists, Location location){
		List<NearbyMerchantItem> result_list = new ArrayList<NearbyMerchantItem>();
		if(merchantDists.size()<=3){

			for(NearbyMerchantDist nd: merchantDists){
				NearbyMerchantItem nmi = new NearbyMerchantItem();
				nmi.setMerchantId(nd.getMerchantId());
				nmi.setMerchantInfo(nd.getMerchantInfo());
				nmi.setMerchantName(nd.getMerchantName());
				double distance = calculateDistance(nd.getLatitude(), nd.getLongitude(), location);
				nmi.setDistance(distance+"");
				result_list.add(nmi);
			}
			return result_list;
		}else{
			int position = -1;
			for(NearbyMerchantDist nd: merchantDists){
				double distance = calculateDistance(nd.getLatitude(), nd.getLongitude(), location);
				if(result_list.size()<3 && position == -1){
					NearbyMerchantItem nmi = new NearbyMerchantItem();
					nmi.setMerchantId(nd.getMerchantId());
					nmi.setMerchantInfo(nd.getMerchantInfo());
					nmi.setMerchantName(nd.getMerchantName());
					nmi.setDistance(distance+"");
					result_list.add(nmi);
					position = 0;
				}else if(result_list.size()<3 && position != -1){
					NearbyMerchantItem nmi = new NearbyMerchantItem();
					nmi.setMerchantId(nd.getMerchantId());
					nmi.setMerchantInfo(nd.getMerchantInfo());
					nmi.setMerchantName(nd.getMerchantName());
					if(distance > Double.parseDouble(result_list.get(position).getDistance())){
						position = result_list.size();
					}
					nmi.setDistance(distance+"");
					result_list.add(nmi);
				}else if(result_list.size() == 3){
					if(distance < Double.parseDouble(result_list.get(position).getDistance())){
						NearbyMerchantItem nmi = new NearbyMerchantItem();
						nmi.setMerchantId(nd.getMerchantId());
						nmi.setMerchantInfo(nd.getMerchantInfo());
						nmi.setMerchantName(nd.getMerchantName());
						nmi.setDistance(distance+"");
						result_list.set(position, nmi);
						int max = position;
						for(int i=0;i<result_list.size();i++){
							if(i!=position){
								if(Double.parseDouble(result_list.get(max).getDistance())<Double.parseDouble(result_list.get(i).getDistance())){
									max = i;
								}
							}
						}
						position = max;
					}
				}
			}
		}
		Collections.sort(result_list, new DistanceComparator());
		return result_list;
	}
	private Double calculateDistance(String alt, String lon, Location  location){
		double radLat1 = rad(Double.parseDouble(alt));
		double radLat2 = rad(Double.parseDouble(location.getLatitude()));
		double subtractLat = radLat1-radLat2;
		double subtractLog = rad(Double.parseDouble(lon))-rad(Double.parseDouble(location.getLongitude()));
		double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(subtractLat/2),2) +
				Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(subtractLog/2),2)));
		distance = distance *6378.137 ;// EARTH_RADIUS;
		distance = Math.round(distance * 10000) / 10;
		return distance;
	}
	private double rad(double d){
		return d*Math.PI/180.0;
	}

	@Override
	public String queryMerchantNameById(String merchantId) {
		try {
			return merchantMapper.queryMerchantNameById(merchantId);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
