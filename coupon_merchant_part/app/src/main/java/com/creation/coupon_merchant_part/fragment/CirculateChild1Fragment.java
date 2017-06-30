package com.creation.coupon_merchant_part.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.activity.CapitalAccountActivity;
import com.creation.coupon_merchant_part.activity.IssueManageActivity;
import com.creation.coupon_merchant_part.activity.MainActivity;
import com.creation.coupon_merchant_part.activity.NoNetActivity;
import com.creation.coupon_merchant_part.pojo.CouponRequestItem;
import com.creation.coupon_merchant_part.pojo.UrlManager;
import com.creation.coupon_merchant_part.utils.AsyncResponse;
import com.creation.coupon_merchant_part.utils.CouponRequestAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
/**
 * Created by Babara Liang on 2016/12/26.
 * Description:这是流通Fragment的子Fragment，实现优惠券发放（或拒绝）的功能
 */

public class CirculateChild1Fragment extends Fragment implements View.OnClickListener{
    private ListView listView;                    //放置优惠券发放请求列表的ListView
    private ArrayList<CouponRequestItem> list;  //请求的数据列表
    private TextView userText;                    //当点击每个条目时，在上方展示用户名
    private TextView expenseTimeText;             //当点击每个条目时，在上方展示消费时间
    private EditText payAmountEditText;           //商家输入消费者的实际消费金额
    private TextView sendAmountText;          //依据规则展示出应发放的优惠券金额
    private Button refuseBtn;                   //拒绝发放按钮
    private Button agreeBtn;                    //同意发放按钮
    private Handler handler = new Handler();       //延时处理
    private Integer reach;                      //优惠劵满＊＊
    private Integer send;                       //优惠劵送**
    private String merchantID;                  //用户id
    private String consumerId;                  //消费者id
    private String applyId;                     //申请表id
    private Handler handlerRequest;
    private int selectedPosition;                       //list选择id
    private Runnable couponApplyRunnable;

