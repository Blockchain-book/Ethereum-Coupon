package com.block.coupon.rpc;

import com.block.coupon.sha3.Sha3;
import com.block.coupon.util.DateUtil;
import com.block.coupon.util.ReadAccount;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by thomas on 2017/3/31.
 */
public class TestOurContract {
    static String bankAccount = "";
    static String merchantAccount = "";
    static String consumerAccount1 = "";
    static String consumerAccount2 = "";
    static String bankContractAddress = "0xfe153a4422ef5c6063be34d42453e36951e3bad8";
    static String merchantContractAddress = "0xe4ea4952c8304b50e5eb87c79d782da130c667f3";
    static String consumer1ContractAddress = "0xb93423cb91b31792d88c1bea529bfd1d58c583dd";
    static String consumer2ContractAddress = "0x1e6df4b2c105a83bde8aee89e18f96a076f34241";
    static String coupon1ContractAddress = "";
    static String coupon2ContractAddress = "";
    static String coupon3ContractAddress = "";
    public static void main(String[] args){
        Web3.init();
        try {
            bankAccount = ReadAccount.getAccount("bankAccount");
            merchantAccount = ReadAccount.getAccount("merchantAccount1");
            consumerAccount1 = ReadAccount.getAccount("consumerAccount1");
            consumerAccount2 = ReadAccount.getAccount("consumerAccount2");
        }catch(IOException ie){
            ie.printStackTrace();
        }
//        deployBankContract();
//        deployMerchantContract();
//        deployConsumerContract();
//        approveSettlement();
//        issueCoupon();
//        grant();
//        getCorrespondingGrant();
//        couponPay();
//        transfer();
//        terminateIssue();
//        setObtainValue();
//        getPassword();
    }

    public static void getPassword(){
        System.out.println(Sha3.sha3("111111"+"cT7CIZpb"));
    }

    public static void setObtainValue(){
        String funcName = "Merchant.grant";
        String[] types={"address","uint","bytes32","bytes","165"};
        String[] content = {"0x1ac4652e9af6f97b94957c56093e1bdf18431ddd",1+"", "2017-04-21","","165"};
        String data = Web3.getData("grant",types,content);
        System.out.println(data);
    }

    public static void getCorrespondingGrant(){
        String[] content = {""};
        String result = Web3.getValueMap("Merchant.getCorrespondingGrant",content,"0xc093bfbaed88f562077ee36a4f0e0f19de356723","0x9d5528d30c8ef3ff37c502a49746a1bd17cd1ed8");
        String address = Web3.decodeReturnValue("address[]",result).get(0);
    }
    public static void terminateIssue(){
//        String funcName = "Merchant.terminateCoupon";
//        String txHash = Web3.sendTransactionMap(funcName,null, "0xc093bfbaed88f562077ee36a4f0e0f19de356723", "0x9d5528d30c8ef3ff37c502a49746a1bd17cd1ed8");
//        String finish = "null";
//        String[] content1 = {txHash};
//        while(finish.equals("null")){
//            finish = Web3.universalCall("eth_getTransactionReceipt",null, content1, null);
//        }
        String unusedCoupons = Web3.getValueMap("Merchant.getUnusedCoupons",null, "0xc093bfbaed88f562077ee36a4f0e0f19de356723", "0x9d5528d30c8ef3ff37c502a49746a1bd17cd1ed8");
        List<String> array1 = Web3.decodeReturnValue("address[]",unusedCoupons);
        System.out.println("====**==== "+(array1.size()));

        String usedCoupons = Web3.getValueMap("Merchant.getUsedCoupons",null, "0xc093bfbaed88f562077ee36a4f0e0f19de356723", "0x9d5528d30c8ef3ff37c502a49746a1bd17cd1ed8");
        List<String> array2 = Web3.decodeReturnValue("address[]",usedCoupons);
        System.out.println("====**==== "+(array2.size()));

        String notGivenCoupons = Web3.getValueMap("Merchant.getNotGivenCoupons",null, "0xc093bfbaed88f562077ee36a4f0e0f19de356723", "0x9d5528d30c8ef3ff37c502a49746a1bd17cd1ed8");
        List<String> array3 = Web3.decodeReturnValue("address[]",notGivenCoupons);
        System.out.println("====**==== "+(array3.size()));

        String historyCoupons = Web3.getValueMap("Merchant.getHistoryCoupons",null, "0xc093bfbaed88f562077ee36a4f0e0f19de356723", "0x9d5528d30c8ef3ff37c502a49746a1bd17cd1ed8");
        List<String> array4 = Web3.decodeReturnValue("address[]",historyCoupons);
        System.out.println("====**==== "+(array4.size()));

//        String settlementResult = Web3.getValueMap("Merchant.getSettlementBalance",null, merchantAccount, merchantContractAddress);
//        Integer settlementBalance = Integer.parseInt(Web3.decodeReturnValue("uint",settlementResult).get(0));
//        System.out.println("===**==="+" "+settlementBalance+"");
    }

