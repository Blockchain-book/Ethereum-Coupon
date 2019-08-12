package com.example.babara.coupon_customer_part.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.babara.coupon_customer_part.R;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView backImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_help);
        backImage = (ImageView)findViewById(R.id.help_back);
        backImage.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.help_back:
                this.finish();
                break;
        }
    }
}
