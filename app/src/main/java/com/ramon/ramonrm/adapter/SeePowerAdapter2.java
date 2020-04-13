package com.ramon.ramonrm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.listclass.AllListClass;

import java.util.List;

public class SeePowerAdapter2 extends BaseAdapter {
    private final View.OnClickListener listener;
    private final List<AllListClass> dataList;

    public SeePowerAdapter2(View.OnClickListener listener, List<AllListClass> dataList) {
        this.listener = listener;
        this.dataList = dataList;
    }
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
        ViewHolder holder=new ViewHolder();
        AllListClass seePowerClass2=(AllListClass)getItem(i);
        view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_seepower2,viewGroup,false);
        holder.type=(TextView)view.findViewById(R.id.listview_seepower2_type);
        holder.name=(TextView)view.findViewById(R.id.listview_seepower2_name);
        holder.power=(TextView)view.findViewById(R.id.listview_seepower2_power);
        holder.photo=(ImageView)view.findViewById(R.id.listview_seepower2_photo);
        holder.type.setText(seePowerClass2.getTxt1());
        holder.name.setText(seePowerClass2.getTxt2());
        holder.power.setText(seePowerClass2.getTxt3());
        holder.photo.setImageResource(seePowerClass2.getInt1());
        holder.photo.setOnClickListener(listener);
        holder.photo.setTag(i);
        return view;
    }

    class ViewHolder {
        TextView type;
        TextView name;
        TextView power;
        ImageView photo;
    }
}
