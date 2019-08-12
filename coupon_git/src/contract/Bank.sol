pragma solidity >=0.4.22 <0.6.0;
contract Bank {
    address owner;
    address[] merchants;
    constructor() public {
        owner = msg.sender;
    }
    modifier OnlyOwner {
        require(
            msg.sender == owner,
            "Only owner can call this function."
        );
        _;
    }
    function createMerchant(address merchantAccount) public OnlyOwner {
        merchants.push(address(new Merchant(merchantAccount)));
    }
    function approve(address merchantAddress, uint amount) public OnlyOwner {
        Merchant m = Merchant(merchantAddress);
        m.settlementApprove(amount);
    }
    function approveWithdraw(address merchantAddress) public OnlyOwner {
        Merchant m = Merchant(merchantAddress);
        m.settlementWithdraw();
    }
    function getMerchants() public view returns(address[] memory) {
        return merchants;
    }
    function getCorrespondingMerchant(address merchantAccount) public view returns(address) {
        uint i = merchants.length;
        for (i = merchants.length - 1; i >= 0; i--) {
            Merchant m = Merchant(merchants[i]);
            if (merchantAccount == m.getOwner()) {
                break;
            }
        }
        return merchants[i];
    }
}
contract Merchant {
    address owner;
    address banker;
    uint settlementBalance;
    address[] unusedCoupons;
    address[] usedCoupons;
    address[] notGivenCoupons;
    address[] historyCoupons;
    address[] curGrant;
    mapping(bytes32 => address[]) grantPair;
    modifier OnlyBanker {
        require(
            msg.sender == banker,
            "Only Banker can call this function."
        );
        _;
    }
    modifier OnlyOwner {
        require(
            msg.sender == owner,
            "Only owner can call this function."
        );
        _;
    }
    constructor(address merchantAccount) public {
        owner = merchantAccount;
        banker = msg.sender;
    }
    function settlementApprove(uint amount) public OnlyBanker {
        settlementBalance += amount;
    }
    function settlementWithdraw() public OnlyBanker {
        settlementBalance = 0;
    }
    function getSettlementBalance() public view returns(uint) {
        return settlementBalance;
    }
    function issueCoupon(uint value, uint limit, uint quantity, bytes32 startDate, bytes32 endDate) public OnlyOwner {
        if (settlementBalance >= (value * quantity)) {
            for (uint i = 0; i < quantity; i++) {
                notGivenCoupons.push(address(new Coupon(value, limit, startDate, endDate)));
                settlementBalance -= value;
            }
        }
    }
    function getNotGivenCoupons() public view returns(address[] memory) {
        return notGivenCoupons;
    }
    function terminateCoupon(bytes32 curDate) public OnlyOwner {
        for (uint k = 0; k < notGivenCoupons.length; k++) {
            historyCoupons.push(notGivenCoupons[k]);
            Coupon c = Coupon(notGivenCoupons[k]);
            settlementBalance += c.getValue();
        }
        delete notGivenCoupons;
        for (uint i = 0; i < unusedCoupons.length; i++) {
            Coupon c1 = Coupon(unusedCoupons[i]);
            if (c1.getEndDate() < curDate) {
                settlementBalance += c1.getValue();
            }
            historyCoupons.push(unusedCoupons[i]);
        }
        delete unusedCoupons;
        for (uint j = 0; j < usedCoupons.length; j++) {
            historyCoupons.push(usedCoupons[j]);
        }
        delete usedCoupons;
    }
    function getUnusedCoupons() public view returns(address[] memory) {
        return unusedCoupons;
    }
    function getUsedCoupons() public view returns(address[] memory) {
        return usedCoupons;
    }
    function getHistoryCoupons() public view returns(address[] memory) {
        return historyCoupons;
    }
    function grant(address _consumer, uint quantity, bytes32 date, bytes32 mark, uint obtainValue) public OnlyOwner {
        if (quantity <= notGivenCoupons.length) {
            Consumer consumer = Consumer(_consumer);
            for (uint i = notGivenCoupons.length - 1; i >= notGivenCoupons.length - quantity; i--) {
                Coupon couponTemp = Coupon(notGivenCoupons[i]);
                couponTemp.setObtainDate(date);
                couponTemp.setState(2);
                couponTemp.setObtainValue(obtainValue);
                couponTemp.setGranter(couponTemp.getOwner());
                couponTemp.setOwner(_consumer);
                consumer.addCoupon(notGivenCoupons[i]);
                unusedCoupons.push(notGivenCoupons[i]);
                curGrant.push(notGivenCoupons[i]);
            }
            grantPair[mark] = curGrant;
            delete curGrant;
            notGivenCoupons.length = notGivenCoupons.length - quantity;
        }
    }
    function getCorrespondingGrant(bytes32 mark) public view returns(address[] memory) {
        return grantPair[mark];
    }
    function confirmCouponPay(uint consumeValue, bytes32 consumeDate, address couponAddr, address _consumer) public OnlyOwner {
        Coupon coupon = Coupon(couponAddr);
        if (consumeValue >= coupon.getLimit()) {
            coupon.setConsumeValue(consumeValue);
            coupon.setConsumeDate(consumeDate);
            coupon.setState(3);
            Consumer consumer = Consumer(_consumer);
            consumer.couponPay(couponAddr);
            uint i = unusedCoupons.length;
            for (i = 0; i < unusedCoupons.length; i++) {
                if (unusedCoupons[i] == couponAddr) {
                    break;
                }
            }
            if (i != unusedCoupons.length) {
                for (uint j = i; j < unusedCoupons.length - 1; j++) {
                    unusedCoupons[j] = unusedCoupons[j + 1];
                }
                unusedCoupons.length -= 1;
                usedCoupons.push(couponAddr);
                settlementBalance += coupon.getValue();
            } else {
                settlementBalance += coupon.getValue();
                Merchant m = Merchant(coupon.getGranter());
                if (m.getOwner() != owner) {
                    m.addToUsedCoupons(couponAddr);
                }
            }
        }
    }
    function getOwner() public view returns(address) {
        return owner;
    }
    function addToUsedCoupons(address couponAddr) public {
        usedCoupons.push(couponAddr);
    }
}
contract Consumer {
    address owner;
    address banker;
    uint state;
    address[] coupons;
    constructor(address bankAccount) public {
        owner = msg.sender;
        banker = bankAccount;
        state = 1;
    }
    modifier OnlyBanker {
        require(
            msg.sender == banker,
            "Only Banker can call this function."
        );
        _;
    }
    function freezeConsumer() public OnlyBanker {
        state = 0;
    }
    function thawConsumer() public OnlyBanker {
        state = 1;
    }
    function addCoupon(address _coupon) public {
        coupons.push(_coupon);
    }
    function couponPay(address couponAddr) public {
        uint i = 0;
        for (; i < coupons.length; i++) {
            if (coupons[i] == couponAddr) {
                break;
            }
        }
        for (uint j = i; j < coupons.length - 1; j++) {
            coupons[j] = coupons[j + 1];
        }
        coupons.length -= 1;
    }
    function transfer(address newConsumer, address _coupon) public {
        Coupon coupon = Coupon(_coupon);
        coupon.setOwner(newConsumer);
        Consumer to = Consumer(newConsumer);
        to.addCoupon(_coupon);
        uint i = 0;
        for (; i < coupons.length; i++) {
            if (coupons[i] == _coupon) {
                break;
            }
        }
        for (uint j = i; j < coupons.length - 1; j++) {
            coupons[j] = coupons[j + 1];
        }
        coupons.length -= 1;
    }
    function getCoupons() public view returns(address[] memory) {
        return coupons;
    }
}
contract Coupon {
    address owner;
    address granter;
    uint value;
    uint limit;
    bytes32 startDate;
    bytes32 endDate;
    uint obtainValue;
    bytes32 obtainDate;
    bytes32 consumeDate;
    uint consumeValue;
    uint state = 1;
    constructor(uint _value, uint _limit, bytes32 _startDate, bytes32 _endDate) public {
        value = _value;
        limit = _limit;
        owner = msg.sender;
        startDate = _startDate;
        endDate = _endDate;
        consumeValue = 0;
        state = 1;
    }
    function setOwner(address addr) public {
        owner = addr;
    }
    function getOwner() public view returns(address) {
        return owner;
    }
    function setObtainDate(bytes32 date) public {
        obtainDate = date;
    }
    function setState(uint _state) public {
        state = _state;
    }
    function setConsumeValue(uint _value) public {
        consumeValue = _value;
    }
    function setConsumeDate(bytes32 _date) public {
        consumeDate = _date;
    }
    function getLimit() public view returns(uint) {
        return limit;
    }
    function getValue() public view returns(uint) {
        return value;
    }
    function getEndDate() public view returns(bytes32) {
        return endDate;
    }
    function getStartDate() public view returns(bytes32) {
        return startDate;
    }
    function getGranter() public view returns(address) {
        return granter;
    }
    function setGranter(address _granter) public {
        granter = _granter;
    }
    function getObtainValue() public view returns(uint) {
        return obtainValue;
    }
    function setObtainValue(uint _obtainValue) public {
        obtainValue = _obtainValue;
    }
    function getState() public view returns(uint) {
        return state;
    }
    function getObtainDate() public view returns(bytes32) {
        return obtainDate;
    }
    function getConsumeValue() public view returns(uint) {
        return consumeValue;
    }
    function getConsumeDate() public view returns(bytes32) {
        return consumeDate;
    }
}