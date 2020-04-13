package com.ramon.ramonrm.project;

import androidx.annotation.NonNull;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.ramon.ramonrm.ShenQingDialog.component.ShowImageUriDialog;
import com.ramon.ramonrm.adapter.XinXiTianXieGridAdapter;
import com.ramon.ramonrm.listclass.AllListClass;
import com.ramon.ramonrm.model.XinXiTianXieinfo;
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
import com.ramon.ramonrm.yinhuan.YinHuanTXXXActivity;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
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


public class XinXiTianXieActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    private ImageButton imgbtn;
    private TextView begbtn,overbtn;
    private Calendar calBegin = Calendar.getInstance(Locale.CHINA);
    private Calendar calEnd = Calendar.getInstance(Locale.CHINA);
    private ImageView imgadd;
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Button sendbtn;
    private EditText contentedit;
    private  List<Uri> URI=new ArrayList<>();
    private GridView gridView;
    private boolean ifpost=false;//判断是否能够上传图片
    //
    //请求码
    private static final int REQUEST_CODE_CHOOSE = 23;//定义请求码常量
    //压缩用
    public static final String SD_APP_DIR_NAME = "TestDir"; //存储程序在外部SD卡上的根目录的名字
    public static final String PHOTO_DIR_NAME = "photo";    //存储照片在根目录下的文件夹名字

    private String address="";//图片地址
    private List<String>listbefor;//压缩前存放的地址
    private List<String>listafter=new ArrayList<>();//压缩后存放的地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xinxitianxie);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//防止软键盘弹出
        initView();
        getDeviceDensity();
    }

    protected void getDeviceDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Config.EXACT_SCREEN_HEIGHT = metrics.heightPixels;
        Config.EXACT_SCREEN_WIDTH = metrics.widthPixels;
    }

    private void initView(){
        imgbtn=(ImageButton)findViewById(R.id.activity_xinxitianxie_imgback);
        imgbtn.setOnClickListener(this);
        begbtn=(TextView)findViewById(R.id.activity_xinxitianxie_bngtime);
        overbtn=(TextView)findViewById(R.id.activity_xinxitianxie_overtime);
        begbtn.setOnClickListener(this);
        overbtn.setOnClickListener(this);
        imgadd=(ImageView)findViewById(R.id.activity_xinxitianxie_selectphoto);
        imgadd.setOnClickListener(this);
        sendbtn=(Button)findViewById(R.id.activity_xinxitianxie_btnok);
        sendbtn.setOnClickListener(this);
        contentedit=(EditText)findViewById(R.id.activity_xinxitianxie_txtconfpassword);
        gridView=(GridView)findViewById(R.id.activity_xinxitianxie_grid);
    }

    @Override
    public void onClick(View view) {
        int vId=view.getId();
        if (vId == R.id.activity_xinxitianxie_imgback){
            AppManagerUtil.instance().finishActivity(XinXiTianXieActivity.this);
        }
        if (vId == R.id.activity_xinxitianxie_bngtime){
            //region 开始日期
            new DatePickerDialog(XinXiTianXieActivity.this,
                    // 绑定监听器
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String strYear = year + "";
                            String strMonth = month < 9 ? "0" + (month + 1) : "" + (month + 1);
                            String strDay = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                            begbtn.setText(strYear + "-" + strMonth + "-" + strDay);
                            calBegin.set(year, month, dayOfMonth);
                        }
                    }
                    , calBegin.get(Calendar.YEAR)
                    , calBegin.get(Calendar.MONTH)
                    , calBegin.get(Calendar.DAY_OF_MONTH)).show();
            //endregion
        }
        if (vId == R.id.activity_xinxitianxie_overtime){
            //region 结束日期
            new DatePickerDialog(XinXiTianXieActivity.this,
                    // 绑定监听器
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String strYear = year + "";
                            String strMonth = month < 9 ? "0" + (month + 1) : "" + (month + 1);
                            String strDay = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                            overbtn.setText(strYear + "-" + strMonth + "-" + strDay);
                            calEnd.set(year,month,dayOfMonth);
                        }
                    }
                    , calEnd.get(Calendar.YEAR)
                    , calEnd.get(Calendar.MONTH)
                    , calEnd.get(Calendar.DAY_OF_MONTH)).show();
            //endregion
        }
        if (vId == R.id.activity_xinxitianxie_btnok){
            XinXiTianXie();
            if (ifpost ==true){
                SendPhoto();
            }else {

            }
        }
        if (vId == R.id.gridview_xinxitianxie_img){
            int pos = (int) view.getTag();
            new ShowImageUriDialog(XinXiTianXieActivity.this,URI,pos).show();
        }
        if (vId ==R.id.gridview_xinxitianxie_delete){
            int pos = (int) view.getTag();
            URI.remove(pos);
            listbefor.remove(pos);
            listafter.remove(pos);

            //Log.e("fwwwweqett",listbefor.size()+"");
            List<AllListClass>list=new ArrayList<>();
            for (int w=0;w<URI.size();w++){
                list.add(new AllListClass(URI.get(w)));
            }
            XinXiTianXieGridAdapter xinXiTianXieGridAdapter=new XinXiTianXieGridAdapter(this,list,context);
            gridView.setAdapter(xinXiTianXieGridAdapter);
        }
        if (vId == R.id.activity_xinxitianxie_selectphoto){
            //弹出拍照，照片弹窗
            // 激活系统图库，选择一张图片
            getPermission();
            Matisse
                    .from(XinXiTianXieActivity.this)
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
    }




    //获取权限
    private void getPermission() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            //Toast.makeText(this, "已经申请相关权限", Toast.LENGTH_SHORT).show();
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取您的相册、照相使用权限", 1, permissions);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //框架要求必须这么写
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    //成功打开权限
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        Toast.makeText(this, "相关权限获取成功", Toast.LENGTH_SHORT).show();
    }
    //用户未同意权限
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "请同意相关权限，否则功能无法使用", Toast.LENGTH_SHORT).show();
    }

    //获取图片uri
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {

            List<Uri> result = Matisse.obtainResult(data);
            listbefor=new ArrayList<>();
            for (int i=0;i<result.size();i++){
                URI.add(result.get(i));

            }
            for (int q=0;q<URI.size();q++){
                listbefor.add(getRealFilePath(XinXiTianXieActivity.this, URI.get(q)));
            }
            compress(listbefor);
            List<AllListClass>list=new ArrayList<>();
            for (int w=0;w<URI.size();w++){
                list.add(new AllListClass(URI.get(w)));
            }
            XinXiTianXieGridAdapter xinXiTianXieGridAdapter=new XinXiTianXieGridAdapter(this,list,context);
            gridView.setAdapter(xinXiTianXieGridAdapter);

        }
    }

    //发送图片
    private void SendPhoto(){
        for (int i=0;i<listafter.size();i++) {
            OkHttpClient client = new OkHttpClient.Builder().readTimeout(1000, TimeUnit.SECONDS).build();
            address = listafter.get(i);
            File file = new File(address);
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("Files", file.getName(),
                            RequestBody.create(MediaType.parse("multipart/form-data"), file))
                    .addFormDataPart("dataTableName", "a")
                    .addFormDataPart("dataID", "a")
                    .addFormDataPart("id", "a")
                    .addFormDataPart("updUserCode",XinXiTianXieinfo.RegionEmpID)
                    .addFormDataPart("title", "a")
                    .addFormDataPart("memo", "")
                    .addFormDataPart("orgFileName",address)
                    .build();

            Request request = new Request.Builder()
                    //.header("Authorization", "ClientID" + UUID.randomUUID())
                    .url("https://www.hyramon.com.cn:14009/api/UploadFile")
                    .post(requestBody)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(XinXiTianXieActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e("fwwwweqett",response.body().string());
                }
            });
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
    private void compress(List<String> list)
    {
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

    private void XinXiTianXie(){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RW_GCProjPlanFshReq_ZengJia_SP");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("yuangongSNo",XinXiTianXieinfo.RegionEmpID);
        reqData.ExtParams.put("projSNo",XinXiTianXieinfo.ProjSNo);
        reqData.ExtParams.put("reqType",XinXiTianXieinfo.TaskType);
        reqData.ExtParams.put("planSNo",XinXiTianXieinfo.PlanSNo);
        reqData.ExtParams.put("taskSNo","");
        reqData.ExtParams.put("finishtimereal",overbtn.getText().toString());
        reqData.ExtParams.put("starttimereal",begbtn.getText().toString());
        reqData.ExtParams.put("content",contentedit.getText().toString());
        try {
            DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(XinXiTianXieActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0){
                                    ifpost=true;
                                }else {
                                    ifpost=false;
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
}
