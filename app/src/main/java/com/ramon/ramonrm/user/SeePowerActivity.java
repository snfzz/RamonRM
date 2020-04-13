package com.ramon.ramonrm.user;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import com.ramon.ramonrm.adapter.SeePowerAdapter;
import com.ramon.ramonrm.adapter.SeePowerAdapter2;
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

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SeePowerActivity extends BaseActivity implements View.OnClickListener {
    private ImageView down;//点击触发底部控件
    private EditText seepowereditText;
    private ImageButton seeimgbtn;
    private TextView ts;//顶部的提示文字
    private int judge=0;//用于判断  0为用户，1为产品，2给钢厂
    private ImageView imgbut;//搜索按键
    private ListView listview1,listview2;
    private List<AllListClass> list1;
    private List<AllListClass>list2;
    private RelativeLayout r1,r2;
    private TextView t1,n1,remind1,remind2;
    private String [] LieBiaoSno;//调用权限列表接口时所需的sno
    private String [] powersno;
    private int deleteitme;//用于标记刷新后的已有权限的listview
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seepower);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                deleteitme=i;
                GetPowerList(LieBiaoSno[i]);
            }
        });
    }

    private void initView(){
        down = (ImageView) findViewById(R.id.activity_seepower_down);
        down.setOnClickListener(this);
        seeimgbtn = (ImageButton)findViewById(R.id.activity_seepower_imgback);
        seeimgbtn.setOnClickListener(this);
        seepowereditText = (EditText)findViewById(R.id.activity_seepower_mingcheng);
        ts = (TextView)findViewById(R.id.activity_seepower_ts);
        imgbut= (ImageView)findViewById(R.id.activity_seepower_btnsearch);
        imgbut.setOnClickListener(this);
        listview1=(ListView)findViewById(R.id.activity_seepower_list1);
        r1=(RelativeLayout)findViewById(R.id.r1);
        t1=(TextView)findViewById(R.id.activity_seepower_type);
        n1=(TextView)findViewById(R.id.activity_seepower_name);
        remind1=(TextView)findViewById(R.id.remind1);
        listview2=(ListView)findViewById(R.id.activity_seepower_list2);
        r2=(RelativeLayout)findViewById(R.id.r2);
        remind2=(TextView)findViewById(R.id.remind2);
    }

    @Override
    public void onClick(View v) {
        int vId=v.getId();
        if (vId == R.id.activity_seepower_down){
            showDialog();
        }
        if (vId == R.id.activity_seepower_imgback){
            AppManagerUtil.instance().finishActivity(SeePowerActivity.this);
        }
        if (vId == R.id.activity_seepower_btnsearch){
            switch (judge){
                case 0: //用户
                    YuanGong(seepowereditText.getText().toString());
                    break;
                case 1://产品
                    Product(seepowereditText.getText().toString());
                    break;
                case 2://工厂
                    SteelMill(seepowereditText.getText().toString());
                    break;
                default:
                    break;
            }
        }
        if (vId == R.id.listview_seepower2_photo){
            final int i= (int) v.getTag();
            DeletePower(powersno[i]);
        }
    }

    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.showdalog_seepower,
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
        dialog.getWindow().findViewById(R.id.showdalog_search_qx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
        //钢厂
        dialog.getWindow().findViewById(R.id.showdalog_search_gc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ts.setText("*当前搜索类别为钢厂，点击箭头切换");
                seepowereditText.setHint("请输入钢厂");
                judge=2;
                clear();
                dialog.hide();
            }
        });
        //用户
        dialog.getWindow().findViewById(R.id.showdalog_search_yh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ts.setText("*当前搜索类别为用户，点击箭头切换");
                judge=0;
                clear();
                seepowereditText.setHint("请输入用户");
                dialog.hide();
            }
        });
        //产品
        dialog.getWindow().findViewById(R.id.showdalog_search_cp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ts.setText("*当前搜索类别为产品，点击箭头切换");
                judge=1;
                clear();
                seepowereditText.setHint("请输入产品");
                dialog.hide();
            }
        });
    }

    private void clear(){
        r1.setVisibility(View.INVISIBLE);
        remind1.setVisibility(View.INVISIBLE);
        r2.setVisibility(View.INVISIBLE);
        remind2.setVisibility(View.INVISIBLE);
        list2=new ArrayList<>();
        SeePowerAdapter2 seePowerAdapter2 = new SeePowerAdapter2(SeePowerActivity.this, list2);
        listview2.setAdapter(seePowerAdapter2);
        list1=new ArrayList<>();
        SeePowerAdapter seePowerAdapter=new SeePowerAdapter(context,R.layout.listview_seepower,list1);
        listview1.setAdapter(seePowerAdapter);
    }

    //产品测试成功
    private void Product(String mingcheng){
        list1=new ArrayList<>();
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","SB_ChanPin_LieBiao_SP");
        //reqData.ExtParams.put("yonghuSNo","");
        //reqData.ExtParams.put("khSNo","");
        reqData.ExtParams.put("mingcheng",mingcheng);
        //reqData.ExtParams.put("cpSNo","");
        reqData.ExtParams.put("chanDiDM","");
        reqData.ExtParams.put("outParams","recordTotal");
        reqData.ExtParams.put("recordTotal","99");
        reqData.ExtParams.put("pageSize","99");
        reqData.ExtParams.put("pageIndex","1");
        //reqData.ExtParams.put("orderStr","");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        try {
            DialogUitl.showProgressDialog(SeePowerActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(SeePowerActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try{
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue==0){
                                    if (resData.DataTable.length==0){
                                        r1.setVisibility(View.INVISIBLE);
                                        remind1.setVisibility(View.INVISIBLE);
                                    }else {
                                        if (judge==0){
                                            t1.setText("部门");
                                            n1.setText("名称");
                                        }else if (judge==1){
                                            t1.setText("类型");
                                            n1.setText("名称");
                                        }else if (judge==2){
                                            t1.setText("类型");
                                            n1.setText("名称");
                                        }
                                        r1.setVisibility(View.VISIBLE);
                                        remind1.setVisibility(View.VISIBLE);
                                    }
                                    LieBiaoSno=new String[resData.DataTable.length];
                                    for (int i=0;i<resData.DataTable.length;i++){
                                        String MingCheng=resData.DataTable[i].get("MingCheng");
                                        list1.add(new AllListClass("产品",MingCheng));
                                        LieBiaoSno[i]=resData.DataTable[i].get("SNo");
                                    }
                                    SeePowerAdapter seePowerAdapter=new SeePowerAdapter(context,R.layout.listview_seepower,list1);
                                    listview1.setAdapter(seePowerAdapter);
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

    private void YuanGong(String yuanGongXM){
        list1=new ArrayList<>();
        ReqData reqData=new ReqData("SQLExec","SysSql","SPExecForTable",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","YH_YongHu_LieBiao_SP");
        reqData.ExtParams.put("outParams","recordTotal");
        reqData.ExtParams.put("recordTotal","99");
        reqData.ExtParams.put("pageSize","99");
        reqData.ExtParams.put("pageIndex","1");
        reqData.ExtParams.put("buMenMc","");
        reqData.ExtParams.put("yuanGongXM",yuanGongXM);
        reqData.ExtParams.put("neiRong","");
        reqData.ExtParams.put("baoKuoTYZX","");
        reqData.ExtParams.put("orderStr","SNo desc");
        reqData.ExtParams.put("yongHuSNo",Session.CurrUser.YongHuSNo);
        try {
            DialogUitl.showProgressDialog(SeePowerActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(SeePowerActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    if (resData.DataTable.length==0){
                                        r1.setVisibility(View.INVISIBLE);
                                        remind1.setVisibility(View.INVISIBLE);
                                    }else {
                                        if (judge==0){
                                            t1.setText("部门");
                                            n1.setText("名称");
                                        }else if (judge==1){
                                            t1.setText("类型");
                                            n1.setText("名称");
                                        }else if (judge==2){
                                            t1.setText("类型");
                                            n1.setText("名称");
                                        }
                                        r1.setVisibility(View.VISIBLE);
                                        remind1.setVisibility(View.VISIBLE);
                                    }
                                    LieBiaoSno=new String[resData.DataTable.length];
                                    for (int i=0;i<resData.DataTable.length;i++){
                                        LieBiaoSno[i]=resData.DataTable[i].get("SNo");
                                        String username=resData.DataTable[i].get("XingMing");
                                        String bmname=resData.DataTable[i].get("BMMingCheng");
                                        list1.add(new AllListClass(bmname,username));
                                    }
                                    SeePowerAdapter seePowerAdapter=new SeePowerAdapter(context,R.layout.listview_seepower,list1);
                                    listview1.setAdapter(seePowerAdapter);
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

    //钢厂
    private void SteelMill(String mingCheng){
        list1=new ArrayList<>();
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","KH_KeHu_LieBiao_SP");
        reqData.ExtParams.put("mingCheng",mingCheng);
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        try {
            DialogUitl.showProgressDialog(SeePowerActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(SeePowerActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            Log.e("fwww",result);
                            ResData resData= GsonUtils.fromJson(result,ResData.class);
                            try {
                                if (resData.RstValue==0){
                                    if (resData.DataTable.length==0){
                                        r1.setVisibility(View.INVISIBLE);
                                        remind1.setVisibility(View.INVISIBLE);
                                    }else {
                                        if (judge==0){
                                            t1.setText("部门");
                                            n1.setText("名称");
                                        }else if (judge==1){
                                            t1.setText("类型");
                                            n1.setText("名称");
                                        }else if (judge==2){
                                            t1.setText("类型");
                                            n1.setText("名称");
                                        }
                                        r1.setVisibility(View.VISIBLE);
                                        remind1.setVisibility(View.VISIBLE);
                                    }
                                    LieBiaoSno=new String[resData.DataTable.length];
                                    for (int i=0;i<resData.DataTable.length;i++){
                                        LieBiaoSno[i]=resData.DataTable[i].get("SNo");
                                        String MingCheng=resData.DataTable[i].get("MingCheng");
                                        list1.add(new AllListClass("钢厂",MingCheng));
                                    }
                                    SeePowerAdapter seePowerAdapter=new SeePowerAdapter(context,R.layout.listview_seepower,list1);
                                    listview1.setAdapter(seePowerAdapter);
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
        } catch (JSONException e) {
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    private void GetPowerList(String sno){
        list2=new ArrayList<>();
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","QX_QuanXian_LieBiao_SP");
        if (judge==0){
            reqData.ExtParams.put("yonghuSNo",sno);
        }else if (judge==1){
            reqData.ExtParams.put("cpSNo",sno);
        }else if (judge==2){
            reqData.ExtParams.put("khSNo",sno);
        }
        reqData.ExtParams.put("outParams","recordTotal");
        reqData.ExtParams.put("recordTotal","999");
        reqData.ExtParams.put("pageSize","999");
        reqData.ExtParams.put("pageIndex","1");
        //reqData.ExtParams.put("orderStr","");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        try {
            DialogUitl.showProgressDialog(SeePowerActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(SeePowerActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue==0){
                                    if (resData.DataTable.length==0){
                                        r2.setVisibility(View.INVISIBLE);
                                        remind2.setVisibility(View.INVISIBLE);
                                        Toast.makeText(SeePowerActivity.this,"暂时无权限",Toast.LENGTH_LONG).show();
                                    }else {
                                        remind2.setVisibility(View.VISIBLE);
                                        r2.setVisibility(View.VISIBLE);
                                        powersno=new String[resData.DataTable.length];
                                        for (int i = 0; i < resData.DataTable.length; i++) {
                                            String type = resData.DataTable[i].get("DeptSNames");
                                            String name = resData.DataTable[i].get("Name");
                                            String power = resData.DataTable[i].get("MC1");
                                            list2.add(new AllListClass(type, name, power, R.mipmap.delete));
                                            powersno[i]=resData.DataTable[i].get("SNo");
                                        }
                                    }
                                    SeePowerAdapter2 seePowerAdapter2 = new SeePowerAdapter2(SeePowerActivity.this, list2);
                                    listview2.setAdapter(seePowerAdapter2);
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

    private void DeletePower(String SNo){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","QX_QuanXian_ShanChu_SP");
        reqData.ExtParams.put("SNo",SNo);
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        try {
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(SeePowerActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try{
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue==0){
                                    GetPowerList(LieBiaoSno[deleteitme]);
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
