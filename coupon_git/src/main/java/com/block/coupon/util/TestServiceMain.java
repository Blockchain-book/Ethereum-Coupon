package com.block.coupon.util;

import com.block.coupon.po.Location;
import com.block.coupon.po.NearbyMerchantDist;
import com.block.coupon.po.NearbyMerchantItem;
import com.block.coupon.service.ConsumerService;
import com.block.coupon.service.MerchantService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 2017/3/28.
 */
public class TestServiceMain {
    public static void main(String[] args){
//        List<NearbyMerchantDist> merchantDists = new ArrayList<NearbyMerchantDist>();
//        setMockData(merchantDists);
//        Location location = new Location();
//        location.setLongitude("30.0");
//        location.setLatitude("20.0");
        ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:/spring/applicationContext.xml");
        ConsumerService consumerService = (ConsumerService)ac.getBean("consumerService");
        try {
            consumerService.CalculateCouponSummary("a578fcd-4783-4b07-a8b6-572b310c4958");
        }catch(Exception e){
            e.printStackTrace();
        }
        //        MerchantService merchantService = (MerchantService)ac.getBean("merchantService");
//        try {
//            List<NearbyMerchantItem> result = merchantService.calculateNearbyThree(merchantDists, location);
//            for (int i = 0; i < result.size(); i++) {
//                System.out.println(result.get(i).getMerchantId() + " " + result.get(i).getDistance());
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
    }
    public static void setMockData(List<NearbyMerchantDist> list){
        NearbyMerchantDist nmd1 = new NearbyMerchantDist();
        nmd1.setMerchantName("test1");
        nmd1.setMerchantInfo("test1");
        nmd1.setMerchantId("1");
        nmd1.setLatitude("2.0");
        nmd1.setLongitude("3.0");
        list.add(nmd1);

        NearbyMerchantDist nmd2 = new NearbyMerchantDist();
        nmd2.setMerchantName("test2");
        nmd2.setMerchantInfo("test2");
        nmd2.setMerchantId("2");
        nmd2.setLatitude("5.0");
        nmd2.setLongitude("10.0");
        list.add(nmd2);

        NearbyMerchantDist nmd3 = new NearbyMerchantDist();
        nmd3.setMerchantName("test3");
        nmd3.setMerchantInfo("test3");
        nmd3.setMerchantId("3");
        nmd3.setLatitude("10.0");
        nmd3.setLongitude("30.0");
        list.add(nmd3);

        NearbyMerchantDist nmd4 = new NearbyMerchantDist();
        nmd4.setMerchantName("test4");
        nmd4.setMerchantInfo("test4");
        nmd4.setMerchantId("4");
        nmd4.setLatitude("20.0");
        nmd4.setLongitude("30.0");
        list.add(nmd4);

        NearbyMerchantDist nmd5 = new NearbyMerchantDist();
        nmd5.setMerchantName("test5");
        nmd5.setMerchantInfo("test5");
        nmd5.setMerchantId("5");
        nmd5.setLatitude("25.0");
        nmd5.setLongitude("40.0");
        list.add(nmd5);

        NearbyMerchantDist nmd6 = new NearbyMerchantDist();
        nmd6.setMerchantName("test6");
        nmd6.setMerchantInfo("test6");
        nmd6.setMerchantId("6");
        nmd6.setLatitude("30.0");
        nmd6.setLongitude("60.0");
        list.add(nmd6);

    }
}
