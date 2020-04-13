package com.ramon.ramonrm.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.model.YongHuSQ;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

public class AuditUserDetailActivity extends BaseActivity implements View.OnClickListener {

    private YongHuSQ mYongHuSQ;
    private ImageView btnBack;
    private EditText txtDaiMa, txtBZ;
    private TextView lblXingMing, lblBuMen, lblIDCard, lblSex, lblPhone, lblEmail;
    private Button btnTongGuo, btnTuiHui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audituserdetail);
        String strYH = getIntent().getStringExtra("YongHuSQ");
        mYongHuSQ = GsonUtils.fromJson(strYH, YongHuSQ.class);
        initView();
        loadData();
    }

    private void initView() {
        btnBack = findViewById(R.id.activity_audituserdetail_imgback);
        btnBack.setOnClickListener(this);
        txtDaiMa = findViewById(R.id.activity_audituserdetail_txtdaima);
        txtBZ = findViewById(R.id.activity_audituserdetail_lblbz);
        lblXingMing = findViewById(R.id.activity_audituserdetail_lblxingming);
        lblBuMen = findViewById(R.id.activity_audituserdetail_lblbmmingcheng);
        lblIDCard = findViewById(R.id.activity_audituserdetail_lblidcard);
        lblSex = findViewById(R.id.activity_audituserdetail_lblsex);
        lblPhone = findViewById(R.id.activity_audituserdetail_lblphone);
        lblEmail = findViewById(R.id.activity_audituserdetail_lblemail);

        btnTongGuo = findViewById(R.id.activity_audituserdetail_btnTongGuo);
        btnTongGuo.setOnClickListener(this);
        btnTuiHui = findViewById(R.id.activity_audituserdetail_btnHuiTui);
        btnTuiHui.setOnClickListener(this);
    }

    private void loadData() {
        if (mYongHuSQ != null) {
            txtDaiMa.setText(mYongHuSQ.DaiMa);
            lblXingMing.setText(mYongHuSQ.XingMing);
            lblBuMen.setText(mYongHuSQ.BMMingCheng);
            lblIDCard.setText(mYongHuSQ.IDCard);
            lblSex.setText(mYongHuSQ.Sex);
            lblPhone.setText(mYongHuSQ.Mobile);
            lblEmail.setText(mYongHuSQ.Email);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            int vId = v.getId();
            if (vId == R.id.activity_audituserdetail_imgback) {
                AppManagerUtil.instance().finishActivity(this);
            }
            if (vId == R.id.activity_audituserdetail_btnTongGuo) {
                subitTongGuo();
            }
            if (vId == R.id.activity_audituserdetail_btnHuiTui) {
                subitHuiTui();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    private void subitTongGuo() {
        String strDM = txtDaiMa.getText().toString().trim();
        if (strDM.length() == 0) {
            MethodUtil.showToast("请输入用户名", context);
            return;
        }
        String strInfo = txtBZ.getText().toString().trim();
        ReqData reqData =ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecNoRtn,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "WX_YongHuSQ_TongGuo_SP");
        reqData.ExtParams.put("sno", mYongHuSQ.SNo);
        reqData.ExtParams.put("chuLiSM", strInfo);
        reqData.ExtParams.put("daiMa", strDM);
        reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
        DialogUitl.showProgressDialog(this, reqData.CmdID, "正在提交");
        try {
            VolleyRequestUtil.RequestPost(this, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(this, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    MethodUtil.showToast("提交完成", context);
                                    AppManagerUtil.instance().finishActivity(AuditUserDetailActivity.this);
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

    private void subitHuiTui() {

        String strInfo = txtBZ.getText().toString().trim();
        if (strInfo.length() == 0) {
            MethodUtil.showToast("请输入说明", context);
            return;
        }
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecNoRtn,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "WX_YongHuSQ_TuiHui_SP");
        reqData.ExtParams.put("sno", mYongHuSQ.SNo);
        reqData.ExtParams.put("chuLiSM", strInfo);
        reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
        DialogUitl.showProgressDialog(this, reqData.CmdID, "正在提交");
        try {
            VolleyRequestUtil.RequestPost(this, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(this, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    MethodUtil.showToast("提交完成", context);
                                    AppManagerUtil.instance().finishActivity(AuditUserDetailActivity.this);
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
