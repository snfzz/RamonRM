package com.ramon.ramonrm.charts;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.axis.AxisLine;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.SplitLine;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.code.Y;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.style.LineStyle;

import java.util.List;

public class GaugeChart {

    Context mContext;
    List<AccessData> lineDatas;

    public GaugeChart(Context context, List<AccessData> datas) {
        this.mContext = context;
        // 获取数据
        this.lineDatas = datas;
    }

    // 将该方法暴露给JavaScript脚本调用
    @JavascriptInterface
    public String getLineChartOptions() {
        GsonOption option = (GsonOption) creatLineChartOptions();
//            Log.d(option.toString());
        return option.toString();
    }

    // 此函数主要是绘 Line 图
    @JavascriptInterface
    public Option creatLineChartOptions() {

        // 创建Option对象
        GsonOption option = new GsonOption();
        // 设置图标标题，并且居中显示

        option.legend().data("访问量").x(X.center).y(Y.bottom).borderWidth(1);
        // 设置y轴为值轴，并且不显示y轴，最大值设置400，最小值-100
        option.yAxis(new ValueAxis()
                .name("IP")
                .axisLine(new AxisLine()
                        .show(true)
                        .lineStyle(new LineStyle().width(0)))
                .max(400)
                .min(-100));
        // 创建类目轴，并且不显示竖着的分割线
        CategoryAxis categoryAxis = new CategoryAxis()
                .splitLine(new SplitLine().show(false))
                .axisLine(new AxisLine().onZero(false));
        // 不显示表格边框，就是围着图标的方框
        option.grid().borderWidth(0);

        // 创建Line数据
        Line line = new Line("访问量").smooth(true);
        // 根据获取的数据赋值
        for (AccessData lineData : lineDatas) {
            // 增加类目，值为日期
            categoryAxis.data(lineData.getDate());
            // 日期对应的数据
            line.data(lineData.getNums());
        }
        // 设置X轴为类目轴
        option.xAxis(categoryAxis);
        // 设置数据
        option.series(line);
        return option;
    }
}
