package com.block.coupon.controller;

import com.block.coupon.po.*;
import com.block.coupon.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value="/merchant")
public class MerchantController {

	@Autowired
	private MerchantService merchantService;

	//	申请结算券
	@RequestMapping(value="/applySettlementCoupon.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, String>  applySettlementCoupon(@RequestBody SettlementOperationCustom soc) throws Exception {
		soc.setOperationType("1");
		String str = merchantService.insertSettlementOperation(soc);
		if ("1".equals(str)){
			BillInfo billInfo = new BillInfo();
			billInfo.setMerchantId(soc.getMerchantId());
			billInfo.setValue(soc.getOperationAmount());
			billInfo.setType("结算券申请");
			str = merchantService.insertBill(billInfo);
		}
		Map<String, String> m=new HashMap<String, String>();
		m.put("resultCode", str);
		return m;
	}

	//	查询结算券余额
	@RequestMapping(value="/querySettlementBalance.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, Integer>  querySettlementBalance(String id) throws Exception {
		Map<String, Integer> m=new HashMap<String, Integer>();
		int balance=0;
		balance=merchantService.querySettlementBalance(id);
		m.put("settlementBalance", balance);
		return m;
	}


//	商户注册
	@RequestMapping(value="/register.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, String> register(@RequestBody MerchantRegisterCustom merchantRegisterCustom)throws Exception{
		Map<String, String> m=new HashMap<String, String>();
		String resultCode=merchantService.insertMerchant(merchantRegisterCustom);
		m.put("resultCode", resultCode);
		return m;
	}


//	商户登录
	@RequestMapping(value="/login.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, String> login(@RequestBody MerchantLogin merchantLogin)throws Exception{
		return merchantService.login(merchantLogin.getAccount(), merchantLogin.getPassword());
	}


//	结算券提现申请
	@RequestMapping(value="/applyWDApp.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, String>  applyWDApp(SettlementOperationCustom soc) throws Exception {
		soc.setOperationType("2");
		String str = merchantService.insertSettlementOperation(soc);
		Map<String, String> m=new HashMap<String, String>();
		m.put("resultCode", str);
		return m;
	}


//	优惠券发行
	@RequestMapping(value="/issueCoupons.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, String> issueCoupons(@RequestBody CouponRulerCustom crc)throws Exception{
		String resultCode=merchantService.insertCouponRuler(crc);
		Map<String, String> m=new HashMap<String, String>();
		m.put("resultCode", resultCode);
		return m;
	}
//	生成bill
	@RequestMapping(value="/generateBill.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, String> generateBill(@RequestBody BillInfo billInfo)throws Exception{
		String resultCode=merchantService.insertBill(billInfo);
		if ("1".equals(resultCode)){
			SettlementOperationCustom settlementOperation = new SettlementOperationCustom();
			settlementOperation.setMerchantId(billInfo.getMerchantId());
			settlementOperation.setOperationType("2");
			settlementOperation.setOperationAmount(billInfo.getValue());
			resultCode = merchantService.insertSettlementOperation(settlementOperation);
		}
		Map<String, String> m=new HashMap<String, String>();
		m.put("resultCode", resultCode);
		return m;
	}


//	查询商户当前发行的优惠券状态
	@RequestMapping(value="/queryCouponsStatus.action",method=RequestMethod.POST)
	public @ResponseBody List<CouponInfo> queryCouponsStatus(@RequestBody QueryCouponStatus queryCouponStatus)throws Exception{
		return merchantService.queryCouponStatus(queryCouponStatus);
	}
//	查询商户bill账单
	@RequestMapping(value="/queryMerchantBill.action",method=RequestMethod.POST)
	public @ResponseBody List<BillInfo> queryMerchantBill(@RequestBody QueryMerchantBill queryMerchantBill)throws Exception{
		return merchantService.queryMerchantBill(queryMerchantBill);
	}
	//	查询已使用和未使用的优惠券
	@RequestMapping(value="/queryCouponsTotal.action", method=RequestMethod.GET)
	public @ResponseBody CouponCountInfo queryCouponsTotal(String id)throws Exception{
		return merchantService.queryTotalCoupons(id);
	}
	//查询未使用的优惠卷额度
	@RequestMapping(value="/queryCouponsUnsed.action",method = RequestMethod.POST)
	public @ResponseBody Map<String, Integer> queryCouponsUnsed(String id)throws Exception{
		Map<String, Integer> m=new HashMap<String, Integer>();
		int balance=0;
		balance= merchantService.queryCouponsUnsed(id);
		m.put("couponBalance", balance);
		return m;
	}

	//查询账户余额
	@RequestMapping(value="/queryAccountBalance.action",method = RequestMethod.POST)
	public @ResponseBody Map<String, Integer> queryAccountBalance(String merchantId)throws Exception{
		Map<String, Integer> m=new HashMap<String, Integer>();
		int balance=0;
		balance= merchantService.queryAccountBalance(merchantId);
		m.put("accountBalance", balance);
		return m;
	}
	//	查询优惠券申请信息
	@RequestMapping(value="/queryCouponApp.action", method=RequestMethod.POST)
	public @ResponseBody List<CouponApplicationCustom> queryCouponApp(String id)throws Exception{
		return merchantService.queryCouponApplication(id);
	}

	//  获取优惠券额度
	@RequestMapping(value="/queryCouponValue.action", method=RequestMethod.POST)
	public @ResponseBody Map<String, Integer> queryCouponValue(String merchantId,String consumptionValue)throws Exception{
		Map<String, Integer> m=merchantService.getCouponValue(merchantId, consumptionValue);
		return m;
	}

