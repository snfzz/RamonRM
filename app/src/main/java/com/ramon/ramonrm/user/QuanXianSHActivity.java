package com.ramon.ramonrm.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.UserAuditListViewAdapter;
import com.ramon.ramonrm.listclass.AllListClass;
import com.ramon.ramonrm.listclass.UserAuditListViewclass;
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
import java.util.List;

public class QuanXianSHActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton imageButton;
    private ImageView search;
    private ListView powerlistview;
    private List<AllListClass> powerlistclass;
    private EditText poweredittext;
    public static String PowerName,PowerSNo;//用于数据传递
    private String [] powername;
    private String [] powersno;
    private RelativeLayout r1;
    private UserAuditListViewAdapter userAuditListViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quanxiansh);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        AuditPower("");
        powerlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                userAuditListViewAdapter.setCurrentItem(i);
                //通知ListView改变状态
                userAuditListViewAdapter.notifyDataSetChanged();
                PowerName=powername[i];
                PowerSNo=powersno[i];
                Intent intent=new Intent(QuanXianSHActivity.this,ClickPowerAuditActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        imageButton = (ImageButton)findViewById(R.id.activity_quanxiansh_imgback);
        imageButton.setOnClickListener(this);
        search = (ImageView) findViewById(R.id.activity_quanxiansh_btnsearch);
        search.setOnClickListener(this);
        powerlistview = (ListView)findViewById(R.id.activity_quanxiansh_listview);
        poweredittext = (EditText)findViewById(R.id.activity_quanxiansh_mingcheng);
        r1=(RelativeLayout)findViewById(R.id.activity_quanxiansh_r1);
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId();
        if (vId == R.id.activity_quanxiansh_imgback){
            AppManagerUtil.instance().finishActivity(QuanXianSHActivity.this);
        }
        if (vId == R.id.activity_quanxiansh_btnsearch){
            AuditPower(poweredittext.getText().toString());
        }
    }

    private void AuditPower(String mingcheng){
        powerlistclass=new ArrayList<>();
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","QX_QuanXianSQ_LieBiao_SP");
        reqData.ExtParams.put("zhuangTaiDM","SQ");
        reqData.ExtParams.put("mingcheng",mingcheng);
        reqData.ExtParams.put("outParams","recordTotal");
        reqData.ExtParams.put("recordTotal","99");
        reqData.ExtParams.put("pageSize","99");
        reqData.ExtParams.put("pageIndex","1");
        //reqData.ExtParams.put("orderStr","");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        try {
            DialogUitl.showProgressDialog(QuanXianSHActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(QuanXianSHActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                powername=new String[resData.DataTable.length];
                                powersno=new String[resData.DataTable.length];
                                if (resData.RstValue==0){
                                    if (resData.DataTable.length==0){
                                        r1.setVisibility(View.INVISIBLE);
                                    }else {
                                        r1.setVisibility(View.VISIBLE);
                                        for (int i = 0; i < resData.DataTable.length; i++) {
                                            String name = resData.DataTable[i].get("Name");
                                            String BM = resData.DataTable[i].get("DeptSNames");
                                            String MC = resData.DataTable[i].get("ShenQingMC");
                                            powersno[i] = resData.DataTable[i].get("SNo");
                                            powername[i] = resData.DataTable[i].get("Name");
                                            powerlistclass.add(new AllListClass(BM, name, MC));
                                        }
                                    }
                                    userAuditListViewAdapter=new UserAuditListViewAdapter(context,R.layout.listview_useraudit,powerlistclass);
                                    powerlistview.setAdapter(userAuditListViewAdapter);
                                }else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            }catch (Exception e){
                                MethodUtil.showToast(e.getMessage(), context);
                            }finally {
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
        }catch (Exception e){
            MethodUtil.showToast(e.getMessage(), context);
        }
    }
}
