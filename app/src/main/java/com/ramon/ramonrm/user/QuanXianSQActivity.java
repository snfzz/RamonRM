package com.ramon.ramonrm.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.MethodUtil;

public class QuanXianSQActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quanxiansq);
        initView();
    }

    private void initView(){
        imgBack = findViewById(R.id.activity_quanxiansq_imgback);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try{
            int vId = v.getId();
            if(vId == R.id.activity_quanxiansq_imgback){
                AppManagerUtil.instance().finishActivity(this);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }
}
