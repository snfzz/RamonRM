package com.ramon.ramonrm.renyuan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.KeHuAdapter;
import com.ramon.ramonrm.model.KeHuInfo;
import com.ramon.ramonrm.model.MsgTaskInfo;
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

public class WorkMsgEditActivity extends BaseActivity implements View.OnClickListener {

    private String[] taskTypes = new String[]{"出差会议", "出差检验", "公司上班", "现场出差", "休假"};
    private int mTaskTypeIndex = 0;
    private AlertDialog dlgTaskType;
    PopupWindow popupWindow;

    private ImageView btnBack, imgSave;
    private EditText txtTask, txtMobile, txtContent;
    private TextView lblTaskType, lblLoc;
    private MsgTaskInfo currMsgTask;
    private Button btnSave;
    private LinearLayout lyMain;

    private ListView lvFactory;
    private EditText txtKey;
    private List<KeHuInfo> listKHInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workmsgedit);
        initView();
        loadTaskData();
    }

    private void initView() {
        btnBack = (ImageView) findViewById(R.id.activity_workmsgedit_imgback);
        btnBack.setOnClickListener(this);
        txtTask = (EditText) findViewById(R.id.activity_workmsgedit_txttask);
        txtMobile = (EditText) findViewById(R.id.activity_workmsgedit_txtmobile);
        txtContent = (EditText) findViewById(R.id.activity_workmsgedit_txtcontent);
        lblTaskType = (TextView) findViewById(R.id.activity_workmsgedit_lbltype);
        lblTaskType.setOnClickListener(this);
        lblLoc = (TextView) findViewById(R.id.activity_workmsgedit_lbldidian);
        lblLoc.setOnClickListener(this);
        btnSave = (Button) findViewById(R.id.activity_workmsgedit_btnsave);
        btnSave.setOnClickListener(this);
        imgSave = (ImageView) findViewById(R.id.activity_workmsgedit_imgsave);
        imgSave.setOnClickListener(this);
        lyMain = (LinearLayout) findViewById(R.id.activity_workmsgedit_lymain);
    }

    @Override
    public void onClick(View v) {
        try {
            int vId = v.getId();
            if (vId == R.id.activity_workmsgedit_imgback) {
                AppManagerUtil.instance().finishActivity(WorkMsgEditActivity.this);
            }
            if (vId == R.id.activity_workmsgedit_lbltype) {
                selectTaskType();
            }
            if (vId == R.id.activity_workmsgedit_lbldidian) {
                selectLoc();
            }
            if (vId == R.id.activity_workmsgedit_btnsave || vId == R.id.activity_workmsgedit_imgsave) {
                saveMsg();
            }
            if (vId == R.id.dialog_taskloc_imgquery) {
                String key = txtKey.getText().toString().trim();
                if (key.length() == 0) {
                    MethodUtil.showToast("请输入关键字", this);
                    return;
                }
                loadFactory(key);
            }
            if (vId == R.id.dialog_taskloc_imgsubmit) {
                String strInput = txtKey.getText().toString().trim();
                if (strInput.length() == 0) {
                    MethodUtil.showToast("请输入关键字", this);
                    return;
                }
                lblLoc.setText(strInput);
                lblLoc.setTag(txtKey.getTag());
                popupWindow.dismiss();
            }
        } catch (Exception ex) {
            MethodUtil.showToast(ex.getMessage(), context);
        }
    }

    private void selectTaskType() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("任务类别");
        alertBuilder.setSingleChoiceItems(taskTypes, mTaskTypeIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mTaskTypeIndex = i;
                lblTaskType.setText(taskTypes[i]);
                dlgTaskType.dismiss();
            }
        });
        dlgTaskType = alertBuilder.create();
        dlgTaskType.show();
    }

    private void selectLoc() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_taskloc, null);
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        txtKey = (EditText) view.findViewById(R.id.dialog_taskloc_txtkey);
        ImageView btnQuery = (ImageView) view.findViewById(R.id.dialog_taskloc_imgquery);
        btnQuery.setOnClickListener(this);
        ImageView btnSubmit = (ImageView) view.findViewById(R.id.dialog_taskloc_imgsubmit);
        btnSubmit.setOnClickListener(this);
        lvFactory = (ListView) view.findViewById(R.id.dialog_taskloc_lvfactory);
        popupWindow = new PopupWindow(view, lyMain.getWidth(), lyMain.getHeight() / 2);
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(false);
        popupWindow.showAsDropDown(lyMain, 0, lyMain.getHeight() / 4);
    }

    private void loadTaskData() {
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_X5Sql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "RM_SMS_GetUserInfo");
        reqData.ExtParams.put("empName", Session.CurrUser.Name);
        DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
        try {
            VolleyRequestUtil.RequestPost(this, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(this, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    if (resData.DataTable.length > 0) {
                                        HashMap<String, String> hashMap = resData.DataTable[0];
                                        currMsgTask = new MsgTaskInfo();
                                        currMsgTask.KHID = hashMap.get("KHID");
                                        currMsgTask.Loc = hashMap.get("Loc");
                                        currMsgTask.Mobile = hashMap.get("Mobile");
                                        currMsgTask.Task = hashMap.get("Task");
                                        currMsgTask.TaskType = hashMap.get("TaskType");
                                        txtTask.setText(currMsgTask.Task);
                                        txtMobile.setText(currMsgTask.Mobile);
                                        lblTaskType.setText(currMsgTask.TaskType);
                                        lblLoc.setText(currMsgTask.Loc);
                                        KeHuInfo khInfo = new KeHuInfo();
                                        khInfo.MingCheng = currMsgTask.Loc;
                                        khInfo.FKID = currMsgTask.KHID;
                                        lblLoc.setTag(khInfo);
                                    }
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, mContext);
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), mContext);
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

    private void loadFactory(String key) {
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        try {
            reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
            reqData.ExtParams.put("spName", "KH_KeHu_LieBiao_All_SP");
            reqData.ExtParams.put("mingCheng", key);
            reqData.ExtParams.put("outParams", "recordTotal");
            reqData.ExtParams.put("recordTotal", "-1");
            DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(this, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(this, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    listKHInfo = new ArrayList<>();
                                    if (resData.DataTable != null) {
                                        for (int i = 0; i < resData.DataTable.length; i++) {
                                            HashMap<String, String> hashMap = resData.DataTable[i];
                                            KeHuInfo khInfo = new KeHuInfo();
                                            khInfo.FKID = hashMap.get("FKID");
                                            khInfo.JianCheng = hashMap.get("JianCheng");
                                            khInfo.MingCheng = hashMap.get("MingCheng");
                                            khInfo.QuYu = hashMap.get("QuYu");
                                            khInfo.SNo = hashMap.get("SNo");
                                            listKHInfo.add(khInfo);
                                        }
                                    }
                                    KeHuAdapter adapter = new KeHuAdapter(context, R.layout.listitem_kehuinfo, listKHInfo);
                                    lvFactory.setAdapter(adapter);
                                    lvFactory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            try {
                                                KeHuInfo khInfo = listKHInfo.get(position);
                                                txtKey.setText(khInfo.MingCheng);
                                                txtKey.setTag(khInfo);
                                            } catch (Exception e) {
                                                MethodUtil.showToast(e.getMessage(), context);
                                            }
                                        }
                                    });
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
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveMsg() {
        String strTask = txtTask.getText().toString().trim();
        String strTaskType = lblTaskType.getText().toString().trim();
        String strMobile = txtMobile.getText().toString().trim();
        String strContent = txtContent.getText().toString().trim();
        String strLoc = lblLoc.getText().toString().trim();
        String strKHID = "";
        KeHuInfo khInfo = (KeHuInfo) lblLoc.getTag();
        if (khInfo != null && khInfo.MingCheng.equals(strLoc)) {
            strKHID = khInfo.FKID;
        }
        if (strTask.length() == 0) {
            MethodUtil.showToast("请输入任务名称", context);
            return;
        }
        if (strTaskType.length() == 0) {
            MethodUtil.showToast("请选择任务类别", context);
            return;
        }
        if (strMobile.length() == 0) {
            MethodUtil.showToast("请输入联系方式", context);
            return;
        }
        if (strLoc.length() == 0) {
            MethodUtil.showToast("请选择工作地点", context);
            return;
        }
        if (strContent.length() == 0) {
            MethodUtil.showToast("请输入短信内容", context);
            return;
        }
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_X5Sql_SPExecNoRtn,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "RM_SMS_Add");
        reqData.ExtParams.put("empName", Session.CurrUser.Name);
        reqData.ExtParams.put("taskType", strTaskType);
        reqData.ExtParams.put("task", strTask);
        reqData.ExtParams.put("loc", strLoc);
        reqData.ExtParams.put("khID", strKHID);
        reqData.ExtParams.put("mobile", strMobile);
        reqData.ExtParams.put("content", strContent);
        reqData.ExtParams.put("containWT", "0");
        reqData.ExtParams.put("inputEmp", Session.CurrUser.Name);
        DialogUitl.showProgressDialog(this, reqData.CmdID, "正在提交短信……");
        try {
            VolleyRequestUtil.RequestPost(this, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(this, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    MethodUtil.showToast("短信提交成功", mContext);
                                    AppManagerUtil.instance().finishActivity(WorkMsgEditActivity.this);
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, mContext);
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), mContext);
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
