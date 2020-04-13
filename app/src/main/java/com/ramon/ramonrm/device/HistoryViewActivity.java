package com.ramon.ramonrm.device;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.charts.ChartResource;
import com.ramon.ramonrm.model.DeviceStatus;
import com.ramon.ramonrm.model.HisData;
import com.ramon.ramonrm.model.ShowText;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DateTimeUtil;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class HistoryViewActivity extends BaseActivity implements View.OnClickListener {
    private Calendar calendar = Calendar.getInstance(Locale.CHINA);
    private ImageButton btnBack;
    private DeviceStatus mSBInfo;
    private TextView lblKeHuMC,lblSheBeiMC,lblStrandID,lblDate,lblTime;
    private ImageView btnPrev,btnQuery,btnNext,btnDropdwon,btnFilter;
    private long currStartTime = 0;
    private LineChartView chartLSSJ;
    private List<Line> listLine;
    private List<AxisValue> axisValues;
    private List<Boolean> listLineVisible;
    private int mLiuHao = 1;
    private String[]mLiuHaoTitle;

    private LinearLayout lyLenged;

    private List<ShowText>listShowText;
    private boolean[] mSelectedLine;

    private AlertDialog dlgStrand;
    private AlertDialog dlgLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historyview);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        String sbInfo = getIntent().getStringExtra("SBInfo");
        mSBInfo = GsonUtils.fromJson(sbInfo, DeviceStatus.class);
        initDataCode();
        initView();
        loadData();
        initLenged();
    }

    private void initView(){
        btnBack = findViewById(R.id.activity_historyview_imgback);
        btnBack.setOnClickListener(this);
        lblKeHuMC = findViewById(R.id.activity_historyview_lblkehumc);
        lblSheBeiMC = findViewById(R.id.activity_historyview_lblshebeimc);
        lblStrandID = findViewById(R.id.activity_historyview_lblstrandid);
        lblStrandID.setOnClickListener(this);
        lblDate = findViewById(R.id.activity_historyview_lbldate);
        lblDate.setOnClickListener(this);
        lblTime = findViewById(R.id.activity_historyview_lbltime);
        lblTime.setOnClickListener(this);
        btnPrev = findViewById(R.id.activity_historyview_imgprev);
        btnPrev.setOnClickListener(this);
        btnQuery = findViewById(R.id.activity_historyview_imgquery);
        btnQuery.setOnClickListener(this);
        btnNext = findViewById(R.id.activity_historyview_imgnext);
        btnNext.setOnClickListener(this);
        btnDropdwon = findViewById(R.id.activity_historyview_imgdropdown);
        btnDropdwon.setOnClickListener(this);
        btnFilter =findViewById(R.id.activity_historyview_imgfilter);
        btnFilter.setOnClickListener(this);
        lyLenged = findViewById(R.id.activity_historyview_lylenged);
        chartLSSJ = findViewById(R.id.activity_historyview_chartlssj);
    }

    private void loadData() {
        lblKeHuMC.setText(mSBInfo.KeHuJC);
        lblSheBeiMC.setText(mSBInfo.DevName);
        long dtStart = System.currentTimeMillis() - 30 * 60 * 1000;
        currStartTime = dtStart;
        lblDate.setText(DateTimeUtil.DateTimeToString(dtStart,"yyyy-MM-dd"));
        lblTime.setText(DateTimeUtil.DateTimeToString(dtStart,"HH:mm"));
        mLiuHaoTitle = new String[mSBInfo.LiuShu];
        for(int i=0;i<mSBInfo.LiuShu;i++){
            mLiuHaoTitle[i] = Session.StrandTitles[i];
        }
    }

    private void initLenged(){
        int index = 0;
        lyLenged.removeAllViews();
        listLineVisible = new ArrayList<>();
        for (int i = 0; i < listShowText.size(); i++) {
            ShowText sText = listShowText.get(i);
            if (sText.IsChecked) {
                TextView txtView = new TextView(this);
                txtView.setText("● " + sText.Title +" ");
                txtView.setTextColor(this.getResources().getColor(R.color.colorTitle));
                txtView.setBackgroundColor(ChartResource.getColor(index));
                txtView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,4,0,0);
                txtView.setLayoutParams(layoutParams);
                txtView.setOnClickListener(new LengedOnClickListener());
                txtView.setTag(index);
                lyLenged.addView(txtView);
                listLineVisible.add(true);
                index++;
            }
        }
    }

    private class LengedOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                TextView lblLenged = (TextView) v;
                int index = Integer.parseInt(v.getTag().toString());
                boolean isVisible = listLineVisible.get(index);
                listLineVisible.set(index, !isVisible);
                lblLenged.setBackgroundColor(isVisible ? getResources().getColor(R.color.colorBackground) : ChartResource.getColor(index));
                showHisChart();
            } catch (Exception e) {
                e.printStackTrace();
                MethodUtil.showToast(e.getMessage(), context);
            }
        }
    }

    private void initDataCode() {
        listShowText = new ArrayList<>();
        for (int i = 0; i < Session.DataZhanShiPZ.length; i++) {
            HashMap<String, String> hashMap = Session.DataZhanShiPZ[i];
            String cpSNo = hashMap.get("ChanPinSNo").toUpperCase().trim();
            if (cpSNo.equals(mSBInfo.ChanPinSNo)) {
                HashMap<String, String> dicTemp = new HashMap<>();
                String[] keyCode = new String[]{"QuXianTu", "ShuZiXianShi", "ZhuangTaiDeng", "GuZhangDeng"};//曲线图放最前面，这样是为了设置让曲线code可以加进来，并且曲线Code可以选中
                String[] keyText = new String[]{"QuXianTuText", "ShuZiXianShiText", "ZhuangTaiDengText", "GuZhangDengText"};
                for (int j = 0; j < keyCode.length; j++) {
                    String code = keyCode[j];
                    String text = keyText[j];
                    String[] codes = hashMap.get(keyCode[j]).split(",");
                    String[] titles = hashMap.get(keyText[j]).split(",");
                    for (int k = 0; k < codes.length; k++) {
                        if (!dicTemp.containsKey(codes[k].trim())) {
                            ShowText sText = new ShowText();
                            sText.Key = codes[k].trim();
                            sText.Title = titles[k].trim();
                            sText.IsChecked = j == 0;
                            dicTemp.put(codes[k].trim(), "");
                            listShowText.add(sText);
                        }
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        v.setEnabled(false);
        try{
            int vId = v.getId();
            if(vId == R.id.activity_historyview_imgback) {
                AppManagerUtil.instance().finishActivity(this);
            }
            if(vId == R.id.activity_historyview_lbldate){
                //选择日期
                selectDate();
            }
            if(vId == R.id.activity_historyview_lbltime){
                //选择时间
                selectTime();
            }
            if(vId == R.id.activity_historyview_imgquery){
                //查询
                loadHistoryData();
            }
            if(vId == R.id.activity_historyview_imgprev){
                //往前30min
                currStartTime = currStartTime-30*60*1000;
                lblDate.setText(DateTimeUtil.DateTimeToString(currStartTime,"yyyy-MM-dd"));
                lblTime.setText(DateTimeUtil.DateTimeToString(currStartTime,"HH:mm"));
                loadHistoryData();
            }
            if(vId == R.id.activity_historyview_imgnext){
                //往后30min
                currStartTime = currStartTime+30*60*1000;
                lblDate.setText(DateTimeUtil.DateTimeToString(currStartTime,"yyyy-MM-dd"));
                lblTime.setText(DateTimeUtil.DateTimeToString(currStartTime,"HH:mm"));
                loadHistoryData();
            }
            if(vId == R.id.activity_historyview_imgfilter){
                //选择曲线
                selectLine();
            }
            if(vId == R.id.activity_historyview_lblstrandid){
                //流号选择
                selectStrand();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
        finally {
            v.setEnabled(true);
        }
    }

    private void selectStrand() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("选择流");
        alertBuilder.setSingleChoiceItems(mLiuHaoTitle, mLiuHao - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mLiuHao = i + 1;
                lblStrandID.setText(mLiuHaoTitle[i]);
                dlgStrand.dismiss();
            }
        });
        dlgStrand = alertBuilder.create();
        dlgStrand.show();
    }

    private void selectLine() {
        String[] listTitle = new String[listShowText.size()];
        mSelectedLine = new boolean[listShowText.size()];
        for (int i = 0; i < listShowText.size(); i++) {
            ShowText sText = listShowText.get(i);
            listTitle[i] = sText.Title;
            mSelectedLine[i] = sText.IsChecked;
        }
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("选择流");
        alertBuilder.setMultiChoiceItems(listTitle, mSelectedLine, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                try {
                    if(which>=0 && which<mSelectedLine.length){
                        mSelectedLine[which] = isChecked;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MethodUtil.showToast(e.getMessage(), context);
                }
            }
        }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    for(int i=0;i<listShowText.size();i++){
                        listShowText.get(i).IsChecked = mSelectedLine[i];
                    }
                    initLenged();
                    listLine.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                    MethodUtil.showToast(e.getMessage(), context);
                }
            }
        }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                    MethodUtil.showToast(e.getMessage(), context);
                }
            }
        });
        dlgLine = alertBuilder.create();
        dlgLine.show();
    }

    private void selectDate(){
        new DatePickerDialog(this,
                // 绑定监听器
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String strYear = year + "";
                        String strMonth = month < 9 ? "0" + (month + 1) : "" + (month + 1);
                        String strDay = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                        lblDate.setText(strYear + "-" + strMonth + "-" + strDay);
                        calendar.set(year, month, dayOfMonth);
                        getDateTime();
                    }
                }
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void selectTime() {
        new TimePickerDialog(this,2,
                // 绑定监听器
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String strHour = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                        String strMin = minute < 10 ? "0" + minute : "" + minute;
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        lblTime.setText(hourOfDay + ":" + strMin);
                        getDateTime();
                    }
                }
                // 设置初始时间
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                // true表示采用24小时制
                , true).show();

    }

    private void getDateTime(){
        String strTime = lblDate.getText() +" "+lblTime.getText();
        currStartTime = DateTimeUtil.StringToDateTime(strTime,"yyyy-MM-dd HH:mm");
    }

    //region 历史数据
    private void loadHistoryData() {
        String startTime = DateTimeUtil.DateTimeToString(currStartTime, "yyyy-MM-dd HH:mm:ss");
        String dataCode = "";
        for (int i=0;i<listShowText.size();i++){
            if(listShowText.get(i).IsChecked) {
                dataCode += listShowText.get(i).Key + "|";
            }
        }
        ReqData reqData =ReqData.createReqData(ReqData.ReqType.T_CmdExec_DevData_GetHisData,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("devCode", mSBInfo.DevSNo);
        reqData.ExtParams.put("devSubCode", mLiuHao + "");
        reqData.ExtParams.put("dataCode", dataCode);
        reqData.ExtParams.put("minutes", "30");
        reqData.ExtParams.put("startTime", startTime);
        try {
            DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(this, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(this, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadHistoryView(resData.DataTable);
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                MethodUtil.showToast(ex.getMessage(), context);
                            } finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        // Volley请求失败时调用的函数
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

    private void loadHistoryView(HashMap<String, String>[] hashData) {
        HashMap<String, Integer> hashKey = new HashMap<>();
        listLine = new ArrayList<>();
        axisValues = new ArrayList<>();
        int index = 0;
        HashMap<String, List<PointValue>> listPoint = new HashMap<>();

        for (int i = 0; i < listShowText.size(); i++) {
            if(listShowText.get(i).IsChecked) {
                String key = listShowText.get(i).Key.toLowerCase();
                Line line = new Line();
                line.setHasLabels(false);
                line.setHasPoints(false);
                line.setColor(ChartResource.getColor(index));
                line.setHasLabels(true);
                listLine.add(line);
                hashKey.put(key, index);
                listPoint.put(key, new ArrayList<PointValue>());
                index++;
            }
        }

        List<HisData>listHisData =new ArrayList<>();
        for(int i=0;i<hashData.length;i++){
            HisData hisData = new HisData();
            String dataCode = hashData[i].get("DataCode").toLowerCase();
            String dataTimeStr = hashData[i].get("DataTimeStr").trim().substring(0, 19);
            String dataValue = hashData[i].get("DataValue");
            double dataTime = Double.parseDouble(hashData[i].get("DataTime"));
            hisData.DataCode = dataCode;
            hisData.DataTime = dataTime;
            hisData.DataValue = dataValue;
            hisData.DataTimeStr = dataTimeStr;
            listHisData.add(hisData);
        }
        Collections.sort(listHisData);
        HashMap<String, Integer> hashXTime = new HashMap<>();
        List<String> listXTime = new ArrayList<>();
        for (int i = 0; i < hashData.length; i++) {
            String dataTime = hashData[i].get("DataTimeStr").trim().substring(0, 19);
            if (!hashXTime.containsKey(dataTime)) {
                hashXTime.put(dataTime, listXTime.size());
                listXTime.add(dataTime);
            }
        }
        Collections.sort(listXTime);
        hashXTime.clear();
        for(int i=0;i<listXTime.size();i++) {
            String dataTime = listXTime.get(i);
            hashXTime.put(dataTime, i);
            axisValues.add(new AxisValue(i).setLabel("T " + dataTime.split(" ")[1]));
        }

        for (int i = 0; i < listHisData.size(); i++) {
            HisData hisData = listHisData.get(i);
            String dataTime = hisData.DataTimeStr;
            String dataCode = hisData.DataCode;
            String dataValue = hisData.DataValue;
            int xId = 0;
            if(hashXTime.containsKey(dataTime)) {
                xId = hashXTime.get(dataTime);
                if (hashKey.containsKey(dataCode)) {
                    listPoint.get(dataCode).add(new PointValue(xId, Float.parseFloat(dataValue)).setLabel(dataValue));
                }
            }
        }
        for (String key : hashKey.keySet()) {
            int i = hashKey.get(key);
            listLine.get(i).setValues(listPoint.get(key));
        }
        showHisChart();
    }

    private void showHisChart() {
        List<Line> list = new ArrayList<>();
        for (int i = 0; i < listLine.size(); i++) {
            boolean isVisible = listLineVisible.get(i);
            if (isVisible) {
                list.add(listLine.get(i));
            }
        }
        LineChartData lineData = new LineChartData(list);
        lineData.setAxisXBottom(new Axis().setValues(axisValues).setHasLines(false).setTextColor(Color.WHITE).setAutoGenerated(false).setTextSize(8).setHasTiltedLabels(true));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setTextColor(Color.WHITE));
        lineData.setValueLabelBackgroundEnabled(true);
        chartLSSJ.setLineChartData(lineData);
        chartLSSJ.setZoomEnabled(true);//是否支持缩放
        chartLSSJ.setZoomType(ZoomType.HORIZONTAL);//缩放方向
        chartLSSJ.setVisibility(View.VISIBLE);
    }
    //endregion
}
