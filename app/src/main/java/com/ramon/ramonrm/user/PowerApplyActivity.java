package com.ramon.ramonrm.user;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.PhotoAdapter;
import com.ramon.ramonrm.adapter.PhotoAdapterDelete;
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

public class PowerApplyActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton applyimage;//后退键
    private ImageView down;//点击出现底部弹框
    private TextView applyts;//顶部提示
    private EditText applymc;//输入查询的关键字
    private ListView list1;//
    private List<AllListClass> photoClasses1;
    private ListView list2;//
    private List<AllListClass>photoClasses2=new ArrayList<>();
    boolean zt=true;//初始时的搜索状态用于底部弹框控制切换
    private ImageView applysearch;
    private RelativeLayout r1,r2;//r1为上方的显示框,r2为下方的显示框
    private String [] applySNo;//存放用户或者客户的sno
    private String [] applyName;
    private String [] applyType;//获取中文类型用于显示
    private String [] applyType2;//获取英文类型用于提交
    private List<String> listSNo=new ArrayList<>();
    private List<String> listType=new ArrayList<>();
    private TextView yxnr;
    private Button submission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_powerapply);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
    }

    private void initView(){
        applyimage=(ImageButton)findViewById(R.id.activity_powerapply_imgback);
        applyimage.setOnClickListener(this);
        applyts=(TextView)findViewById(R.id.activity_powerapply_ts);
        down=(ImageView)findViewById(R.id.activity_powerapply_down);
        down.setOnClickListener(this);
        applymc=(EditText)findViewById(R.id.activity_powerapply_mingcheng);
        list1=(ListView)findViewById(R.id.activity_powerapply_listview1);
        applysearch=(ImageView)findViewById(R.id.activity_powerapply_btnsearch);
        applysearch.setOnClickListener(this);
        r1=(RelativeLayout)findViewById(R.id.activity_powerapply_r1);
        list2=(ListView)findViewById(R.id.activity_powerapply_listview2);
        r2=(RelativeLayout)findViewById(R.id.activity_powerapply_r2);
        yxnr=(TextView)findViewById(R.id.yxnr);
        submission=(Button)findViewById(R.id.activity_powerapply_submission);
        submission.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.activity_powerapply_imgback){
            AppManagerUtil.instance().finishActivity(PowerApplyActivity.this);
        }
        if (vId == R.id.activity_powerapply_down){
            showDialog();
        }
        if (vId == R.id.activity_powerapply_btnsearch){
            if (zt==true){
                GetPowerUser(applymc.getText().toString());
            }else {
                GetPowerProduct(applymc.getText().toString());
            }
        }
        if (vId == R.id.activity_powerapply_submission){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String datasno=String.join("|",listSNo);
                String leibie=String.join("|",listType);
                PowerApply(datasno,leibie);
            }
        }
        //下面是适配器中的点击事件
        if (vId == R.id.listview_photo_photo){
            final int i= (int) v.getTag();
            if (listSNo.contains(applySNo[i])==true){
                Toast.makeText(PowerApplyActivity.this,"请勿重复添加",Toast.LENGTH_LONG).show();
            }else {
                if (listSNo.size()==0){
                    r2.setVisibility(View.VISIBLE);
                    yxnr.setVisibility(View.VISIBLE);
                    submission.setVisibility(View.VISIBLE);
                }
                listSNo.add(applySNo[i]);
                listType.add(applyType2[i]);
                photoClasses2.add(new AllListClass(applyType[i],applyName[i],R.mipmap.delete));
                PhotoAdapterDelete photoAdapterdelete=new PhotoAdapterDelete(PowerApplyActivity.this,photoClasses2);
                list2.setAdapter(photoAdapterdelete);
            }


        }
        if (vId == R.id.listview_delete_photo){
            final int i=(int)v.getTag();
            photoClasses2.remove(i);
            listType.remove(i);
            listSNo.remove(i);
            PhotoAdapterDelete photoAdapterdelete=new PhotoAdapterDelete(PowerApplyActivity.this,photoClasses2);
            list2.setAdapter(photoAdapterdelete);
            if (listSNo.size()==0){
                r2.setVisibility(View.INVISIBLE);
                yxnr.setVisibility(View.INVISIBLE);
                submission.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.showdalog_powerapply,
                null);
        final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        //取消
        dialog.getWindow().findViewById(R.id.showdalog_apply_qx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
        //客户
        dialog.getWindow().findViewById(R.id.showdalog_apply_kh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zt=true;
                applyts.setText("*当前搜索类别为客户，点击箭头切换");
                applymc.setHint("请输入客户");
                dialog.hide();
            }
        });
        //产品
        dialog.getWindow().findViewById(R.id.showdalog_apply_cp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zt=false;
                applyts.setText("*当前搜索类别为产品，点击箭头切换");
                applymc.setHint("请输入产品");
                dialog.hide();
            }
        });
    }

    //获取未点击前的列表产品
    private void GetPowerProduct(String mingcheng){
        photoClasses1=new ArrayList<>();
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","SB_ChanPin_LieBiao_All_SP");
        reqData.ExtParams.put("mingcheng",mingcheng);
        reqData.ExtParams.put("outParams","recordTotal");
        reqData.ExtParams.put("recordTotal","99");
        reqData.ExtParams.put("pageSize","99");
        reqData.ExtParams.put("pageIndex","1");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        try{
            DialogUitl.showProgressDialog(PowerApplyActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(PowerApplyActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                applySNo=new String[resData.DataTable.length];
                                applyName=new String[resData.DataTable.length];
                                applyType=new String[resData.DataTable.length];
                                applyType2=new String[resData.DataTable.length];
                                if (resData.RstValue == 0){
                                    if (resData.DataTable.length==0){
                                        r1.setVisibility(View.INVISIBLE);
                                    }else {
                                        r1.setVisibility(View.VISIBLE);
                                        for (int i = 0; i < resData.DataTable.length; i++) {
                                            String name = resData.DataTable[i].get("MingCheng");
                                            applySNo[i]=resData.DataTable[i].get("SNo");
                                            applyName[i]=name;
                                            applyType[i]="产品";
                                            applyType2[i]="CP";
                                            photoClasses1.add(new AllListClass("产品", name, R.mipmap.add));
                                        }
                                    }
                                    PhotoAdapter photoAdapter=new PhotoAdapter(PowerApplyActivity.this,photoClasses1);
                                    list1.setAdapter(photoAdapter);
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

    //获取未点击前的列表客户
    private void GetPowerUser(String mingcheng){
        photoClasses1=new ArrayList<>();
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","KH_KeHu_LieBiao_All_SP");
        reqData.ExtParams.put("mingcheng",mingcheng);
        reqData.ExtParams.put("outParams","recordTotal");
        reqData.ExtParams.put("recordTotal","999");
        reqData.ExtParams.put("pageSize","999");
        reqData.ExtParams.put("pageIndex","1");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        try{
            DialogUitl.showProgressDialog(PowerApplyActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(PowerApplyActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                applySNo=new String[resData.DataTable.length];
                                applyName=new String[resData.DataTable.length];
                                applyType=new String[resData.DataTable.length];
                                applyType2=new String[resData.DataTable.length];
                                if (resData.RstValue == 0){
                                    if (resData.DataTable.length==0){
                                        r1.setVisibility(View.INVISIBLE);
                                    }else {
                                        r1.setVisibility(View.VISIBLE);
                                        for (int i = 0; i < resData.DataTable.length; i++) {
                                            String name = resData.DataTable[i].get("MingCheng");
                                            applySNo[i]=resData.DataTable[i].get("SNo");
                                            applyName[i]=name;
                                            applyType[i]="客户";
                                            applyType2[i]="KH";
                                            photoClasses1.add(new AllListClass("客户", name, R.mipmap.add));
                                        }
                                    }
                                    PhotoAdapter photoAdapter=new PhotoAdapter(PowerApplyActivity.this,photoClasses1);
                                    list1.setAdapter(photoAdapter);
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

    private void PowerApply(String dataSNos,String leibieDMs){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","QX_QuanXianSQ_ZengJia_SP");
        reqData.ExtParams.put("bz","");
        reqData.ExtParams.put("dataSNos",dataSNos);
        reqData.ExtParams.put("leibieDMs",leibieDMs);
        reqData.ExtParams.put("yongHuSNo",Session.CurrUser.YongHuSNo);
        try {
            DialogUitl.showProgressDialog(PowerApplyActivity.this, reqData.CmdID, "正在提交申请");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(PowerApplyActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try{
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                Log.e("fww",result);
                                if (resData.RstValue==0){
                                    Toast.makeText(PowerApplyActivity.this,"提交成功",Toast.LENGTH_LONG).show();
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
