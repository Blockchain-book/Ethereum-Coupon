package com.example.babara.coupon_customer_part.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.babara.coupon_customer_part.R;
import com.example.babara.coupon_customer_part.pojo.NearbyMerchantItem;
import com.example.babara.coupon_customer_part.utils.NearbyMerchantAdapter;
import com.example.babara.coupon_customer_part.utils.UrlManager;
import com.example.babara.coupon_customer_part.zxing.activity.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PayCouponActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backImageView;           //返回按钮
    private Button scanCodePayButton;          //“扫一扫”按钮
    private TextView nearbyMerchantText;       //“附近商家”按钮
    private ListView listView;
    private ArrayList<NearbyMerchantItem> list;
    private String getMerchantUrl;
    private String getMerchantRequest;
    private String latitude;
    private String longitude;
    private LocationClient mLocationClient = null;
    private BDLocationListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pay_coupon);
        bindView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开始定位
        mLocationClient.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationClient.stop();
    }

    //UI组件初始化和监听功能
    public void bindView() {
        backImageView = (ImageView) findViewById(R.id.pay_coupon_back);
        scanCodePayButton = (Button) findViewById(R.id.scan_code_pay_button);

        backImageView.setOnClickListener(this);
        scanCodePayButton.setOnClickListener(this);

        mListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                int locType = bdLocation.getLocType();
                switch (locType){
                    case BDLocation.TypeServerError:
                        Toast.makeText(getApplicationContext(),"服务端网络定位失败",Toast.LENGTH_SHORT).show();
                        break;
                    case BDLocation.TypeNetWorkException:
                        Toast.makeText(getApplicationContext(),"请检查网络是否畅通",Toast.LENGTH_SHORT).show();
                        break;
                    case BDLocation.TypeCriteriaException:
                        Toast.makeText(getApplicationContext(),"请检查是否处于飞行模式或重启手机",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        latitude = bdLocation.getLatitude()+"";
                        longitude = bdLocation.getLongitude()+"";
                        String fetchNearByUrl = UrlManager.createUrlString("/merchant/fetchNearby.action");
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("latitude",latitude);
                            jsonObject.put("longitude",longitude);
                            String requestCode = jsonObject.toString();
                            new HttpTask(requestCode,fetchNearByUrl).execute();
                        }catch (Exception e){

                        }
                        break;
                }
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        };
        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        mLocationClient.registerLocationListener(mListener);
    }

    private void initLocation() {
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        locationClientOption.setCoorType("gcj02");
        locationClientOption.setScanSpan(10000);
        locationClientOption.setIsNeedAddress(false);
        locationClientOption.setOpenGps(true);
        locationClientOption.setIsNeedLocationDescribe(false); //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationClientOption.setIgnoreKillProcess(true); //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationClientOption.SetIgnoreCacheException(false);
        locationClientOption.setEnableSimulateGps(true);
        mLocationClient.setLocOption(locationClientOption);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_coupon_back:
                this.finish();
                break;
            case R.id.scan_code_pay_button:
                Intent intent = new Intent(PayCouponActivity.this, CaptureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type","pay");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }

    }
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
            if (result!=null && !"[]".equals(result)){
                super.onPostExecute(result);
                try{
                    final ArrayList<NearbyMerchantItem> list = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(result);

                    int length = jsonArray.length();

                    NearbyMerchantItem[] items = new NearbyMerchantItem[length];
                    for(int i = 0; i < length; i++){
                        JSONObject tempJsonObject = jsonArray.getJSONObject(i);
                        String merchantId = tempJsonObject.getString("merchantId");
                        String distance = tempJsonObject.getString("distance")+"米";
                        String merchantName = tempJsonObject.getString("merchantName");
                        String merchantInfo = tempJsonObject.getString("merchantInfo");
                        items[i] = new NearbyMerchantItem(merchantId, distance, merchantName, merchantInfo);
                        list.add(items[i]);
                    }


                    NearbyMerchantAdapter arrayAdpter = new NearbyMerchantAdapter(PayCouponActivity.this, R.layout.nearby_merchant_item, list);
                    listView = (ListView) findViewById(R.id.testListView);
                    listView.setAdapter(arrayAdpter);

                    //点击列表中的每一条，会将该条目的信息展示在上部分，以共商家查看详细信息，确定发放或者取消
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            NearbyMerchantItem m = list.get(position);
                            Intent intent = new Intent(PayCouponActivity.this, ConfirmPayActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("merchantInfo", m);
                            intent.putExtras(bundle);
                            startActivity(intent);
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

}



