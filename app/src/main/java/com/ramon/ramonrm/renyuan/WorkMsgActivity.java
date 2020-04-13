package com.ramon.ramonrm.renyuan;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentTransaction;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.util.BaseActivity;

public class WorkMsgActivity extends BaseActivity implements View.OnClickListener {

    RelativeLayout rlayMain;
    WorkMsgFragment fragWorkMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workmsg);
        rlayMain = (RelativeLayout) findViewById(R.id.activity_workmsg_rlaymain);
        fragWorkMsg = new WorkMsgFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.activity_workmsg_rlaymain, fragWorkMsg).show(fragWorkMsg).commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {

    }
}
