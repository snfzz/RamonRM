package com.ramon.ramonrm.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.softupdate.UpdateManager;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.MethodUtil;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private Button btnUpdate;
    private ImageButton btnBack;
    private TextView lblVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        btnUpdate = findViewById(R.id.activity_about_btnupdate);
        btnUpdate.setOnClickListener(this);
        lblVersion = findViewById(R.id.activity_about_lblversion);
        UpdateManager updateManager = new UpdateManager(this);
        lblVersion.setText("版本号：" + updateManager.getVersionName(context));
        btnBack = findViewById(R.id.activity_about_imgback);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try{
            int vId = v.getId();
            if (vId == R.id.activity_about_btnupdate) {
                UpdateManager updateManager = new UpdateManager(this);
                updateManager.checkUpdate();
            }
            if(vId == R.id.activity_about_imgback) {
                AppManagerUtil.instance().finishActivity(this);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            MethodUtil.showToast(ex.getMessage(), context);
        }
    }
}
