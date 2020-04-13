package com.ramon.ramonrm.yinhuan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.ShenQingDialog.base.Config;
import com.ramon.ramonrm.ShenQingDialog.component.ShowImageUriDialog;
import com.ramon.ramonrm.adapter.XinXiTianXieGridAdapter;
import com.ramon.ramonrm.listclass.AllListClass;
import com.ramon.ramonrm.model.XinXiTianXieinfo;
import com.ramon.ramonrm.project.XinXiTianXieActivity;
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
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class YinHuanTXXXActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton imgbtn;
    private TextView chanpin,shebei,dengji;//获取产品与设备,等级
    private String [] cpnum;
    private String [] sbnum;
    private EditText neirong,beizhu,biaoti;
    private Button btnbc;
    private String GroupSNo="";
    private String [] GroupSNoAll;
    private String SBSNo="";
    private String [] SBSNoAll;
    private String yhdj="YHYiBan";
    private String chanpinSNo="";
    private String [] chanpinSNoAll;
    private GridView photogrid;
    private ImageView addphoto;
    private  List<Uri> URI=new ArrayList<>();
    private List<String>listbefor;//压缩前存放的地址
    private List<String>listafter=new ArrayList<>();//压缩后存放的地址
    private String address="";//图片地址
    private static final int REQUEST_CODE_CHOOSE = 23;//定义请求码常量
    //压缩用
    public static final String SD_APP_DIR_NAME = "TestDir"; //存储程序在外部SD卡上的根目录的名字
    public static final String PHOTO_DIR_NAME = "photo";    //存储照片在根目录下的文件夹名字
    private String dataID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yinhuantxxx);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        GetSheBei();
        GetChanPin();
        getDeviceDensity();
    }
    //点击放大
    protected void getDeviceDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Config.EXACT_SCREEN_HEIGHT = metrics.heightPixels;
        Config.EXACT_SCREEN_WIDTH = metrics.widthPixels;
    }

    private void initView(){
        imgbtn=findViewById(R.id.activity_yinhuantxxx_imgback);
        chanpin=findViewById(R.id.activity_yinhuantxxx_cp);
        shebei=findViewById(R.id.activity_yinhuantxxx_lmsb);
        dengji=findViewById(R.id.activity_yinhuantxxx_dj);
        neirong=findViewById(R.id.activity_yinhuantxxx_txtcontent);
        beizhu=findViewById(R.id.activity_yinhuantxxx_txtbj);
        btnbc=findViewById(R.id.activity_yinhuantxxx_bc);
        biaoti=findViewById(R.id.activity_yinhuantxxx_bt);
        photogrid=findViewById(R.id.activity_yinhuantxxx_grid);
        addphoto=findViewById(R.id.activity_yinhuandengji_add);
        photogrid=findViewById(R.id.activity_yinhuantxxx_grid);
        imgbtn.setOnClickListener(this);
        chanpin.setOnClickListener(this);
        shebei.setOnClickListener(this);
        dengji.setOnClickListener(this);
        btnbc.setOnClickListener(this);
        addphoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int vId=view.getId();
        if (vId == R.id.activity_yinhuantxxx_imgback){
            AppManagerUtil.instance().finishActivity(YinHuanTXXXActivity.this);
        }
        if (vId == R.id.activity_yinhuantxxx_cp){
            ChanPindalog(cpnum);
        }
        if (vId == R.id.activity_yinhuantxxx_lmsb){
            SheBeidalog(sbnum);
        }
        if (vId == R.id.activity_yinhuantxxx_dj){
            DengJidalog();
        }
        if (vId == R.id.activity_yinhuantxxx_bc) {
            if (neirong.getText().toString().length() == 0) {
                Toast.makeText(YinHuanTXXXActivity.this,"请补充内容",Toast.LENGTH_LONG).show();
            } else {
                if (listafter.size()==0){
                    sureAdd();
                }else {
                    BaoCun();
                }

            }
        }
        if (vId == R.id.activity_yinhuandengji_add){
            Matisse
                    .from(YinHuanTXXXActivity.this)
                    .choose(MimeType.ofAll())//照片视频全部显示
                    .countable(true)//有序选择图片
                    .maxSelectable(9)//最大选择数量为9
                    .gridExpectedSize(600)//图片显示表格的大小getResources()
                    .capture(true)//选择照片时，是否显示拍照
                    .captureStrategy(new CaptureStrategy(true, "PhotoPicker"))//参数1 true表示拍
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)//图像选择和预览活动所需的方向。
                    .thumbnailScale(0.85f)//缩放比例
                    .theme(R.style.Matisse_Zhihu)//主题  暗色主题 R.style.Matisse_Dracula
                    .imageEngine(new GlideEngine())//加载方式
                    .forResult(REQUEST_CODE_CHOOSE);//请求码
        }
        if (vId == R.id.gridview_xinxitianxie_delete){
            int pos = (int) view.getTag();
            URI.remove(pos);
            listbefor.remove(pos);
            listafter.remove(pos);
            List<AllListClass>list=new ArrayList<>();
            for (int w=0;w<URI.size();w++){
                list.add(new AllListClass(URI.get(w)));
            }
            XinXiTianXieGridAdapter xinXiTianXieGridAdapter=new XinXiTianXieGridAdapter(this,list,context);
            photogrid.setAdapter(xinXiTianXieGridAdapter);
        }
        if (vId == R.id.gridview_xinxitianxie_img){
            int pos = (int) view.getTag();
            new ShowImageUriDialog(YinHuanTXXXActivity.this,URI,pos).show();
        }
    }



    private void GetSheBei(){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("keHuSNo","");
        reqData.ExtParams.put("outParams","");
        reqData.ExtParams.put("spName","WX_SB_SheBei_Status_SP");
        try {
            DialogUitl.showProgressDialog(YinHuanTXXXActivity.this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(YinHuanTXXXActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    sbnum=new String[resData.DataTable.length+1];
                                    sbnum[0]="暂不选择";
                                    SBSNoAll=new String[resData.DataTable.length+1];
                                    SBSNoAll[0]="";
                                    for (int i=0;i<resData.DataTable.length;i++){
                                        sbnum[i+1]=resData.DataTable[i].get("SBMingCheng");
                                        SBSNoAll[i+1]=resData.DataTable[i].get("SBSNo");
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

    private void GetChanPin(){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","KH_ChanPin_LieBiao_SP");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("recordTotal","1");
        reqData.ExtParams.put("outParams","recordTotal");
        reqData.ExtParams.put("groupSNo","");
        try {
            DialogUitl.showProgressDialog(YinHuanTXXXActivity.this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(YinHuanTXXXActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    cpnum=new String[resData.DataTable.length];
                                    GroupSNoAll=new String[resData.DataTable.length];
                                    chanpinSNoAll=new String[resData.DataTable.length];
                                    for (int i=0;i<resData.DataTable.length;i++){
                                        cpnum[i]=resData.DataTable[i].get("MingCheng");
                                        GroupSNoAll[i]=resData.DataTable[i].get("GroupSNo");
                                        chanpinSNoAll[i]=resData.DataTable[i].get("SNo");
                                    }
                                    chanpin.setText(cpnum[0]);
                                    chanpinSNo=chanpinSNoAll[0];
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

    //产品的dalog
    private void ChanPindalog(final String [] cptxt) {
        View view = getLayoutInflater().inflate(R.layout.dialog_one,
                null);
        final Dialog dialog = new Dialog(YinHuanTXXXActivity.this, R.style.MyUsualDialog);
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
        final NumberPicker numberPicker = dialog.getWindow().findViewById(R.id.dialog_npicker1);
        numberPicker.setWrapSelectorWheel(false);
        //设置需要显示的内容数组
        numberPicker.setDisplayedValues(null);
        numberPicker.setMaxValue(cptxt.length-1);
        numberPicker.setDisplayedValues(cptxt);
        numberPicker.setValue(0);
        //关闭编辑模式
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //分割线颜色
        setNumberPickerDividerColor(numberPicker);
        //设置滑动监听
        TextView onename=dialog.getWindow().findViewById(R.id.dialog_onename);
        onename.setText("产品名称");
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            //当NunberPicker的值发生改变时，将会激发该方法
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //  Toast.makeText(getActivity(), numbers[newVal], Toast.LENGTH_SHORT).show();
            }
        });
        dialog.getWindow().findViewById(R.id.dialog_oneno).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().findViewById(R.id.dialog_oneis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chanpin.setText(cptxt[numberPicker.getValue()]);
                GroupSNo=GroupSNoAll[numberPicker.getValue()];
                chanpinSNo=chanpinSNoAll[numberPicker.getValue()];
                dialog.dismiss();
            }
        });
    }

    //设备的dalog
    private void SheBeidalog(final String [] sbtxt) {
        View view = getLayoutInflater().inflate(R.layout.dialog_one,
                null);
        final Dialog dialog = new Dialog(YinHuanTXXXActivity.this, R.style.MyUsualDialog);
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
        final NumberPicker numberPicker = dialog.getWindow().findViewById(R.id.dialog_npicker1);
        numberPicker.setWrapSelectorWheel(false);
        //设置需要显示的内容数组
        numberPicker.setDisplayedValues(null);
        numberPicker.setMaxValue(sbtxt.length-1);
        numberPicker.setDisplayedValues(sbtxt);
        numberPicker.setValue(0);
        //关闭编辑模式
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //分割线颜色
        setNumberPickerDividerColor(numberPicker);
        //设置滑动监听
        TextView onename=dialog.getWindow().findViewById(R.id.dialog_onename);
        onename.setText("设备");
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            //当NunberPicker的值发生改变时，将会激发该方法
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //  Toast.makeText(getActivity(), numbers[newVal], Toast.LENGTH_SHORT).show();
            }
        });
        dialog.getWindow().findViewById(R.id.dialog_oneno).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().findViewById(R.id.dialog_oneis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shebei.setText(sbtxt[numberPicker.getValue()]);
                SBSNo=SBSNoAll[numberPicker.getValue()];
                dialog.dismiss();
            }
        });
    }

    //隐患的dalog
    private void DengJidalog() {
        final String [] djtxt={"一般隐患","较大隐患","严重隐患","重大隐患"};
        final  String [] yhdjtxt={"YHYiBan","YHJiaoDa","YHYanZhong","YHZhongDa"};
        View view = getLayoutInflater().inflate(R.layout.dialog_one,
                null);
        final Dialog dialog = new Dialog(YinHuanTXXXActivity.this, R.style.MyUsualDialog);
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
        final NumberPicker numberPicker = dialog.getWindow().findViewById(R.id.dialog_npicker1);
        numberPicker.setWrapSelectorWheel(false);
        //设置需要显示的内容数组
        numberPicker.setDisplayedValues(null);
        numberPicker.setMaxValue(djtxt.length-1);
        numberPicker.setDisplayedValues(djtxt);
        numberPicker.setValue(0);
        //关闭编辑模式
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //分割线颜色
        setNumberPickerDividerColor(numberPicker);
        //设置滑动监听
        TextView onename=dialog.getWindow().findViewById(R.id.dialog_onename);
        onename.setText("隐患等级");
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            //当NunberPicker的值发生改变时，将会激发该方法
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //  Toast.makeText(getActivity(), numbers[newVal], Toast.LENGTH_SHORT).show();
            }
        });
        dialog.getWindow().findViewById(R.id.dialog_oneno).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().findViewById(R.id.dialog_oneis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dengji.setText(djtxt[numberPicker.getValue()]);
                yhdj=yhdjtxt[numberPicker.getValue()];
                dialog.dismiss();
            }
        });
    }

    private void setNumberPickerDividerColor(NumberPicker number) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(number, new ColorDrawable(ContextCompat.getColor(YinHuanTXXXActivity.this, R.color.colorPrimary)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void BaoCun(){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("bz",beizhu.getText().toString());
        reqData.ExtParams.put("chanpinSNo",chanpinSNo);
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("content",neirong.getText().toString());
        reqData.ExtParams.put("dataID","");
        reqData.ExtParams.put("groupSNo",GroupSNo);
        reqData.ExtParams.put("mingCheng",biaoti.getText().toString());
        reqData.ExtParams.put("outParams","dataID");
        if (SBSNo.equals("")){

        }else {
            reqData.ExtParams.put("rmshebeiSNo",SBSNo);
        }
        reqData.ExtParams.put("spName","KH_YinHuan_ZengJia_SP");
        reqData.ExtParams.put("status","ChuLiZhongYH");
        reqData.ExtParams.put("yinhuandj",yhdj);
        try {
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(YinHuanTXXXActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    dataID=resData.DataValues.get("dataID");
                                    SendPhoto();
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



    //获取图片uri
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            listbefor=new ArrayList<>();
            List<Uri> result = Matisse.obtainResult(data);
            for (int i=0;i<result.size();i++){
                URI.add(result.get(i));
            }
            for (int q=0;q<URI.size();q++){
                listbefor.add(getRealFilePath(YinHuanTXXXActivity.this, URI.get(q)));
            }
            compress(listbefor);
            List<AllListClass>list=new ArrayList<>();
            for (int w=0;w<URI.size();w++){
                list.add(new AllListClass(URI.get(w)));
            }
            XinXiTianXieGridAdapter xinXiTianXieGridAdapter=new XinXiTianXieGridAdapter(this,list,context);
            photogrid.setAdapter(xinXiTianXieGridAdapter);

        }
    }

    /**
     *  根据Uri获取文件真实地址
     */
    public static String getRealFilePath(Context context, Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String realPath = null;
        if (scheme == null)
            realPath = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            realPath = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA},
                    null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        realPath = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        if (TextUtils.isEmpty(realPath)) {
            if (uri != null) {
                String uriString = uri.toString();
                int index = uriString.lastIndexOf("/");
                String imageName = uriString.substring(index);
                File storageDir;

                storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                File file = new File(storageDir, imageName);
                if (file.exists()) {
                    realPath = file.getAbsolutePath();
                } else {
                    storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File file1 = new File(storageDir, imageName);
                    realPath = file1.getAbsolutePath();
                }
            }
        }
        return realPath;
    }

    //进行图片压缩
    private void compress(final List<String> list)
    {
        listafter=new ArrayList<>();
        String _Path = getImagesPath();
        Luban.with(this)
                .load(list)
                .ignoreBy(100)
                .setTargetDir(_Path)
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                        //Toast.makeText(XinXiTianXieActivity.this, "I'm start", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        //Log.e("压缩后图片大小",file.length() / 1024 + "k");
                        //Log.e("压缩后图片大小",file.getAbsolutePath());
                        listafter.add(file.getAbsolutePath());
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                        e.printStackTrace();
                    }
                }).launch();
    }

    private String getImagesPath()
    {
        return createPathIfNotExist("/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME);
    }

    private String createPathIfNotExist(String pPath)
    {
        boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
        if (!sdExist) {
            Log.e("path", "SD卡不存在");
            return null;
        }
        String _AbsolutePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + pPath;
        System.out.println("dbDir->" + _AbsolutePath);
        File dirFile = new File(_AbsolutePath);
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs())
            {
                Log.e("path", "文件夹创建失败");
                return null;
            }
        }
        return _AbsolutePath;
    }

    //在没图片时确定是否上传
    private void sureAdd() {
        View view = getLayoutInflater().inflate(R.layout.show_dalog_renyuananpai,
                null);
        final Dialog dialog = new Dialog(YinHuanTXXXActivity.this, R.style.MyUsualDialog);
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
        message.setText("未上传隐患图片,是否继续保存?");
        dialog.getWindow().findViewById(R.id.show_dalog_renyuananpai_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().findViewById(R.id.show_dalog_renyuananpai_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaoCun();
                dialog.dismiss();
            }
        });

    }

    //上传成功的界面
    private void ShowYes() {
        View view = getLayoutInflater().inflate(R.layout.show_dalog_yes,
                null);
        final Dialog dialog = new Dialog(YinHuanTXXXActivity.this, R.style.MyUsualDialog);
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
        TextView textView=dialog.getWindow().findViewById(R.id.show_yes_message);
        textView.setText("上传成功");
        dialog.getWindow().findViewById(R.id.show_dalog_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManagerUtil.instance().finishActivity(YinHuanTXXXActivity.this);
                dialog.dismiss();
            }
        });
    }

    private void SendPhoto(){
        for (int i=0;i<listafter.size();i++) {
            OkHttpClient client = new OkHttpClient.Builder().readTimeout(1000, TimeUnit.SECONDS).build();
            address = listafter.get(i);
            File file = new File(address);
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("Files", file.getName(),
                            RequestBody.create(MediaType.parse("multipart/form-data"), file))
                    .addFormDataPart("dataTableName", "RBIZ.dbo.KH_YinHuan")
                    .addFormDataPart("dataID", dataID)
                    .addFormDataPart("id", "")
                    .addFormDataPart("updUserCode",Session.CurrUser.YongHuSNo)
                    .addFormDataPart("title", "")
                    .addFormDataPart("memo", "")
                    .addFormDataPart("orgFileName",address)
                    .build();
            Request request = new Request.Builder()
                    //.header("Authorization", "ClientID" + UUID.randomUUID())
                    .url(APIConfig.APIHOST + "/Api/UploadFile")
                    .post(requestBody)
                    .build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(YinHuanTXXXActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //Log.e("fwwwweqett",response.body().string());
                }
            });
        }
    }



}