    public static void transfer(){
        String funcName = "Consumer.transfer";
        String[] content = {consumer2ContractAddress, coupon2ContractAddress};
        String txHash = Web3.sendTransactionMap(funcName,content, consumerAccount1, consumer1ContractAddress);
        String finish = "null";
        String[] content1 = {txHash};
        while(finish.equals("null")){
            finish = Web3.universalCall("eth_getTransactionReceipt",null, content1, null);
        }

        String couponOut = Web3.getValueMap("Consumer.getCoupons",null, consumerAccount1, consumer1ContractAddress);
        List<String> outAddress = Web3.decodeReturnValue("address[]",couponOut);
        System.out.println("====**==== "+(outAddress.size()==0));

        String couponIn = Web3.getValueMap("Consumer.getCoupons",null, consumerAccount2, consumer2ContractAddress);
        List<String> inAddress = Web3.decodeReturnValue("address[]",couponIn);
        System.out.println("====**==== "+(inAddress.get(0)).equals(coupon2ContractAddress));

        String couponOwner = Web3.getValueMap("Coupon.getOwner", null,consumerAccount2,coupon2ContractAddress);
        List<String> owner = Web3.decodeReturnValue("address",couponOwner);
        System.out.println("====**==== "+(owner.get(0)).equals(consumer2ContractAddress));
    }

    public static void couponPay(){
        String funcName = "Merchant.confirmCouponPay";
        String[] content = {"119","2017-04-03",coupon3ContractAddress, consumer1ContractAddress};
        String txHash = Web3.sendTransactionMap(funcName,content, merchantAccount, merchantContractAddress);
        String finish = "null";
        String[] content1 = {txHash};
        while(finish.equals("null")){
            finish = Web3.universalCall("eth_getTransactionReceipt",null, content1, null);
        }
        String obtainedCoupons = Web3.getValueMap("Consumer.getCoupons",null, consumerAccount1, consumer1ContractAddress);
        List<String> movedCouponAddress = Web3.decodeReturnValue("address[]",obtainedCoupons);
        System.out.println("====**==== "+(movedCouponAddress.size()==0));

        String unusedCoupons = Web3.getValueMap("Merchant.getUsedCoupons",null, merchantAccount, merchantContractAddress);
        String movedCoupon = Web3.decodeReturnValue("address[]",unusedCoupons).get(0);
        System.out.println("====**==== "+movedCoupon.equals(coupon3ContractAddress));
    }

    public static void grant(){
        String funcName = "Merchant.grant";
        String[] content = {consumer1ContractAddress,"1","2017-04-03","111"};
        String txHash = Web3.sendTransactionMap(funcName, content, merchantAccount, merchantContractAddress);
        String finish = "null";
        String[] content1 = {txHash};
        while(finish.equals("null")){
            finish = Web3.universalCall("eth_getTransactionReceipt",null, content1, null);
        }
        String unusedCoupons = Web3.getValueMap("Merchant.getUnusedCoupons",null, merchantAccount, merchantContractAddress);
        String movedCoupon = Web3.decodeReturnValue("address[]",unusedCoupons).get(0);
        System.out.println("====**==== "+movedCoupon.equals(coupon3ContractAddress));

        String notGivenCoupons = Web3.getValueMap("Merchant.getNotGivenCoupons",null, merchantAccount, merchantContractAddress);
        Integer notMovedCouponAmount = Web3.decodeReturnValue("address[]",notGivenCoupons).size();
        System.out.println("====**==== "+notMovedCouponAmount+"");

        String obtainedCoupons = Web3.getValueMap("Consumer.getCoupons",null, consumerAccount1, consumer1ContractAddress);
        String movedCouponAddress = Web3.decodeReturnValue("address[]",obtainedCoupons).get(0);
        System.out.println("====**==== "+movedCouponAddress.equals(coupon3ContractAddress));
    }

