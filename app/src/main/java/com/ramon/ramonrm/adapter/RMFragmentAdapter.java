package com.ramon.ramonrm.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class RMFragmentAdapter extends FragmentPagerAdapter {

    public Fragment[] mFragments;
    public String[] mTitles;

    public RMFragmentAdapter(FragmentManager fManager) {
        super(fManager);
    }

    public void setDatas(Fragment[] fragments, String[] titles) {
        mFragments = fragments;
        mTitles = titles;
    }

    //获得每个页面的下标
    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    //获得List的大小
    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
