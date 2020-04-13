package com.ramon.ramonrm.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AbsListView;
import android.widget.TextView;

import com.ramon.ramonrm.R;

public class RMRefreshListView extends ListView implements AbsListView.OnScrollListener {

    private View viewBottom; //尾文件
    private View viewHeader; //头文件
    private int totaItemCounts;//用于表示是下拉还是上拉
    private int lassVisible; //上拉
    private int firstVisible; //下拉
    private OnLoadListener loadListener; //接口回调
    private int bottomHeight;//尾文件高度
    private int headerHeight; //头文件高度
    private int Yload;//位置
    boolean isLoading;//加载状态
    private TextView lblHeaderTitle;//头文件textview显示加载文字

    public RMRefreshListView(Context context) {
        super(context);
    }

    public RMRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public RMRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    private void Init(Context context) {
        //拿到头布局文件xml
        viewHeader = LinearLayout.inflate(context, R.layout.listview_header, null);
        lblHeaderTitle = (TextView) viewHeader.findViewById(R.id.listview_header_lbltitle);

        //拿到尾布局文件
        viewBottom = LinearLayout.inflate(context, R.layout.listview_bottom, null);
        //测量尾文件高度
        viewBottom.measure(0, 0);
        //拿到高度
        bottomHeight = viewBottom.getMeasuredHeight();
        //隐藏view
        viewBottom.setPadding(0, -bottomHeight, 0, 0);
        viewHeader.measure(0, 0);
        headerHeight = viewHeader.getMeasuredHeight();
        viewHeader.setPadding(0, -headerHeight, 0, 0);
        //添加listview底部
        this.addFooterView(viewBottom);
        //添加到listview头部
        this.addHeaderView(viewHeader);
        //设置拉动监听
        this.setOnScrollListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Yload = (int) ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getY();
                int paddingY = headerHeight + (moveY - Yload) / 2;
                if (paddingY < 0) {
                    lblHeaderTitle.setText("下拉刷新........");
                }
                if (paddingY > 0) {
                    lblHeaderTitle.setText("松开刷新........");
                }
                viewHeader.setPadding(0, paddingY, 0, 0);
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (totaItemCounts == lassVisible && scrollState == SCROLL_STATE_IDLE) {
            if (!isLoading) {
                isLoading = true;
                viewBottom.setPadding(0, 0, 0, 0);
                //加载数据
                if (loadListener != null)
                    loadListener.onLoad();
            }
        }

        if (firstVisible == 0 && scrollState == SCROLL_STATE_IDLE) {
            viewHeader.setPadding(0, 0, 0, 0);
            lblHeaderTitle.setText("正在刷新.......");

            if (loadListener != null)
                loadListener.onPullLoad();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        this.firstVisible = firstVisibleItem;
        this.lassVisible = firstVisibleItem + visibleItemCount;
        this.totaItemCounts = totalItemCount;
    }

    //加载完成
    public void loadComplete() {
        isLoading = false;
        viewBottom.setPadding(0, -bottomHeight, 0, 0);
        viewHeader.setPadding(0, -headerHeight, 0, 0);
    }

    public void setOnLoadListener(OnLoadListener loadListener) {
        this.loadListener = loadListener;
    }

    public interface OnLoadListener {
        void onLoad();

        void onPullLoad();
    }
}