    public static void issueCoupon(){
        String funcName = "Merchant.issueCoupon";
        String[] content = {"10","100","3","2017-04-02","2017-04-03"};
        String txHash = Web3.sendTransactionMap(funcName, content, merchantAccount, merchantContractAddress);
        String finish = "null";
        String[] content1 = {txHash};
        while(finish.equals("null")){
            finish = Web3.universalCall("eth_getTransactionReceipt",null, content1, null);
        }
        String issues = Web3.getValueMap("Merchant.getNotGivenCoupons",null, merchantAccount, merchantContractAddress);
        List<String> coupons = Web3.decodeReturnValue("address[]",issues);
        for(String c:coupons){
            System.out.println("===**==="+" "+c);
        }
        String settlementResult = Web3.getValueMap("Merchant.getSettlementBalance",null, merchantAccount, merchantContractAddress);
        Integer settlementBalance = Integer.parseInt(Web3.decodeReturnValue("uint",settlementResult).get(0));
        System.out.println("===**==="+" "+settlementBalance+"");
    }

    public static void approveSettlement(){
        String funcName = "Bank.approve";
        String[] content = {"1234567",merchantContractAddress,"1000"};
        String txHash = Web3.sendTransactionMap(funcName, content, bankAccount, bankContractAddress);
        String finish = "null";
        String[] content1 = {txHash};
        while(finish.equals("null")){
            finish = Web3.universalCall("eth_getTransactionReceipt", null, content1, null);
        }
        String settlementResult = Web3.getValueMap("Merchant.getSettlementBalance",null, merchantAccount, merchantContractAddress);
        Integer settlementBalance = Integer.parseInt(Web3.decodeReturnValue("uint",settlementResult).get(0));
        System.out.println("===**==="+" "+settlementBalance+"");
    }
    public static void deployConsumerContract(){
        String functionName = "eth_compileSolidity";
        Boolean flag = null;
        String object = null;
        String[] content={"pragma solidity^0.4.10;contract Bank{address owner;address[]merchants;address firstChecker;address secondChecker;function Bank(){owner=msg.sender;firstChecker=0x123;secondChecker=0x456;}modifier OnlyOwner{if(msg.sender!=owner){throw;}_;}function createMerchant(address merchantAccount)OnlyOwner{merchants.push(new Merchant(merchantAccount));}function approve(bytes signature,address merchantAddress,uint amount)OnlyOwner{Merchant m=Merchant(merchantAddress);m.settlementApprove(amount);}function approveWithdraw(address merchantAddress)OnlyOwner{Merchant m=Merchant(merchantAddress);m.settlementWithdraw();}function getMerchants()constant returns(address[]){return merchants;}function getCorrespondingMerchant(address merchantAccount)constant returns(address){uint i=merchants.length;for(i=merchants.length-1;i>=0;i--){Merchant m=Merchant(merchants[i]);if(merchantAccount==m.getOwner()){break;}}return merchants[i];}}contract Merchant{address owner;address banker;uint settlementBalance;address[]unusedCoupons;address[]usedCoupons;address[]notGivenCoupons;address[]historyCoupons;address[]curGrant;mapping(bytes32=>address[])grantPair;modifier OnlyBanker{if(banker!=msg.sender){throw;}_;}modifier OnlyOwner{if(owner!=msg.sender){throw;}_;}function Merchant(address merchantAccount){owner=merchantAccount;banker=msg.sender;}function settlementApprove(uint amount)OnlyBanker{settlementBalance+=amount;}function settlementWithdraw()OnlyBanker{settlementBalance=0;}function getSettlementBalance()constant returns(uint){return settlementBalance;}function issueCoupon(uint value,uint limit,uint quantity,bytes32 startDate,bytes32 endDate)OnlyOwner{if(settlementBalance>=(value*quantity)){for(uint i=0;i<quantity;i++){notGivenCoupons.push(new Coupon(value,limit,startDate,endDate,owner));settlementBalance-=value;}}}function getNotGivenCoupons()constant returns(address[]){return notGivenCoupons;}function terminateCoupon()OnlyOwner{for(uint k=0;k<notGivenCoupons.length;k++){historyCoupons.push(notGivenCoupons[k]);}delete notGivenCoupons;for(uint i=0;i<unusedCoupons.length;i++){historyCoupons.push(notGivenCoupons[i]);Coupon c=Coupon(notGivenCoupons[i]);settlementBalance+=c.getValue();}delete unusedCoupons;for(uint j=0;j<usedCoupons.length;j++){historyCoupons.push(usedCoupons[j]);}delete usedCoupons;}function getUnusedCoupons()constant returns(address[]){return unusedCoupons;}function getUsedCoupons()constant returns(address[]){return usedCoupons;}function getHistoryCoupons()constant returns(address[]){return historyCoupons;}function grant(address _consumer,uint quantity,bytes32 date,bytes32 mark)OnlyOwner{if(quantity<=notGivenCoupons.length){Consumer consumer=Consumer(_consumer);for(uint i=notGivenCoupons.length-1;i>=notGivenCoupons.length-quantity;i--){Coupon couponTemp=Coupon(notGivenCoupons[i]);couponTemp.setObtainDate(date);couponTemp.setState(2);couponTemp.setOwner(_consumer);consumer.addCoupon(notGivenCoupons[i]);unusedCoupons.push(notGivenCoupons[i]);curGrant.push(notGivenCoupons[i]);}grantPair[mark]=curGrant;delete curGrant;notGivenCoupons.length=notGivenCoupons.length-quantity;}}function getCorrespondingGrant(bytes32 mark)constant returns(address[]){return grantPair[mark];}function confirmCouponPay(uint consumeValue,bytes32 consumeDate,address couponAddr,address _consumer)OnlyOwner{Coupon coupon=Coupon(couponAddr);if(consumeValue>=coupon.getLimit()){coupon.setConsumeValue(consumeValue);coupon.setConsumeDate(consumeDate);coupon.setState(3);Consumer consumer=Consumer(_consumer);consumer.couponPay(couponAddr);uint i=unusedCoupons.length;for(i=0;i<unusedCoupons.length;i++){if(unusedCoupons[i]==couponAddr){break;}}if(i!=unusedCoupons.length){for(uint j=i;j<unusedCoupons.length-1;j++){unusedCoupons[j]=unusedCoupons[j+1];}unusedCoupons.length-=1;usedCoupons.push(couponAddr);settlementBalance+=coupon.getValue();}else{settlementBalance+=coupon.getValue();}}}function getOwner()constant returns(address){return owner;}}contract Consumer{address owner;address banker;uint state;address[]coupons;function Consumer(address bankAccount){owner=msg.sender;banker=bankAccount;state=1;}modifier OnlyBanker{if(msg.sender!=banker){throw;}_;}function freezeConsumer()OnlyBanker{state=0;}function thawConsumer()OnlyBanker{state=1;}function addCoupon(address _coupon){coupons.push(_coupon);}function couponPay(address couponAddr){uint i=0;for(;i<coupons.length;i++){if(coupons[i]==couponAddr){break;}}for(uint j=i;j<coupons.length-1;j++){coupons[j]=coupons[j+1];}coupons.length-=1;}function transfer(address newConsumer,address _coupon){Coupon coupon=Coupon(_coupon);coupon.setOwner(newConsumer);Consumer to=Consumer(newConsumer);to.addCoupon(_coupon);uint i=0;for(;i<coupons.length;i++){if(coupons[i]==_coupon){break;}}for(uint j=i;j<coupons.length-1;j++){coupons[j]=coupons[j+1];}coupons.length-=1;}function getCoupons()constant returns(address[]){return coupons;}}contract Coupon{address owner;uint value;uint limit;bytes32 startDate;bytes32 endDate;bytes32 obtainDate;bytes32 consumeDate;uint consumeValue;uint state;function Coupon(uint _value,uint _limit,bytes32 _startDate,bytes32 _endDate,address _owner){value=_value;limit=_limit;owner=_owner;startDate=_startDate;endDate=_endDate;consumeValue=0;state=1;}function setOwner(address addr){owner=addr;}function getOwner()constant returns(address){return owner;}function setObtainDate(bytes32 date){obtainDate=date;}function setState(uint _state){state=_state;}function setConsumeValue(uint _value){consumeValue=_value;}function setConsumeDate(bytes32 _date){consumeDate=_date;}function getLimit()returns(uint){return limit;}function getValue()constant returns(uint){return value;}}"};
        String result=Web3.universalCall(functionName, object, content, flag);
        JSONObject js=JSONObject.fromObject(result);
        JSONObject multi=(JSONObject)js.get("<stdin>:Consumer");
        String code=multi.getString("code");
        String functionName2 = "eth_sendTransaction";
        Boolean flag2 = null;
        String object2 = "{\"from\": \""+consumerAccount2+"\", \"gas\": \""+"0x470000"+"\", \"data\": \""+code+"\",\"arg0\":\"" + bankAccount + "\"}";
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
        System.out.println("===========");
        System.out.println(contractAddress);
        System.out.println("===========");
    }

