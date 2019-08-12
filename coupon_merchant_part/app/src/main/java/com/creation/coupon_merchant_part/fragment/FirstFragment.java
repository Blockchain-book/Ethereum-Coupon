package com.creation.coupon_merchant_part.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.activity.CouponStateActivity;
import com.creation.coupon_merchant_part.activity.IssueActivity;
import com.creation.coupon_merchant_part.activity.IssueManageActivity;
import com.creation.coupon_merchant_part.activity.TicketApplyActivity;

/**
 * Created by Babara Liang on 2016/12/26.
 * Description:该类主要展现首页上的四个功能按钮，并且实现点击时的跳转。
 */

public class FirstFragment extends Fragment implements View.OnClickListener{
    private TextView issueText ;            //首页优惠券发行（可点击）
    private TextView issueManageText;       //首页优惠券管理（可点击）
    private TextView stateManageText;       //首页状态管理（可点击）
    private TextView ticketApplyText;       //首页结算券申请（可点击）

    public  FirstFragment(){
    }

    @Nullable //@Nullable表示定义的字段可以为空
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first,container,false);
        //UI组件初始化
        issueText = (TextView)view.findViewById(R.id.issue_text);
        issueManageText = (TextView)view.findViewById(R.id.issue_manage_text);
        stateManageText = (TextView)view.findViewById(R.id.state_manage_text);
        ticketApplyText = (TextView)view.findViewById(R.id.ticket_apply_text);
        //按钮监听
        issueText.setOnClickListener(this);
        issueManageText.setOnClickListener(this);
        stateManageText.setOnClickListener(this);
        ticketApplyText.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.issue_text:
                Intent intent = new Intent(getActivity(), IssueActivity.class);
                startActivity(intent);
                break;
            case R.id.issue_manage_text:
                Intent intent2 = new Intent(getActivity(), IssueManageActivity.class);
                startActivity(intent2);
                break;

            case R.id.state_manage_text:
                Intent intent3 = new Intent(getActivity(), CouponStateActivity.class);
                startActivity(intent3);
                break;
            case R.id.ticket_apply_text:
                //点击“结算券申请”，跳转到结算券申请页面
                Intent intent4 = new Intent(getActivity(), TicketApplyActivity.class);
                startActivity(intent4);
                break;
        }
    }

}