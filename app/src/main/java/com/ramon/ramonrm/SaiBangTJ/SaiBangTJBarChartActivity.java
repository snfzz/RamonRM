package com.ramon.ramonrm.SaiBangTJ;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.SaiBangTJ.LiuFragment.SBTJChartLiuFragment;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class SaiBangTJBarChartActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton imgbtn;
    //Tablayout
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<Fragment> listfragment;//用于存放fragment
    private List<String>listname;//用于存放tab的名称
    private MyAdapter adapter;//fragment适配器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saibangtjbarchart);
        init();
        AddViewPager();
    }

    private void init(){
        imgbtn=findViewById(R.id.activity_saibangtjbarchart_imgback);
        imgbtn.setOnClickListener(this);
        tabLayout=findViewById(R.id.activity_saibangtjbarchart_tabs);
        viewPager=findViewById(R.id.activity_saibangtjbarchart_viewpage);
    }

    @Override
    public void onClick(View view) {
        int vId=view.getId();
        if (vId == R.id.activity_saibangtjbarchart_imgback){
            AppManagerUtil.instance().finishActivity(SaiBangTJBarChartActivity.this);
        }
    }

    private void AddViewPager(){
        listfragment=new ArrayList<>();
        listname=new ArrayList<>();
        for (int i=0;i<3;i++){
            listfragment.add(new SBTJChartLiuFragment(i+1));
            listname.add("第"+(i+1)+"流");
        }
        adapter=new MyAdapter(getSupportFragmentManager());
        adapter.getData(listfragment,listname);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    //创建Fragment的适配器
    public class MyAdapter extends FragmentPagerAdapter {

        public List<Fragment> mFragmentsz;
        public List<String> mTitlesz;

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        public void getData(List<Fragment>list,List<String>name){
            mFragmentsz=list;
            mTitlesz=name;
        }

        //获得每个页面的下标
        @Override
        public Fragment getItem(int position) {
            return mFragmentsz.get(position);
        }
        //获得List的大小
        @Override
        public int getCount() {
            return mFragmentsz.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitlesz.get(position);
        }
    }
}
