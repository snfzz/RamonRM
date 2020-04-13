package com.ramon.ramonrm.renyuan;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.WorkMsgAdapter;
import com.ramon.ramonrm.model.WorkMsgInfo;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.DateTimeUtil;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WorkMsgFragment extends Fragment implements View.OnClickListener {

    Calendar calBegin = Calendar.getInstance(Locale.CHINA);
    Calendar calEnd = Calendar.getInstance(Locale.CHINA);

    private Activity mActivity;
    private ListView lvWorkMsg;
    private ImageView imgFilter,imgAdd;
    private Button btnQuery;
    private LinearLayout lyCondition;

    private EditText txtEmpName,txtRegName,txtKeHuName;
    private TextView lblBeginDate,lblEndDate,lblBeginDateTitle,lblEndDateTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //不同的Activity对应不同的布局
        View view = inflater.inflate(R.layout.fragment_workmsg, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        lvWorkMsg = (ListView) mActivity.findViewById(R.id.fragment_workmsg_lvworkmsg);
        imgFilter = (ImageView) mActivity.findViewById(R.id.fragment_workmsg_imgfilter);
        imgFilter.setOnClickListener(this);
        imgAdd = (ImageView) mActivity.findViewById(R.id.fragment_workmsg_imgadd);
        imgAdd.setOnClickListener(this);
        btnQuery = (Button) mActivity.findViewById(R.id.fragment_workmsg_btnquery);
        btnQuery.setOnClickListener(this);
        lyCondition = (LinearLayout) mActivity.findViewById(R.id.fragment_workmsg_rlaycondition);
        txtEmpName = (EditText) mActivity.findViewById(R.id.fragment_workmsg_txtempname);
        txtRegName = (EditText) mActivity.findViewById(R.id.fragment_workmsg_txtregname);
        txtKeHuName = (EditText) mActivity.findViewById(R.id.fragment_workmsg_txtkehuname);
        lblBeginDate = (TextView) mActivity.findViewById(R.id.fragment_workmsg_lblbegindate);
        lblBeginDate.setOnClickListener(this);
        lblBeginDateTitle = (TextView)mActivity.findViewById(R.id.fragment_workmsg_lblbegindatetitle);
        lblBeginDateTitle.setOnClickListener(this);
        lblEndDate = (TextView) mActivity.findViewById(R.id.fragment_workmsg_lblenddate);
        lblEndDate.setOnClickListener(this);
        lblEndDateTitle = (TextView)mActivity.findViewById(R.id.fragment_workmsg_lblenddatetitle);
        lblEndDateTitle.setOnClickListener(this);
        long dtNow = System.currentTimeMillis();
        lblEndDate.setText(DateTimeUtil.DateTimeToString(dtNow, "yyyy-MM-dd"));
        dtNow = dtNow - 24 * 60 * 60 * 1000;
        lblBeginDate.setText(DateTimeUtil.DateTimeToString(dtNow, "yyyy-MM-dd"));
        loadWorkMsgData();
    }
    @Override
    public void onClick(View v) {
        try {
            MethodUtil.hideSoftInputFromFragment(getView());
            int vId = v.getId();
            if (vId == R.id.fragment_workmsg_imgfilter) {
                //region 显示/隐藏搜索框
                if (lyCondition.getVisibility() == View.VISIBLE) {
                    lyCondition.setVisibility(View.GONE);
                } else {
                    lyCondition.setVisibility(View.VISIBLE);
                }
                //endregion
            }
            if (vId == R.id.fragment_workmsg_btnquery) {
                //region查询按钮
                loadWorkMsgData();
                //endregion
            }
            if (vId == R.id.fragment_workmsg_imgadd) {
                //region 写日志
                Intent intent = new Intent(mActivity, WorkMsgEditActivity.class);
                mActivity.startActivity(intent);
                //endregion
            }
            if (vId == R.id.fragment_workmsg_lblbegindate|| vId==R.id.fragment_workmsg_lblbegindatetitle) {
                //region 开始日期
                new DatePickerDialog(mActivity,
                        // 绑定监听器
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String strYear = year + "";
                                String strMonth = month < 9 ? "0" + (month + 1) : "" + (month + 1);
                                String strDay = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                                lblBeginDate.setText(strYear + "-" + strMonth + "-" + strDay);
                                calBegin.set(year, month, dayOfMonth);
                            }
                        }
                        , calBegin.get(Calendar.YEAR)
                        , calBegin.get(Calendar.MONTH)
                        , calBegin.get(Calendar.DAY_OF_MONTH)).show();
                //endregion
            }
            if (vId == R.id.fragment_workmsg_lblenddate|| vId==R.id.fragment_workmsg_lblenddatetitle) {
                //region 结束日期
                new DatePickerDialog(mActivity,
                        // 绑定监听器
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String strYear = year + "";
                                String strMonth = month < 9 ? "0" + (month + 1) : "" + (month + 1);
                                String strDay = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                                lblEndDate.setText(strYear + "-" + strMonth + "-" + strDay);
                                calEnd.set(year,month,dayOfMonth);
                            }
                        }
                        , calEnd.get(Calendar.YEAR)
                        , calEnd.get(Calendar.MONTH)
                        , calEnd.get(Calendar.DAY_OF_MONTH)).show();
                //endregion
            }
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), mActivity);
        }
    }

    private void loadWorkMsgData() {
        String beginDate = lblBeginDate.getText().toString();
        String endDate = lblEndDate.getText().toString();
        String empName = txtEmpName.getText().toString();
        String regName = txtRegName.getText().toString();
        String loc = txtKeHuName.getText().toString();
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_X5Sql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "RM_SMS_GetList_ByDay_WX");
        reqData.ExtParams.put("startDT", beginDate);
        reqData.ExtParams.put("endDT", endDate);
        reqData.ExtParams.put("optEmp", Session.CurrUser.Name);
        reqData.ExtParams.put("smsEmp", empName);
        reqData.ExtParams.put("regName", regName);
        reqData.ExtParams.put("loc", loc);
        DialogUitl.showProgressDialog(mActivity, reqData.CmdID, "正在加载数据……");
        try {
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(mActivity, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    List<WorkMsgInfo> listMsg = new ArrayList<>();
                                    for (int i = 0; i < resData.DataTable.length; i++) {
                                        HashMap<String,String>hashMap = resData.DataTable[i];
                                        WorkMsgInfo wmInfo = new WorkMsgInfo();
                                        wmInfo.Content = hashMap.get("Content");
                                        wmInfo.EmpName = hashMap.get("EmpName").trim();
                                        wmInfo.ID = hashMap.get("ID");
                                        wmInfo.Info = hashMap.get("Info");
                                        wmInfo.InputDT = hashMap.get("InputDT");
                                        wmInfo.KHID = hashMap.get("KHID");
                                        wmInfo.Loc = hashMap.get("Loc");
                                        wmInfo.Mobile = hashMap.get("Mobile");
                                        wmInfo.RegName = hashMap.get("RegName");
                                        wmInfo.SMSDay = hashMap.get("SMSDay");
                                        wmInfo.Task = hashMap.get("Task");
                                        wmInfo.TaskType = hashMap.get("TaskType");
                                        listMsg.add(wmInfo);
                                    }
                                    WorkMsgAdapter adapter = new WorkMsgAdapter(mContext, R.layout.listitem_workmsg, listMsg);
                                    lvWorkMsg.setAdapter(adapter);
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
            MethodUtil.showToast(e.getMessage(), mActivity);
        }
    }
}