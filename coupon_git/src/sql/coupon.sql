CREATE DATABASE  IF NOT EXISTS `coupon` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `coupon`;
-- MySQL dump 10.13  Distrib 5.7.9, for osx10.9 (x86_64)
--
-- Host: 127.0.0.1    Database: 
-- ------------------------------------------------------
-- Server version	5.7.10

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bank_staff`
--

DROP TABLE IF EXISTS `bank_staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank_staff` (
  `id` varchar(40) NOT NULL,
  `account` varchar(11) NOT NULL,
  `password` varchar(70) NOT NULL,
  `salt` varchar(20) NOT NULL,
  `publicKey` varchar(50) DEFAULT NULL,
  `position` varchar(2) NOT NULL COMMENT '1:操作员\n2:复审员',
  `securityCode` varchar(70) NOT NULL,
  `online` varchar(2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `account_UNIQUE` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `consumer`
--

DROP TABLE IF EXISTS `consumer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consumer` (
  `id` varchar(40) NOT NULL,
  `account` varchar(11) NOT NULL,
  `password` varchar(70) NOT NULL,
  `salt` varchar(20) NOT NULL,
  `publicKey` varchar(50) NOT NULL,
  `contractAddress` varchar(50) NOT NULL,
  `token` varchar(70) DEFAULT NULL,
  `isFrozen` varchar(2) NOT NULL DEFAULT '0',
  `mark` varchar(2) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `account_UNIQUE` (`account`),
  UNIQUE KEY `contractAddress_UNIQUE` (`contractAddress`),
  UNIQUE KEY `publicKey_UNIQUE` (`publicKey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `coupon`
--

DROP TABLE IF EXISTS `coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `coupon` (
  `id` varchar(50) NOT NULL,
  `rulerId` varchar(50) NOT NULL,
  `contractAddress` varchar(50) NOT NULL,
  `ownerId` varchar(50) NOT NULL,
  `couponValue` int(11) NOT NULL,
  `consumptionDate` varchar(20) DEFAULT NULL,
  `consumptionValue` varchar(10) DEFAULT NULL,
  `markCode` varchar(50) DEFAULT NULL,
  `status` varchar(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `contractAddress_UNIQUE` (`contractAddress`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `coupon_application`
--

DROP TABLE IF EXISTS `coupon_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `coupon_application` (
  `id` varchar(40) NOT NULL,
  `merchantId` varchar(40) NOT NULL,
  `consumerId` varchar(40) NOT NULL,
  `consumptionTime` varchar(30) DEFAULT NULL,
  `consumptionValue` varchar(10) DEFAULT NULL,
  `couponValue` int(11) DEFAULT NULL,
  `status` varchar(2) NOT NULL DEFAULT '2',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `coupon_pay_application`
--

DROP TABLE IF EXISTS `coupon_pay_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `coupon_pay_application` (
  `id` varchar(40) NOT NULL,
  `merchantId` varchar(40) NOT NULL,
  `consumerId` varchar(40) NOT NULL,
  `couponId` varchar(40) NOT NULL,
  `applicationTime` varchar(30) NOT NULL,
  `applicationCode` varchar(40) NOT NULL,
  `status` varchar(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `coupon_ruler`
--

DROP TABLE IF EXISTS `coupon_ruler`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `coupon_ruler` (
  `id` varchar(40) NOT NULL,
  `merchantId` varchar(40) NOT NULL,
  `reach` int(11) NOT NULL,
  `give` int(11) NOT NULL,
  `isAccumulation` varchar(2) NOT NULL,
  `capping` int(11) NOT NULL,
  `totalAmount` int(11) NOT NULL,
  `issueDate` varchar(20) NOT NULL,
  `validStartDate` varchar(20) NOT NULL,
  `validEndDate` varchar(20) NOT NULL,
  `status` varchar(2) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchant`
--

DROP TABLE IF EXISTS `merchant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchant` (
  `id` varchar(40) NOT NULL,
  `account` varchar(11) NOT NULL,
  `password` varchar(70) NOT NULL,
  `salt` varchar(20) NOT NULL,
  `token` varchar(70) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `address` varchar(50) NOT NULL,
  `licence` varchar(13) NOT NULL,
  `legalEntityName` varchar(40) NOT NULL,
  `businessScope` varchar(50) NOT NULL,
  `publicKey` varchar(50) NOT NULL,
  `settlementBalance` int(11) NOT NULL DEFAULT '0',
  `contractAddress` varchar(50) NOT NULL,
  `conCouponRulerId` varchar(40) DEFAULT NULL,
  `status` varchar(2) NOT NULL DEFAULT '0',
  `accountBalance` int(11) NOT NULL DEFAULT '0',
  `description` varchar(70) DEFAULT NULL,
  `latitude` varchar(50) NOT NULL,
  `longitude` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `account_UNIQUE` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchant_register`
--

-- DROP TABLE IF EXISTS `merchant_register`;
-- /*!40101 SET @saved_cs_client     = @@character_set_client */;
-- /*!40101 SET character_set_client = utf8 */;
-- CREATE TABLE `merchant_register` (
--   `id` varchar(40) NOT NULL,
--   `account` varchar(11) NOT NULL,
--   `password` varchar(70) NOT NULL,
--   `salt` varchar(20) NOT NULL,
--   `name` varchar(50) NOT NULL,
--   `address` varchar(50) NOT NULL,
--   `licence` varchar(13) NOT NULL,
--   `legalEntityName` varchar(40) NOT NULL,
--   `businessScope` varchar(50) NOT NULL,
--   `status` varchar(2) NOT NULL DEFAULT '0',
--   PRIMARY KEY (`id`),
--   UNIQUE KEY `id_UNIQUE` (`id`),
--   UNIQUE KEY `account_UNIQUE` (`account`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- /*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `settlement_operation`
--

DROP TABLE IF EXISTS `settlement_operation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settlement_operation` (
  `id` varchar(40) NOT NULL,
  `merchantId` varchar(40) NOT NULL,
  `operationAmount` int(11) NOT NULL,
  `operationDate` varchar(20) NOT NULL,
  `operationType` varchar(2) NOT NULL,
  `operatorId` varchar(40) DEFAULT NULL,
  `operationTime` varchar(30) DEFAULT NULL,
  `operatorOpinion` varchar(1000) DEFAULT NULL,
  `checkStatus` varchar(2) NOT NULL DEFAULT '1',
  `firstEncryptStr` varchar(100) DEFAULT NULL,
  `recheckId` varchar(40) DEFAULT NULL,
  `recheckTime` varchar(30) DEFAULT NULL,
  `recheckOpinion` varchar(1000) DEFAULT NULL,
  `recheckStatus` varchar(2) NOT NULL DEFAULT '1',
  `secondEncryptStr` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-09-30 10:13:44
