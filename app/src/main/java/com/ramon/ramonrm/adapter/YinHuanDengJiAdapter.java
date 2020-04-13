package com.ramon.ramonrm.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.listclass.AllListClass;

import java.util.List;

public class YinHuanDengJiAdapter extends BaseAdapter {
    //当前Item被点击的位置
    private int currentItem=-1;

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }


    private final View.OnClickListener listener;
    private final List<AllListClass> dataList;
    public YinHuanDengJiAdapter(View.OnClickListener listener, List<AllListClass> dataList) {
        this.listener = listener;
        this.dataList = dataList;
    }
    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolderDelete viewHolderDelete=new ViewHolderDelete();
        AllListClass allListClass=(AllListClass)getItem(i);
        view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_yinhuandengji,viewGroup,false);
        viewHolderDelete.txt1=(TextView)view.findViewById(R.id.listview_yinhuandengji_txt1);
        viewHolderDelete.txt2=(TextView)view.findViewById(R.id.listview_yinhuandengji_txt2);
        viewHolderDelete.txt3=(TextView)view.findViewById(R.id.listview_yinhuandengji_txt3);
        viewHolderDelete.txt4=(TextView)view.findViewById(R.id.listview_yinhuandengji_txt4);
        viewHolderDelete.txt5=(TextView)view.findViewById(R.id.listview_yinhuandengji_txt5);
        viewHolderDelete.txt6=(TextView)view.findViewById(R.id.listview_yinhuandengji_txt6);
        viewHolderDelete.txt7=(TextView)view.findViewById(R.id.listview_yinhuandengji_txt7);
        viewHolderDelete.txt8=(TextView)view.findViewById(R.id.listview_yinhuandengji_txt8);
        viewHolderDelete.l1=(LinearLayout)view.findViewById(R.id.listview_yinhuandengji_l1);

        viewHolderDelete.txt1.setText(allListClass.getTxt1());
        if (viewHolderDelete.txt1.getText().toString().equals("较大隐患")){
            viewHolderDelete.txt1.setTextColor(Color.rgb(255,240,0));
        }
        if (viewHolderDelete.txt1.getText().toString().equals("严重隐患")){
            viewHolderDelete.txt1.setTextColor(Color.rgb(255,165,0));
        }
        if (viewHolderDelete.txt1.getText().toString().equals("重大隐患")){
            viewHolderDelete.txt1.setTextColor(Color.rgb(255,0,0));
        }
        viewHolderDelete.txt2.setText(allListClass.getTxt2());
        viewHolderDelete.txt3.setText(allListClass.getTxt3());
        viewHolderDelete.txt4.setText(allListClass.getTxt4());
        viewHolderDelete.txt5.setText(allListClass.getTxt5());
        viewHolderDelete.txt6.setText(allListClass.getTxt6());
        viewHolderDelete.txt7.setText(allListClass.getTxt7());
        viewHolderDelete.txt8.setText(allListClass.getTxt8());


        //查看详细
        viewHolderDelete.txt9=(TextView)view.findViewById(R.id.listview_yinhuandengji_txt9);
        viewHolderDelete.txt9.setTag(i);
        viewHolderDelete.txt9.setOnClickListener(listener);
        if (currentItem == i) {
            view.setBackgroundResource(R.color.colorBackground);
        }
        return view;
    }

    class ViewHolderDelete {
        TextView txt1;
        TextView txt2;
        TextView txt3;
        TextView txt4;
        TextView txt5;
        TextView txt6;
        TextView txt7;
        TextView txt8;
        TextView txt9;
        LinearLayout l1;
    }

}
