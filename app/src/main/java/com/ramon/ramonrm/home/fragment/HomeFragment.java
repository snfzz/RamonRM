package com.ramon.ramonrm.home.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.VolleyError;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.MapSession;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.alarm.AnLiKuActivity;
import com.ramon.ramonrm.charts.ChartPoint;
import com.ramon.ramonrm.fahuo.FaHuoActivity;
import com.ramon.ramonrm.home.GuideViewAdapter;
import com.ramon.ramonrm.project.fragment.InstallationTaskActivity;
import com.ramon.ramonrm.renyuan.KaoQinDKActivity;
import com.ramon.ramonrm.renyuan.WorkMsgActivity;
import com.ramon.ramonrm.project.AnZhuangProjectActivity;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.video.JiShuZCActivity;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;
import com.ramon.ramonrm.weihuproject.WeiHuProjectActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class HomeFragment extends Fragment implements FlagmentDataRefreshInterface {
    public HomeFragment(BaseActivity mParent) {
        super();
        this.mParent = mParent;
    }

    private BaseActivity mParent;
    private Activity mActivity;

    private ScrollView sView;

    //region 滚动视图
    private Handler imgHandler;
    private Runnable runnable;
    private ViewPager homeViewPager;
    private int currentIndex = 0;
    private int[] imageView = {R.mipmap.dot0, R.mipmap.dot1, R.mipmap.dot2};
    private ImageView[] tips;
    //endregion

    //region 功能菜单
    private GridView gvFunc;
    private List<Map<String, Object>> glFuncs;
    private SimpleAdapter saFunc;
    private int[] imgFuncs = {R.mipmap.azrw, R.mipmap.whrw, R.mipmap.alk, R.mipmap.gzrz, R.mipmap.fhgl, R.mipmap.jszc, R.mipmap.mrdk, R.mipmap.more};
    private String[] strFuncs = {"azrw", "whrw", "alk", "gzrz", "fhgl", "jszc", "mrdk", "more"};
    private String[] txtFuncs = {"安装任务", "维护任务", "案例库", "工作日志", "发货管理", "技术支持", "每日打卡", "更多"};
    //endregion

    //region 设备总览 -- 仪表盘显示
    private WebView webView;
    private PieChartView pieChart;
    //endregion

    //region 产品总览 -- 柱状图
    ComboLineColumnChartView chartChanPin;
    //endregion

    //region 故障信息
    private ArrayList<Fragment> fgAlarms;
    private ArrayList<TextView> lblAlarms;
    private int selectedIndex = -1;
    //endregion

    //region 人员跟踪
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    //endregion

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //不同的Activity对应不同的布局
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        initScrollView();
        initViewPager();
        initFuncView();
        initGaugeView();
        initChanPinView();
        initAlarmView();
        initMap();
        refreshData();
    }

    @Override
    public void refreshData() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initScrollView() {
        sView = (ScrollView) mActivity.findViewById(R.id.fragment_homepage_scrollview);
    }

    //region 图片滚动视图
    private void initViewPager() {
        homeViewPager = mActivity.findViewById(R.id.fragment_homepage_viewpager);//滑动的图片
        //region 首页界面图片切换控件初始化
        ViewGroup group = (ViewGroup) mActivity.findViewById(R.id.fragment_homepage_viewgroup);
        homeViewPager = (ViewPager) mActivity.findViewById(R.id.fragment_homepage_viewpager);
        final ArrayList<View> list = new ArrayList<View>();
        tips = new ImageView[imageView.length];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(mActivity);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.dot_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.dot_normal);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewPager.LayoutParams.WRAP_CONTENT,
                    ViewPager.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            group.addView(imageView, layoutParams);
        }
        // 将imageview添加到view
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < imageView.length; i++) {
            ImageView iv = new ImageView(mActivity);
            iv.setLayoutParams(params);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageResource(imageView[i]);
            list.add(iv);
        }
        homeViewPager.setAdapter(new GuideViewAdapter(list));

        homeViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setImageBackground(position % imageView.length);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        imgHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                homeViewPager.setCurrentItem(currentIndex);
                currentIndex = (currentIndex + 1) % imageView.length;
                //要做的事情
                imgHandler.postDelayed(this, 5000);
            }
        };

        imgHandler.postDelayed(runnable, 5000);

        //endregion n
    }

    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.drawable.dot_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.dot_normal);
            }
        }
    }
    //endregion

    //region 功能菜单
    private void initFuncView() {
        gvFunc = (GridView) mActivity.findViewById(R.id.fragment_homepage_gvfunc);
        loadFunc();
    }

    private void loadFunc() {
        glFuncs = new ArrayList<>();
        for (int i = 0; i < imgFuncs.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", imgFuncs[i]);
            map.put("text", txtFuncs[i]);
            map.put("code", strFuncs[i]);
            glFuncs.add(map);
        }
        String[] form = {"image", "text"};
        int[] image = {R.id.func_menuitem_img, R.id.func_menuitem_txt};
        saFunc = new SimpleAdapter(mActivity, glFuncs, R.layout.func_menuitem, form, image);
        gvFunc.setAdapter(saFunc);
        gvFunc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> funcMap = (Map<String, Object>) parent.getAdapter().getItem(position);
                String funcCode = funcMap.get("code").toString().toLowerCase();
                switch (funcCode) {
                    case "azrw": {
                        Intent intent = new Intent(mActivity, InstallationTaskActivity.class);
                        mActivity.startActivity(intent);
                        break;
                    }
                    case "whrw": {
                        Intent intent=new Intent(mActivity, WeiHuProjectActivity.class);
                        mActivity.startActivity(intent);
                        break;
                    }
                    case "alk": {
                        Intent intent = new Intent(mActivity, AnLiKuActivity.class);
                        mActivity.startActivity(intent);
                        break;
                    }
                    case "bjgl":
                        break;
                    case "gzrz": {
                        Intent intent = new Intent(mActivity, WorkMsgActivity.class);
                        mActivity.startActivity(intent);
                        break;
                    }
                    case "fhgl": {
                        Intent intent = new Intent(mActivity, FaHuoActivity.class);
                        mActivity.startActivity(intent);
                        break;
                    }
                    case "qxgl":
                        break;
                    case "jszc": {
                        Intent intent = new Intent(mActivity, JiShuZCActivity.class);
                        mActivity.startActivity(intent);
                        break;
                    }
                    case "mrdk": {
                        Intent intent = new Intent(mActivity, KaoQinDKActivity.class);
                        mActivity.startActivity(intent);
                        break;
                    }
                    case "more":
                        //生成并发送广播
                        Intent intent = new Intent("jerry");
                        intent.putExtra("change", "yes");
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                        break;
                }
            }
        });
    }
    //endregion

    //region 设备总览 -- 仪表盘显示
    private void initGaugeView() {
//        webView = (WebView) mActivity.findViewById(R.id.fragment_homepage_webgauge);
//
//        // 获取指定数据格式的数据,此处可以和外部交互
//        List<AccessData> datas = AccessData.getWeekData();
//
//        WebSettings webSettings = webView.getSettings();
//
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setSupportZoom(false);
//        webSettings.setAllowFileAccess(true);
//        webSettings.setDisplayZoomControls(false);;
//
//        webView.loadUrl("file:///android_asset/myechart.html");
        pieChart = (PieChartView) mActivity.findViewById(R.id.fragment_homepage_piechart);
        pieChart.setVisibility(View.INVISIBLE);
        loadPieData();
    }

    private void loadPieData() {
        try {
            ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
            DialogUitl.showProgressDialog(mActivity, reqData.CmdID, "正在加载数据……");
            reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
            reqData.ExtParams.put("spName", "SB_ShuJuK_TongJi_WX");
            reqData.ExtParams.put("tongjiType", "0");
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(mActivity, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadPieChart(resData.DataTable);
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

    private void loadPieChart(HashMap<String, String>[] hashData) {
        if (hashData == null || hashData.length == 0) {
            return;
        }
        int total = Integer.parseInt(hashData[0].get("Cnt"));
        int normal = Integer.parseInt(hashData[0].get("PingCnt"));
        SliceValue sValue1 = new SliceValue(normal, Color.parseColor("#548B54")).setLabel("通讯正常:" + normal);
        SliceValue sValue2 = new SliceValue(total - normal, Color.parseColor("#83bff6")).setLabel("通讯异常:" + (total - normal));
        List<SliceValue> sValues = new ArrayList<>();
        sValues.add(sValue1);
        sValues.add(sValue2);
        PieChartData pieChartData = new PieChartData(sValues);
        pieChartData.setHasLabels(true);
        pieChartData.setHasLabelsOutside(true);//设置饼图外面是否显示值
        pieChartData.setHasCenterCircle(true);//设置饼图中间是否有第二个圈
        pieChartData.setCenterCircleColor(Color.parseColor("#2C2C2C"));//设置饼图中间圈的颜色
        pieChartData.setValueLabelBackgroundEnabled(true);
        pieChartData.setCenterText1("总数：" + total);
        pieChartData.setCenterText1Color(Color.WHITE);
        pieChartData.setCenterText1FontSize(12);
        pieChart.setPieChartData(pieChartData);
        pieChart.setViewportCalculationEnabled(true);//设置饼图自动适应大小
        pieChart.startDataAnimation();
        pieChart.setChartRotation(90, true);//设置饼图旋转角度，且是否为动画
        pieChart.setChartRotationEnabled(true);//设置饼图是否可以手动旋转
        pieChart.setZoomEnabled(true);//是否支持缩放
        pieChart.setZoomType(ZoomType.HORIZONTAL);//缩放方向
        pieChart.setVisibility(View.VISIBLE);
    }
    //endregion

    //region 产品总览 -- 柱状图
    private void initChanPinView() {
        chartChanPin = (ComboLineColumnChartView) mActivity.findViewById(R.id.fragment_homepage_chartcpzl);
        loadChanPinData();
    }

    private void loadChanPinData() {
        try {
            ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
            DialogUitl.showProgressDialog(mActivity, reqData.CmdID, "正在加载数据……");
            reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
            reqData.ExtParams.put("spName", "SB_ShuJuK_TongJi_WX");
            reqData.ExtParams.put("tongjiType", "1");
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(mActivity, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadChanPinChart(resData.DataTable);
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

    private void loadChanPinChart(HashMap<String, String>[] hashData) {
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<Column> columns = new ArrayList<Column>();
        List<Line> listLine = new ArrayList<>();
        List<SubcolumnValue> values;//单个柱状高度及颜色适配
        int numColumns = hashData.length;
        numColumns = numColumns > 5 ? 5 : numColumns;
        List<PointValue> listPoint = new ArrayList<>();
        for (int i = 0; i < numColumns; i++) {
            ChartPoint chartPoint = new ChartPoint();
            chartPoint.XValue = hashData[i].get("CPJianCheng");
            chartPoint.YValue = Integer.parseInt(hashData[i].get("SBNum"));
            values = new ArrayList<SubcolumnValue>();
            //X轴添加自定义坐标
            axisValues.add(new AxisValue(i).setLabel(chartPoint.XValue));
            values.add(new SubcolumnValue(chartPoint.YValue, Color.parseColor("#83bff6")));
            ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter(0);
            columns.add(new Column(values).setHasLabelsOnlyForSelected(false).setHasLabels(true).setFormatter(chartValueFormatter));
            listPoint.add(new PointValue(i, Integer.parseInt(hashData[i].get("PingCnt"))).setLabel(hashData[i].get("PingCnt")));
        }
        Line line = new Line(listPoint);
        line.setColor(Color.parseColor("#188df0"));
        line.setHasLabels(true);
        listLine.add(line);
        ColumnChartData columnData = new ColumnChartData(columns);//表格的数据实例
        columnData.setFillRatio(0.5f);
//        columnData.setAxisXBottom(new Axis(axisValues).setHasLines(false).setTextColor(Color.WHITE).setValues(axisValues).setTextSize(8));
//        columnData.setAxisYLeft(new Axis().setHasLines(true).setTextColor(Color.WHITE));
//        columnData.setValueLabelBackgroundEnabled(true);
        LineChartData lineData = new LineChartData(listLine);
//        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(false).setTextColor(Color.WHITE).setValues(axisValues).setTextSize(8));
//        lineData.setAxisYLeft(new Axis().setHasLines(true).setTextColor(Color.WHITE));
//        lineData.setValueLabelBackgroundEnabled(true);
        ComboLineColumnChartData chartData = new ComboLineColumnChartData(columnData, lineData);
        chartData.setAxisXBottom(new Axis(axisValues).setHasLines(false).setTextColor(Color.WHITE).setValues(axisValues).setTextSize(8));
        chartData.setAxisYLeft(new Axis().setHasLines(true).setTextColor(Color.WHITE));
        chartData.setValueLabelBackgroundEnabled(true);
        chartChanPin.setComboLineColumnChartData(chartData);
        chartChanPin.setZoomEnabled(true);//是否支持缩放
        chartChanPin.setZoomType(ZoomType.HORIZONTAL);//缩放方向
    }
    //endregion

    //region 故障信息
    private void initAlarmView() {
        fgAlarms = new ArrayList<>();
        fgAlarms.add(new ChanPinFragment());
        fgAlarms.add(new TodayFragment());
        fgAlarms.add(new WeekFragment());
        lblAlarms = new ArrayList<>();
        lblAlarms.add((TextView) mActivity.findViewById(R.id.fragment_homepage_lblcpbj));
        lblAlarms.add((TextView) mActivity.findViewById(R.id.fragment_homgpage_lbljrbj));
        lblAlarms.add((TextView) mActivity.findViewById(R.id.fragment_homepage_lblbzbj));
        for (int i = 0; i < lblAlarms.size(); i++) {
            lblAlarms.get(i).setOnClickListener(new TextViewClick());
        }
        switchFragment(0);
    }

    private class TextViewClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int index = Integer.parseInt(v.getTag().toString());
            switchFragment(index);
        }
    }

    private void switchFragment(int index) {
        if (index == selectedIndex) return;
        FragmentTransaction transaction = this.mParent.getSupportFragmentManager().beginTransaction();
        if (selectedIndex >= 0) {
            TextView lblOld = lblAlarms.get(selectedIndex);
            transaction.hide(fgAlarms.get(selectedIndex));
            lblOld.setTextColor(ContextCompat.getColor(mActivity, R.color.colorTitle));
        }
        lblAlarms.get(index).setTextColor(ContextCompat.getColor(mActivity, R.color.colorMain));
        if (fgAlarms.get(index).isAdded() == false) {
            transaction.add(R.id.fragment_homepage_ryalarmmain, fgAlarms.get(index));
        }
        transaction.show(fgAlarms.get(index)).commitAllowingStateLoss();

        selectedIndex = index;
    }
    //endregion

    //region 人员跟踪
    private void initMap() {
        mMapView = (MapView) mActivity.findViewById(R.id.fragment_homepage_homemap);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(5.0f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        MapSession.setTempMapView(mMapView);
        MapSession.setTempBaiduMap(mBaiduMap);
        loadMapData();
    }

    private void loadMapData() {
        try {
            ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_SysSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
            DialogUitl.showProgressDialog(mActivity, reqData.CmdID, "正在加载数据……");
            reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
            reqData.ExtParams.put("spName", "RS_YuanGong_ZhuangTaiLB_SP");
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(mActivity, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadMapView(resData.DataTable);
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

    private void loadMapView(HashMap<String, String>[] hashData) {
        mBaiduMap.clear();
        List<OverlayOptions> listOverLay = new ArrayList<>();
        for (int i = 0; i < hashData.length; i++) {
            HashMap hashMap = hashData[i];
            double lng = Double.parseDouble(hashMap.get("Lon").toString());
            double lat = Double.parseDouble(hashMap.get("Lat").toString());
            String title = hashMap.get("DeptSNames").toString() + " " + hashMap.get("Name").toString();
            //定义Maker坐标点
            LatLng point = new LatLng(lat, lng);
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(getMarkerView(title));
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);
            listOverLay.add(option);
        }
        mBaiduMap.addOverlays(listOverLay);
    }
    private View getMarkerView(String text) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.listitem_baidumarker, null);
        Button button = view.findViewById(R.id.listitem_baidudmarker_text);
        ImageView imageView = view.findViewById(R.id.listitem_baidudmarker_image);
        button.setText(text);
        return view;
    }
    //endregion


    //地图在生命周期中
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        MapSession.setTempBaiduMap(null);
        MapSession.setTempMapView(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
}