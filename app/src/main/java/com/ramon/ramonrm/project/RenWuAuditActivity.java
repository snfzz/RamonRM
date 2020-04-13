package com.ramon.ramonrm.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.RenWuAuditAdapter;
import com.ramon.ramonrm.listclass.AllListClass;
import com.ramon.ramonrm.model.InstallationTaskinfo;
import com.ramon.ramonrm.model.RenWuAuditInfo;
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

public class RenWuAuditActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton imgbtnback;
    private ListView listview;
    private List<AllListClass> list=new ArrayList<>();
    private ImageView img1,img2;
    private TextView txt1,txt2;
    private String SH="",FS="";//审核与复审
    private String [] sendtxt1;
    private String [] sendtxt2;
    private String [] sendtxt3;
    private String [] sendtxt4;
    private String [] sendtxt5;
    private String [] sendtxt6;
    private String [] sendtxt7;
    private String [] sendtxt8;
    private String [] sendtxt9;
    private String [] sendtxt10;
    private String [] sendtxt11;
    private String [] ProjSNo;
    private String [] CJSNo;
    private String [] sno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renwuaudit);
        initView();
        GetData(SH,FS);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView1=listview.getChildAt(i - listview.getFirstVisiblePosition()).findViewById(R.id.listview_rentuaudit_time);
                String testnum=textView1.getText().toString();
                for (int num=0;num<sendtxt2.length;num++){
                    if (testnum.equals(sendtxt2[num])){
                        i=num;
                    }
                }
                RenWuAuditInfo renWuAuditInfo=new RenWuAuditInfo();
                renWuAuditInfo.ShenQingRenMC=sendtxt1[i];
                renWuAuditInfo.CJSJ=sendtxt2[i];
                renWuAuditInfo.SQName   =sendtxt3[i];
                renWuAuditInfo.AuditHTH=sendtxt4[i];
                renWuAuditInfo.KeHuMC=sendtxt5[i];
                renWuAuditInfo.RegionName=sendtxt6[i];
                renWuAuditInfo.RWName=sendtxt7[i];
                renWuAuditInfo.AdtRst=sendtxt8[i];
                renWuAuditInfo.AdtContent=sendtxt9[i];
                renWuAuditInfo.ReAdtRst=sendtxt10[i];
                renWuAuditInfo.ReAdtContent=sendtxt11[i];
                renWuAuditInfo.ProjSNo=ProjSNo[i];
                renWuAuditInfo.CJSNo=CJSNo[i];
                renWuAuditInfo.SNo=sno[i];
                Intent intent=new Intent(RenWuAuditActivity.this,ShenQingXiangXiActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        imgbtnback=(ImageButton)findViewById(R.id.activity_renwuaudit_imgback);
        imgbtnback.setOnClickListener(this);
        listview=(ListView)findViewById(R.id.activity_renwuaudit_listview);
        img1=(ImageView)findViewById(R.id.activity_renwuaudit_img1);
        img1.setOnClickListener(this);
        img2=(ImageView)findViewById(R.id.activity_renwuaudit_img2);
        img2.setOnClickListener(this);
        txt1=(TextView)findViewById(R.id.activity_renwuaudit_txt1);
        txt2=(TextView)findViewById(R.id.activity_renwuaudit_txt2);
    }

    @Override
    public void onClick(View view) {
        int vId=view.getId();
        if (vId == R.id.activity_renwuaudit_imgback){
            AppManagerUtil.instance().finishActivity(RenWuAuditActivity.this);
        }
        if (vId == R.id.listview_rentuaudit_audit){
            int i= (int) view.getTag();
            TextView textView1=listview.getChildAt(i - listview.getFirstVisiblePosition()).findViewById(R.id.listview_rentuaudit_time);
            String testnum=textView1.getText().toString();
            for (int num=0;num<sendtxt2.length;num++){
                if (testnum.equals(sendtxt2[num])){
                    i=num;
                }
            }
            showDialog3(i);
        }
        if (vId == R.id.activity_renwuaudit_img1){
            showDialog();
        }
        if (vId == R.id.activity_renwuaudit_img2){
            showDialog2();
        }

    }

    private void GetData(final String sh, final String fs){
        list=new ArrayList<>();
        final RenWuAuditInfo renWuAuditInfo=new RenWuAuditInfo();
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RW_GCProjPlanFshReq_LieBiao_SP");
        if (getIntent().getStringExtra("judge").equals("null")){
            reqData.ExtParams.put("projSNo","");
        }else {
            reqData.ExtParams.put("projSNo", InstallationTaskinfo.RenWuBH);//InstallationTaskinfo.RenWuBH
        }
        reqData.ExtParams.put("recordTotal","999");
        reqData.ExtParams.put("pageIndex","1");
        reqData.ExtParams.put("cjSNo","admin");
        try {
            DialogUitl.showProgressDialog(RenWuAuditActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(RenWuAuditActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                //Log.e("fwww",result);
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    if (resData.DataTable.length==0){

                                    }else {
                                        sendtxt1=new String[resData.DataTable.length];
                                        sendtxt2=new String[resData.DataTable.length];
                                        sendtxt3=new String[resData.DataTable.length];
                                        sendtxt4=new String[resData.DataTable.length];
                                        sendtxt5=new String[resData.DataTable.length];
                                        sendtxt6=new String[resData.DataTable.length];
                                        sendtxt7=new String[resData.DataTable.length];
                                        sendtxt8=new String[resData.DataTable.length];
                                        sendtxt9=new String[resData.DataTable.length];
                                        sendtxt10=new String[resData.DataTable.length];
                                        sendtxt11=new String[resData.DataTable.length];
                                        ProjSNo=new String[resData.DataTable.length];
                                        CJSNo=new String[resData.DataTable.length];
                                        sno=new String[resData.DataTable.length];

                                        for (int i = 0; i < resData.DataTable.length; i++) {
                                            renWuAuditInfo.ShenQingRenMC = resData.DataTable[i].get("ShenQingRenMC");
                                            renWuAuditInfo.CJSJ = resData.DataTable[i].get("CJSJ");
                                            renWuAuditInfo.SQName = resData.DataTable[i].get("SQName");
                                            renWuAuditInfo.AuditHTH = resData.DataTable[i].get("HTH");
                                            renWuAuditInfo.KeHuMC = resData.DataTable[i].get("KeHuMC");


                                            sendtxt1[i]=resData.DataTable[i].get("ShenQingRenMC");
                                            sendtxt2[i]=resData.DataTable[i].get("CJSJ");
                                            sendtxt3[i]=resData.DataTable[i].get("SQName");
                                            sendtxt4[i]=resData.DataTable[i].get("HTH");
                                            sendtxt5[i]=resData.DataTable[i].get("KeHuMC");
                                            sendtxt6[i]=resData.DataTable[i].get("RegionName");
                                            sendtxt7[i]=resData.DataTable[i].get("RWName");
                                            sendtxt8[i]=resData.DataTable[i].get("AdtRst");
                                            sendtxt9[i]=resData.DataTable[i].get("AdtContent");//有几个没用
                                            sendtxt10[i]=resData.DataTable[i].get("ReAdtRst");
                                            sendtxt11[i]=resData.DataTable[i].get("ReAdtContent");//有几个没用
                                            ProjSNo[i]=resData.DataTable[i].get("ProjSNo");
                                            CJSNo[i]=resData.DataTable[i].get("CJSNo");
                                            sno[i]=resData.DataTable[i].get("SNo");

                                            if (sh.equals("") && fs.equals("")) {
                                                list.add(new AllListClass(renWuAuditInfo.ShenQingRenMC, renWuAuditInfo.CJSJ, renWuAuditInfo.SQName, renWuAuditInfo.AuditHTH, renWuAuditInfo.KeHuMC));
                                            }
                                            else if (sh.equals("")){
                                                if (fs.equals(resData.DataTable[i].get("ReAdtRst"))){
                                                    list.add(new AllListClass(renWuAuditInfo.ShenQingRenMC, renWuAuditInfo.CJSJ, renWuAuditInfo.SQName, renWuAuditInfo.AuditHTH, renWuAuditInfo.KeHuMC));
                                                }
                                            }
                                            else if (fs.equals("")){
                                                if (sh.equals(resData.DataTable[i].get("AdtRst"))){
                                                    list.add(new AllListClass(renWuAuditInfo.ShenQingRenMC, renWuAuditInfo.CJSJ, renWuAuditInfo.SQName, renWuAuditInfo.AuditHTH, renWuAuditInfo.KeHuMC));
                                                }
                                            }
                                            else {
                                                if (sh.equals(resData.DataTable[i].get("AdtRst"))&&fs.equals(resData.DataTable[i].get("ReAdtRst"))){
                                                    list.add(new AllListClass(renWuAuditInfo.ShenQingRenMC, renWuAuditInfo.CJSJ, renWuAuditInfo.SQName, renWuAuditInfo.AuditHTH, renWuAuditInfo.KeHuMC));
                                                }
                                            }
                                        }
                                    }
                                    RenWuAuditAdapter renWuAuditAdapter=new RenWuAuditAdapter(RenWuAuditActivity.this,list);
                                    listview.setAdapter(renWuAuditAdapter);
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
    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.showdalog_renwuaudit,
                null);
        final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        //window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        wl.gravity= Gravity.CENTER;
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.getWindow().findViewById(R.id.showdalog_renwuaudit_t1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt1.setText("审核结果:全部");
                SH="";
                GetData(SH,FS);
                dialog.hide();
            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_renwuaudit_t2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt1.setText("审核结果:未审核");
                SH="WShenHe";
                GetData(SH,FS);
                dialog.hide();
            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_renwuaudit_t3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt1.setText("审核结果:通过");
                SH="TGShenHe";
                GetData(SH,FS);
                dialog.hide();

            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_renwuaudit_t4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt1.setText("审核结果:未通过");
                SH="WTGShenHe";
                GetData(SH,FS);
                dialog.hide();

            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_renwuaudit_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
    }


    private void showDialog2() {
        View view = getLayoutInflater().inflate(R.layout.showdalog_renwuaudit,
                null);
        final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        //window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        wl.gravity= Gravity.CENTER;
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.getWindow().findViewById(R.id.showdalog_renwuaudit_t1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt2.setText("审核结果:全部");
                FS="";
                GetData(SH,FS);
                dialog.hide();
            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_renwuaudit_t2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt2.setText("审核结果:未审核");
                FS="WShenHe";
                GetData(SH,FS);
                dialog.hide();
            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_renwuaudit_t3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt2.setText("审核结果:通过");
                FS="TGShenHe";
                GetData(SH,FS);
                dialog.hide();

            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_renwuaudit_t4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt2.setText("审核结果:未通过");
                FS="WTGShenHe";
                GetData(SH,FS);
                dialog.hide();

            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_renwuaudit_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
    }

    private void showDialog3(final int i) {
        View view = getLayoutInflater().inflate(R.layout.showdalog_renwuauditaudit,
                null);
        final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        //window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        wl.gravity= Gravity.CENTER;
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        final EditText editText=dialog.getWindow().findViewById(R.id.showdalog_renwuauditaudit_shuoming);
        dialog.getWindow().findViewById(R.id.showdalog_renwuauditaudit_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuditPass(editText.getText().toString(),CJSNo[i],sno[i]);
            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_renwuauditaudit_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuditBack(editText.getText().toString(),CJSNo[i],sno[i]);
            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_renwuauditaudit_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
        dialog.show();
    }

    private void AuditPass(String adtContent,String cjsno,String SNo){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RW_GCProjPlanFshReq_TongGuo_SP");
        reqData.ExtParams.put("adtSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("adtContent",adtContent);
        reqData.ExtParams.put("CJSNo",cjsno);
        reqData.ExtParams.put("sqSNo",SNo);
        try {
            DialogUitl.showProgressDialog(RenWuAuditActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(RenWuAuditActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    Log.e("fwww",result);
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

    private void AuditBack(String adtContent,String cjsno,String SNo){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RW_GCProjPlanFshReq_HuiTui_SP");
        reqData.ExtParams.put("adtSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("adtContent",adtContent);
        reqData.ExtParams.put("cjSNo",cjsno);
        reqData.ExtParams.put("sqSNo",SNo);
        try {
            DialogUitl.showProgressDialog(RenWuAuditActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(RenWuAuditActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    Log.e("fwww",result);
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
