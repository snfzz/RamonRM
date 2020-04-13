package com.ramon.ramonrm.yinhuan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.os.Handler;
import android.os.Message;
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
import android.widget.ImageView;
import android.widget.NumberPicker;
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
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class YinHuanTxxxWriteActivity extends BaseActivity implements View.OnClickListener {
    private ImageView imgbtn;
    private GridView gridView;
    private List<AllListClass> list;
    private List<String> daloglist;
    private String [] id;//用于记录需要删除的图片的id
    private String [] imagepath;//获取图片的uri
    private String dataID;

    //新增图片
    private int num=0;
    private ImageView addimg;
    private static final int REQUEST_CODE_CHOOSE = 23;//定义请求码常量
    //压缩用
    public static final String SD_APP_DIR_NAME = "TestDir"; //存储程序在外部SD卡上的根目录的名字
    public static final String PHOTO_DIR_NAME = "photo";    //存储照片在根目录下的文件夹名字
    private  List<Uri> URI=new ArrayList<>();
    private List<String>listbefor;//压缩前存放的地址
    private List<String>listafter=new ArrayList<>();//压缩后存放的地址
    private String address="";//图片地址

    //获取填写信息
    private String [] sbnum;
    private String [] SBSNoAll;
    private TextView dj,shebei,chanpin;
    private String yhdj="YHYiBan";
    private String SBSNo="";


    private String [] cpnum;
    private String [] GroupSNoAll;
    private String [] chanpinSNoAll;
    private String chanpinSNo="";
    private String GroupSNo="";

    //用于显示内容与备注中的内容
    private EditText bz,content;
    private String writebz="";
    private String writecontent="";

    private Button baocun;

    private EditText biaoti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yinhuantxxxwrite);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        init();
        GetSheBei();
        GetChanPin();
        GetImage(dataID);
        getDeviceDensity();
    }

    private void init(){
        dataID=getIntent().getStringExtra("sno");
        writebz=getIntent().getStringExtra("bz");
        writecontent=getIntent().getStringExtra("content");
        imgbtn=findViewById(R.id.activity_yinhuantxxxwrite_imgback);
        imgbtn.setOnClickListener(this);
        gridView=findViewById(R.id.activity_yinhuantxxxwrite_grid);
        addimg=findViewById(R.id.activity_yinhuandengji_add);
        addimg.setOnClickListener(this);
        dj=findViewById(R.id.activity_yinhuantxxxwrite_dj);
        dj.setOnClickListener(this);
        shebei=findViewById(R.id.activity_yinhuantxxxwrite_lmsb);
        shebei.setOnClickListener(this);
        chanpin=findViewById(R.id.activity_yinhuantxxxwrite_cp);
        chanpin.setOnClickListener(this);
        content=findViewById(R.id.activity_yinhuantxxxwrite_txtcontent);
        bz=findViewById(R.id.activity_yinhuantxxxwrite_txtbj);
        bz.setText(writebz);
        content.setText(writecontent);
        baocun=findViewById(R.id.activity_yinhuantxxxwrite_bc);
        baocun.setOnClickListener(this);
        biaoti=findViewById(R.id.activity_yinhuantxxxwrite_bt);
    }

    @Override
    public void onClick(View view) {
        int vId=view.getId();
        if (vId == R.id.activity_yinhuantxxxwrite_imgback){
            AppManagerUtil.instance().finishActivity(YinHuanTxxxWriteActivity.this);
        }
        if (vId == R.id.gridview_xinxitianxie_img){
            int i= (int) view.getTag();
            new ShowImagesDialog(YinHuanTxxxWriteActivity.this,daloglist,i).show();
        }
        if (vId == R.id.gridview_xinxitianxie_delete){
            int i =(int)view.getTag();
            showDelete(id[i]);
        }
        if (vId == R.id.activity_yinhuandengji_add){
            Matisse
                    .from(YinHuanTxxxWriteActivity.this)
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
        if (vId == R.id.activity_yinhuantxxxwrite_dj){
            DengJidalog();
        }
        if (vId == R.id.activity_yinhuantxxxwrite_lmsb){
            SheBeidalog(sbnum);
        }
        if (vId == R.id.activity_yinhuantxxxwrite_cp){
            ChanPindalog(cpnum);
        }
        if (vId == R.id.activity_yinhuantxxxwrite_bc){
            if (content.getText().toString().length()==0){
                Toast.makeText(YinHuanTxxxWriteActivity.this,"请补充内容",Toast.LENGTH_LONG).show();
            }else {
                if (list.size()==0){
                    sureAdd();
                }else {
                    XiuGai();
                }
            }
        }
    }

    /*
    1.获取图片
    2.删除图片
    3.新增图片
    */
    //获取图片
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
            DialogUitl.showProgressDialog(YinHuanTxxxWriteActivity.this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(YinHuanTxxxWriteActivity.this,reqData) {
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
                                    YinHuanCKXXAdapter adapter=new YinHuanCKXXAdapter(YinHuanTxxxWriteActivity.this,list,context);
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

    //删除图片
    private void showDelete(final String id) {
        View view = getLayoutInflater().inflate(R.layout.show_dalog_renyuananpai,
                null);
        final Dialog dialog = new Dialog(YinHuanTxxxWriteActivity.this, R.style.MyUsualDialog);
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
            DialogUitl.showProgressDialog(YinHuanTxxxWriteActivity.this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(YinHuanTxxxWriteActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    GetImage(dataID);
                                    Toast.makeText(YinHuanTxxxWriteActivity.this,"删除成功",Toast.LENGTH_LONG).show();
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

    protected void getDeviceDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Config.EXACT_SCREEN_HEIGHT = metrics.heightPixels;
        Config.EXACT_SCREEN_WIDTH = metrics.widthPixels;
    }

    //增加图片
    //获取图片uri
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            listbefor=new ArrayList<>();
            URI=new ArrayList<>();
            List<Uri> result = Matisse.obtainResult(data);
            for (int i=0;i<result.size();i++){
                URI.add(result.get(i));
            }
            for (int q=0;q<URI.size();q++){
                listbefor.add(getRealFilePath(YinHuanTxxxWriteActivity.this, URI.get(q)));
            }
            compress(listbefor,listbefor.size());
            //SendPhoto(listafter);
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
    private void compress(final List<String> list, final int size)
    {
        //
        // listafter=new ArrayList<>();
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
                        //listafter.add(file.getAbsolutePath());
                        OkHttpClient client = new OkHttpClient.Builder().readTimeout(1000, TimeUnit.SECONDS).build();
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
                                Toast.makeText(YinHuanTxxxWriteActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                num++;
                                //Log.e("fwwwweqett",response.body().string());
                                if (num==size) {
                                    Message message = new Message();
                                    handler.sendMessage(message);
                                }
                            }
                        });

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

    Handler handler=new Handler(){
        public void handleMessage(Message message){
            num=0;
            GetImage(dataID);
        }
    };

    //调用修改接口
    private void XiuGai(){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("bz",bz.getText().toString());
        reqData.ExtParams.put("chanpinSNo",chanpinSNo);
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("content",content.getText().toString());
        reqData.ExtParams.put("mingcheng",biaoti.getText().toString());
        reqData.ExtParams.put("rmshebeiSNo",SBSNo);
        reqData.ExtParams.put("sno",dataID);
        reqData.ExtParams.put("spName","KH_YinHuan_XiuGai_SP");
        reqData.ExtParams.put("yinhuandj",yhdj);
        try {
            DialogUitl.showProgressDialog(YinHuanTxxxWriteActivity.this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(YinHuanTxxxWriteActivity.this,reqData) {
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

    //获取设备数据
    private void GetSheBei(){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("keHuSNo","");
        reqData.ExtParams.put("outParams","");
        reqData.ExtParams.put("spName","WX_SB_SheBei_Status_SP");
        try {
            DialogUitl.showProgressDialog(YinHuanTxxxWriteActivity.this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(YinHuanTxxxWriteActivity.this,reqData) {
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
            DialogUitl.showProgressDialog(YinHuanTxxxWriteActivity.this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(YinHuanTxxxWriteActivity.this,reqData) {
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

    //设备的dalog
    private void DengJidalog() {
        final String [] djtxt={"一般隐患","较大隐患","严重隐患","重大隐患"};
        final  String [] yhdjtxt={"YHYiBan","YHJiaoDa","YHYanZhong","YHZhongDa"};
        View view = getLayoutInflater().inflate(R.layout.dialog_one,
                null);
        final Dialog dialog = new Dialog(YinHuanTxxxWriteActivity.this, R.style.MyUsualDialog);
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
                dj.setText(djtxt[numberPicker.getValue()]);
                yhdj=yhdjtxt[numberPicker.getValue()];
                dialog.dismiss();
            }
        });
    }

    //设备的dalog
    private void SheBeidalog(final String [] sbtxt) {
        View view = getLayoutInflater().inflate(R.layout.dialog_one,
                null);
        final Dialog dialog = new Dialog(YinHuanTxxxWriteActivity.this, R.style.MyUsualDialog);
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

    private void setNumberPickerDividerColor(NumberPicker number) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(number, new ColorDrawable(ContextCompat.getColor(YinHuanTxxxWriteActivity.this, R.color.colorPrimary)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    //产品的dalog
    private void ChanPindalog(final String [] cptxt) {
        View view = getLayoutInflater().inflate(R.layout.dialog_one,
                null);
        final Dialog dialog = new Dialog(YinHuanTxxxWriteActivity.this, R.style.MyUsualDialog);
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

    //在没图片时确定是否上传
    private void sureAdd() {
        View view = getLayoutInflater().inflate(R.layout.show_dalog_renyuananpai,
                null);
        final Dialog dialog = new Dialog(YinHuanTxxxWriteActivity.this, R.style.MyUsualDialog);
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
                XiuGai();
                dialog.dismiss();
            }
        });
    }

    //上传成功的界面
    private void ShowYes() {
        View view = getLayoutInflater().inflate(R.layout.show_dalog_yes,
                null);
        final Dialog dialog = new Dialog(YinHuanTxxxWriteActivity.this, R.style.MyUsualDialog);
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
                AppManagerUtil.instance().finishActivity(YinHuanTxxxWriteActivity.this);
                dialog.dismiss();
            }
        });
    }

}
