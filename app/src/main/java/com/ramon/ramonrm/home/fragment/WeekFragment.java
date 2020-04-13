package com.ramon.ramonrm.home.fragment;

import android.app.Activity;
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


import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class WeekFragment extends Fragment implements FlagmentDataRefreshInterface {
    private boolean isLoad = false;
    private HashMap<String,String>[]hashMap;
    private Activity mActivity;
    private LineChartView chartChanPin;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //不同的Activity对应不同的布局
        View view = inflater.inflate(R.layout.fragment_weekpage, container, false);
        if(isLoad){
            loadChart(hashMap);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        chartChanPin = (LineChartView) mActivity.findViewById(R.id.fragment_weekpage_chartcpbj);
        if (!isLoad) {
            isLoad = true;
            chartChanPin.setVisibility(View.INVISIBLE);
            refreshData();
        }
    }

    @Override
    public void refreshData() {
        try {
            ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
            reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
            reqData.ExtParams.put("spName", "BJ_BaoJing_TongJi_SP");
            reqData.ExtParams.put("tongjiType", "1");
            DialogUitl.showProgressDialog(mActivity, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(mActivity, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
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
        }
    }

    private void loadChart(HashMap<String, String>[] hashData) {
        if(hashData==null)return;
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        int numColumns = hashData.length;
        List<Line> lines = new ArrayList<>();
        List<PointValue> listPoint = new ArrayList<>();
        for (int i = 0; i < numColumns; i++) {
            int alarmNum = Integer.parseInt(hashData[i].get("alarmNum"));
            //X轴添加自定义坐标
            axisValues.add(new AxisValue(i).setLabel(hashData[i].get("dayTime").substring(5)));
            listPoint.add(new PointValue(i, Integer.parseInt(hashData[i].get("alarmNum"))).setLabel(hashData[i].get("alarmNum")));
        }

        Line line = new Line(listPoint);
        line.setColor(Color.parseColor("#188df0"));
        line.setHasLabels(true);
        lines.add(line);
        LineChartData lineData = new LineChartData(lines);//表格的数据实例
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(false).setTextColor(Color.WHITE).setValues(axisValues).setTextSize(8));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setTextColor(Color.WHITE));
        lineData.setValueLabelBackgroundEnabled(true);
        chartChanPin.setLineChartData(lineData);
        chartChanPin.setZoomEnabled(true);//是否支持缩放
        chartChanPin.setZoomType(ZoomType.HORIZONTAL);//缩放方向
        chartChanPin.setVisibility(View.VISIBLE);
    }
}
