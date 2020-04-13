package com.ramon.ramonrm.home.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.adapter.RMFragmentAdapter;
import com.ramon.ramonrm.device.CollectDevFragment;
import com.ramon.ramonrm.device.SearchDevFragment;

import lecho.lib.hellocharts.model.Viewport;

public class DevFragment extends Fragment {

    private Activity mActivity;
    private TabLayout tlTitle;
    private ViewPager vpFragment;
    private Fragment[]arrFragments;
    private String[] arrTitles={"收藏设备","查询设备"};
    private RMFragmentAdapter rmAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //不同的Activity对应不同的布局
        View view = inflater.inflate(R.layout.fragment_devicepage, container, false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        tlTitle = (TabLayout) mActivity.findViewById(R.id.fragment_devicepage_tablay);
        vpFragment = (ViewPager) mActivity.findViewById(R.id.fragment_devicepage_viewpager);
        arrFragments = new Fragment[2];
        arrFragments[0] = new CollectDevFragment();
        arrFragments[1] = new SearchDevFragment();
        rmAdapter = new RMFragmentAdapter(getFragmentManager());
        rmAdapter.setDatas(arrFragments, arrTitles);
        vpFragment.setAdapter(rmAdapter);
        tlTitle.setupWithViewPager(vpFragment);
    }
}
