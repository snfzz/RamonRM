package com.ramon.ramonrm.home;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.MapSession;
import com.ramon.ramonrm.MyLocationListener;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.controls.HomeMenuItem;
import com.ramon.ramonrm.home.fragment.DevFragment;
import com.ramon.ramonrm.home.fragment.FuncFragment;
import com.ramon.ramonrm.home.fragment.HomeFragment;
import com.ramon.ramonrm.home.fragment.MineFragment;
import com.ramon.ramonrm.renyuan.WorkMsgFragment;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DelayedUtil;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.video.VideoCallActivity;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMTextElem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;


public class HomeActivity extends BaseActivity {
    private LocalBroadcastManager broadcastManager;//注册广播，用于跳转

    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private LocationClient mLocationClient = null;

    private TextView lblTitle;
    private HomeMenuItem[] menuItems;
    private Fragment[] fragments;
    private int checkedMenuItemIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        IntentFilter filter = new IntentFilter("action_send");
        registerReceiver(broadcastReceiver, filter);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    initMap();
                    initView();
                    registerReceiver();//注册广播
                    menuItemClick(R.id.activity_home_mitemhome);
                    TIMlogin(Session.CurrUser.YongHuSNo);
                    loadZhanShiPZ();
                    Looper.loop();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //region 通知提醒--内部通知
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals("action_send")) {
                    if (intent.getExtras().getString("data").equals("video")) {
                        String fromUser = intent.getStringExtra("fromUser");
                        String fromUserName = intent.getStringExtra("fromUserName");
                        String fromUserDept = intent.getStringExtra("fromUserDept");
                        int roomId = intent.getIntExtra("roomID", 0);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel("110", "bob", NotificationManager.IMPORTANCE_HIGH);
                            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.createNotificationChannel(channel);
                            Notification.Builder builder = new Notification.Builder(getApplicationContext());
                            //创建通知时指定channelID
                            builder.setChannelId("110");
                            builder.setShowWhen(true);
                            builder.setDefaults(Notification.DEFAULT_ALL);
                            builder.setAutoCancel(true);
                            Intent intent1 = new Intent(getApplicationContext(), VideoCallActivity.class);
                            intent1.putExtra("data", "video");
                            intent1.putExtra("fromUser", fromUser);
                            intent1.putExtra("fromUserName", fromUserName);
                            intent1.putExtra("fromUserDept",fromUserDept);
                            intent1.putExtra("roomID", roomId);
                            intent1.putExtra("role", 20);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(intent1);
                        } else {
                            Notification.Builder messageNotification = new Notification.Builder(getApplication());
                            messageNotification.setShowWhen(true);
                            messageNotification.setWhen(System.currentTimeMillis());
                            messageNotification.setDefaults(Notification.DEFAULT_ALL);
                            messageNotification.setAutoCancel(true);
                            Intent intent1 = new Intent(getApplicationContext(), VideoCallActivity.class);
                            intent1.putExtra("data", "video");
                            intent1.putExtra("fromUser", fromUser);
                            intent1.putExtra("fromUserName", fromUserName);
                            intent1.putExtra("roomID", roomId);
                            intent1.putExtra("fromUserDept",fromUserDept);
                            intent1.putExtra("role", 20);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(intent1);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void sendNotifyData(Intent intent) {
        sendBroadcast(intent);
    }
    //endregion

    private void initMap() {
        mMapView = (MapView) findViewById(R.id.activity_home_mapview);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        MapSession.setMapView(mMapView);
        MapSession.setBaiduMap(mBaiduMap);
        mLocationClient = new LocationClient(this);
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.mipmap.icon_geo);
        MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, false, mCurrentMarker);
        mBaiduMap.setMyLocationConfiguration(configuration);
        MapSession.setLocationClient(mLocationClient);
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(60000);

        //设置locationClientOption
        mLocationClient.setLocOption(option);

        //注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);

        //开启地图定位图层
        mLocationClient.start();
    }

    private void initView() {
        menuItems = new HomeMenuItem[5];
        menuItems[0] = (HomeMenuItem) findViewById(R.id.activity_home_mitemhome);
        menuItems[1] = (HomeMenuItem) findViewById(R.id.activity_home_mitemfunc);
        menuItems[2] = (HomeMenuItem) findViewById(R.id.activity_home_mitemdev);
        menuItems[3] = (HomeMenuItem) findViewById(R.id.activity_home_mitemmsg);
        menuItems[4] = (HomeMenuItem) findViewById(R.id.activity_home_mitemmine);
        for (int i = 0; i < menuItems.length; i++) {
            menuItems[i].setOnClickListener(new MyOnClickLisener());
        }
        fragments = new Fragment[5];
        fragments[0] = new HomeFragment(this);
        fragments[1] = new FuncFragment();
        fragments[2] = new DevFragment();
        fragments[3] = new WorkMsgFragment();
        fragments[4] = new MineFragment();

        lblTitle = (TextView) findViewById(R.id.activity_home_lbltitle);
    }

    private class MyOnClickLisener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == R.id.activity_home_mitemhome || vId == R.id.activity_home_mitemfunc || vId == R.id.activity_home_mitemdev || vId == R.id.activity_home_mitemmsg || vId == R.id.activity_home_mitemmine) {
                //切换菜单
                menuItemClick(vId);
            }
        }
    }

    private void menuItemClick(int vId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < menuItems.length; i++) {
            if (menuItems[i].getId() == vId) {
                lblTitle.setText(menuItems[i].getTitle());
                boolean isChecked = menuItems[i].getIsChecked();
                if (!isChecked) {
                    menuItems[i].setIsChecked(true);
                    menuItems[i].setIsChecked(true);
                    if (checkedMenuItemIndex >= 0)
                        transaction.hide(fragments[checkedMenuItemIndex]);//隐藏上一个Fragment
                    if (fragments[i].isAdded() == false) {
                        transaction.add(R.id.activity_home_rymain, fragments[i]);
                    }
                    transaction.show(fragments[i]).commitAllowingStateLoss();
                }
            } else {
                menuItems[i].setIsChecked(false);
            }
        }
        for (int i = 0; i < menuItems.length; i++) {
            if (menuItems[i].getIsChecked()) {
                checkedMenuItemIndex = i;
            }
        }
    }

    private void TIMlogin(final String yhSNo) {
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_CmdExec_SCADA_GetUserSig,Session.SessionId,Session.ValidMD5);
        try {
            reqData.ExtParams.put("userCode", yhSNo);
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(HomeActivity.this, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    String userSig = resData.DataValues.get("UserSig");
                                    Session.UserSig = userSig;
                                    TIMManager.getInstance().login(yhSNo, userSig, new TIMCallBack() {
                                        @Override
                                        public void onError(int code, String desc) {
                                            //错误码 code 和错误描述 desc，可用于定位请求失败原因
                                            //错误码 code 列表请参见错误码表
                                            Log.d("Tag:TIM", "login failed. code: " + code + " errmsg: " + desc);
                                        }

                                        @Override
                                        public void onSuccess() {
                                            TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
                                                @Override
                                                public boolean onNewMessages(List<TIMMessage> list) {
                                                    for (int i = 0; i < list.size(); i++) {
                                                        TIMMessage msg = list.get(i);
                                                        long time = msg.timestamp() * 1000;

                                                        if (time < Session.LoginTime) {
                                                            continue;
                                                        }
                                                        //登录之后的消息进行判断
                                                        for (int j = 0; j < msg.getElementCount(); ++j) {
                                                            TIMElem elem = msg.getElement(j);

                                                            String fromUser = msg.getSender();
                                                            //获取当前元素的类型
                                                            TIMElemType elemType = elem.getType();
                                                            Log.d("Tag:TIM", "elem type: " + elemType.name());
                                                            if (elemType == TIMElemType.Text) {
                                                                //处理文本消息
                                                                String[] info = ((TIMTextElem) elem).getText().split(":");
                                                                if (info[1].toLowerCase().equals("roomid")) {
                                                                    String fromUserName = "";
                                                                    String fromUserDept = "";
                                                                    if (info.length >= 5) {
                                                                        fromUserName = info[4];
                                                                    }
                                                                    if (info.length > 4) {
                                                                        fromUserDept = info[3];
                                                                    }
                                                                    int roomID = Integer.parseInt(info[2]);
                                                                    if (VideoCallActivity.isCalling) {
                                                                        sendTIMMessage(fromUser, "over:calling");
                                                                    } else {
                                                                        VideoCallActivity.isCalling = true;
                                                                        Intent intent = new Intent("action_send");
                                                                        intent.putExtra("data", "video");
                                                                        intent.putExtra("fromUser", fromUser);
                                                                        intent.putExtra("fromUserName", fromUserName);
                                                                        intent.putExtra("roomID", roomID);
                                                                        intent.putExtra("fromUserDept",fromUserDept);
                                                                        sendNotifyData(intent);
                                                                    }
                                                                } else if (info[1].toLowerCase().equals("over")) {
                                                                    if (VideoCallActivity.getInstance() != null) {
                                                                        VideoCallActivity.getInstance().over("对方已挂断");
                                                                    }
                                                                }
                                                            } else if (elemType == TIMElemType.Image) {
                                                                //处理图片消息
                                                            }
                                                        }
                                                    }
                                                    return false;
                                                }
                                            });
                                            Log.d("Tag：TIM", "login succ");
                                        }
                                    });
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        // Volley请求失败时调用的函数
                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void TIMLogout() {
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {

                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                Log.d("Tag:TIM", "logout failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess() {
                //登出成功
            }
        });
    }

    private void loadZhanShiPZ() {
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "ZS_ZhanShiPZ_LieBiao_SP");
        reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("outParams", "recordTotal");
        reqData.ExtParams.put("recordTotal", "1");
        DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
        try {
            VolleyRequestUtil.RequestPost(this, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(this, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    Session.DataZhanShiPZ = resData.DataTable;
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), context);
                                ex.printStackTrace();
                            } finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog(reqData.CmdID);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            mMapView.onResume();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        MapSession.setMapView(null);
        TIMLogout();
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (DelayedUtil.isFastClick(2000)) {
            MethodUtil.showToast("再按一次退出", this);
            // 设置标志位为false
        } else {
            AppManagerUtil.instance().AppExit(this);
        }
    }

    //接收广播
    private void registerReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("jerry");
        broadcastManager.registerReceiver(mAdDownLoadReceiver, intentFilter);
    }

    private BroadcastReceiver mAdDownLoadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String change = intent.getStringExtra("change");
            //接收广播的内容
            if ("yes".equals(change)) {
                // 这地方只能在主线程中刷新UI,子线程中无效，因此用Handler来实现
                new Handler().post(new Runnable() {
                    public void run() {
                        //需要刷新的内容
                        menuItemClick(R.id.activity_home_mitemfunc);
                    }
                });
            }
        }
    };
}
