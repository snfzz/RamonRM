package com.ramon.ramonrm.renyuan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.user.AuditUserDetailActivity;
import com.ramon.ramonrm.user.PowerApplyActivity;
import com.ramon.ramonrm.user.QuanXianSHActivity;
import com.ramon.ramonrm.user.SeePowerActivity;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;

public class PowerAdministrationActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton imgbtn;
    //用户审核，权限审核，权限申请，权限查看
    private RelativeLayout administrationyhsh,administrationqxsh,administrationqxsq,administrationqxck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poweradministration);
        initView();
    }

    private void initView(){
        imgbtn = (ImageButton)findViewById(R.id.activity_poweradministration_imgback);
        imgbtn.setOnClickListener(this);
        administrationyhsh=(RelativeLayout)findViewById(R.id.fragment_poweradministration_yhsh);
        administrationyhsh.setOnClickListener(this);
        administrationqxck=(RelativeLayout)findViewById(R.id.fragment_poweradministration_qxck);
        administrationqxck.setOnClickListener(this);
        administrationqxsh=(RelativeLayout)findViewById(R.id.fragment_poweradministration_qxsh);
        administrationqxsh.setOnClickListener(this);
        administrationqxsq=(RelativeLayout)findViewById(R.id.fragment_poweradministration_qxsq);
        administrationqxsq.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        Intent intent;
        if (vId == R.id.activity_poweradministration_imgback){
            AppManagerUtil.instance().finishActivity(PowerAdministrationActivity.this);
        }
        if (vId == R.id.fragment_poweradministration_qxck){
            intent=new Intent(PowerAdministrationActivity.this, SeePowerActivity.class);
            startActivity(intent);
        }
        if (vId == R.id.fragment_poweradministration_qxsh){
            intent=new Intent(PowerAdministrationActivity.this, QuanXianSHActivity.class);
            startActivity(intent);
        }
        if (vId == R.id.fragment_poweradministration_qxsq){
            intent=new Intent(PowerAdministrationActivity.this, PowerApplyActivity.class);
            startActivity(intent);
        }
        if (vId == R.id.fragment_poweradministration_yhsh){
            intent=new Intent(PowerAdministrationActivity.this, AuditUserDetailActivity.class);
            startActivity(intent);
        }
    }
}
