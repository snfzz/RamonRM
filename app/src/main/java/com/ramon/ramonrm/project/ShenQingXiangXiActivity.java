package com.ramon.ramonrm.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;

import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.ShenQingDialog.base.Config;
import com.ramon.ramonrm.ShenQingDialog.component.ShowImagesDialog;
import com.ramon.ramonrm.adapter.ShenQingRcyAdapter;
import com.ramon.ramonrm.model.RenWuAuditInfo;
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

import java.util.ArrayList;

import static com.baidu.mapapi.BMapManager.getContext;

public class ShenQingXiangXiActivity extends BaseActivity implements View.OnClickListener {
    private ImageView imgbtn;
    private TextView txt1,txt2,txt3,txt4,txt5,txt6,txt7,txt8,txt9,txt10,txt11;
    private Button btnpass,btnback;
    private EditText editText;
    private Boolean panduantup;//用于判断是否加载了图片
    private ArrayList<String> list=new ArrayList<>();//存放网址
    private RecyclerView mRecyclerView;
    private ShenQingRcyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shenqingxiangxi);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        SetTxt();
        GetImage(RenWuAuditInfo.ProjSNo);
        getDeviceDensity();
        //recyclerview
        mLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mAdapter = new ShenQingRcyAdapter(ShenQingXiangXiActivity.this, list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context,2));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter.setItemClickListener(new ShenQingRcyAdapter.OnRecyclerViewClickListener() {
            @Override
            public void onItemClickListener(View view) {
                int position = mRecyclerView.getChildAdapterPosition(view);
                if (panduantup==true){
                    new ShowImagesDialog(ShenQingXiangXiActivity.this, list,position).show();
                }
            }
        });
    }

    private void initView(){
        imgbtn=(ImageView)findViewById(R.id.activity_shenqingxiangxi_imgback);
        imgbtn.setOnClickListener(this);
        txt1=(TextView)findViewById(R.id.activity_shenqingxiangxi_txt1);
        txt2=(TextView)findViewById(R.id.activity_shenqingxiangxi_txt2);
        txt3=(TextView)findViewById(R.id.activity_shenqingxiangxi_txt3);
        txt4=(TextView)findViewById(R.id.activity_shenqingxiangxi_txt4);
        txt5=(TextView)findViewById(R.id.activity_shenqingxiangxi_txt5);
        txt6=(TextView)findViewById(R.id.activity_shenqingxiangxi_txt6);
        txt7=(TextView)findViewById(R.id.activity_shenqingxiangxi_txt7);
        txt8=(TextView)findViewById(R.id.activity_shenqingxiangxi_txt8);
        txt9=(TextView)findViewById(R.id.activity_shenqingxiangxi_txt9);
        txt10=(TextView)findViewById(R.id.activity_shenqingxiangxi_txt10);
        txt11=(TextView)findViewById(R.id.activity_shenqingxiangxi_txt11);
        btnpass=(Button)findViewById(R.id.activity_shenqingxiangxi_pass);
        btnpass.setOnClickListener(this);
        btnback=(Button)findViewById(R.id.activity_shenqingxiangxi_return);
        btnback.setOnClickListener(this);
        editText=(EditText)findViewById(R.id.activity_shenqingxiangxi_edittext);
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_shenqingxiangxi_recy);
    }

    @Override
    public void onClick(View view) {
        int vId=view.getId();
        if (vId == R.id.activity_shenqingxiangxi_imgback){
            AppManagerUtil.instance().finishActivity(ShenQingXiangXiActivity.this);
        }
        if (vId == R.id.activity_shenqingxiangxi_pass){
            Pass();
        }
        if (vId == R.id.activity_shenqingxiangxi_return){
            Back();
        }
    }

    private void SetTxt(){
        txt1.setText(RenWuAuditInfo.ShenQingRenMC);
        txt2.setText(RenWuAuditInfo.CJSJ);
        txt3.setText(RenWuAuditInfo.SQName);
        txt4.setText(RenWuAuditInfo.AuditHTH);
        txt5.setText(RenWuAuditInfo.KeHuMC);
        txt6.setText(RenWuAuditInfo.RegionName);
        txt7.setText(RenWuAuditInfo.RWName);
        switch (RenWuAuditInfo.AdtRst){
            case "WShenHe":
                txt8.setText("未审核");
                break;
            case "TGShenHe":
                txt8.setText("通过审核");
                break;
            case "WTGShenHe":
                txt8.setText("未通过审核");
                break;
            default:
                break;
        }
        txt9.setText(RenWuAuditInfo.AdtContent);
        switch (RenWuAuditInfo.ReAdtRst){
            case "WShenHe":
                txt10.setText("未审核");
                break;
            case "TGShenHe":
                txt10.setText("通过审核");
                break;
            case "WTGShenHe":
                txt10.setText("未通过审核");
                break;
            default:
                break;
        }
        txt11.setText(RenWuAuditInfo.ReAdtContent);
    }

    private void GetImage(String ProjSNo){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RW_GCProjFile_LieBiao_SP");
        reqData.ExtParams.put("cjSNo","admin");
        reqData.ExtParams.put("projSNo",ProjSNo);
        reqData.ExtParams.put("recordTotal","1");
        reqData.ExtParams.put("outParams","recordTotal");
        try {
            DialogUitl.showProgressDialog(ShenQingXiangXiActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(ShenQingXiangXiActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0) {
                                    if (resData.DataTable.length == 0) {
                                        panduantup=false;
                                    }else {
                                        for (int i = 0; i < resData.DataTable.length; i++) {
                                            panduantup=true;
                                            String url1 = resData.DataTable[i].get("FilePath");
                                            String url2 = resData.DataTable[i].get("FileName");
                                            String url = "http://www.hyramon.com.cn:14010" + url1 + "/" + url2;
                                            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 700, getContext().getResources().getDisplayMetrics());
                                            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 290f, getContext().getResources().getDisplayMetrics());
                                            list.add(url);
                                        }
                                        // 设置布局管理器
                                        mRecyclerView.setLayoutManager(mLayoutManager);
                                        // 设置adapter
                                        mRecyclerView.setAdapter(mAdapter);
                                    }
                                }else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            }catch (Exception e){
                                MethodUtil.showToast(e.getMessage(), context);
                            }finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }
                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog(reqData.CmdID);
                            MethodUtil.showToast(VolleyErrorHelper.getMessage(error, mContext), context);
                        }
                    });
        }catch (Exception e){
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    private void Pass(){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RW_GCProjPlanFshReq_TongGuo_SP");
        reqData.ExtParams.put("adtSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("adtContent",editText.getText().toString());
        reqData.ExtParams.put("cjSNo",RenWuAuditInfo.CJSNo);
        reqData.ExtParams.put("sqSNo",RenWuAuditInfo.SNo);
        try{
            DialogUitl.showProgressDialog(ShenQingXiangXiActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(ShenQingXiangXiActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){

                                }else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            }catch (Exception e){
                                MethodUtil.showToast(e.getMessage(), context);
                            }finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog(reqData.CmdID);
                            MethodUtil.showToast(VolleyErrorHelper.getMessage(error, mContext), context);
                        }
                    });
        }catch (Exception e){
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    private void Back(){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RW_GCProjPlanFshReq_HuiTui_SP");
        reqData.ExtParams.put("adtSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("adtContent",editText.getText().toString());
        reqData.ExtParams.put("cjSNo",RenWuAuditInfo.CJSNo);
        reqData.ExtParams.put("sqSNo",RenWuAuditInfo.SNo);
        try {
            DialogUitl.showProgressDialog(ShenQingXiangXiActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(ShenQingXiangXiActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){

                                }else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            }catch (Exception e){
                                MethodUtil.showToast(e.getMessage(), context);
                            }finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog(reqData.CmdID);
                            MethodUtil.showToast(VolleyErrorHelper.getMessage(error, mContext), context);
                        }
                    });
        }catch (Exception e){
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    protected void getDeviceDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Config.EXACT_SCREEN_HEIGHT = metrics.heightPixels;
        Config.EXACT_SCREEN_WIDTH = metrics.widthPixels;
    }


}
