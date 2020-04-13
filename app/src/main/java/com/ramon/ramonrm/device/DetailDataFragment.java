package com.ramon.ramonrm.device;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.style.LineStyle;
import com.bin.david.form.data.table.TableData;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.charts.ChartPoint;
import com.ramon.ramonrm.charts.ChartResource;
import com.ramon.ramonrm.model.DeviceStatus;
import com.ramon.ramonrm.model.HisData;
import com.ramon.ramonrm.model.TableBean;
import com.ramon.ramonrm.util.DateTimeUtil;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class DetailDataFragment extends Fragment {
    private int mLiuHao = 1;
    private int mLiuShu = 1;
    private String mSBSNo = "";
    private String mCPSNo = "";
    private HashMap<String, String> mShowConfig;
    private DeviceStatus mSBInfo;

    private HashMap<String, Integer> mShuZhiShow;
    private HashMap<String, Integer> mZhuangTaiShow;
    private HashMap<String, Integer> mGuZhangShow;
    private HashMap<String, Integer> mQuXianTu;

    private List<String> mShuZhiShowText;
    private List<String> mZhuangTaiShowText;
    private List<String> mGuZhangShowText;
    private List<String> mQuXianTuText;

    private List<String> mShuZhiShowCode;
    private List<String> mZhuangTaiShowCode;
    private List<String> mGuZhangShowCode;
    private List<String> mQuXianTuCode;

    //region 实时数据
    private SmartTable sTabResult;
    private List<Column> sTabColumns;
    private List<TableBean> sTabDatas;
    private HashMap<String, String>[] mRealData;
    //endregion

    //region 统计数据
    private ColumnChartView chartTJSJ;
    private HashMap<String, String>[] mTongJiData;
    //endregion

    //region 历史数据
    private ImageView imgHisRefresh;
    private RelativeLayout rlayHisData;
    private LineChartView chartLSSJ;
    private List<TextView> listView;
    private LinearLayout lyLenged;
    private List<Line> listLine;
    private List<Boolean> listLineVisible;
    private List<AxisValue> axisValues;
    private TextView lblMoreHisData;
    //endregion

    private Activity mActivity;

    public DetailDataFragment(HashMap<String, String> showConfig, HashMap<String, String>[] hashData, DeviceStatus sbInfo, int liuHao) {
        mSBInfo = sbInfo;
        mSBSNo = mSBInfo.DevSNo;
        mCPSNo = mSBInfo.ChanPinSNo;
        mLiuHao = liuHao;
        mLiuShu = mSBInfo.LiuShu;
        mShowConfig = showConfig;
        mRealData = hashData;
        mShuZhiShowText = new ArrayList<>();
        mZhuangTaiShowText = new ArrayList<>();
        mGuZhangShowText = new ArrayList<>();
        mQuXianTuText = new ArrayList<>();
        mShuZhiShowCode = new ArrayList<>();
        mZhuangTaiShowCode = new ArrayList<>();
        mGuZhangShowCode = new ArrayList<>();
        mQuXianTuCode = new ArrayList<>();
        String[] keyCode = new String[]{"ShuZiXianShi", "ZhuangTaiDeng", "GuZhangDeng", "QuXianTu"};
        String[] keyText = new String[]{"ShuZiXianShiText", "ZhuangTaiDengText", "GuZhangDengText", "QuXianTuText"};
        List<String>[] listText = new List[4];
        listText[0] = mShuZhiShowText;
        listText[1] = mZhuangTaiShowText;
        listText[2] = mGuZhangShowText;
        listText[3] = mQuXianTuText;
        List<String>[] listCode = new List[4];
        listCode[0] = mShuZhiShowCode;
        listCode[1] = mZhuangTaiShowCode;
        listCode[2] = mGuZhangShowCode;
        listCode[3] = mQuXianTuCode;
        HashMap<String, Integer>[] tempMap = new HashMap[4];
        for (int i = 0; i < tempMap.length; i++) {
            tempMap[i] = new HashMap<>();
            String[] codes = mShowConfig.get(keyCode[i]).split(",");
            String[] titles = mShowConfig.get(keyText[i]).split(",");
            for (int j = 0; j < codes.length; j++) {
                listText[i].add(titles[j]);
                listCode[i].add(codes[j].trim().toLowerCase());
                tempMap[i].put(codes[j].trim().toLowerCase(), j);
            }
        }
        mShuZhiShow = tempMap[0];
        mZhuangTaiShow = tempMap[1];
        mGuZhangShow = tempMap[2];
        mQuXianTu = tempMap[3];
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //不同的Activity对应不同的布局
        View view = inflater.inflate(R.layout.fragment_detaildata, container, false);
        sTabResult = view.findViewById(R.id.fragment_detaildata_stabresult);
        chartTJSJ = view.findViewById(R.id.fragment_detaildata_charttjsj);
        chartLSSJ = view.findViewById(R.id.fragment_detaildata_chartlssj);
        lyLenged = view.findViewById(R.id.fragment_detaildata_lyhis);
        rlayHisData = view.findViewById(R.id.fragment_detaildata_rlayhisdata);
        imgHisRefresh = view.findViewById(R.id.fragment_detaildata_imghisdatarefresh);
        imgHisRefresh.setOnClickListener(new MyOnClickListener());
        lblMoreHisData = view.findViewById(R.id.fragment_detaildata_lblmorehisdata);
        lblMoreHisData.setOnClickListener(new MyOnClickListener());
        return view;
    }

    public class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                int vId = v.getId();
                if (vId == R.id.fragment_detaildata_imghisdatarefresh) {
                    loadHistoryData();
                }
                if(vId == R.id.fragment_detaildata_lblmorehisdata){
                    Intent intent = new Intent(mActivity,HistoryViewActivity.class);
                    String sbInfo = GsonUtils.toJson(mSBInfo);
                    intent.putExtra("SBInfo",sbInfo);
                    mActivity.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadRealView(mRealData);
                    if (mTongJiData != null) {
                        loadTongJiView(mTongJiData);
                    } else {
                        loadTongJiData();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();

        handler.postDelayed(runnable,3000);

    }

    //region 实时数据
    private void loadRealView(HashMap<String, String>[] hashData) {

        sTabColumns = new ArrayList<>();
        sTabColumns.add(new Column<String>("名称", "Bean1"));
        sTabColumns.add(new Column<String>("值", "Bean2"));
        sTabDatas = new ArrayList<>();

        //region 数字显示
        for (int i_First = 0; i_First < mShuZhiShowCode.size(); i_First++) {
            String key = mShuZhiShowCode.get(i_First);
            TableBean bean = new TableBean();
            bean.Bean1 = mShuZhiShowText.get(mShuZhiShow.get(key));
            for (int i = 0; i < hashData.length; i++) {
                HashMap<String, String> hashMap = hashData[i];
                int devSubCode = Integer.parseInt(hashMap.get("devSubCode"));
                if (devSubCode == mLiuHao) {
                    String dataCode = hashMap.get("dataCode").toLowerCase();
                    double dataValue = Double.parseDouble(hashMap.get("dataValue"));
                    if (key.equals(dataCode)) {
                        bean.Bean2 = String.format("%.3f", dataValue);
                        //Log.e("dsaddsa",bean.Bean2);
                        sTabDatas.add(bean);
                        break;
                    }
                }
            }
        }
        //endregion
        //region 状态灯
        for (int i_First = 0; i_First < mZhuangTaiShowCode.size(); i_First++) {
            String key = mZhuangTaiShowCode.get(i_First);
            TableBean bean = new TableBean();
            bean.Bean1 = mZhuangTaiShowText.get(mZhuangTaiShow.get(key));
            for (int i = 0; i < hashData.length; i++) {
                HashMap<String, String> hashMap = hashData[i];
                int devSubCode = Integer.parseInt(hashMap.get("devSubCode"));
                if (devSubCode == mLiuHao) {
                    String dataCode = hashMap.get("dataCode").toLowerCase();
                    double dataValue = Double.parseDouble(hashMap.get("dataValue"));
                    if (key.equals(dataCode)) {
                        bean.Bean2 = dataValue > 0.5 ? bean.Bean1 : "-";
                        sTabDatas.add(bean);
                        break;
                    }
                }
            }
        }
        //endregion
        //region 故障灯
        for (int i_First = 0; i_First < mGuZhangShowCode.size(); i_First++) {
            String key = mGuZhangShowCode.get(i_First);
            TableBean bean = new TableBean();
            bean.Bean1 = mGuZhangShowText.get(mGuZhangShow.get(key));
            for (int i = 0; i < hashData.length; i++) {
                HashMap<String, String> hashMap = hashData[i];
                int devSubCode = Integer.parseInt(hashMap.get("devSubCode"));
                if (devSubCode == mLiuHao) {
                    String dataCode = hashMap.get("dataCode").toLowerCase();
                    double dataValue = Double.parseDouble(hashMap.get("dataValue"));
                    if (key.equals(dataCode)) {
                        bean.Bean2 = dataValue > 0.5 ? bean.Bean1 : "-";
                        sTabDatas.add(bean);
                        break;
                    }
                }
            }
        }
        //endregion

        //region 表格显示
        LinearLayout ly = (LinearLayout) mActivity.findViewById(R.id.fragment_detaildata_lyrealdata);

        TableData<TableBean> tableData = new TableData<TableBean>("实时数据", sTabDatas, sTabColumns);
        sTabResult.getConfig().setColumnTitleStyle(new FontStyle(50, Color.parseColor("#4f94cd")));
        sTabResult.getConfig().setContentStyle(new FontStyle(45, Color.WHITE));
        sTabResult.getConfig().setContentGridStyle(new LineStyle(mActivity, 1, Color.parseColor("#FF2C2C2C")));
        sTabResult.getConfig().setColumnTitleGridStyle(new LineStyle(mActivity, 1, Color.parseColor("#FF2C2C2C")));
        sTabResult.getConfig().setShowYSequence(false);
        sTabResult.getConfig().setShowXSequence(false);
        sTabResult.getConfig().setShowTableTitle(false);
        sTabResult.setTableData(tableData);

        sTabResult.getConfig().setMinTableWidth(ly.getWidth());
        //endregion
    }
    //endregion

    //region 统计数据
    private void loadTongJiData() {
        String beginDate = DateTimeUtil.DateTimeToString(System.currentTimeMillis() - 7 * 24 * 3600 * 1000, "yyyy-MM-dd");
        String endDate = DateTimeUtil.DateTimeToString(System.currentTimeMillis() + 24 * 3600 * 1000, "yyyy-MM-dd");
        ReqData reqData =ReqData.createReqData(ReqData.ReqType.T_SQLExec_TTLSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "TJ_SheBei_List_SP");
        reqData.ExtParams.put("sheBeiSNo", mSBSNo);
        reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("beginTime", beginDate);
        reqData.ExtParams.put("endTime", endDate);
        reqData.ExtParams.put("liuhao", mLiuHao + "");
        try {
            DialogUitl.showProgressDialog(mActivity, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(mActivity, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    mTongJiData = resData.DataTable;
                                    loadTongJiView(resData.DataTable);
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, mActivity);
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), mActivity);
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
            MethodUtil.showToast(e.getMessage(), mActivity);
        }
    }

    private void loadTongJiView(HashMap<String, String>[] hashData) {
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<lecho.lib.hellocharts.model.Column> columns = new ArrayList<lecho.lib.hellocharts.model.Column>();
        List<Line> listLine = new ArrayList<>();
        List<SubcolumnValue> values;//单个柱状高度及颜色适配
        int numColumns = hashData.length;
        for (int i = 0; i < numColumns; i++) {
            ChartPoint chartPoint = new ChartPoint();
            chartPoint.XValue = hashData[i].get("TJDay");
            switch (mCPSNo) {
                case "WCPXX0000000003":
                case "WCPXX0000000004":
                    //板坯非正弦
                    chartPoint.YValue = Float.parseFloat(hashData[i].get("SUM_RunTime"));
                    break;
                case "WCPXX0000000001":
                case "WCPXX0000000002":
                case "WCPXX0000000012":
                case "WCPXX0000000013":
                case "WCPXX0000000016":
                case "WCPXX0000000017":
                case "WCPXX0000000018":
                    //塞棒 液面计 测铝
                    chartPoint.YValue = Float.parseFloat(hashData[i].get("r3mm"));
                    break;
                case "WCPXX0000000006":
                    //机械手喷号
                    chartPoint.YValue = Float.parseFloat(hashData[i].get("penHaolv"));
                    break;
                case "WCPXX0000000010":
                    //振动测渣
                    chartPoint.YValue = Float.parseFloat(hashData[i].get("rzhunbao"));
                    break;
                case "WCPXX0000000007": //转炉测渣
                case "WCPXX0000000011": //电磁测渣
                    chartPoint.YValue = Float.parseFloat(hashData[i].get("rzhunbao"));
                    break;
            }
            values = new ArrayList<SubcolumnValue>();
            //X轴添加自定义坐标
            axisValues.add(new AxisValue(i).setLabel(chartPoint.XValue));
            values.add(new SubcolumnValue(chartPoint.YValue, Color.parseColor("#83bff6")));
            ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter(2);
            columns.add(new lecho.lib.hellocharts.model.Column(values).setHasLabelsOnlyForSelected(false).setHasLabels(true).setFormatter(chartValueFormatter));
        }
        ColumnChartData columnData = new ColumnChartData(columns);//表格的数据实例
        columnData.setFillRatio(0.5f);
        columnData.setAxisXBottom(new Axis().setHasLines(false).setTextColor(Color.WHITE).setValues(axisValues).setTextSize(8));
        columnData.setAxisYLeft(new Axis().setHasLines(true).setTextColor(Color.WHITE));
        columnData.setValueLabelBackgroundEnabled(true);

        chartTJSJ.setColumnChartData(columnData);
        chartTJSJ.setZoomEnabled(true);//是否支持缩放
        chartTJSJ.setZoomType(ZoomType.HORIZONTAL);//缩放方向
    }
    //endregion

    //region 历史数据
    private void loadHistoryData() {
        String startTime = DateTimeUtil.DateTimeToString(System.currentTimeMillis() - 30 * 60 * 1000, "yyyy-MM-dd HH:mm:ss");
        String dataCode = "";
        for (String key : mQuXianTu.keySet()) {
            dataCode += key + "|";
        }
        ReqData reqData =ReqData.createReqData(ReqData.ReqType.T_CmdExec_DevData_GetHisData,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("devCode", mSBSNo);
        reqData.ExtParams.put("devSubCode", mLiuHao + "");
        reqData.ExtParams.put("dataCode", dataCode);
        reqData.ExtParams.put("minutes", "30");
        reqData.ExtParams.put("startTime", startTime);
        try {
            DialogUitl.showProgressDialog(mActivity, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(mActivity, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadHistoryView(resData.DataTable);
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, mActivity);
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), mActivity);
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
            MethodUtil.showToast(e.getMessage(), mActivity);
        }
    }

    //region 历史数据加载
    private void loadHistoryView(HashMap<String, String>[] hashData) {
        lyLenged.removeAllViews();
        HashMap<String, Integer> hashKey = new HashMap<>();
        listLine = new ArrayList<>();
        listView = new ArrayList<>();
        listLineVisible = new ArrayList<>();
        axisValues = new ArrayList<>();
        int index = 0;
        HashMap<String, List<PointValue>> listPoint = new HashMap<>();

        for (int i = 0; i < mQuXianTuCode.size(); i++) {
            String key = mQuXianTuCode.get(i);
            Line line = new Line();
            line.setHasLabels(false);
            line.setHasPoints(false);
            line.setColor(ChartResource.getColor(index));
            line.setHasLabels(true);
            listLine.add(line);
            String title = mQuXianTuText.get(mQuXianTu.get(key));
            TextView txtView = new TextView(mActivity);
            txtView.setText(title);
            txtView.setTextColor(mActivity.getResources().getColor(R.color.colorTitle));
            txtView.setBackgroundColor(ChartResource.getColor(index));
            txtView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            txtView.setOnClickListener(new LengedOnClickListener());
            txtView.setTag(index);
            lyLenged.addView(txtView);
            listView.add(txtView);
            hashKey.put(key, index);
            listPoint.put(key, new ArrayList<PointValue>());
            index++;
            listLineVisible.add(true);
        }

        List<HisData> listHisData = new ArrayList<>();
        for (int i = 0; i < hashData.length; i++) {
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
        for (int i = 0; i < listHisData.size(); i++) {
            String dataTime = listHisData.get(i).DataTimeStr.trim().substring(0, 19);
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
            if (hashXTime.containsKey(dataTime)) {
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

    //endregion

    private void showHisChart() {
        List<Line> list = new ArrayList<>();
        for (int i = 0; i < listLine.size(); i++) {
            boolean isVisible = listLineVisible.get(i);
            if (isVisible) {
                list.add(listLine.get(i));
            }
        }
        LineChartData lineData = new LineChartData(list);
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(false).setTextColor(Color.WHITE).setAutoGenerated(false).setTextSize(8).setHasTiltedLabels(true));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setTextColor(Color.WHITE));
        lineData.setValueLabelBackgroundEnabled(true);
        chartLSSJ.setLineChartData(lineData);
        chartLSSJ.setZoomEnabled(true);//是否支持缩放
        chartLSSJ.setZoomType(ZoomType.HORIZONTAL);//缩放方向
        rlayHisData.setVisibility(View.VISIBLE);
    }

    private class LengedOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                TextView lblLenged = (TextView) v;
                int index = Integer.parseInt(v.getTag().toString());
                boolean isVisible = listLineVisible.get(index);
                listLineVisible.set(index, !isVisible);
                lblLenged.setBackgroundColor(isVisible ? mActivity.getResources().getColor(R.color.colorBackground) : ChartResource.getColor(index));
                showHisChart();
            } catch (Exception e) {
                e.printStackTrace();
                MethodUtil.showToast(e.getMessage(), mActivity);
            }
        }
    }
    //endregion

    //刷新UI

    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            try {
                GetNewData();
                handler.postDelayed(runnable,3000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private Handler handler=new Handler(){};

    //
    private void GetNewData() {
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_CmdExec_DevData_GetDevData,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("devCode", mSBSNo);
        try {
            VolleyRequestUtil.RequestPost(getActivity(), APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(getActivity(), reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadRealView(resData.DataTable);
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, getActivity());
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), getActivity());
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
            MethodUtil.showToast(e.getMessage(), getActivity());
        }
    }


    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

}