    public static void deployMerchantContract(){
        String functionName = "Bank.createMerchant";
        String[] content = {merchantAccount};
        String txHash = Web3.sendTransactionMap(functionName, content, bankAccount, bankContractAddress);
        String[] content1 = {txHash};
        String jsonResult = "null";
        while(jsonResult.equals("null")) {
            jsonResult = Web3.universalCall("eth_getTransactionReceipt", null, content1, null);
        }

        String funcName = "Bank.getMerchants";
        String result = Web3.getValueMap(funcName, null, bankAccount, bankContractAddress);
        List<String> addresses = Web3.decodeReturnValue("address[]", result);
        System.out.println("&&&&&&"+addresses.get(addresses.size()-1));
    }

    public static void deployBankContract(){
        String functionName = "eth_compileSolidity";
        Boolean flag = null;
        String object = null;

        String[] content={"pragma solidity^0.4.10;contract Bank{address owner;address[]merchants;address firstChecker;address secondChecker;function Bank(){owner=msg.sender;firstChecker=0x123;secondChecker=0x456;}modifier OnlyOwner{if(msg.sender!=owner){throw;}_;}function createMerchant(address merchantAccount)OnlyOwner{merchants.push(new Merchant(merchantAccount));}function approve(bytes signature,address merchantAddress,uint amount)OnlyOwner{Merchant m=Merchant(merchantAddress);m.settlementApprove(amount);}function approveWithdraw(address merchantAddress)OnlyOwner{Merchant m=Merchant(merchantAddress);m.settlementWithdraw();}function getMerchants()constant returns(address[]){return merchants;}function getCorrespondingMerchant(address merchantAccount)constant returns(address){uint i=merchants.length;for(i=merchants.length-1;i>=0;i--){Merchant m=Merchant(merchants[i]);if(merchantAccount==m.getOwner()){break;}}return merchants[i];}}contract Merchant{address owner;address banker;uint settlementBalance;address[]unusedCoupons;address[]usedCoupons;address[]notGivenCoupons;address[]historyCoupons;address[]curGrant;mapping(bytes32=>address[])grantPair;modifier OnlyBanker{if(banker!=msg.sender){throw;}_;}modifier OnlyOwner{if(owner!=msg.sender){throw;}_;}function Merchant(address merchantAccount){owner=merchantAccount;banker=msg.sender;}function settlementApprove(uint amount)OnlyBanker{settlementBalance+=amount;}function settlementWithdraw()OnlyBanker{settlementBalance=0;}function getSettlementBalance()constant returns(uint){return settlementBalance;}function issueCoupon(uint value,uint limit,uint quantity,bytes32 startDate,bytes32 endDate)OnlyOwner{if(settlementBalance>=(value*quantity)){for(uint i=0;i<quantity;i++){notGivenCoupons.push(new Coupon(value,limit,startDate,endDate,owner));settlementBalance-=value;}}}function getNotGivenCoupons()constant returns(address[]){return notGivenCoupons;}function terminateCoupon()OnlyOwner{for(uint k=0;k<notGivenCoupons.length;k++){historyCoupons.push(notGivenCoupons[k]);}delete notGivenCoupons;for(uint i=0;i<unusedCoupons.length;i++){historyCoupons.push(notGivenCoupons[i]);Coupon c=Coupon(notGivenCoupons[i]);settlementBalance+=c.getValue();}delete unusedCoupons;for(uint j=0;j<usedCoupons.length;j++){historyCoupons.push(usedCoupons[j]);}delete usedCoupons;}function getUnusedCoupons()constant returns(address[]){return unusedCoupons;}function getUsedCoupons()constant returns(address[]){return usedCoupons;}function getHistoryCoupons()constant returns(address[]){return historyCoupons;}function grant(address _consumer,uint quantity,bytes32 date,bytes32 mark)OnlyOwner{if(quantity<=notGivenCoupons.length){Consumer consumer=Consumer(_consumer);for(uint i=notGivenCoupons.length-1;i>=notGivenCoupons.length-quantity;i--){Coupon couponTemp=Coupon(notGivenCoupons[i]);couponTemp.setObtainDate(date);couponTemp.setState(2);couponTemp.setOwner(_consumer);consumer.addCoupon(notGivenCoupons[i]);unusedCoupons.push(notGivenCoupons[i]);curGrant.push(notGivenCoupons[i]);}grantPair[mark]=curGrant;delete curGrant;notGivenCoupons.length=notGivenCoupons.length-quantity;}}function getCorrespondingGrant(bytes32 mark)constant returns(address[]){return grantPair[mark];}function confirmCouponPay(uint consumeValue,bytes32 consumeDate,address couponAddr,address _consumer)OnlyOwner{Coupon coupon=Coupon(couponAddr);if(consumeValue>=coupon.getLimit()){coupon.setConsumeValue(consumeValue);coupon.setConsumeDate(consumeDate);coupon.setState(3);Consumer consumer=Consumer(_consumer);consumer.couponPay(couponAddr);uint i=unusedCoupons.length;for(i=0;i<unusedCoupons.length;i++){if(unusedCoupons[i]==couponAddr){break;}}if(i!=unusedCoupons.length){for(uint j=i;j<unusedCoupons.length-1;j++){unusedCoupons[j]=unusedCoupons[j+1];}unusedCoupons.length-=1;usedCoupons.push(couponAddr);settlementBalance+=coupon.getValue();}else{settlementBalance+=coupon.getValue();}}}function getOwner()constant returns(address){return owner;}}contract Consumer{address owner;address banker;uint state;address[]coupons;function Consumer(address bankAccount){owner=msg.sender;banker=bankAccount;state=1;}modifier OnlyBanker{if(msg.sender!=banker){throw;}_;}function freezeConsumer()OnlyBanker{state=0;}function thawConsumer()OnlyBanker{state=1;}function addCoupon(address _coupon){coupons.push(_coupon);}function couponPay(address couponAddr){uint i=0;for(;i<coupons.length;i++){if(coupons[i]==couponAddr){break;}}for(uint j=i;j<coupons.length-1;j++){coupons[j]=coupons[j+1];}coupons.length-=1;}function transfer(address newConsumer,address _coupon){Coupon coupon=Coupon(_coupon);coupon.setOwner(newConsumer);Consumer to=Consumer(newConsumer);to.addCoupon(_coupon);uint i=0;for(;i<coupons.length;i++){if(coupons[i]==_coupon){break;}}for(uint j=i;j<coupons.length-1;j++){coupons[j]=coupons[j+1];}coupons.length-=1;}function getCoupons()constant returns(address[]){return coupons;}}contract Coupon{address owner;uint value;uint limit;bytes32 startDate;bytes32 endDate;bytes32 obtainDate;bytes32 consumeDate;uint consumeValue;uint state;function Coupon(uint _value,uint _limit,bytes32 _startDate,bytes32 _endDate,address _owner){value=_value;limit=_limit;owner=_owner;startDate=_startDate;endDate=_endDate;consumeValue=0;state=1;}function setOwner(address addr){owner=addr;}function getOwner()constant returns(address){return owner;}function setObtainDate(bytes32 date){obtainDate=date;}function setState(uint _state){state=_state;}function setConsumeValue(uint _value){consumeValue=_value;}function setConsumeDate(bytes32 _date){consumeDate=_date;}function getLimit()returns(uint){return limit;}function getValue()constant returns(uint){return value;}}"};
        String result=Web3.universalCall(functionName, object, content, flag);

        JSONObject js=JSONObject.fromObject(result);
        JSONObject multi=(JSONObject)js.get("<stdin>:Bank");
        String code=multi.getString("code");

        String functionName2 = "eth_sendTransaction";
        Boolean flag2 = null;
        String object2 = "{\"from\": \""+bankAccount+"\", \"gas\": \""+"0x470000"+"\", \"data\": \""+code+"\"}";
        String[] content2 = null;
        String txHash=Web3.universalCall(functionName2, object2, content2, flag2);
        System.out.println(txHash);

        System.out.println(txHash);
        String functionName3 = "eth_getTransactionReceipt";
        String[] content3 = {txHash};
        String finish="null";
        while(finish.equals("null")){
            finish = Web3.universalCall(functionName3, null, content3, null);
        }
        JSONObject tsxInfo=JSONObject.fromObject(finish);
        String contractAddress=tsxInfo.getString("contractAddress");
        System.out.println("===========");
        System.out.println(contractAddress);
        System.out.println("===========");
    }
}
