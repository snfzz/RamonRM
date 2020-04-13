package com.ramon.ramonrm.renyuan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.weihuproject.WeiHuProjectActivity;

public class FunctionalCollection extends BaseActivity implements View.OnClickListener {
    private ImageButton imgbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functionalcollection);
        initView();
    }

    private void initView(){
        imgbtn=findViewById(R.id.activity_functionalcollection_imgback);
        imgbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int vId=view.getId();
        if (vId == R.id.activity_functionalcollection_imgback){
            AppManagerUtil.instance().finishActivity(FunctionalCollection.this);
        }
    }
}
