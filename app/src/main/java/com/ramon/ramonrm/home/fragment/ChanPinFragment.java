package com.ramon.ramonrm.home.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.charts.ChartPoint;
import com.ramon.ramonrm.util.DateTimeUtil;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

public class ChanPinFragment extends Fragment implements FlagmentDataRefreshInterface {
    private HashMap<String,String>[]hashMap;
    private boolean isLoad=false;
    private Activity mActivity;
    private ColumnChartView chartChanPin;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //不同的Activity对应不同的布局
        View view = inflater.inflate(R.layout.fragment_chanpinpage, container, false);
        if(isLoad){
            loadChart(hashMap);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        chartChanPin = (ColumnChartView) mActivity.findViewById(R.id.fragment_chanpinpage_chartcpbj);
        if (!isLoad) {
            isLoad = true;
            chartChanPin.setVisibility(View.INVISIBLE);
            refreshData();
        }
        chartChanPin.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {
                Intent intent=new Intent(getActivity(), WoringProductActivity.class);
                startActivity(intent);
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }
    @Override
    //生产警报
    public void refreshData() {
        try {
            long currTime = System.currentTimeMillis();
            long beginTime = currTime;
            long endTime = currTime + 24 * 60 * 60 * 1000;
            ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
            reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
            reqData.ExtParams.put("spName", "BJ_BaoJing_TongJi_Home_SP");
            reqData.ExtParams.put("tjType", "0");
            reqData.ExtParams.put("beginTime", DateTimeUtil.DateTimeToString(beginTime,"yyyy/MM/dd"));
            reqData.ExtParams.put("endTime", DateTimeUtil.DateTimeToString(endTime,"yyyy/MM/dd"));
            DialogUitl.showProgressDialog(mActivity,  reqData.CmdID,"正在加载数据……");
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req",  reqData,
                    new VolleyListenerInterface(mActivity, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData,String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    hashMap = resData.DataTable;

                                    loadChart(resData.DataTable);
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, mActivity);
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), mActivity);
                            } finally {
                                DialogUitl.dismissProgressDialog( reqData.CmdID);
                            }
                        }

                        // Volley请求失败时调用的函数
                        @Override
                        public void onMyError(ReqData reqData,VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog( reqData.CmdID);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadChart(HashMap<String, String>[] hashData) {
        if(hashData==null)return;
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;//单个柱状高度及颜色适配
        int numColumns = hashData.length;
        numColumns = numColumns > 5 ? 5 : numColumns;
        for (int i = 0; i < numColumns; i++) {
            ChartPoint chartPoint = new ChartPoint();
            chartPoint.XValue = hashData[i].get("JianCheng");
            chartPoint.YValue = Integer.parseInt(hashData[i].get("AlarmDevNum"));
            values = new ArrayList<SubcolumnValue>();
            //X轴添加自定义坐标
            axisValues.add(new AxisValue(i).setLabel(chartPoint.XValue));
            values.add(new SubcolumnValue(chartPoint.YValue, Color.parseColor("#83bff6")));
            ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter(0);
            columns.add(new Column(values).setHasLabelsOnlyForSelected(false).setHasLabels(true).setFormatter(chartValueFormatter));
        }
        ColumnChartData columnData = new ColumnChartData(columns);//表格的数据实例
        columnData.setFillRatio(0.5f);
        columnData.setAxisXBottom(new Axis(axisValues).setHasLines(false).setTextColor(Color.WHITE).setValues(axisValues).setTextSize(8));
        columnData.setAxisYLeft(new Axis().setHasLines(true).setTextColor(Color.WHITE));
        columnData.setValueLabelBackgroundEnabled(true);
        chartChanPin.setColumnChartData(columnData);
        chartChanPin.setZoomEnabled(true);//是否支持缩放
        chartChanPin.setZoomType(ZoomType.HORIZONTAL);//缩放方向
        chartChanPin.setVisibility(View.VISIBLE);
    }
}
