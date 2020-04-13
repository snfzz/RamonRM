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

public class BeiJianGuanLiAdapter1 extends ArrayAdapter {

    private int currentItem=-1;

    public BeiJianGuanLiAdapter1(Context context, int resource, List<AllListClass> objects) {
        super(context, resource, objects);
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AllListClass allListClass = (AllListClass) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.listview_changzhugangchang, null);

        TextView name = (TextView)view.findViewById(R.id.listview_changzhugangchang_name);
        TextView type = (TextView) view.findViewById(R.id.listview_changzhugangchang_type);

        name.setText(allListClass.getTxt2());
        type.setText(allListClass.getTxt1());

        if (currentItem == position) {
            //如果被点击，设置当前TextView被选中

            name.setSelected(true);
            type.setSelected(true);
        } else {
            //如果没有被点击，设置当前TextView未被选中

            name.setSelected(false);
            type.setSelected(false);
        }


        return view;
    }

}
