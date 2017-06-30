import com.block.coupon.controller.MerchantController;
import com.block.coupon.mapper.MerchantMapper;
import com.block.coupon.mapper.SettlementOperationMapper;
import com.block.coupon.po.QuerySettlementCustom;
import com.block.coupon.po.SettlementOperation;
import com.block.coupon.po.SettlementRecord;
import com.block.coupon.service.MerchantService;
import com.block.coupon.util.JSONUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Created by admin on 2017/2/28.
 */
public class FundAccountTest extends SpringTest{
    /**
     * 资金账户测试*
     **/
    private SettlementOperation settlementOperation;
    private SettlementRecord settlementRecord;
    private QuerySettlementCustom querySettlementCustom;
    private QuerySettlementCustom qsc;
    @Autowired
    private MerchantService merchantService;
    private MerchantMapper merchantMapper;
    private SettlementOperationMapper settlementOperationMapper;
    private String merchantId="1";
    @Before
    public void setup(){
        super.setup();
        ApplicationContext ac = new FileSystemXmlApplicationContext("classpath:spring/applicationContext.xml");
        settlementOperationMapper = ac.getBean(SettlementOperationMapper.class);
        merchantMapper = ac.getBean(MerchantMapper.class);
        querySettlementCustom = new QuerySettlementCustom();
        querySettlementCustom.setStartDate("2017-01-02");
        querySettlementCustom.setEndDate("2017-01-09");
        querySettlementCustom.getMerchantId("1");

        settlementOperation=new SettlementOperation();
        settlementOperation.setMerchantId("1");
        settlementOperation.setOperationAmount(1000);
        settlementOperation.setCheckStatus("10");
        settlementOperation.setOperationType("账户余额");

    }
    @Test
    public void testMer_accountController() {
        try{
           // settlementOperationMapper.querySFCList();设断点进行查看
            String request_param = JSONUtils.objTojson(querySettlementCustom);
            MvcResult result = mockMvc.perform(post("/merchant/querySettlementRecord.action")
                    .contentType(MediaType.APPLICATION_JSON).content(request_param))
                    .andExpect(status().isOk())
                    .andExpect(handler().handlerType(MerchantController.class))
                    .andExpect(handler().methodName("querySettlementRecord")).andReturn();
            String resultString = result.getResponse().getContentAsString();
            System.out.println(resultString);
            List<SettlementRecord> list = JSONUtils.jArraytoList(resultString, SettlementRecord.class);
            assertNotNull(list);
            if(list.size()!=0){
                assertEquals(SettlementRecord.class,list.get(0).getClass());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testMer_accuntService() throws Exception {
        List<SettlementRecord> result_list=merchantService.querySettlementRecord(qsc);
        assertNotNull(result_list);
        assertEquals(result_list.get(0).getClass(),SettlementOperation.class);
        SettlementRecord settlementRecord = result_list.get(0);
        assertNotNull(settlementRecord.getMerchantId());
        assertNotNull(settlementRecord.getOperationAmount());
        assertNotNull(settlementRecord.getOperationDate());
        assertNotNull(settlementRecord.getOperationType());
    }


    @Test
    public void testMer_accountMapper() throws Exception{
        //String merchant_id = settlementOperation.getId();
        Integer conCurrentId = merchantMapper.querySettlementBalance(merchantId);
        assertEquals(conCurrentId,settlementOperation.getMerchantId());
        List<SettlementRecord> result = merchantMapper.querySettlementRecord(qsc);
        assertNotNull(result);
        assertEquals(result.get(0).getClass(),SettlementOperation.class);
        SettlementRecord settlementRecord = result.get(0);
        assertNotNull(settlementRecord.getMerchantId());
        assertNotNull(settlementRecord.getOperationAmount());
        assertNotNull(settlementRecord.getOperationDate());
        assertNotNull(settlementRecord.getOperationType());
    }
}