	//	查询优惠券支付申请
	@RequestMapping(value="/queryCouponPayApp.action", method=RequestMethod.POST)
	public @ResponseBody List<CouponPayApplicationCustom> queryCouponPayApp(String id)throws Exception{
		return merchantService.queryCouponPayApp(id);
	}

	//	处理优惠券申请请求
	@RequestMapping(value="/updateCouponApp.action", method=RequestMethod.POST)
	public @ResponseBody Map<String, String> updateCouponApp(@RequestBody CouponApplicationCustom couponApplicationCustom)throws Exception{
		System.out.print(couponApplicationCustom+"\n\n\n\n\n");
		String resultCode=merchantService.dealCouponApplicaiton(couponApplicationCustom);
		Map<String, String> m=new HashMap<String, String>();
		m.put("resultCode", resultCode);
		return m;
	}

	//

//	处理优惠券使用请求
//	原来传入两个参数，另一个参数为id，用于后台更新coupon的ownerId
	@RequestMapping(value="/updateCouponPayApp.action", method=RequestMethod.POST)
	public @ResponseBody Map<String, String> updateCouponPayApp(@RequestBody UpdateCouponPayParam updateCouponPayParam)throws Exception{
		String resultCode=merchantService.dealCouponPayApp(updateCouponPayParam.getId(), updateCouponPayParam.getApplicationCode());
		Map<String, String> m=new HashMap<String, String>();
		m.put("resultCode", resultCode);
		return m;
	}

	//	查询结算券流水
	@RequestMapping(value="/querySettlementRecord.action", method=RequestMethod.POST)
	public @ResponseBody List<SettlementRecord> querySettlementRecord(@RequestBody QuerySettlementCustom qsc)throws Exception{
		return merchantService.querySettlementRecord(qsc);
	}

	@RequestMapping(value="/terminateIssue.action", method=RequestMethod.POST)
	public @ResponseBody Map<String, String> terminateIssue(String couponRulerId) throws Exception{
		String result = merchantService.terminateIssue(couponRulerId);
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("resultCode", result);
		return resultMap;
	}

	//	修改账户密码(guowei)
	@RequestMapping(value="/modifyPassword.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, String> modifyPassword(@RequestBody ModifyPassVo mpv) throws Exception{
		String resultCode = merchantService.modifyPassword(mpv);
		Map<String, String> result_map  = new HashMap<String, String>();
		result_map.put("resultCode", resultCode);
		return result_map;
	}


	//查看商户账号已绑定的银行账户
	@RequestMapping(value="/selectBankAccount.action", method=RequestMethod.POST)
	public @ResponseBody Map<String, String> selectBankAccount(String merchantNumber)throws Exception{
		String bankNumber=merchantService.selectBankAccount(merchantNumber);
		Map<String, String> m=new HashMap<String, String>();
		m.put("bankNumber", bankNumber);
		return m;
	}



	// encapsulate terminate issue controller
	@RequestMapping(value="/terminateIssueEncapsulate.action",method=RequestMethod.POST)
	public @ResponseBody TerminateIssueEncapsulation terminateIssueDataEncapsulate(String merchantId) throws Exception{
		TerminateIssueEncapsulation terminateIssueEncapsulation = merchantService.terminateIssueDataEncapsulate(merchantId);
		return terminateIssueEncapsulation;
	}

	// encapsulate couponState page info
	@RequestMapping(value="/couponStateInfoEncapsulate.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, Integer> couponStateInfoEncapsulate(String merchantId)throws Exception {
		Map<String, Integer> result = new HashMap<String, Integer>();
		CouponCountInfo cci = merchantService.queryTotalCoupons(merchantId);
		result.put("total", cci.getTotalUnusedCount() + cci.getTotalUsedCount());
		result.put("alreadyUse", cci.getTotalUsedCount());
		result.put("notUse", cci.getTotalUnusedCount());
		return result;
	}

	@RequestMapping(value="/queryMerchantInfo.action",method=RequestMethod.POST)
	public @ResponseBody MerchantAccountSetting queryMerchantInfo(String merchantId) throws Exception{
		return merchantService.queryMerchantInfo(merchantId);
	}
	// 查看商户名称、简介
	@RequestMapping(value="/queryMerchantInfoByAccount.action",method=RequestMethod.POST)
	public @ResponseBody Merchant queryMerchantInfoByAccount(String id)throws Exception {
		Merchant smi = merchantService.queryMerchantInfoByAccount(id);
		return smi;
	}

	//	重新提交商户简介
	@RequestMapping(value="/submitMerchantIntro.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, String> submitMerchantIntro(@RequestBody MerchantSeting merchantSeting)throws Exception{
		Map<String, String> m=new HashMap<String, String>();
		String resultCode=merchantService.insertMerchantIntro(merchantSeting);
		m.put("resultCode", resultCode);
		return m;
	}

	//	查询附近三个商家
	@RequestMapping(value="/fetchNearby.action",method=RequestMethod.POST)
	public @ResponseBody List<NearbyMerchantItem> fetchNearby(@RequestBody Location location) throws Exception{
		List<NearbyMerchantItem> list = merchantService.fetchNearby(location);
		return list;
	}

	//根据商户id查询商户名称
	@RequestMapping(value = "/queryMerchantNameById.action",method = RequestMethod.POST)
	public @ResponseBody Map<String,String> queryMerchantNameById(String merchantId) throws Exception{
		String merchantName = merchantService.queryMerchantNameById(merchantId);
		Map<String,String> result = new HashMap<>();
		result.put("merchantName",merchantName);
		return result;
	}
}

















