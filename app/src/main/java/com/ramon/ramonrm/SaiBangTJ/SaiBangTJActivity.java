package com.ramon.ramonrm.SaiBangTJ;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.project.XinXiTianXieActivity;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

public class SaiBangTJActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton imgbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saibangtj);
        initView();
        test1();
        test2();
    }


    private void initView(){
        imgbtn=findViewById(R.id.activity_saibangtj_imgback);
        imgbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int vId=view.getId();
        if (vId == R.id.activity_saibangtj_imgback){
            AppManagerUtil.instance().finishActivity(SaiBangTJActivity.this);
        }
    }

    //波动获取
    private void test1(){
        ReqData reqData=new ReqData("SQLExec","TTLSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("kehuMC","");
        reqData.ExtParams.put("spName","TJ_SB_BoDongTJ_SP");
        reqData.ExtParams.put("shebeiSNo","");
        reqData.ExtParams.put("stsType","1");
        reqData.ExtParams.put("sbType","1");
        reqData.ExtParams.put("beginTime","2020/01/01");
        reqData.ExtParams.put("endTime","2020/04/08");
        try {
            DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(SaiBangTJActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                Log.e("fweqeq  test1:  ",result);
                                if (resData.RstValue == 0){

                                }else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            }catch (Exception e){
                                MethodUtil.showToast(e.getMessage(), context);
                                e.printStackTrace();
                            }finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog(reqData.CmdID);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    //命中率获取
    private void test2(){
        ReqData reqData=new ReqData("SQLExec","TTLSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","TJ_SaiBang_LieBiao_SP");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("gangchangMC","");
        reqData.ExtParams.put("sheBeiSNo","");
        reqData.ExtParams.put("bystrand","1");
        reqData.ExtParams.put("byAllTime","1");
        try {
            DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(SaiBangTJActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                Log.e("fweqeq  test2:  ",result);
                                if (resData.RstValue == 0){

                                }else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            }catch (Exception e){
                                MethodUtil.showToast(e.getMessage(), context);
                                e.printStackTrace();
                            }finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog(reqData.CmdID);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }



    public  void loge  (String tag, String msg) {
        if (tag == null || tag.length() == 0
                || msg == null || msg.length() == 0)
            return;

        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize ) {// 长度小于等于限制直接打印
            Log.e(tag, msg);
        }else {
            while (msg.length() > segmentSize ) {// 循环分段打印日志
                String logContent = msg.substring(0, segmentSize );
                msg = msg.replace(logContent, "");
                Log.e(tag,"-------------------"+ logContent);
            }
            Log.e(tag,"-------------------"+ msg);// 打印剩余日志
        }
    }

}
