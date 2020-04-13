package com.ramon.ramonrm.user;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.ClickPowerAuditAdapter;
import com.ramon.ramonrm.listclass.AllListClass;
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

public class ClickPowerAuditActivity extends BaseActivity implements View.OnClickListener {
    private ImageView clickiamgeview;
    private Boolean clicktype=true;
    QuanXianSHActivity powerAuditActivity;
    private TextView clickuser;
    private Button pass,goback;//通过，退回
    private EditText explain;//说明内容
    private String LeiBieDM="",DataSNo="";//
    ListView clickpowerlistview;
    List<AllListClass> list;
    private List<Integer>listnum;
    String [] powersno;
    String [] powerdm;
    List<String> listsno;
    List<String> listdm;
    List<String>clonesno,clonedm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clickpoweraudit);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        GetDetailedInformation();
    }

    private void initView(){
        powerAuditActivity=new QuanXianSHActivity();
        clickiamgeview = (ImageView)findViewById(R.id.activity_clickpoweraudit_imgback);
        clickiamgeview.setOnClickListener(this);
        clickuser = (TextView)findViewById(R.id.activity_clickuseraudit_user);
        clickuser.setText(powerAuditActivity.PowerName);
        pass =(Button)findViewById(R.id.activity_clickpoweraudit_pass);
        pass.setOnClickListener(this);
        goback = (Button)findViewById(R.id.activity_clickpoweraudit_return);
        goback.setOnClickListener(this);
        explain = (EditText)findViewById(R.id.activity_clickuseraudit_explain);
        clickpowerlistview=(ListView)findViewById(R.id.activity_clickpoweraudit_list);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.activity_clickpoweraudit_imgback){
            AppManagerUtil.instance().finishActivity(ClickPowerAuditActivity.this);
        }
        if (vId == R.id.listview_clickpoweraudit_type){
            final int i= (int) v.getTag();
            if (listnum.get(i)==1){
                listnum.set(i,0);
                listsno.set(i,"");
                listdm.set(i,"");
            }else {
                listnum.set(i,1);
                listsno.set(i,powersno[i]);
                listdm.set(i,powerdm[i]);
            }
            String str="";
            int a=powerdm.length;//用于测试时的标记
            for (int w=0;w<listnum.size();w++){
                if (listnum.get(w)!=0){
                    a=a+1;
                }else {
                    a=a-1;
                }
                str=str+listnum.get(w);
            }
            if (a==0){
                clicktype=false;
            }else {
                clicktype=true;
            }
        }


        if (vId == R.id.activity_clickpoweraudit_pass){
            if (clicktype==true){
                clonesno=new ArrayList<>();
                clonedm=new ArrayList<>();
                clonesno.addAll(listsno);
                clonedm.addAll(listdm);
                //Log.e("fww",DataSNo);
                //Log.e("fww",LeiBieDM);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    List<String> nullArr = new ArrayList<String>();
                    nullArr.add("");
                    clonesno.removeAll(nullArr);
                    clonedm.removeAll(nullArr);
                    DataSNo=String.join("|",clonesno);
                    LeiBieDM=String.join("|",clonedm);
                    Log.e("fww",DataSNo);
                    Log.e("fww",LeiBieDM);
                }
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    List<String> nullArr = new ArrayList<String>();
//                    nullArr.add("");
////                    listsno.removeAll(nullArr);
////                    listdm.removeAll(nullArr);
//                    DataSNo=String.join("|",listsno);
//                    LeiBieDM=String.join("|",listdm);
//                }
                Pass(LeiBieDM,DataSNo);
            }else {
                Toast.makeText(ClickPowerAuditActivity.this,"请先选中",Toast.LENGTH_LONG).show();
            }
        }
        if (vId == R.id.activity_clickpoweraudit_return){
            if (clicktype==true){
                Goback();
            }else {
                Toast.makeText(ClickPowerAuditActivity.this,"请先选中",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void GetDetailedInformation(){
        list=new ArrayList<>();
        listnum=new ArrayList<>();
        listsno=new ArrayList<>();
        listdm=new ArrayList<>();
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","QX_QuanXianSQMX_LieBiao_SP");
        reqData.ExtParams.put("sqSNo",powerAuditActivity.PowerSNo);
        reqData.ExtParams.put("outParams","recordTotal");
        reqData.ExtParams.put("recordTotal","99");
        reqData.ExtParams.put("pageSize","99");
        reqData.ExtParams.put("pageIndex","1");
        //reqData.ExtParams.put("orderStr","");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        //Log.e("fw",Session.CurrUser.YongHuSNo);
        try {
            DialogUitl.showProgressDialog(ClickPowerAuditActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(ClickPowerAuditActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                powersno=new String[resData.DataTable.length];
                                powerdm=new String[resData.DataTable.length];
                                if (resData.RstValue==0){
                                    if (resData.DataTable.length==0){
                                        pass.setVisibility(View.INVISIBLE);
                                        goback.setVisibility(View.INVISIBLE);
                                    }else {
                                        for (int i = 0; i < resData.DataTable.length; i++) {
                                            String LeiBieMC = resData.DataTable[i].get("LeiBieMC");
                                            String MC1 = resData.DataTable[i].get("MC1");
                                            list.add(new AllListClass(LeiBieMC, MC1, true));
                                            listnum.add(1);
                                            powersno[i]=resData.DataTable[i].get("DataSNo");
                                            powerdm[i]=resData.DataTable[i].get("LeiBieDM");
                                            listdm.add(resData.DataTable[i].get("LeiBieDM"));
                                            listsno.add(resData.DataTable[i].get("DataSNo"));
                                        }
                                    }
                                    ClickPowerAuditAdapter clickPowerAuditAdapter=new ClickPowerAuditAdapter(ClickPowerAuditActivity.this,list);
                                    clickpowerlistview.setAdapter(clickPowerAuditAdapter);
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

    private void Pass(String leibieDMs,String DataSNo){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","QX_QuanXianSQ_TongGuo_SP");
        reqData.ExtParams.put("SNo",powerAuditActivity.PowerSNo);
        reqData.ExtParams.put("chuLiSM",explain.getText().toString());
        reqData.ExtParams.put("leibieDMs",leibieDMs);
        reqData.ExtParams.put("dataSNos",DataSNo);
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        try{
            DialogUitl.showProgressDialog(ClickPowerAuditActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(ClickPowerAuditActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                Log.e("fw",result);
                                ResData resData=GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue==0){

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

    private void Goback(){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","QX_QuanXianSQ_TuiHui_SP");
        reqData.ExtParams.put("SNo",powerAuditActivity.PowerSNo);
        reqData.ExtParams.put("chuLiSM",explain.getText().toString());
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        try{
            DialogUitl.showProgressDialog(ClickPowerAuditActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(ClickPowerAuditActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                Log.e("fw",result);
                                ResData resData=GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue==0){

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
