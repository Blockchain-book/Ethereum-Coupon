package com.creation.coupon_merchant_part.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.creation.coupon_merchant_part.R;
/**
 * Description:该类实现帮助用户使用该APP的功能
 */
public class HelpActivity extends Activity implements View.OnClickListener{
    private ImageView backImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_help);
        backImageView = (ImageView)findViewById(R.id.help_back);
        backImageView.setOnClickListener(this);
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
