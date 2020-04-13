package com.ramon.ramonrm.alarm;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.AnLiAdapter;
import com.ramon.ramonrm.model.AnLiInfo;
import com.ramon.ramonrm.renyuan.WorkMsgEditActivity;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnLiKuActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private EditText txtChanPinMC, txtKeyWord;
    private Button btnQuery;
    private ListView lvAnLiKu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anliku);
        initView();
    }

    private void initView() {
        btnBack = findViewById(R.id.activity_anliku_imgback);
        btnBack.setOnClickListener(this);
        txtChanPinMC = findViewById(R.id.activity_anliku_txtchanpinmc);
        txtKeyWord = findViewById(R.id.activity_anliku_txtkeyword);
        btnQuery = findViewById(R.id.activity_anliku_btnquery);
        btnQuery.setOnClickListener(this);
        lvAnLiKu = findViewById(R.id.activity_anliku_lvanliku);
        lvAnLiKu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    MethodUtil.showToast(position+"",context);
                } catch (Exception e) {
                    e.printStackTrace();
                    MethodUtil.showToast(e.getMessage(), context);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        try {
            MethodUtil.hideSoftInputFromActivity(this);
            int vId = v.getId();
            if (vId == R.id.activity_anliku_imgback) {
                AppManagerUtil.instance().finishActivity(this);
            }
            if (vId == R.id.activity_anliku_btnquery) {
                loadAnLiData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), this);
        }
    }

    private void loadAnLiData() {
        String keyword = txtKeyWord.getText().toString().trim();
        if (keyword.length() == 0) {
            MethodUtil.showToast("请输入关键字", context);
            return;
        }
        String chanpinMC = txtChanPinMC.getText().toString().trim();
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable, Session.SessionId, Session.ValidMD5);
        reqData.ExtParams.put("spName", "AL_AnLiKu_LieBiao_SP");
        reqData.ExtParams.put("keyword", keyword);
        reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("chanpinMC", chanpinMC);
        reqData.ExtParams.put("recordTotal", "1");
        reqData.ExtParams.put("outParams", "recordTotal");
        DialogUitl.showProgressDialog(this, reqData.CmdID, "正在提交搜索……");
        try {
            VolleyRequestUtil.RequestPost(this, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(this, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadAnLiView(resData.DataTable);
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

    private void loadAnLiView(HashMap<String, String>[] hashData) {
        List<AnLiInfo> listAnLi = new ArrayList<>();
        for (int i = 0; i < hashData.length; i++) {
            HashMap<String, String> hashMap = hashData[i];
            AnLiInfo anli = new AnLiInfo();
            anli.SNo = hashMap.get("SNo");
            anli.ChanPinJC = hashMap.get("JianCheng");
            anli.ChanPinMC = hashMap.get("MingCheng");
            anli.ChanPinSNo = hashMap.get("ChanPinSNo");
            anli.ChanPinDM = hashMap.get("ChanPinDM");
            anli.WenTiMS = hashMap.get("WenTiMS");
            anli.ChuLiCS = hashMap.get("ChuLiCS");
            anli.DianPingCS = Integer.parseInt(hashMap.get("DianPingNum"));
            listAnLi.add(anli);
        }
        AnLiAdapter adapter = new AnLiAdapter(context, R.layout.listitem_anliinfo, listAnLi);
        lvAnLiKu.setAdapter(adapter);
    }
}
