package com.ramon.ramonrm.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MD5Util;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.util.PreferencesUtil;
import com.ramon.ramonrm.volley.VolleyErrorHelper;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

import static com.baidu.mapapi.BMapManager.getContext;

public class PasswordActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton imgBack;
    private EditText txtOldPsw, txtNewPsw, txtConfPsw;
    private Button btnOK;//确认改变密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        initView();
    }

    private void initView() {
        imgBack = findViewById(R.id.activity_password_imgback);
        imgBack.setOnClickListener(this);
        txtOldPsw = findViewById(R.id.activity_password_txtoldpassword);
        txtNewPsw = findViewById(R.id.activity_password_txtoldpassword);
        txtConfPsw = findViewById(R.id.activity_password_txtoldpassword);
        btnOK = findViewById(R.id.activity_password_btnok);
        btnOK.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        try {
            int vId = view.getId();
            if (vId == R.id.activity_password_imgback) {
                AppManagerUtil.instance().finishActivity(PasswordActivity.this);
            }
            if (vId == R.id.activity_password_btnok) {
                String oldPsw = txtOldPsw.getText().toString();
                if (oldPsw.length() == 0) {
                    MethodUtil.showToast("请输入旧密码", context);
                    return;
                }
                String newPsw = txtNewPsw.getText().toString();
                if (newPsw.length() == 0) {
                    MethodUtil.showToast("请输入新密码", context);
                    return;
                }
                String confPsw = txtConfPsw.getText().toString();
                if (!newPsw.equals(confPsw)) {
                    MethodUtil.showToast("新密码与确认密码不相同", context);
                    return;
                }
                changePassword(oldPsw, newPsw);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    private void changePassword(String oldPsw, final String newPsw) {
        ReqData reqData =ReqData.createReqData(ReqData.ReqType.T_SQLExec_SysSql_SPExecNoRtn,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "YH_YongHu_XiuGaiMM_SP");
        reqData.ExtParams.put("sno", Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("cJSNo", Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("oldMM", MD5Util.Hash(oldPsw));
        reqData.ExtParams.put("newMM", MD5Util.Hash(newPsw));
        try {
            DialogUitl.showProgressDialog(PasswordActivity.this, reqData.CmdID, "正在修改密码");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(PasswordActivity.this, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                //Log.e("zzz",result);
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    PreferencesUtil.getInstance(getContext()).saveParam("password", newPsw);
                                    txtConfPsw.setText("");
                                    txtOldPsw.setText("");
                                    txtNewPsw.setText("");
                                    MethodUtil.showToast("修改密码成功", context);
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            } catch (Exception e) {
                                MethodUtil.showToast(e.getMessage(), context);
                            } finally {
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
        } catch (Exception E) {
            MethodUtil.showToast(E.getMessage(), context);
        }
    }
}
