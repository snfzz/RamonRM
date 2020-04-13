package com.ramon.ramonrm.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.YongHuSQAdapter;
import com.ramon.ramonrm.model.YongHuSQ;
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

public class AuditUserActivity extends BaseActivity implements View.OnClickListener {

    private static final int SUBIT_RESULT = 1;

    private ImageView btnSearch,btnBack;
    private EditText txtInput;
    private ListView lvYongHuSQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audituser);
        initView();
        loadYongHuSQ();
    }

    private void initView() {
        btnBack = findViewById(R.id.activity_audituser_imgback);
        btnBack.setOnClickListener(this);
        btnSearch = findViewById(R.id.activity_audituser_btnsearch);
        btnSearch.setOnClickListener(this);
        txtInput = findViewById(R.id.activity_audituser_txtmingcheng);
        lvYongHuSQ = findViewById(R.id.activity_audituser_lvyonghusq);
        lvYongHuSQ.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    YongHuSQ yhSQ = ((YongHuSQAdapter.ViewHolder) view.getTag()).YongHuSQ;
                    if (yhSQ != null) {
                        Intent intent = new Intent(AuditUserActivity.this, AuditUserDetailActivity.class);
                        intent.putExtra("YongHuSQ", GsonUtils.toJson(yhSQ));
                        startActivityForResult(intent, SUBIT_RESULT);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    MethodUtil.showToast(ex.getMessage(), context);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        try {
            int vId = v.getId();
            if (vId == R.id.activity_audituser_btnsearch) {
                loadYongHuSQ();
            }
            if(vId == R.id.activity_audituser_imgback){
                AppManagerUtil.instance().finishActivity(this);
            }

        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    private void loadYongHuSQ() {
        String inputVal = txtInput.getText().toString().trim();
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable, Session.SessionId, Session.ValidMD5);
        reqData.ExtParams.put("spName", "WX_YongHuSQ_LieBiao_SP");
        reqData.ExtParams.put("zhuangTaiDM", "SQ");
        reqData.ExtParams.put("yuanGongXM", inputVal);
        reqData.ExtParams.put("outParams", "recordTotal");
        reqData.ExtParams.put("recordTotal", "1");
        reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
        try {
            DialogUitl.showProgressDialog(AuditUserActivity.this, reqData.CmdID, "正在加载数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(AuditUserActivity.this, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadYongHuSQData(resData.DataTable);
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

    private void loadYongHuSQData(HashMap<String,String>[]hashData) {
        List<YongHuSQ> listYongHuSQ = new ArrayList<>();
        for (int i = 0; i < hashData.length; i++) {
            HashMap<String, String> hashMap = hashData[i];
            YongHuSQ yhSQ = new YongHuSQ();
            yhSQ.BMMingCheng = hashMap.get("BMMingCheng");
            yhSQ.BuMenSNo = hashMap.get("BuMenSNo");
            yhSQ.Email = hashMap.get("Email");
            yhSQ.IDCard = hashMap.get("IDCard");
            yhSQ.SNo = hashMap.get("SNo");
            yhSQ.Sex = hashMap.get("Sex");
            yhSQ.XingMing = hashMap.get("XingMing");
            yhSQ.DaiMa = hashMap.get("DaiMa");
            yhSQ.ShenQingDM = hashMap.get("ShenQingDM");
            yhSQ.ShenQingDMSNoMC = hashMap.get("ShenQingDMSNoMC");
            yhSQ.ZhuangTaiDM = hashMap.get("ZhuangTaiDM");
            yhSQ.Mobile = hashMap.get("Mobile");
            listYongHuSQ.add(yhSQ);
        }
        YongHuSQAdapter adapter = new YongHuSQAdapter(context, R.layout.listitem_yonghusq, listYongHuSQ);
        lvYongHuSQ.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case SUBIT_RESULT: {
                    loadYongHuSQ();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

}