    public CirculateChild1Fragment(){
    }
    //@Nullable表示定义的字段可以为空
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circulate_child1,container,false);
        listView = (ListView)view.findViewById(R.id.coupon_request_list1);
        userText = (TextView)view.findViewById(R.id.circulate_user_text);
        expenseTimeText = (TextView)view.findViewById(R.id.circulate_time_text);
        payAmountEditText = (EditText)view.findViewById(R.id.circulate_pay_text);
        sendAmountText = (TextView)view.findViewById(R.id.circulate_send_text);
        refuseBtn = (Button)view.findViewById(R.id.coupon_apply_refuse);
        refuseBtn.setEnabled(true);
        agreeBtn = (Button)view.findViewById(R.id.coupon_apply_agree);
        agreeBtn.setEnabled(true);

        //获取MainActivity中获取的请求列表
        //如果列表不为空，则展示列表的第一个（类似于实现第一个Item的自动点击功能）
        //获取商家id
        SharedPreferences sp = getActivity().getSharedPreferences("currentSession", Context.MODE_PRIVATE);
        merchantID = sp.getString("merchantId", "111");
        reach = sp.getInt("reach",200);
        send = sp.getInt("give",50);

        networkListener();

        //支付金额EditText焦点监听
        payAmountEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(delayRun!=null){
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                //延迟800ms，如果不再输入字符，则执行该线程的run方法
                handler.postDelayed(delayRun, 100);
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        handlerRequest.removeCallbacks(couponApplyRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handlerRequest=new Handler();
        couponApplyRunnable=new Runnable() {
            @Override
            public void run() {
                //请求
                String urlString = new UrlManager().createUrlString("/merchant/queryCouponApp.action");
                String requestState = "id="+merchantID;
                new HttpTask(requestState, urlString).execute();

                handlerRequest.postDelayed(this, 30000);
            }
        };
        handlerRequest.postDelayed(couponApplyRunnable, 2000);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refuseBtn.setOnClickListener((View.OnClickListener) this);
        agreeBtn.setOnClickListener((View.OnClickListener) this);
    }

    public void onClick(View v){
        if ("".equals(payAmountEditText.getText().toString()) || payAmountEditText.getText().toString() == null){
            Toast.makeText(v.getContext(),"消费金额不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if ("暂无申请用户".equals(userText.getText().toString())){
            Toast.makeText(v.getContext(),"暂无申请结算券的用户",Toast.LENGTH_SHORT).show();
            return;
        }
        String status = "0";
        switch (v.getId()){
            case R.id.coupon_apply_agree:
                status = "1";
                break;
        }
        JSONObject conditionJson = new JSONObject();
        try {
            conditionJson.put("merchantId", merchantID);
            conditionJson.put("status",status);
            conditionJson.put("couponNum",sendAmountText.getText().toString());
            conditionJson.put("couponValue",send*(Integer.parseInt(sendAmountText.getText().toString())));
            conditionJson.put("consumerId",consumerId);
            conditionJson.put("consumptionValue",payAmountEditText.getText().toString());
            conditionJson.put("id",applyId);
        }catch(JSONException je){
            je.printStackTrace();
        }
        String conditionString = conditionJson.toString();
        String urlString = new UrlManager().createUrlString("/merchant/updateCouponApp.action");
        new SettleHttpTask(conditionString,urlString).execute();
        refuseBtn.setEnabled(false);
        agreeBtn.setEnabled(false);
    }

    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            String payAmount = payAmountEditText.getText().toString();
            if (payAmount != null && "".equals(payAmount) == false){
                sendAmountText.setText(String.valueOf(Integer.parseInt(payAmount)/reach));
            }
        }
    };

    public class HttpTask extends AsyncTask<Void, Void, String> {
        private String conditionString;
        private String urlString;


        public HttpTask(String conditionString,String urlString){
            this.conditionString = conditionString;
            this.urlString = urlString;
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        //在这里使用AsyncResponse接口来实现获取doInBackground的执行结果
        @Override
        protected void onPostExecute(String result) {
            if (result != null && !("[]".equals(result))){
                super.onPostExecute(result);
                try{
                    list = new ArrayList<CouponRequestItem>();
                    JSONArray jsonArray = new JSONArray(result);
                    int length = jsonArray.length();
                    CouponRequestItem[] items = new CouponRequestItem[length];
                    for(int i = 0; i < length; i++){
                        JSONObject tempJsonObject = jsonArray.getJSONObject(i);
                        String usernameStr = tempJsonObject.getString("account");
                        String timeStr = tempJsonObject.getString("consumptionTime");
                        String id = tempJsonObject.getString("consumerId");
                        String applyId = tempJsonObject.getString("id");
                        items[i] = new CouponRequestItem(usernameStr, timeStr,id,applyId);
                        list.add(items[i]);
                    }
                    if(list!=null){
                        userText.setText(list.get(0).getUser());
                        expenseTimeText.setText(list.get(0).getTime());
                        consumerId = list.get(0).getId();
                        applyId = list.get(0).getApplyId();
                        selectedPosition = 0;
                    }
                    CouponRequestAdapter arrayAdpter = new CouponRequestAdapter(getActivity(), R.layout.coupon_request_item, list);
                    listView.setAdapter(arrayAdpter);
                    //点击列表中的每一条，会将该条目的信息展示在上部分，以共商家查看详细信息，确定发放或者取消
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                            CouponRequestItem c = list.get(position);
                            Toast.makeText(getActivity(), c.getUser(), Toast.LENGTH_SHORT).show();
                            userText.setText(c.getUser());
                            expenseTimeText.setText(c.getTime());
                            consumerId = c.getId();
                            applyId = c.getApplyId();
                            selectedPosition = position;
                        }
                    });
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(Void... params) {
            try{
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                String data = conditionString;
                byte[] bytes = data.getBytes();
                conn.getOutputStream().write(bytes); // 写出数据
                //读取服务器的返回结果
                InputStream is = null;
                StringBuffer sb = new StringBuffer();
                String str = "";
                if (conn.getResponseCode() == 200) {
                    is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    while((str = reader.readLine())!=null){
                        sb.append(str);
                    }
                }
                String result = sb.toString();
                return result;
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public class SettleHttpTask extends AsyncTask<Void, Void, String> {
        private String conditionString;
        private String urlString;


        public SettleHttpTask(String conditionString,String urlString){
            this.conditionString = conditionString;
            this.urlString = urlString;
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        //在这里使用AsyncResponse接口来实现获取doInBackground的执行结果
        @Override
        protected void onPostExecute(String result) {
            if (result != null){
                super.onPostExecute(result);
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.getString("resultCode");
                    if ("1".equals(res)){
                        Toast.makeText(getContext(),"申请处理成功",Toast.LENGTH_SHORT).show();
                        payAmountEditText.setText("");
                        list.remove(selectedPosition);
                        if (list!=null && list.size() != 0){
                            CouponRequestAdapter arrayAdpter = new CouponRequestAdapter(getActivity(), R.layout.coupon_request_item, list);
                            listView.setAdapter(arrayAdpter);
                            userText.setText(list.get(0).getUser());
                            expenseTimeText.setText(list.get(0).getTime());
                            consumerId = list.get(0).getId();
                            applyId = list.get(0).getApplyId();
                            selectedPosition = 0;
                        }else{
                            userText.setText("暂无申请用户");
                            expenseTimeText.setText("**时间");
                            sendAmountText.setText("0");
                        }
                    }else {
                        Toast.makeText(getContext(),"申请处理失败",Toast.LENGTH_SHORT).show();
                    }
                    refuseBtn.setEnabled(true);
                    agreeBtn.setEnabled(true);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(Void... params) {
            try{
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                String data = conditionString;
                byte[] bytes = data.getBytes();
                conn.getOutputStream().write(bytes); // 写出数据
                //读取服务器的返回结果
                InputStream is = null;
                StringBuffer sb = new StringBuffer();
                String str = "";
                if (conn.getResponseCode() == 200) {
                    is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    while((str = reader.readLine())!=null){
                        sb.append(str);
                    }
                }
                String result = sb.toString();
                return result;
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    //网络监听
    class NetworkChangeReceiver extends BroadcastReceiver {
        //每当网络状态发生变化时，onReceive方法会得到执行
        //执行后实时更新网路状态isNetworkAvailable
        @Override
        public void onReceive(Context context, Intent intent){
            ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(getActivity().getApplicationContext().CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable()){
                String urlString = new UrlManager().createUrlString("/merchant/queryCouponApp.action");
                String requestState = "id="+merchantID;
                new HttpTask(requestState, urlString).execute();
            }
            else{
                Toast.makeText(context, "网络连接断开", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void networkListener(){
        //每当网络状态发生变化时，系统会发出"android.net.conn.CONNECTIVITY_CHANGE"的广播
        //要给intentFilter添加这样一个action，所以想要监听什么样的广播，就添加相应的action
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        CirculateChild1Fragment.NetworkChangeReceiver networkChangeReceiver = new CirculateChild1Fragment.NetworkChangeReceiver();
        //调用 registerReceiver()进行注册，NetworkChangeReceiver()会收到
        // 所有值为"android.net.conn.CONNECTIVITY_CHANGE"的广播
        getActivity().registerReceiver(networkChangeReceiver, intentFilter);
    }
}