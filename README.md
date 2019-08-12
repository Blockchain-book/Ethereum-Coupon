# Ethereum-Coupon
基于以太坊的通用电子优惠券系统

# 文件目录
+ `coupon_cunstomer_part` 是客户的app源码
+ `coupin_merchant_part`  是商户的app源码
+ `coupon_git/src/main` 是后端源码
+ `coupon_git/src/sql` 是数据库
+ `coupon_git/src/contract` 是合约代码 

# 版本说明
+ Android SDK 版本为 AndroidX
+ EVM 版本选择 homestead `（在Remix上编译的时候注意修改）`
+ Solidity版本为 0.5.1

# 文件说明
+ `coupon_git/src/main/resources/account.properties` 配置区块链账户地址
+ `coupon_git/src/main/resources/db.properties` 配置mysql数据库信息
+ `coupon_git/src/main.java.com.block.coupon/rpc/WebRecourceObj.java` 配置区块链ip地址
+ `coupon_customer_part/app/src/main.java.com.example.babara.coupon_customer_part/utils/UrlManager.java` 客户app配置服务器ip地址
+ `coupon_merchant_part/app/src/main.java.com.creation.coupon_customer_part/pojo/UrlManager.java` 商户app配置服务器ip地址