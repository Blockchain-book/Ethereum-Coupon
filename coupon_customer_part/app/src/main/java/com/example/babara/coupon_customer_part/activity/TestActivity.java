package com.example.babara.coupon_customer_part.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.babara.coupon_customer_part.R;

public class TestActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textView = (TextView)findViewById(R.id.test);
        Bundle bundle = getIntent().getExtras();   //得到传过来的bundle
        String data = bundle.getString("result");//读出数据
        textView.setText(data);

    }
}
