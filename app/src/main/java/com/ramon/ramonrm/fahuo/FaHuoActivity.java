package com.ramon.ramonrm.fahuo;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.FaHuoAdapter;
import com.ramon.ramonrm.model.FaHuoInfo;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FaHuoActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton btnBack;

    Calendar calBegin = Calendar.getInstance(Locale.CHINA);
    Calendar calEnd = Calendar.getInstance(Locale.CHINA);

    private ListView lvFaHuoSQ;
    private ImageView imgFilter;
    private Button btnQuery;
    private LinearLayout lyCondition;

    private EditText txtSQRName, txtSHRName;
    private TextView lblBeginDate, lblEndDate, lblBeginDateTitle, lblEndDateTitle;

    private List<FaHuoInfo>listFaHuoSQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fahuo);
        initView();
    }

    private void initView() {
        btnBack = (ImageButton) findViewById(R.id.activity_fahuo_imgback);
        btnBack.setOnClickListener(this);
        imgFilter = (ImageView) findViewById(R.id.activity_fahuo_imgfilter);
        imgFilter.setOnClickListener(this);
        lyCondition = (LinearLayout) findViewById(R.id.activity_fahuo_rlaycondition);
        txtSQRName = (EditText) findViewById(R.id.activity_fahuo_txtsqrname);
        txtSHRName = (EditText) findViewById(R.id.activity_fahuo_txtshrname);
        txtSHRName.setText(Session.CurrUser.Name);
        lblBeginDate = (TextView) findViewById(R.id.activity_fahuo_lblbegindate);
        lblBeginDate.setOnClickListener(this);
        lblEndDate = (TextView) findViewById(R.id.activity_fahuo_lblenddate);
        lblEndDate.setOnClickListener(this);
        lblBeginDateTitle = (TextView) findViewById(R.id.activity_fahuo_lblbegindatetitle);
        lblBeginDateTitle.setOnClickListener(this);
        lblEndDateTitle = (TextView) findViewById(R.id.activity_fahuo_lblenddatetitle);
        lblEndDateTitle.setOnClickListener(this);
        btnQuery = (Button)findViewById(R.id.activity_fahuo_btnquery);
        btnQuery.setOnClickListener(this);
        lvFaHuoSQ = (ListView)findViewById(R.id.activity_fahuo_lvfahuosq);
    }

    @Override
    public void onClick(View v) {
        try {
            int vId = v.getId();
            if (vId == R.id.activity_fahuo_imgback) {
                AppManagerUtil.instance().finishActivity(FaHuoActivity.this);
            }
            if (vId == R.id.activity_fahuo_imgfilter) {
                //region 显示/隐藏搜索框
                if (lyCondition.getVisibility() == View.VISIBLE) {
                    lyCondition.setVisibility(View.GONE);
                } else {
                    lyCondition.setVisibility(View.VISIBLE);
                }
                //endregion
            }
            if (vId == R.id.activity_fahuo_lblbegindate || vId == R.id.activity_fahuo_lblbegindatetitle) {
                //region 开始日期
                new DatePickerDialog(this,
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
            if (vId == R.id.activity_fahuo_lblenddate || vId == R.id.activity_fahuo_lblenddatetitle) {
                //region 结束日期
                new DatePickerDialog(this,
                        // 绑定监听器
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String strYear = year + "";
                                String strMonth = month < 9 ? "0" + (month + 1) : "" + (month + 1);
                                String strDay = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                                lblEndDate.setText(strYear + "-" + strMonth + "-" + strDay);
                                calEnd.set(year, month, dayOfMonth);
                            }
                        }
                        , calEnd.get(Calendar.YEAR)
                        , calEnd.get(Calendar.MONTH)
                        , calEnd.get(Calendar.DAY_OF_MONTH)).show();
                //endregion
            }
            if(vId == R.id.activity_fahuo_btnquery){
                //region 查询
                MethodUtil.hideSoftInputFromActivity(this);
                loadFaHuoSQ();
                //endregion
            }
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    private void loadFaHuoSQ() {
        String beginDate = lblBeginDate.getText().toString();
        String endDate = lblEndDate.getText().toString();
        String sqrName = txtSQRName.getText().toString();
        String shrName = txtSHRName.getText().toString();
        ReqData reqData =ReqData.createReqData(ReqData.ReqType.T_SQLExec_X5Sql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "RM_OtherFHSQ_LieBiao_WX_SP");
        reqData.ExtParams.put("beginDate", beginDate);
        reqData.ExtParams.put("endDate", endDate);
        reqData.ExtParams.put("sqrname", sqrName);
        reqData.ExtParams.put("shrName", shrName);
        DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
        try {
            VolleyRequestUtil.RequestPost(this, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(this, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    listFaHuoSQ = new ArrayList<>();
                                    for (int i = 0; i < resData.DataTable.length; i++) {
                                        HashMap<String,String> hashMap = resData.DataTable[i];
                                        FaHuoInfo fhInfo = new FaHuoInfo();
                                        fhInfo.fID = hashMap.get("fID");
                                        fhInfo.fZXZT = hashMap.get("fZXZT");
                                        fhInfo.fLSH = hashMap.get("fLSH");
                                        fhInfo.fSQR = hashMap.get("fSQR");
                                        fhInfo.fSQBM = hashMap.get("fSQBM");
                                        fhInfo.fSQSJ = hashMap.get("fSQSJ");
                                        fhInfo.fYQDHSJ = hashMap.get("fYQDHSJ");
                                        fhInfo.fSBH = hashMap.get("fSBH");
                                        fhInfo.fLB = hashMap.get("fLB");
                                        fhInfo.fBZ = hashMap.get("fBZ");
                                        fhInfo.fFSHDWMC = hashMap.get("fFSHDWMC");
                                        fhInfo.fSHDZ = hashMap.get("fSHDZ");
                                        fhInfo.fSHR = hashMap.get("fSHR");
                                        fhInfo.fSHRDH = hashMap.get("fSHRDH");
                                        fhInfo.fFHSJ = hashMap.get("fFHSJ");
                                        fhInfo.fFHFS = hashMap.get("fFHFS");
                                        fhInfo.fFHDH = hashMap.get("fFHDH");
                                        fhInfo.fBZFS = hashMap.get("fBZFS");
                                        fhInfo.fFHZXDH = hashMap.get("fFHZXDH");
                                        fhInfo.fZXD = hashMap.get("fZXD");
                                        fhInfo.fZL = hashMap.get("fZL");
                                        fhInfo.fDHQR = hashMap.get("fDHQR");
                                        listFaHuoSQ.add(fhInfo);
                                    }
                                    FaHuoAdapter adapter = new FaHuoAdapter(mContext, R.layout.listitem_fahuoinfo, listFaHuoSQ);
                                    lvFaHuoSQ.setAdapter(adapter);
                                    lvFaHuoSQ.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            try{

                                            }
                                            catch (Exception e) {
                                                e.printStackTrace();
                                                MethodUtil.showToast(e.getMessage(), context);
                                            }
                                        }
                                    });
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
            MethodUtil.showToast(e.getMessage(), this);
        }
    }
}