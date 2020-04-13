package com.ramon.ramonrm.renyuan;


import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.android.volley.VolleyError;
import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.MapSession;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.DaKaAdapter;
import com.ramon.ramonrm.model.DaKaInfo;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DateTimeUtil;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.ImageUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class KaoQinDKActivity extends BaseActivity implements View.OnClickListener {
    private static final int TAKE_PHONE = 20;//启动相机码
    private File outputImage;
    private Uri imageUri;

    private PopupWindow popupWindow;
    private LinearLayout lyMain;

    private boolean isRun = true;
    private String[] typeNames = {"出差上班打卡", "出差下班打卡", "早上打卡", "中午打卡", "下午打卡", "补打卡"};
    private String[] typeCodes = {"CCSB", "CCXB", "GSSW", "GSZW", "GSXW", "BuDaKa"};
    private GeoCoder mCoder;

    Calendar calendar = Calendar.getInstance(Locale.CHINA);

    private ImageView imgBack;

    private TextView lblDaKaType;
    private AlertDialog dlgDaKaType;
    private int mDaKaTypeIndex = 0;

    private TextView lblBDTime;

    private double lng, lat;
    private TextView lblDaKaDiDian;
    private ImageView imgDaKaDiDian;
    private AlertDialog dlgDaKaDiDian;
    private String[] dakaDiDians;
    private int mDaKaDiDIanIndex = 0;
    private boolean isGPS = false;

    private TextView lblBuKaTime,lblBuKaTimeTitle;
    private RelativeLayout lyBuKaTime;
    private EditText txtBuKaMemo;
    private RelativeLayout lyBuKaMemo;
    private ImageView imgDaKa;
    private Bitmap dkImage;
    private ListView lvDaKa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaoqindk);
        loadLocation();
        initView();
        loadDaKaType();
        loadDaKaData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRun) {
                    try {
                        long time = System.currentTimeMillis();
                        String strTime = DateTimeUtil.DateTimeToString(time, "HH:mm:ss");
                        lblBDTime.setText(strTime);
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void initView() {
        imgBack = (ImageView) findViewById(R.id.activity_kaoqindk_imgback);
        imgBack.setOnClickListener(this);
        lblDaKaType = (TextView) findViewById(R.id.activity_kaoqindk_lbldktype);
        lblDaKaType.setOnClickListener(this);
        lblBDTime = (TextView) findViewById(R.id.activity_kaoqindk_lblbdtime);
        lblDaKaDiDian = (TextView) findViewById(R.id.activity_kaoqindk_lbldkdidian);
        lblDaKaDiDian.setOnClickListener(this);
        imgDaKaDiDian = (ImageView) findViewById(R.id.activity_kaoqindk_imglocation);
        imgDaKaDiDian.setOnClickListener(this);
        lblBuKaTime = (TextView) findViewById(R.id.activity_kaoqindk_lblbukatime);
        lblBuKaTime.setOnClickListener(this);
        lblBuKaTimeTitle = (TextView)findViewById(R.id.activity_kaoqindk_lblbukatimetitle);
        lblBuKaTimeTitle.setOnClickListener(this);
        txtBuKaMemo = (EditText) findViewById(R.id.activity_kaoqindk_lblbukamemo);
        lyBuKaTime = (RelativeLayout) findViewById(R.id.activity_kaoqindk_lybukatime);
        lyBuKaMemo = (RelativeLayout) findViewById(R.id.activity_kaoqindk_lybukamemo);
        imgDaKa = (ImageView) findViewById(R.id.activity_kaoqindk_imgdakabutton);
        imgDaKa.setOnClickListener(this);
        lvDaKa = (ListView) findViewById(R.id.activity_kaoqindk_lvdaka);
        lyMain = (LinearLayout) findViewById(R.id.activity_kaoqindk_lymain);
    }

    @Override
    public void onClick(View v) {
        try {
            int vId = v.getId();
            if (vId == R.id.activity_kaoqindk_imgback) {
                AppManagerUtil.instance().finishActivity(KaoQinDKActivity.this);
            }
            if (vId == R.id.activity_kaoqindk_lbldktype) {
                selectDaKaType();
            }
            if (vId == R.id.activity_kaoqindk_lbldkdidian) {
                selectDaKaDiDian();
            }
            if (vId == R.id.activity_kaoqindk_imgdkdidian) {
                loadLocation();
            }
            if (vId == R.id.activity_kaoqindk_imgdakabutton) {
                dakaTiJiao();
            }
            if (vId == R.id.activity_kaoqindk_lblbukatime || vId == R.id.activity_kaoqindk_lblbukatimetitle) {
                selectBuKaSJ();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    private void loadLocation() {
        final ReqData reqData = new ReqData();
        BDLocation location = MapSession.getLocation();
        DialogUitl.showProgressDialog(this, reqData.CmdID, "获取定位信息……");
        if (location == null) {
            MethodUtil.showToast("无定位信息，请检查网络", context);
        } else {
            if (location.getLocType() != BDLocation.TypeGpsLocation) {
                isGPS = false;
                MethodUtil.showToast("非GPS定位，定位精度不高", context);
            } else {
                isGPS = true;
            }
            Address addr = location.getAddress();
            double lng = location.getLongitude();
            double lat = location.getLatitude();
            this.lng = lng;
            this.lat = lat;
            if (mCoder == null) {
                mCoder = GeoCoder.newInstance();
            }
            mCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                @Override
                public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                }

                @Override
                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                    try {
                        List<PoiInfo> listPoi = reverseGeoCodeResult.getPoiList();
                        dakaDiDians = new String[listPoi.size()];
                        for (int i = 0; i < listPoi.size(); i++) {
                            dakaDiDians[i] = listPoi.get(i).getAddress();
                        }
                        if (dakaDiDians.length > 0) {
                            mDaKaDiDIanIndex = 0;
                            lblDaKaDiDian.setText(dakaDiDians[0]);
                        } else {
                            mDaKaDiDIanIndex = -1;
                            lblDaKaDiDian.setText("");
                            MethodUtil.showToast("未获得位置，无法进行打卡", context);
                        }
                    }
                    catch(Exception ex){
                         ex.printStackTrace();
                    }
                    DialogUitl.dismissProgressDialog(reqData.CmdID);
                }
            });
            mCoder.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(lat, lng)).radius(500));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCoder != null) {
            mCoder.destroy();
            mCoder = null;
        }
        isRun = false;
    }

    private void loadDaKaType() {
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_SysSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "RS_GetDaKaType_SP");
        reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
        DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
        try {
            VolleyRequestUtil.RequestPost(this, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(this, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    String title = resData.DataTable[0].get("title");
                                    String code = resData.DataTable[0].get("code");
                                    for (int i = 0; i < typeCodes.length; i++) {
                                        if (typeCodes[i].equals(code)) {
                                            mDaKaTypeIndex = i;
                                            lblDaKaType.setText(title);
                                            if (code.equals("BuDaKa")) {
                                                lyBuKaMemo.setVisibility(View.VISIBLE);
                                                lyBuKaTime.setVisibility(View.VISIBLE);
                                            } else {
                                                lyBuKaMemo.setVisibility(View.GONE);
                                                lyBuKaTime.setVisibility(View.GONE);
                                            }
                                            break;
                                        }
                                    }
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), context);
                                ex.printStackTrace();
                            } finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog(reqData.CmdID);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    private void selectDaKaType() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("打卡类别");
        alertBuilder.setSingleChoiceItems(typeNames, mDaKaTypeIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDaKaTypeIndex = i;
                lblDaKaType.setText(typeNames[i]);
                String code = typeCodes[i];
                if (code.equals("BuDaKa")) {
                    lyBuKaMemo.setVisibility(View.VISIBLE);
                    lyBuKaTime.setVisibility(View.VISIBLE);
                } else {
                    lyBuKaMemo.setVisibility(View.GONE);
                    lyBuKaTime.setVisibility(View.GONE);
                }
                dlgDaKaType.dismiss();
            }
        });
        dlgDaKaType = alertBuilder.create();
        dlgDaKaType.show();
    }

    private void selectDaKaDiDian() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("打开地点");
        alertBuilder.setSingleChoiceItems(dakaDiDians, mDaKaDiDIanIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDaKaDiDIanIndex = i;
                lblDaKaDiDian.setText(dakaDiDians[i]);
                dlgDaKaDiDian.dismiss();
            }
        });
        dlgDaKaDiDian = alertBuilder.create();
        dlgDaKaDiDian.show();
    }

    private void selectBuKaSJ() {
        new TimePickerDialog(this,2,
                // 绑定监听器
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String strHour = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                        String strMin = minute < 10 ? "0" + minute : "" + minute;
                        lblBuKaTime.setText(hourOfDay + ":" + strMin);
                    }
                }
                // 设置初始时间
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                // true表示采用24小时制
                , true).show();

    }

    private void dakaTiJiao() {
        if (mDaKaDiDIanIndex < 0) {
            MethodUtil.showToast("未获得位置，无法进行打卡", context);
            return;
        }
        String code = typeCodes[mDaKaTypeIndex];
        String bdkMemo = txtBuKaMemo.getText().toString();
        if (code.equals("BuDaKa")) {
            if (bdkMemo.length() == 0) {
                MethodUtil.showToast("补打卡需要输入补卡说明", context);
                return;
            }
        }
        takePhone();
    }

    private void takePhone() {
        String fileName = DateTimeUtil.DateTimeToString(System.currentTimeMillis(), "yyyyMMdd_HHmmss");
        //创建一个File对象用于存储拍照后的照片
        outputImage = new File(ImageUitl.PHOTO_DIR, fileName + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //判断Android版本是否是Android7.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(KaoQinDKActivity.this, "com.ramon.ramonrm.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }

        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            switch (requestCode) {
                case TAKE_PHONE: {
                    //拍照完成后对图片进行压缩+加水印，最后上传
                    String dkDiZhi = lblDaKaDiDian.getText().toString();
                    String fileName = outputImage.getPath();
                    Bitmap bitmap = ImageUitl.getLocalImage(fileName);
                    Bitmap bitmap1 = ImageUitl.imageYaSuo(bitmap);
                    Bitmap bitmap2 = ImageUitl.drawTextToLeftTop(bitmap1, dkDiZhi, Color.WHITE);
                    dkImage = bitmap2;
                    ImageUitl.saveImage(bitmap2, fileName);
                    insertDaKa();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    private void insertDaKa() {
        String address = lblDaKaDiDian.getText().toString();
        String bkMemo = txtBuKaMemo.getText().toString();
        String bkSJ = DateTimeUtil.DateTimeToString(System.currentTimeMillis(), "yyyy-MM-dd") + " " + lblBuKaTime.getText().toString();
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_SysSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "RS_DaKa_ZengJia_SP");
        reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("yonghuSNo", Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("lon", this.lng + "");
        reqData.ExtParams.put("lat", this.lat + "");
        reqData.ExtParams.put("address", address);
        reqData.ExtParams.put("leiBieDM", typeCodes[mDaKaTypeIndex]);
        reqData.ExtParams.put("BZ", bkMemo);
        reqData.ExtParams.put("buDaSJ", bkSJ);
        reqData.ExtParams.put("isBuKa", typeCodes[mDaKaTypeIndex].equals("BuDaKa") ? "1" : "0");
        reqData.ExtParams.put("isGPS", isGPS ? "1" : "0");
        reqData.ExtParams.put("outParams", "dataID|fileID");
        reqData.ExtParams.put("dataID", "");
        reqData.ExtParams.put("fileID", "");
        DialogUitl.showProgressDialog(this, reqData.CmdID, "正在提交打卡记录……");
        try {
            VolleyRequestUtil.RequestPost(this, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(this, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    String dataID = resData.DataTable[0].get("DataID");
                                    String fileID = resData.DataTable[0].get("FileID");
                                    Message msg = new Message();
                                    Bundle data = new Bundle();
                                    data.putString("dataID", dataID);
                                    data.putString("fileID", fileID);
                                    msg.setData(data);
                                    handler.sendMessage(msg);
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), context);
                                ex.printStackTrace();
                            } finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog(reqData.CmdID);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String fileID = data.getString("fileID");
            String dataID = data.getString("dataID");
            String fileName = outputImage.getPath();
            HashMap<String, String> hashData = new HashMap<>();
            hashData.put("dataTableName", "RSYS.dbo.RS_DaKa");
            hashData.put("dataID", dataID);
            hashData.put("updUserCode", Session.CurrUser.YongHuSNo);
            hashData.put("fileID", fileID);
            hashData.put("title", "");
            hashData.put("memo", "");
            hashData.put("id", fileID);
            hashData.put("orgFileName", fileName);
            ImageUitl.uploadImage(APIConfig.APIHOST + "/Api/UploadFile", fileName, hashData, handlerUploadFile);
        }
    };

    Handler handlerUploadFile = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            loadDaKaData();
        }
    };

    private void loadDaKaData() {
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_SysSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "RS_DaKa_LieBiao_Today_SP");
        reqData.ExtParams.put("YongHuSNo", Session.CurrUser.YongHuSNo);
        DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
        try {
            VolleyRequestUtil.RequestPost(this, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(this, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadDaKaView(resData.DataTable);
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), context);
                                ex.printStackTrace();
                            } finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog(reqData.CmdID);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    private void loadDaKaView(HashMap<String, String>[] hashData) {
        List<DaKaInfo> listInfo = new ArrayList<>();
        for (int i = 0; i < hashData.length; i++) {
            HashMap<String, String> hashMap = hashData[i];
            DaKaInfo dkInfo = new DaKaInfo();
            dkInfo.SNo = hashMap.get("SNo");
            dkInfo.LeiBieDM = hashMap.get("LeiBieDM");
            for (int j = 0; j < typeCodes.length; j++) {
                if (dkInfo.LeiBieDM.equals(typeCodes[j])) {
                    dkInfo.LeiBieMC = typeNames[j];
                }
            }
            dkInfo.Lng = Double.parseDouble(hashMap.get("Lon"));
            dkInfo.Lat = Double.parseDouble(hashMap.get("Lat"));
            dkInfo.FilePath = hashMap.get("FilePath");
            dkInfo.FileName = hashMap.get("FileName");
            dkInfo.FileExt = hashMap.get("FileExt");
            dkInfo.FileSize = hashMap.get("FileSize");
            dkInfo.Address = hashMap.get("Address");
            dkInfo.DaKaSJ = hashMap.get("DaKaSJ");
            dkInfo.ImageUrl = APIConfig.APIHOST + "/" + dkInfo.FilePath + "/" + dkInfo.FileName;
            listInfo.add(dkInfo);
        }
        DaKaAdapter adapter = new DaKaAdapter(context, R.layout.listitem_dakainfo, listInfo);
        adapter.setOnImageClickListener(new DaKaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object item, View view) {
                try {
                    DaKaInfo daKaInfo = (DaKaInfo) item;
                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = layoutInflater.inflate(R.layout.dialog_imageview, null);
                    ImageView imageView = (ImageView) view.findViewById(R.id.activity_imageview_imgview);
                    Bitmap img = ImageUitl.getImage(context, daKaInfo.FileName, daKaInfo.ImageUrl, R.mipmap.noimage);
                    imageView.setImageBitmap(img);
                    int imgWidth = img.getWidth();
                    int imgHeight = img.getHeight();
                    WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    int lyWidth = lyMain.getWidth();
                    int lyHeight = lyMain.getHeight();
                    int w = imgWidth < lyWidth ? imgWidth : lyWidth;
                    int h = imgHeight < lyHeight ? imgHeight : lyHeight;
                    popupWindow = new PopupWindow(view, w, h);
                    // 使其聚集
                    popupWindow.setFocusable(true);
                    // 设置允许在外点击消失
                    popupWindow.setOutsideTouchable(true);
                    int offsetX = (lyWidth - w) / 2;
                    int offsetY = (lyHeight - h) / 2;
                    popupWindow.showAtLocation(lyMain, Gravity.CENTER,0,0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        lvDaKa.setAdapter(adapter);
    }
}
