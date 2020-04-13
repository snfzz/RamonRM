package com.ramon.ramonrm.device;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.google.android.material.tabs.TabLayout;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.AlarmAdapter;
import com.ramon.ramonrm.adapter.RMFragmentAdapter;
import com.ramon.ramonrm.controls.RMListView;
import com.ramon.ramonrm.model.AlarmInfo;
import com.ramon.ramonrm.model.DeviceStatus;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DateTimeUtil;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.ImageUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DevDetailActivity extends BaseActivity {

    private DeviceStatus mSBInfo;
    private String mKeHuMC, mCPSNo, mSheBeiMC, mSBSNo;
    private int mLiuShu;
    private ImageView btnBack,imgTitle;

    private TextView lblKeHuMC, lblSheBeiMC;
    private RMListView lvAlarm;
    private ScrollView mScrollView;

    private TabLayout tlTitle;
    private ViewPager vpFragment;
    private Fragment[] arrFragments;
    private String[] arrTitles;
    private RMFragmentAdapter rmAdapter;
    private HashMap<String,String>mZhanShiPZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devdetail);
        try {
            String sbInfo = getIntent().getStringExtra("SBInfo");
            mSBInfo = GsonUtils.fromJson(sbInfo,DeviceStatus.class);
            mKeHuMC = getIntent().getStringExtra("KeHuMC");
            mCPSNo = getIntent().getStringExtra("CPSNo");
            mSheBeiMC = getIntent().getStringExtra("SBMingCheng");
            mSBSNo = getIntent().getStringExtra("SBSNo");
            mLiuShu = getIntent().getIntExtra("LiuShu", 1);
            for(int i=0;i<Session.DataZhanShiPZ.length;i++){
                HashMap<String,String>hashMap = Session.DataZhanShiPZ[i];
                String cpSNo = hashMap.get("ChanPinSNo").toLowerCase();
                if(cpSNo.equals(mCPSNo.toLowerCase())){
                    mZhanShiPZ = hashMap;
                }
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    initView();
                    loadStrandData();
                    loadAlarmData();
                    String url = APIConfig.APIHOST+"/Image/"+mCPSNo+".png";
                    String fileName = mCPSNo+".png";
                    Bitmap bitMap = ImageUitl.getLocalImageDefPath(fileName);
                    if(bitMap == null) {
                        bitMap = ImageUitl.getUrlImage(url);
                        ImageUitl.saveImageDefPath(bitMap, fileName);
                    }
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = bitMap;
                    handle.sendMessage(msg);
                }
            }).start();
        } catch (Exception ex) {
            MethodUtil.showToast(ex.getMessage(), context);
        }
    }


    //region 获取网络图片
    //在消息队列中实现对控件的更改

    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    System.out.println("111");
                    Bitmap bmp=(Bitmap)msg.obj;

                    if(bmp!=null)
                        imgTitle.setImageBitmap(bmp);
                    else imgTitle.setImageDrawable(context.getResources().getDrawable(R.mipmap.dot0));
                    break;
            }
        };
    };

    //endregion

    private void initView() {
        lblKeHuMC =  findViewById(R.id.activity_devdetail_kehumc);
        lblKeHuMC.setText(mKeHuMC);
        lblSheBeiMC =  findViewById(R.id.activity_devdetail_shebeimc);
        lblSheBeiMC.setText(mSheBeiMC);
        btnBack = findViewById(R.id.activity_devdetail_imgback);
        btnBack.setOnClickListener(new MyOnClickListener());
        tlTitle =  findViewById(R.id.activity_devdetail_tablay);
        vpFragment = findViewById(R.id.activity_devdetail_viewpager);
        lvAlarm =  findViewById(R.id.activity_devdetail_lvalarm);
        imgTitle = findViewById(R.id.activity_devdetail_imgtitle);
        mScrollView = findViewById(R.id.activity_devdetail_scrollview);
        lvAlarm.setScrollView(mScrollView);
    }

    private void loadStrandData() {
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_CmdExec_DevData_GetDevData,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("devCode", mSBSNo);
        DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
        try {
            VolleyRequestUtil.RequestPost(this, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(this, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadDataView(resData.DataTable);
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


    private void loadAlarmData() {
        String beginDate = DateTimeUtil.DateTimeToString(System.currentTimeMillis(), "yyyy-MM-dd");
        String endDate = DateTimeUtil.DateTimeToString(System.currentTimeMillis() + 24 * 3600 * 1000, "yyyy-MM-dd");
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "BJ_BaoJing_LieBiao_SP");
        reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("outParams", "recordTotal");
        reqData.ExtParams.put("sheBeiSNo", mSBSNo);
        reqData.ExtParams.put("recordTotal", "1");
        reqData.ExtParams.put("beginDate", beginDate);
        reqData.ExtParams.put("endDate", endDate);
        DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
        try {
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(context, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadAlarmView(resData.DataTable);
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), context);
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

    private void loadAlarmView(HashMap<String, String>[] hashData) {
        List<AlarmInfo> listAlarm = new ArrayList<>();
        for (int i = 0; i < hashData.length; i++) {
            AlarmInfo aInfo = fillData(hashData[i]);
            listAlarm.add(aInfo);
        }
        AlarmAdapter adapter = new AlarmAdapter(context, R.layout.listitem_alarminfo, listAlarm);
        lvAlarm.setAdapter(adapter);
    }

    private AlarmInfo fillData(HashMap<String, String> hashMap) {
        AlarmInfo aInfo = new AlarmInfo();
        aInfo.BeginTime = hashMap.get("BeginTime");
        aInfo.ChanPinDM = hashMap.get("ChanPinDM");
        aInfo.ChanPinJC = hashMap.get("ChanPinJC");
        aInfo.ChanPinMC = hashMap.get("ChanPinMC");
        aInfo.ChanPinSNo = hashMap.get("ChanPinSNo");
        boolean ifhave=hashMap.containsKey("ChiXuSJ");
        if (ifhave==true){
            aInfo.ChiXuSJ = hashMap.get("ChiXuSJ");
        }else {
            aInfo.ChiXuSJ = "--";
            //Log.e("dsassda","false");
        }
        aInfo.ChuLiGZ = hashMap.get("ChuLiGZ");
        aInfo.DaiMa = hashMap.get("DaiMa");
        aInfo.DengJiDM = hashMap.get("DengJiDM");
        aInfo.DengJiName = hashMap.get("DengJiDMname");
        aInfo.EndTime = hashMap.get("EndTime");
        aInfo.GuZhangMC = hashMap.get("GuZhangMC");
        aInfo.KeHuJC = hashMap.get("KeHuJC");
        aInfo.KeHuMC = hashMap.get("KeHuMC");
        aInfo.KeHuSNo = hashMap.get("KeHuSNo");
        aInfo.LiuHao = Integer.parseInt(hashMap.get("LiuHao"));
        aInfo.MiaoShuGZ = hashMap.get("MiaoShuGZ");
        aInfo.AlarmSNo = hashMap.get("SNo");
        aInfo.SheBeiMC = hashMap.get("SheBeiMC");
        aInfo.SheBeiSNo = hashMap.get("SheBeiSNo");
        aInfo.YuanYinGZ = hashMap.get("YuanYinGZ");
        aInfo.bJTime = hashMap.get("bJTime");
        return aInfo;
    }

    private void loadDataView(HashMap<String, String>[] hashData) {
        arrFragments = new Fragment[mLiuShu];
        arrTitles = new String[mLiuShu];
        for (int i = 0; i < mLiuShu; i++) {
            arrFragments[i] = new DetailDataFragment(mZhanShiPZ, hashData, mSBInfo , i + 1);
            arrTitles[i] = Session.StrandTitles[i];
        }
        rmAdapter = new RMFragmentAdapter(getSupportFragmentManager());
        rmAdapter.setDatas(arrFragments, arrTitles);
        vpFragment.setAdapter(rmAdapter);
        vpFragment.setOffscreenPageLimit(0);
        tlTitle.setupWithViewPager(vpFragment);


    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            try {
                if (view.getId() == R.id.activity_devdetail_imgback) {
                    AppManagerUtil.instance().finishActivity(DevDetailActivity.this);
                }
            } catch (Exception e) {
                MethodUtil.showToast(e.getMessage(), context);
                e.printStackTrace();
            }
        }
    }
}
