package com.ramon.ramonrm.alarm;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.MethodUtil;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class AnLiDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private ImageView btnDianPing;
    private TextView lblChanPinMC,lblWenTiMS,lblChuLiCS,lblDianPingCS;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anlidetail);
        initView();
    }

    private void initView() {
        btnBack = findViewById(R.id.activity_anlidetail_imgback);
        btnBack.setOnClickListener(this);
        btnDianPing = findViewById(R.id.activity_anlidetail_imgdianping);
        btnDianPing.setOnClickListener(this);
        lblChanPinMC = findViewById(R.id.activity_anlidetail_lblchanpinmc);
        lblWenTiMS = findViewById(R.id.activity_anlidetail_lblwentims);
        lblChuLiCS = findViewById(R.id.activity_anlidetail_lblchulics);
        lblDianPingCS = findViewById(R.id.activity_anlidetail_lbldianpingcs);
    }

    @Override
    public void onClick(View v) {
        try {
            int vId = v.getId();
            if (vId == R.id.activity_anlidetail_imgback) {
                AppManagerUtil.instance().finishActivity(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), this);
        }
    }
}
