package com.ramon.ramonrm.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class RMScrollView extends ScrollView {
    private int mLastX = 0, mLastY = 0;

    public RMScrollView(Context context) {
        this(context, null);
    }

    public RMScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RMScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
            mLastX = x;
            mLastY = y;
            return super.onInterceptTouchEvent(event);
        } else {
            return true;
        }
    }
}
