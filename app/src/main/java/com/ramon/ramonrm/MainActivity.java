package com.ramon.ramonrm;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.igexin.sdk.PushManager;
import com.ramon.ramonrm.home.LoginActivity;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyErrorHelper;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.session.SessionWrapper;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            if (SessionWrapper.isMainProcess(getApplicationContext())) {
                TIMSdkConfig config = new TIMSdkConfig(APIConfig.SDKAppId).enableLogPrint(false);
                //初始化 SDK
                TIMManager.getInstance().init(getApplicationContext(), config);
            }
            PushManager.getInstance().initialize(context, RMPushService.class);
            int count = 0;
            while (PushManager.getInstance().getClientid(context) == null && count <100){
                if (PushManager.getInstance().getClientid(context) != null) {
                    break;
                } else {
                    Thread.sleep(100);
                    count++;
                }
            }
            String clientId = PushManager.getInstance().getClientid(context);
            if (clientId == null || clientId.length() == 0) {
                Log.e("ClientID", "ClientID 为空");
            } else {
                Log.d("ClientID", clientId);
            }
            Session.ClientId = clientId;
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    startActivity(LoginActivity.class);
                    AppManagerUtil.instance().finishActivity(MainActivity.this);
                }
            };
            timer.schedule(task, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}