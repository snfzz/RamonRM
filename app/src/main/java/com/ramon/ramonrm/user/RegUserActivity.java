package com.ramon.ramonrm.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.controls.PickerMultiColumnDialog;
import com.ramon.ramonrm.home.HomeActivity;
import com.ramon.ramonrm.home.LoginActivity;
import com.ramon.ramonrm.model.PickerMultiColumnData;
import com.ramon.ramonrm.model.UserInfo;
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
import java.util.HashMap;
import java.util.List;

public class RegUserActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton imgBack;
    private EditText txtXingMing, txtCardID, txtPhone, txtEmail;
    private TextView lblDeptName;
    private RadioButton rbMan, rbWoman,rbYuanGong,rbKeHu;
    private Button btnReg;
    private List<PickerMultiColumnData>listDeptData;
    private PickerMultiColumnData selectedPickerData;
    private List<PickerMultiColumnData>listGroupData;
    private PickerMultiColumnData selectedGroupData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reguser);
        initView();
        loadDeptName();
        loadGroupName();
    }

    private void initView() {
        imgBack = findViewById(R.id.activity_reguser_imgback);
        imgBack.setOnClickListener(this);
        lblDeptName = findViewById(R.id.activity_reguser_lbldeptname);
        lblDeptName.setOnClickListener(this);
        txtXingMing = findViewById(R.id.activity_reguser_txtxingming);
        txtCardID = findViewById(R.id.activity_reguser_txtcardid);
        txtPhone = findViewById(R.id.activity_reguser_txtphone);
        txtEmail = findViewById(R.id.activity_reguser_txtemail);
        rbMan = findViewById(R.id.activity_reguser_rbman);
        rbWoman = findViewById(R.id.activity_reguser_rbwoman);
        rbYuanGong = findViewById(R.id.activity_reguser_rbygyh);
        rbYuanGong.setOnClickListener(this);
        rbKeHu = findViewById(R.id.activity_reguser_rbkhyh);
        rbKeHu.setOnClickListener(this);
        btnReg = findViewById(R.id.activity_reguser_btnreg);
        btnReg.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        try {
            int vId = view.getId();
            if (vId == R.id.activity_reguser_imgback) {
                AppManagerUtil.instance().finishActivity(this);
            }
            if (vId == R.id.activity_reguser_lbldeptname) {
                if (rbYuanGong.isChecked()) {
                    PickerMultiColumnDialog dlg = new PickerMultiColumnDialog(this, listDeptData, new PickerMultiColumnDialog.PickedListener() {
                        @Override
                        public void onAddressPicked(PickerMultiColumnData data1, PickerMultiColumnData data2, PickerMultiColumnData data3) {
                            if (data3 != null) {
                                selectedPickerData = data3;
                                lblDeptName.setText(data3.Title);
                            } else if (data2 != null) {
                                selectedPickerData = data2;
                                lblDeptName.setText(data2.Title);
                            } else if (data1 != null) {
                                selectedPickerData = data1;
                                lblDeptName.setText(data1.Title);
                            }
                        }
                    });
                    dlg.showDialog();
                } else {
                    PickerMultiColumnDialog dlg = new PickerMultiColumnDialog(this, listGroupData, new PickerMultiColumnDialog.PickedListener() {
                        @Override
                        public void onAddressPicked(PickerMultiColumnData data1, PickerMultiColumnData data2, PickerMultiColumnData data3) {
                            if (data3 != null) {
                                selectedGroupData = data3;
                                lblDeptName.setText(data3.Title);
                            } else if (data2 != null) {
                                selectedGroupData = data2;
                                lblDeptName.setText(data2.Title);
                            } else if (data1 != null) {
                                selectedGroupData = data1;
                                lblDeptName.setText(data1.Title);
                            }
                        }
                    });
                    dlg.showDialog();
                }
            }
            if (vId == R.id.activity_reguser_btnreg) {
                MethodUtil.hideSoftInputFromActivity(this);
                regUser();
            }
            if (vId == R.id.activity_reguser_rbygyh) {
                lblDeptName.setText(selectedPickerData.Title);
            }
            if (vId == R.id.activity_reguser_rbkhyh) {
                lblDeptName.setText(selectedGroupData.Title);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            MethodUtil.showToast(ex.getMessage(), context);
        }
    }

    private void loadDeptName() {
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSqlZC_SPExecForTable,"","");
        reqData.ExtParams.put("spName", "RS_Dept_ListAll_SP");
        try {
            DialogUitl.showProgressDialog(RegUserActivity.this, reqData.CmdID, "正在加载数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(RegUserActivity.this, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadDeptView(resData.DataTable);
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

    private void loadDeptView(HashMap<String, String>[] hashData) {
        listDeptData = new ArrayList<>();
        for (int i = 0; i < hashData.length; i++) {
            HashMap<String, String> hashMap = hashData[i];
            int lvlIndex = Integer.parseInt(hashMap.get("LvlIndex"));
            if (lvlIndex == 0) {
                PickerMultiColumnData data = new PickerMultiColumnData();
                data.Level = lvlIndex;
                data.Key = hashMap.get("SNo").trim();
                data.Title = hashMap.get("SName").trim();
                data.ParentKey = "";
                listDeptData.add(data);
                loadPickerData( data, hashData);
            }
        }
        if(listDeptData.size()>0) {
            if (listDeptData.get(0).Children.size() == 0) {
                selectedPickerData = listDeptData.get(0);
                lblDeptName.setText(selectedPickerData.Title);
            } else if (listDeptData.get(0).Children.get(0).Children.size() == 0) {
                selectedPickerData = listDeptData.get(0).Children.get(0);
                lblDeptName.setText(selectedPickerData.Title);
            } else {
                selectedPickerData = listDeptData.get(0).Children.get(0).Children.get(0);
                lblDeptName.setText(selectedPickerData.Title);
            }
        }
    }

    private void loadPickerData(PickerMultiColumnData currData, HashMap<String, String>[] hashData) {
        for (int i = 0; i < hashData.length; i++) {
            HashMap<String, String> hashMap = hashData[i];
            String parentKey = hashMap.get("PSNo").trim();
            if (parentKey.equals(currData.Key)) {
                PickerMultiColumnData data = new PickerMultiColumnData();
                data.Level = Integer.parseInt(hashMap.get("LvlIndex"));
                data.Key = hashMap.get("SNo").trim();
                data.Title = hashMap.get("SName").trim();
                data.ParentKey = parentKey;
                currData.Children.add(data);
                loadPickerData(data, hashData);
            }
        }
    }

    private void loadGroupName(){
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSqlZC_SPExecForTable,"","");
        reqData.ExtParams.put("spName", "KH_Group_LieBiao_SP");
        try {
            DialogUitl.showProgressDialog(RegUserActivity.this, reqData.CmdID, "正在加载数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(RegUserActivity.this, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    listGroupData = new ArrayList<>();
                                    for (int i = 0; i < resData.DataTable.length; i++) {
                                        HashMap<String, String> hashMap = resData.DataTable[i];
                                        int lvlIndex = Integer.parseInt(hashMap.get("LvlIndex"));
                                        if (lvlIndex == 0) {
                                            PickerMultiColumnData data = new PickerMultiColumnData();
                                            data.Level = lvlIndex;
                                            data.Key = hashMap.get("SNo").trim();
                                            data.Title = hashMap.get("Name").trim();
                                            data.ParentKey = "";
                                            listGroupData.add(data);
                                            loadGroupData(data, resData.DataTable);
                                        }
                                    }
                                    if(listGroupData.size()>0) {
                                        if (listGroupData.get(0).Children.size() == 0) {
                                            selectedGroupData = listGroupData.get(0);
                                        } else if (listGroupData.get(0).Children.get(0).Children.size() == 0) {
                                            selectedGroupData = listGroupData.get(0).Children.get(0);
                                        } else {
                                            selectedGroupData = listGroupData.get(0).Children.get(0).Children.get(0);
                                        }
                                    }
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

    private void loadGroupData(PickerMultiColumnData currData,HashMap<String,String>[]hashData){
        for (int i = 0; i < hashData.length; i++) {
            HashMap<String, String> hashMap = hashData[i];
            String parentKey = hashMap.get("PSNo").trim();
            if (parentKey.equals(currData.Key)) {
                PickerMultiColumnData data = new PickerMultiColumnData();
                data.Level = Integer.parseInt(hashMap.get("LvlIndex"));
                data.Key = hashMap.get("SNo").trim();
                data.Title = hashMap.get("Name").trim();
                data.ParentKey = parentKey;
                currData.Children.add(data);
                loadGroupData(data, hashData);
            }
        }
    }

    private void regUser() {
        if (selectedPickerData == null && rbYuanGong.isChecked()) {
            MethodUtil.showToast("请选择部门", context);
            return;
        }
        if (selectedGroupData == null && rbKeHu.isChecked()) {
            MethodUtil.showToast("请选择部门", context);
            return;
        }
        String deptSNo = "";
        if(rbYuanGong.isChecked()){
            deptSNo = selectedPickerData.Key;
        }
        else{
            deptSNo = selectedGroupData.Key;
        }
        String strXingMing = txtXingMing.getText().toString().trim();
        if (strXingMing.length() == 0) {
            MethodUtil.showToast("请输入姓名", context);
            return;
        }
        String cardID = txtCardID.getText().toString().trim();
        if (cardID.length() < 10) {
            MethodUtil.showToast("请输入身份证", context);
            return;
        }
        String phone = txtPhone.getText().toString().trim();
        String eMail = txtEmail.getText().toString().trim();
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSqlZC_SPExecNoRtn,"","");
        reqData.ExtParams.put("spName", "WX_YongHuSQ_NewYongHu_SP");
        reqData.ExtParams.put("buMenSNo", deptSNo);
        reqData.ExtParams.put("xingMing", strXingMing);
        reqData.ExtParams.put("sex", rbMan.isChecked() ? rbMan.getText().toString() : rbWoman.getText().toString());
        reqData.ExtParams.put("IDCard", cardID);
        reqData.ExtParams.put("mobile", phone);
        reqData.ExtParams.put("email", eMail);
        reqData.ExtParams.put("yonghuLB", rbYuanGong.isChecked()?"YGYH":"KHYH");
        try {
            DialogUitl.showProgressDialog(RegUserActivity.this, reqData.CmdID, "正在提交注册申请");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(RegUserActivity.this, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    MethodUtil.showToast("注册提交成功",context);
                                    txtCardID.setText("");
                                    txtEmail.setText("");
                                    txtPhone.setText("");
                                    txtXingMing.setText("");
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
