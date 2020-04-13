package com.ramon.ramonrm.home;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/12/17.
 */

public class GuideViewAdapter extends PagerAdapter {

    private List<View> list;

    public GuideViewAdapter(List<View> list) {
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView(list.get(position % list.size()));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        try {
            container.addView(list.get(position % list.size()), 0);
        } catch (Exception ex) {

        }
        return list.get(position % list.size());
    }
}
