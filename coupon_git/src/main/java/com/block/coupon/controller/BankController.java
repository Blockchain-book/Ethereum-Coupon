package com.block.coupon.controller;

import com.block.coupon.po.*;
import com.block.coupon.service.BankService;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value="/bank")
public class BankController {	
	
	@Autowired
	private BankService bankService;

    private  String SUCCESS = "";
	
//	结算券申请信息列表查询，是初审和复审列表信息查询入口
	@RequestMapping(value="/querySettlementApplicationList.action",method=RequestMethod.GET)
	public @ResponseBody List<SettlementApplication> querySettlementApplicationList(String id) throws Exception {
		List<SettlementApplication> list=  bankService.querySettlementApplicationList(id);
		return list;
	}

	//结算券初审申请信息列表
    @RequestMapping(value="/queryFirstSettlementList.action",method=RequestMethod.POST)
    public @ResponseBody Map<String,List<SettlementApplication>> queryFirstSettlementList() throws Exception {
        List<SettlementApplication> list=  bankService.querySFCList();
        Map<String,List<SettlementApplication>> result = new HashMap<>();
        result.put("list",list);
        return result;
    }

    //结算券复审申请信息列表
    @RequestMapping(value="/queryReCheckSettlementList.action",method=RequestMethod.POST)
    public @ResponseBody Map<String,List<SettlementApplication>> queryReCheckSettlementList() throws Exception {
        List<SettlementApplication> list=  bankService.querySSCList();
        Map<String,List<SettlementApplication>> result = new HashMap<>();
        result.put("list",list);
        return result;
    }
	
//	结算券提现信息列表查询，是初审和复审列表信息查询入口
	@RequestMapping(value="/querySettlementWdList.action",method=RequestMethod.GET)
	public @ResponseBody List<SettlementApplication> querySettlementWdList(String id) throws Exception {
		List<SettlementApplication> list=  bankService.querySettlementWdList(id);
		return list;
	}

    //结算券初审提现信息列表
    @RequestMapping(value="/queryFirstWithdrawSettlementList.action",method=RequestMethod.POST)
    public @ResponseBody Map<String,List<SettlementApplication>> queryFirstWithdrawSettlementList() throws Exception {
        List<SettlementApplication> list=  bankService.querySWDFList();
        Map<String,List<SettlementApplication>> result = new HashMap<>();
        result.put("list",list);
        return result;
    }

    //结算券初审提现信息列表
    @RequestMapping(value="/queryReWithdrawSettlementList.action",method=RequestMethod.POST)
    public @ResponseBody Map<String,List<SettlementApplication>> queryReWithdrawSettlementList() throws Exception {
        List<SettlementApplication> list=  bankService.querySWDSList();
        Map<String,List<SettlementApplication>> result = new HashMap<>();
        result.put("list",list);
        return result;
    }
	
//	结算券申请初审结果更新
	@RequestMapping(value="/scFirstCheck.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, String> scFirstCheck(SettlementOperationCustom soc) throws Exception {
		Map<String, String> m=new HashMap<String, String>();
		String str=bankService.updateSettlementFirstCheck(soc);
		m.put("resultCode", str);
		return m;
	}


//	结算券提现初审结果更新
	@RequestMapping(value="/wdFirstCheck.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, String> wdFirstCheck(SettlementOperationCustom soc) throws Exception {
		Map<String, String> m=new HashMap<String, String>();
		String str=bankService.updateSettlementFirstCheck(soc);
		m.put("resultCode", str);
		return m;
	}

	
//	查询注册信息尚未审核的商户
	@RequestMapping(value="/queryUncheckMerchants.action",method=RequestMethod.GET)
	public @ResponseBody List<MerchantRegisterCustom> queryUncheckMerchants()throws Exception{
		return bankService.queryUncheckMerchant();
	}


