package com.ramon.ramonrm.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.model.UserInfo;
import com.ramon.ramonrm.user.RegUserActivity;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MD5Util;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyErrorHelper;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

public class LoginActivity extends BaseActivity {

    private EditText txtUserName, txtPassword;
    private Button btnLogin;
    private TextView lblReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            String[] qx = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.DISABLE_KEYGUARD,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WAKE_LOCK,
                    "android.hardware.camera",
                    "android.hardware.camera.autofocus"
            };
            ActivityCompat.requestPermissions(this, qx, 50);

            this.initView();
            boolean isLogout = getIntent().getBooleanExtra("IsLogout", false);
            if (!isLogout) {
                String uName = getParam("username", "").toString();
                String uPassword = getParam("password", "").toString();
                if (uName.length() > 0) {
                    txtUserName.setText(uName);
                    txtPassword.setText(uPassword);
                    uName = uName + "|Android|" + Session.ClientId;
                    login(uName, uPassword);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initView() {
        txtUserName = (EditText) findViewById(R.id.activity_login_txtusername);
        txtPassword = (EditText) findViewById(R.id.activity_lgoin_txtpassword);
        btnLogin = (Button) findViewById(R.id.activity_login_btnlogin);
        btnLogin.setOnClickListener(new MyOnClickLisener());
        lblReg = (TextView) findViewById(R.id.activity_login_lblreg);
        lblReg.setOnClickListener(new MyOnClickLisener());
    }

    private class MyOnClickLisener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            try {
                int vID = view.getId();
                if (vID == R.id.activity_login_btnlogin) {
                    //登录
                    String uName = txtUserName.getText().toString() + "|Android|" + Session.ClientId;
                    String uPassword = txtPassword.getText().toString();
                    login(uName,uPassword);
                } else if (vID == R.id.activity_login_lblreg) {
                    //注册
                    Intent intent = new Intent(LoginActivity.this, RegUserActivity.class);
                    startActivity(intent);
                }
            } catch (Exception ex) {
                MethodUtil.showToast(ex.getMessage(), context);
            }
        }
    }

    private void login(String uName,String uPassword) {
        String md5PSW = MD5Util.Hash(uPassword);
        if (uName.length() == 0 || uPassword.length() == 0) {
            MethodUtil.showToast("用户名和密码不能为空", context);
        } else {
            ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_CmdExec_Sys_Login,"","");
            reqData.ExtParams.put("userCode", uName);//账号
            reqData.ExtParams.put("userPwd", md5PSW);
            reqData.ExtParams.put("openID", uName);//账号
            try {
                DialogUitl.showProgressDialog(LoginActivity.this, reqData.CmdID, "正在登录");
                VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                        new VolleyListenerInterface(LoginActivity.this, reqData) {
                            // Volley请求成功时调用的函数
                            @Override
                            public void onMySuccess(ReqData reqData, String result) {
                                try {
                                    ResData resData = GsonUtils.fromJson(result,ResData.class);
                                    if (resData.RstValue == 0) {
                                        Session.SessionId = resData.DataValues.get("sessionID");
                                        Session.ValidMD5 = resData.DataValues.get("validMD5");
                                        Session.CurrUser = new UserInfo();
                                        Session.CurrUser.DeptSNames = resData.dataRowTable[0].get("DeptSNames");
                                        Session.CurrUser.DeptSNos = resData.dataRowTable[0].get("DeptSNos");
                                        Session.CurrUser.YongHuSNo = resData.dataRowTable[0].get("YongHuSNo");
                                        Session.CurrUser.YuanGongSNo = resData.dataRowTable[0].get("YuanGongSNo");
                                        Session.CurrUser.Name = resData.dataRowTable[0].get("Name");
                                        Session.CurrUser.Sex = resData.dataRowTable[0].get("Sex");
                                        Session.CurrUser.YongHuLB = resData.dataRowTable[0].get("YongHuLB");
                                        Session.LoginTime = System.currentTimeMillis();
                                        saveParam("username", txtUserName.getText().toString());
                                        saveParam("password",txtPassword.getText().toString());
                                        startActivity(HomeActivity.class);
                                        AppManagerUtil.instance().finishActivity(LoginActivity.this);
                                    } else {
                                        MethodUtil.showToast(resData.RstMsg, context);
                                    }
                                } catch (Exception ex) {
                                    MethodUtil.showToast(ex.getMessage(), context);
                                } finally {
                                    DialogUitl.dismissProgressDialog(reqData.CmdID);
                                }
                            }

                            // Volley请求失败时调用的函数
                            @Override
                            public void onMyError(ReqData reqData, VolleyError error) {
                                error.printStackTrace();
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                                MethodUtil.showToast(VolleyErrorHelper.getMessage(error, mContext), context);
                            }
                        });
            } catch (Exception e) {
                MethodUtil.showToast(e.getMessage(), context);
            }
        }
    }
}
