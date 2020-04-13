package com.ramon.ramonrm.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.ScrollView;

public class RMListView extends ListView {
    private int nowY = 0;

    public RMListView(Context context) {
        super(context);
    }

    public RMListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RMListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private ScrollView mHorizontalScrollViewEx2;

    public void setScrollView(ScrollView view) {
        mHorizontalScrollViewEx2 = view;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        if (mHorizontalScrollViewEx2 != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    nowY = y;
                    mHorizontalScrollViewEx2.requestDisallowInterceptTouchEvent(true);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (this.getFirstVisiblePosition() == 0
                            && y > nowY) {
                        mHorizontalScrollViewEx2.requestDisallowInterceptTouchEvent(false);
                        break;
                    } else if (this.getLastVisiblePosition() == this.getCount() - 1
                            && y < nowY) {
                        mHorizontalScrollViewEx2.requestDisallowInterceptTouchEvent(false);
                        break;
                    }
                    mHorizontalScrollViewEx2.requestDisallowInterceptTouchEvent(true);
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    break;
                }
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(event);
    }
}