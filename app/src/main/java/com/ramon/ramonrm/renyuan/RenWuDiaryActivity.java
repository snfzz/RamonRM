package com.ramon.ramonrm.renyuan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.model.InstallationTaskinfo;
import com.ramon.ramonrm.model.RenWuInfo;
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

public class RenWuDiaryActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton imgbtnback;
    private EditText editgs,editddgs,editddyy,editwtfk,editgznr;//工时,等待工时，等待原因，问题反馈，工作内容
    RenWuInfo renWuInfo=new RenWuInfo();
    Button btnsave;
    TextView txttitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renwudiary);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        GetLog();
        txttitle.setText(InstallationTaskinfo.Title1+"-"+InstallationTaskinfo.Title2);
    }

    private void initView(){
        imgbtnback=(ImageButton)findViewById(R.id.activity_renwudiary_imgback);
        imgbtnback.setOnClickListener(this);
        editgs=(EditText)findViewById(R.id.activity_renwudiary_gs);
        editddgs=(EditText)findViewById(R.id.activity_renwudiary_ddgs);
        editddyy=(EditText)findViewById(R.id.activity_renwudiary_ddyy);
        editwtfk=(EditText)findViewById(R.id.activity_renwudiary_wtfk);
        editgznr=(EditText)findViewById(R.id.activity_renwudiary_gznr);
        btnsave=(Button)findViewById(R.id.activity_renwudiary_save);
        btnsave.setOnClickListener(this);
        txttitle=(TextView)findViewById(R.id.activity_renwudiary_title);
    }

    @Override
    public void onClick(View view) {
        int vId =view.getId();
        if (vId == R.id.activity_renwudiary_imgback){
            AppManagerUtil.instance().finishActivity(RenWuDiaryActivity.this);
        }
        if (vId == R.id.activity_renwudiary_save){
            SaveLog();
        }
    }
    //进入时查看是否已经保存过日志，如果保存过进行显示
    private void GetLog(){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RW_GCTaskDaily_GetTodayDail_SP");
        reqData.ExtParams.put("empSNo", InstallationTaskinfo.RegionEmpID);
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("projSNo",InstallationTaskinfo.RenWuBH);
        try{
            DialogUitl.showProgressDialog(RenWuDiaryActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(RenWuDiaryActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try{
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    if (resData.DataTable.length==0){

                                    }else {
                                        for (int i = 0; i < resData.DataTable.length; i++) {
                                            renWuInfo.workContent = resData.DataTable[i].get("WorkContent");
                                            renWuInfo.workHours = resData.DataTable[i].get("WorkHours");
                                            renWuInfo.waitReason = resData.DataTable[i].get("WaitReason");
                                            renWuInfo.waitHours = resData.DataTable[i].get("WaitHours");
                                            renWuInfo.workProblem = resData.DataTable[i].get("WorkProblem");
                                        }
                                        editgs.setText(renWuInfo.workHours);
                                        editgs.setText(renWuInfo.waitHours);
                                        editddyy.setText(renWuInfo.waitReason);
                                        editwtfk.setText(renWuInfo.workProblem);
                                        editgznr.setText(renWuInfo.workContent);
                                    }
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

    private void SaveLog(){
        if (editgs.getText().toString().equals("")){
            Toast.makeText(RenWuDiaryActivity.this,"请输入工时",Toast.LENGTH_LONG).show();
            return;
        }
        if (editwtfk.getText().toString().equals("")){
            Toast.makeText(RenWuDiaryActivity.this,"请输入工作问题",Toast.LENGTH_LONG).show();
            return;
        }
        if (editgznr.getText().toString().equals("")){
            Toast.makeText(RenWuDiaryActivity.this,"请输入工作内容",Toast.LENGTH_LONG).show();
            return;
        }
        if (isNumericZidai(editgs.getText().toString())==false){
            Toast.makeText(RenWuDiaryActivity.this,"请输入正确工时",Toast.LENGTH_LONG).show();
            return;
        }
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RW_GCTaskDaily_ZengJia_SP");
        reqData.ExtParams.put("empSNo",InstallationTaskinfo.RegionEmpID);
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("projSNo",InstallationTaskinfo.RenWuBH);
        reqData.ExtParams.put("workHours",editgs.getText().toString());
        reqData.ExtParams.put("workContent",editgznr.getText().toString());
        reqData.ExtParams.put("workProblem",editwtfk.getText().toString());
        reqData.ExtParams.put("waitHours",editddgs.getText().toString());
        reqData.ExtParams.put("hasWaiting", editddgs.getText().toString().length()==0?"0":"1");
        reqData.ExtParams.put("waitReason",editddyy.getText().toString());
        try {
            DialogUitl.showProgressDialog(RenWuDiaryActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(RenWuDiaryActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try{
                                Log.e("fww",result);
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){

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

    //判断输入的工时是否为数组
    public static boolean isNumericZidai(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


}
