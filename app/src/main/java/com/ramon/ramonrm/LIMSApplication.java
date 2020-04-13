package com.ramon.ramonrm;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.ramon.ramonrm.webapi.APIConfig;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.session.SessionWrapper;

import java.util.List;

/**
 * Created by user on 2016/11/19.
 */

public class LIMSApplication extends Application {

    private static LIMSApplication instance;

    public static LIMSApplication getInstance() {
        return instance;
    }
    public static RequestQueue volleyQueue;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        /* Volley配置 */
        // 建立Volley的Http请求队列
        volleyQueue = Volley.newRequestQueue(getApplicationContext());

        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);

        com.igexin.sdk.PushManager.getInstance().initialize(getApplicationContext(), RMPushService.class);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    // 开放Volley的HTTP请求队列接口
    public static RequestQueue getRequestQueue() {
        return volleyQueue;
    }
}
