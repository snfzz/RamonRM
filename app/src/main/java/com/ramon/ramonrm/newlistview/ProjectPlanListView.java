package com.ramon.ramonrm.newlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class ProjectPlanListView extends ListView {
    public ProjectPlanListView(Context context) {
        super(context);
    }

    public  ProjectPlanListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public  ProjectPlanListView(Context context, AttributeSet attrs,
                            int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
