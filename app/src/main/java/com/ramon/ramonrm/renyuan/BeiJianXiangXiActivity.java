package com.ramon.ramonrm.renyuan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;

public class BeiJianXiangXiActivity extends BaseActivity implements View.OnClickListener {
    ImageButton imgbtnback;
    TextView txt1,txt2,txt3,txt4,txt5,txt6,txt7,txt8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beijianxiangxi);
        initView();
        txt1.setText(getIntent().getStringExtra("txt1"));
        txt2.setText(getIntent().getStringExtra("txt2"));
        txt3.setText(getIntent().getStringExtra("txt3"));
        txt4.setText(getIntent().getStringExtra("txt4"));
        txt8.setText(getIntent().getStringExtra("txt5"));
    }

    private void initView(){
        imgbtnback=(ImageButton)findViewById(R.id.activity_beijianxiangxi_imgback);
        imgbtnback.setOnClickListener(this);
        txt1=findViewById(R.id.activity_beijianxiangxi_txt1);
        txt2=findViewById(R.id.activity_beijianxiangxi_txt2);
        txt3=findViewById(R.id.activity_beijianxiangxi_txt3);
        txt4=findViewById(R.id.activity_beijianxiangxi_txt4);
        txt5=findViewById(R.id.activity_beijianxiangxi_txt5);
        txt6=findViewById(R.id.activity_beijianxiangxi_txt6);
        txt7=findViewById(R.id.activity_beijianxiangxi_txt7);
        txt8=findViewById(R.id.activity_beijianxiangxi_txt8);
    }

    @Override
    public void onClick(View view) {
        int vId=view.getId();
        if (vId == R.id.activity_beijianxiangxi_imgback){
            AppManagerUtil.instance().finishActivity(BeiJianXiangXiActivity.this);
        }
    }
}
