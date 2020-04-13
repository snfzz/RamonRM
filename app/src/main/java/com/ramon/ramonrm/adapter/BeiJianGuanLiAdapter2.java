package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.listclass.AllListClass;

import java.util.List;

public class BeiJianGuanLiAdapter2  extends ArrayAdapter {
    //当前Item被点击的位置
    private int currentItem=-1;

    public BeiJianGuanLiAdapter2(Context context, int resource, List<AllListClass> objects) {
        super(context, resource, objects);
    }
    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AllListClass allListClass = (AllListClass) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.listview_beijianqingdan, null);

        TextView userauditadapterName = (TextView)view.findViewById(R.id.listview_beijianqingdang_name);
        TextView fnum = (TextView) view.findViewById(R.id.listview_beijianqingdang_fnum);
        TextView lnum = (TextView) view.findViewById(R.id.listview_beijianqingdang_lnum);

        userauditadapterName.setText(allListClass.getTxt1());
        fnum.setText(allListClass.getTxt2());
        lnum.setText(allListClass.getTxt3());

        if (currentItem == position) {
            //如果被点击，设置当前TextView被选中
            userauditadapterName.setSelected(true);
            fnum.setSelected(true);
            lnum.setSelected(true);
        } else {
            //如果没有被点击，设置当前TextView未被选中
            userauditadapterName.setSelected(false);
            fnum.setSelected(false);
            lnum.setSelected(false);
        }

        return view;
    }
}
