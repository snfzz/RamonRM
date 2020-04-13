package com.ramon.ramonrm.project;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.GuardedBy;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.GridViewImageAdapter;
import com.ramon.ramonrm.model.PlanReqInfo;
import com.ramon.ramonrm.model.ShowText;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlanShenHeActivity extends BaseActivity  implements View.OnClickListener {

    public final  static  int RST_TongGuo = 1;
    public final  static int RST_HuiTui = -1;
    public final  static int RST_None = 0;

    private ImageButton btnBack;
    private TextView lblSQRen, lblSQTime, lblSQName, lblHTH, lblKeHuMC, lblRegionName, lblNeiRong, lblAdtRst, lblAdtContent, lblReAdtRst, lblReAdtContent;
    private EditText txtContent;
    private Button btnTongGuo, btnHuiTui;

    private PlanReqInfo mReqInfo;

    private GridView gvImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planshenhe);
        String reqInfo = getIntent().getStringExtra("PlanReqInfo");
        mReqInfo = GsonUtils.fromJson(reqInfo, PlanReqInfo.class);
        initView();
        loadData();
        loadImage();
    }

    private void initView() {
        btnBack = findViewById(R.id.activity_planshenhe_imgback);
        btnBack.setOnClickListener(this);
        lblSQRen = findViewById(R.id.activity_planshenhe_lblsqr);
        lblSQTime = findViewById(R.id.activity_planshenhe_lblsqtime);
        lblSQName = findViewById(R.id.activity_planshenhe_lblsqtime);
        lblHTH = findViewById(R.id.activity_planshenhe_lblhth);
        lblKeHuMC = findViewById(R.id.activity_planshenhe_lblkehumc);
        lblRegionName = findViewById(R.id.activity_planshenhe_lblquyu);
        lblNeiRong = findViewById(R.id.activity_planshenhe_lblneirong);
        lblAdtRst = findViewById(R.id.activity_planshenhe_lbladtrst);
        lblAdtContent = findViewById(R.id.activity_planshenhe_lbladtcontent);
        lblReAdtRst = findViewById(R.id.activity_planshenhe_lblreadtrst);
        lblReAdtContent = findViewById(R.id.activity_planshenhe_lblreadtcontent);
        txtContent = findViewById(R.id.activity_planshenhe_txtcontent);
        btnTongGuo = findViewById(R.id.activity_planshenhe_btntongguo);
        btnTongGuo.setOnClickListener(this);
        btnHuiTui = findViewById(R.id.activity_planshenhe_btntuihui);
        btnHuiTui.setOnClickListener(this);
        gvImage = findViewById(R.id.activity_planshenhe_gvimage);
    }

    private void loadData() {
        lblSQRen.setText(mReqInfo.ShenQingRenMC);
        lblSQTime.setText(mReqInfo.ReqDT);
        lblSQName.setText(mReqInfo.SQName);
        lblHTH.setText(mReqInfo.HTH);
        lblKeHuMC.setText(mReqInfo.KeHuMC);
        lblRegionName.setText(mReqInfo.RegionName);
        lblNeiRong.setText(mReqInfo.RWName);
        lblAdtRst.setText(mReqInfo.AdtRstTitle);
        lblAdtContent.setText(mReqInfo.AdtContent);
        lblReAdtRst.setText(mReqInfo.ReAdtRstTitle);
        lblReAdtContent.setText(mReqInfo.ReAdtContent);
    }

    @Override
    public void onClick(View v) {
        try {
            int vId = v.getId();
            if (vId == R.id.activity_planshenhe_imgback) {
                setResult(RST_None);
                AppManagerUtil.instance().finishActivity(this);
            }
            if (vId == R.id.activity_planshenhe_btntongguo) {
                submitShenHe(true);
            }
            if (vId == R.id.activity_planshenhe_btntuihui) {
                submitShenHe(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    private void submitShenHe(final boolean isTongGuo) {
        String content = txtContent.getText().toString().trim();
        if(!isTongGuo && content.length() == 0) {
            MethodUtil.showToast("请输入审核意见", context);
            return;
        }
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecNoRtn,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RW_GCProjPlanFshReq_TongGuo_SP");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("adtSNo",isTongGuo?"TGShenHe":"WTGShenHe");
        reqData.ExtParams.put("sqSNo",mReqInfo.SNo);
        reqData.ExtParams.put("adtContent",content);
        try {
            DialogUitl.showProgressDialog(context, reqData.CmdID, "正在提交审核……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(context, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    setResult(isTongGuo?RST_TongGuo:RST_HuiTui);
                                    AppManagerUtil.instance().finishActivity(PlanShenHeActivity.this);
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

    private void loadImage() {
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "RW_GCProjFile_LieBiao_SP");
        reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("outParams", "recordTotal");
        reqData.ExtParams.put("recordTotal", "1");
        reqData.ExtParams.put("projSno", mReqInfo.ProjSNo);
        reqData.ExtParams.put("planSNo", mReqInfo.PlanSNo);
        DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
        try {
            VolleyRequestUtil.RequestPost(this, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(this, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    List<ShowText> listShowText = new ArrayList<>();
                                    for (int i = 0; i < resData.DataTable.length; i++) {
                                        HashMap<String, String> hashMap = resData.DataTable[i];
                                        ShowText sText = new ShowText();
                                        String filePath = hashMap.get("FilePath");
                                        String fileName = hashMap.get("FileName");
                                        sText.Title = fileName;
                                        sText.Key = APIConfig.APIHOST + "/" + filePath + "/" + fileName;
                                        listShowText.add(sText);
                                    }
                                    GridViewImageAdapter adapter = new GridViewImageAdapter(context, R.layout.griditem_imageview, listShowText);
                                    gvImage.setAdapter(adapter);
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
}