	/**
	 * 银行员工注册
	 * @return
	 * @author wufaxiang
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String bankStaffRegister(@RequestParam("username") String account, @RequestParam("password") String password,
									@RequestParam("positionOptions") String  position , Map<String, String> m) throws Exception{

		BankStaffCustom bankStaffCustom = new BankStaffCustom();
		// 设置属性
		bankStaffCustom.setAccount(account);
		bankStaffCustom.setPassword(password);
		bankStaffCustom.setPublicKey("test_publickey");
		bankStaffCustom.setPosition(position);
		bankStaffCustom.setSecurityCode("123");
		String resultCode = this.bankService.insertBankStaff(bankStaffCustom);
		if(resultCode.equals("0")) {
			m.put("error_code", "账号已存在，请重新填写");
			SUCCESS = "bank/login";
		}else {
			m.put("error_code", "恭喜您注册成功，请登录");
			SUCCESS = "bank/login";
		}
		return SUCCESS;
	}

    /**
     * 员工登录
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String bankStaffLogin(@RequestParam("username") String account, @RequestParam("password") String password,
                                 @RequestParam(value = "repassword", required = false) String repassword, Map<String, String> m, HttpSession session) throws Exception{
        System.out.println(account + " .. " + password) ;
//		System.out.println("bankService-login: ");
        String resultCode = this.bankService.login(account, password).get("resultCode");
        if(resultCode == "0" ) {
            m.put("error_code", "用户名错误，请重新输入");
            SUCCESS = "bank/login";
        } else if(resultCode == "1") {
            m.put("error_code","密码错误，请重新输入");
            SUCCESS = "bank/login";
        }else if(resultCode == "2"){
			BankStaffCustom bankStaffCustom = this.bankService.queryBankStaffByAccount(account);
//			System.out.println("bankStaffCustom: ");
//			System.out.println(bankStaffCustom.getOnline() instanceof Object);
            if(bankStaffCustom.getOnline() != null && bankStaffCustom.getOnline().equals("1")){
				m.put("error_code","您已在线，请勿重复登录");
				SUCCESS = "bank/login";
			}else {
				String position = bankStaffCustom.getPosition();
				if (position.equals("1")) {
					SUCCESS = "redirect:../main/toFirstTrial.action";
				} else if (position.equals("2")) {
					SUCCESS = "redirect:../main/toReCheck.action";
				}
				// 将用户写入session
				session.setAttribute("currenBankStaff", bankStaffCustom);
//				bankStaffCustom.setOnline("1");
//				bankService.updateOnline(bankStaffCustom);
			}
        }
        return SUCCESS;
    }

//	冻结异常账户
	@RequestMapping(value="/freezeExceptionAccount.action",method=RequestMethod.POST)
	public @ResponseBody Map<String,String> freezeExceptionAccount(String account)throws Exception{
		return bankService.freezeExceptionAccount(account);
	}

//	解冻异常账户
	@RequestMapping(value="/thawExceptionAccount.action",method = RequestMethod.POST)
	public @ResponseBody Map<String,String> thawExceptionAccount(String account)throws Exception{
		return bankService.thawExceptionAccount(account);
	}

	// 商户通过或拒绝审核
	@RequestMapping(value = "/merchantPass", method = RequestMethod.POST)
	public @ResponseBody Map<String,String> merchantPass(@RequestParam("merchantId") String id,
							   @RequestParam("status") String status,
							   @RequestParam(value = "operatorOpinion", required = false) String operatorOpinion ,
							   HttpSession session) throws Exception {
		System.out.println(status + "---" + id);
		// 1. 将此商户id的商户的stauts更新: 1--通过， 2--拒绝
		String res;
		try {
			this.bankService.updateStatus(status,id);
			res = "1";
		}catch (Exception e){
			e.printStackTrace();
			res = "0";
		}

		Map<String,String> result = new HashMap<>();
		result.put("resultCode",res);
		return result;
	}

	@RequestMapping(value = "/bankStaffCheck", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> merchantCheck(@RequestParam("pass_id") String id,
								@RequestParam("status")String status,
								@RequestParam(value = "operatorOpinion", required = false) String operationOpinion,
								HttpSession session
							  ) throws Exception {
		System.out.println(status+"----------"+id);
		BankStaffCustom bankStaffCustom = ((BankStaffCustom)session.getAttribute("currenBankStaff"));
		Map<String, Object> map = this.bankService.merchantCheck(id, status, operationOpinion, bankStaffCustom.getId());
		return map;
	}

	@RequestMapping(value="/queryConsumerList.action",method=RequestMethod.POST)
	public @ResponseBody Map<String,List<ConsumerInfo>> queryConsumerList()throws Exception{
		Map<String,List<ConsumerInfo>> result = new HashMap<String,List<ConsumerInfo>>();
    	List<ConsumerInfo> consumers = bankService.queryConsumerList();
    	result.put("normalList",consumers);
    	return result;
	}

	@RequestMapping(value="/queryAbnormalList.action",method=RequestMethod.POST)
	public @ResponseBody Map<String,List<ConsumerInfo>> queryAbnormalList()throws Exception{
		Map<String,List<ConsumerInfo>> result = new HashMap<String,List<ConsumerInfo>>();
		List<ConsumerInfo> consumers = bankService.queryAbnormalList();
		result.put("abnormalList",consumers);
		return result;
	}

	@RequestMapping(value="/queryFrozenConsumer.action",method=RequestMethod.POST)
	public @ResponseBody Map<String,List<ConsumerInfo>> queryFrozenConsumer()throws Exception{
		Map<String,List<ConsumerInfo>> result = new HashMap<String,List<ConsumerInfo>>();
		List<ConsumerInfo> consumers = bankService.queryFrozenConsumer();
		result.put("frozenConsumerList",consumers);
		return result;
	}

	//将指定用户标记为abnormal
	@RequestMapping(value="/markAbnormal.action", method=RequestMethod.POST)
	public @ResponseBody Map<String,String> markAbnormal(String consumerId)throws Exception{
    	String resultCode = bankService.markAbnormal(consumerId);
    	Map<String, String> result = new HashMap<String, String>();
    	result.put("resultCode",resultCode);
    	return result;
	}

	//将指定用户标记为abnormal
	@RequestMapping(value="/markNormal.action", method=RequestMethod.POST)
	public @ResponseBody Map<String,String> markNormal(String consumerId)throws Exception{
		String resultCode = bankService.markNormal(consumerId);
		Map<String, String> result = new HashMap<String, String>();
		result.put("resultCode",resultCode);
		return result;
	}

	//商户审核页面的加载action-guowei
	@RequestMapping(value="/queryUncheckMerchants.action",method=RequestMethod.POST)
	public @ResponseBody Map<String, List<MerchantRegisterCustom>> queryUncheckedMerchants()throws Exception{
		List<MerchantRegisterCustom> list_merchant = this.bankService.queryUncheckedMerchant();
		Map<String, List<MerchantRegisterCustom>> result = new HashMap<>();
		result.put("uncheckedMerchantList", list_merchant);
		return result;
	}

	@RequestMapping(value="/queryAllCouponMes.action",method=RequestMethod.POST)
	public @ResponseBody Map<String,List<CouponMes>> queryAllCouponMes() throws Exception{
		Map<String,List<CouponMes>> Mes = bankService.queryAllCouponMes();
		return Mes;
	}

//	返回搜索消费者结果
	@RequestMapping(value="/searchConsumer.action",method=RequestMethod.POST)
	public @ResponseBody Map<String,List<ConsumerInfo>> searchConsumer(
			@RequestParam("account")String account,
			@RequestParam("status")String status) throws Exception{
    	List<ConsumerInfo> list = bankService.searchConsumer(account, status);
    	Map<String, List<ConsumerInfo>> result = new HashMap<String, List<ConsumerInfo>>();
    	result.put("list",list);
    	return result;
	}

//	返回优惠券搜索结果
	@RequestMapping(value="/searchCoupon.action",method=RequestMethod.POST)
	public @ResponseBody Map<String,List<CouponMes>> searchCoupon(@RequestParam("merchantName")String merchantName) throws Exception{
		Map<String,List<CouponMes>> Mes = bankService.searchCoupon(merchantName);
		return Mes;
	}
	//logout
	@RequestMapping(value="/logout.action",method = RequestMethod.POST)
	public @ResponseBody Map<String, String> logout(HttpSession session) throws Exception{
    	BankStaffCustom bsc = (BankStaffCustom) session.getAttribute("currenBankStaff");
    	bsc.setOnline("0");
    	bankService.updateOnline(bsc);
    	session.removeAttribute("currenBankStaff");
    	Map<String,String> result = new HashMap<String,String>();
    	if(session.getAttribute("currenBankStaff") == null){
    		result.put("resultCode","1");
		}else{
    		result.put("resultCode","0");
		}
		return result;
	}
}
