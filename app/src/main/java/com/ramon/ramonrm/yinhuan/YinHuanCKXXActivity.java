package com.ramon.ramonrm.yinhuan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.ShenQingDialog.base.Config;
import com.ramon.ramonrm.ShenQingDialog.component.ShowImagesDialog;
import com.ramon.ramonrm.adapter.XinXiTianXieGridAdapter;
import com.ramon.ramonrm.adapter.YinHuanCKXXAdapter;
import com.ramon.ramonrm.adapter.YinHuanDengJiAdapter;
import com.ramon.ramonrm.listclass.AllListClass;
import com.ramon.ramonrm.project.ShenQingXiangXiActivity;
import com.ramon.ramonrm.renyuan.YinHuanDengJiActivity;
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

public class YinHuanCKXXActivity extends BaseActivity implements View.OnClickListener {
    private String sno;
    private ImageButton imgbtn;
    private TextView dj,cp,sb,zt,bt,nr,bz,jg;
    private String [] imagepath;
    private List<AllListClass> list;
    private List<String> daloglist;
    private GridView gridView;
    private String [] id;
    private EditText editText;
    private Button dealbtn;
    private ImageView imgdeleta,bianxie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yinhuanckxx);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        init();
        GetYHXX(sno);
        GetImage(sno);
        getDeviceDensity();//点击图片后需要显示的dalog

    }

    private void init(){
        sno=getIntent().getStringExtra("sno");
        imgbtn=findViewById(R.id.activity_yinhuanckxx_imgback);
        imgbtn.setOnClickListener(this);
        dj=findViewById(R.id.activity_yinhuanckxx_tt1);
        cp=findViewById(R.id.activity_yinhuanckxx_tt2);
        sb=findViewById(R.id.activity_yinhuanckxx_tt3);
        zt=findViewById(R.id.activity_yinhuanckxx_tt4);
        bt=findViewById(R.id.activity_yinhuanckxx_tt5);
        nr=findViewById(R.id.activity_yinhuanckxx_tt6);
        bz=findViewById(R.id.activity_yinhuanckxx_tt7);
        jg=findViewById(R.id.activity_yinhuanckxx_tt8);
        gridView=findViewById(R.id.activity_yinhuanckxx_grid);
        editText=findViewById(R.id.activity_yinhuanckxx_txtcontent);
        dealbtn=findViewById(R.id.activity_yinhuanckxx_deal);
        dealbtn.setOnClickListener(this);
        imgdeleta=findViewById(R.id.activity_yinhuanckxx_delete);
        imgdeleta.setOnClickListener(this);
        bianxie=findViewById(R.id.activity_yinhuanckxx_write);
        bianxie.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int vId=view.getId();
        if (vId == R.id.activity_yinhuanckxx_imgback){
            AppManagerUtil.instance().finishActivity(YinHuanCKXXActivity.this);
        }
        if (vId == R.id.gridview_xinxitianxie_img){
            int i= (int) view.getTag();
            new ShowImagesDialog(YinHuanCKXXActivity.this,daloglist,i).show();
        }
        if (vId == R.id.gridview_xinxitianxie_delete){
            int i =(int)view.getTag();
            showDelete(id[i]);
        }
        if (vId == R.id.activity_yinhuanckxx_deal){
            Deal();
        }
        if (vId == R.id.activity_yinhuanckxx_delete){
            showDialogDelete(sno);
        }
        if (vId == R.id.activity_yinhuanckxx_write){
            Intent intent=new Intent(YinHuanCKXXActivity.this,YinHuanTxxxWriteActivity.class);
            intent.putExtra("sno",sno);
            intent.putExtra("bz",bz.getText().toString());
            intent.putExtra("content",nr.getText().toString());
            startActivity(intent);
        }
    }

    protected void getDeviceDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Config.EXACT_SCREEN_HEIGHT = metrics.heightPixels;
        Config.EXACT_SCREEN_WIDTH = metrics.widthPixels;
    }

    private void GetYHXX(String sno){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","KH_YinHuan_LieBiao_SP");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("yhSNo",sno);
        reqData.ExtParams.put("keyword","");//搜索
        reqData.ExtParams.put("yhZT","");//ChuLiZhongYH,WanChengYH
        reqData.ExtParams.put("rmSBSNo","");
        reqData.ExtParams.put("beginDate","");
        reqData.ExtParams.put("endDate","");
        reqData.ExtParams.put("outParams","recordTotal");
        reqData.ExtParams.put("recordTotal","1");
        try {
            DialogUitl.showProgressDialog(YinHuanCKXXActivity.this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(YinHuanCKXXActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    for (int i=0;i<resData.DataTable.length;i++){
                                        String YinHuanDJName=resData.DataTable[i].get("YinHuanDJName");
                                        String ChanPinMC=resData.DataTable[i].get("ChanPinMC");
                                        String RMSBMingCheng=resData.DataTable[i].get("RMSBMingCheng");
                                        if (RMSBMingCheng.length() == 0){
                                            RMSBMingCheng="-";
                                        }
                                        String Status=resData.DataTable[i].get("Status");
                                        if (Status.equals("ChuLiZhongYH")){
                                            Status="处理中";
                                            editText.setVisibility(View.VISIBLE);
                                            dealbtn.setVisibility(View.VISIBLE);
                                            bianxie.setVisibility(View.VISIBLE);
                                        }else {
                                            editText.setVisibility(View.GONE);
                                            dealbtn.setVisibility(View.GONE);
                                            bianxie.setVisibility(View.GONE);
                                            Status="已完成";
                                        }
                                        String MingCheng=resData.DataTable[i].get("MingCheng");
                                        String Content=resData.DataTable[i].get("Content");
                                        String BZ=resData.DataTable[i].get("BZ");
                                        if (BZ.length()==0){
                                            BZ="-";
                                        }
                                        String Result=resData.DataTable[i].get("Result");
                                        if (Status.equals("已完成")&&Result.length()==0){
                                            Result="-";
                                        }

                                        dj.setText(YinHuanDJName);
                                        cp.setText(ChanPinMC);
                                        sb.setText(RMSBMingCheng);
                                        zt.setText(Status);
                                        bt.setText(MingCheng);
                                        nr.setText(Content);
                                        bz.setText(BZ);
                                        jg.setText(Result);
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

    private void GetImage(String dataID){
        list=new ArrayList<>();
        daloglist=new ArrayList<>();
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","KH_File_LieBiao_SP");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("dataID",dataID);
        reqData.ExtParams.put("outParams","recordTotal");
        reqData.ExtParams.put("recordTotal","1");
        try {
            DialogUitl.showProgressDialog(YinHuanCKXXActivity.this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(YinHuanCKXXActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    imagepath=new String[resData.DataTable.length];
                                    id=new String[resData.DataTable.length];
                                    for (int i=0;i<resData.DataTable.length;i++){
                                        String ID=resData.DataTable[i].get("ID");
                                        String FilePath=resData.DataTable[i].get("FilePath");
                                        String FileName=resData.DataTable[i].get("FileName");
                                        id[i]=ID;
                                        imagepath[i]="http://www.hyramon.com.cn:14010"+FilePath+"/"+FileName;
                                        list.add(new AllListClass(imagepath[i]));
                                        daloglist.add(imagepath[i]);
                                    }
                                    YinHuanCKXXAdapter adapter=new YinHuanCKXXAdapter(YinHuanCKXXActivity.this,list,context);
                                    gridView.setAdapter(adapter);
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

    private void showDelete(final String id) {
        View view = getLayoutInflater().inflate(R.layout.show_dalog_renyuananpai,
                null);
        final Dialog dialog = new Dialog(YinHuanCKXXActivity.this, R.style.MyUsualDialog);
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
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView message=dialog.getWindow().findViewById(R.id.message);
        message.setText("确认删除该图片?");
        dialog.getWindow().findViewById(R.id.show_dalog_renyuananpai_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().findViewById(R.id.show_dalog_renyuananpai_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShanChu(id);
                dialog.dismiss();
            }
        });
    }

    private void ShanChu(String scid){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","KH_File_ShanChu_SP");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("fileID",scid);
        try {
            DialogUitl.showProgressDialog(YinHuanCKXXActivity.this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(YinHuanCKXXActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    GetImage(sno);
                                    Toast.makeText(YinHuanCKXXActivity.this,"删除成功",Toast.LENGTH_LONG).show();
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

    private void Deal(){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","KH_YinHuan_ChuLi_SP");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("result",editText.getText().toString());
        reqData.ExtParams.put("sno",sno);
        reqData.ExtParams.put("status","WanChengYH");
        try {
            DialogUitl.showProgressDialog(YinHuanCKXXActivity.this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(YinHuanCKXXActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    GetYHXX(sno);
                                    Toast.makeText(YinHuanCKXXActivity.this,"处理成功",Toast.LENGTH_LONG).show();
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

    private void showDialogDelete(final String SNo) {
        View view = getLayoutInflater().inflate(R.layout.show_dalog_renyuananpai,
                null);
        final Dialog dialog = new Dialog(YinHuanCKXXActivity.this, R.style.MyUsualDialog);
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
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView message=dialog.getWindow().findViewById(R.id.message);
        message.setText("确认删除该隐患信息?");
        dialog.getWindow().findViewById(R.id.show_dalog_renyuananpai_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().findViewById(R.id.show_dalog_renyuananpai_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteYH(SNo);
                dialog.dismiss();
            }
        });
    }

    //删除
    private void DeleteYH(String sno){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","KH_YinHuan_ShanChu_SP");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("sno",sno);
        try {
            DialogUitl.showProgressDialog(YinHuanCKXXActivity.this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(YinHuanCKXXActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    ShowYes();
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

    private void ShowYes() {
        View view = getLayoutInflater().inflate(R.layout.show_dalog_yes,
                null);
        final Dialog dialog = new Dialog(YinHuanCKXXActivity.this, R.style.MyUsualDialog);
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
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().findViewById(R.id.show_dalog_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManagerUtil.instance().finishActivity(YinHuanCKXXActivity.this);
                dialog.dismiss();
            }
        });
    }

    public void onRestart(){
        GetYHXX(sno);
        GetImage(sno);
        super.onRestart();
    }
}
