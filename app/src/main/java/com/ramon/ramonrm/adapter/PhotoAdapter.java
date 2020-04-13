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

public class PhotoAdapter extends BaseAdapter {

    private final View.OnClickListener listener;
    private final List<AllListClass> dataList;

    public PhotoAdapter(View.OnClickListener listener, List<AllListClass> dataList) {
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
        AllListClass photoClass= (AllListClass) getItem(i);
        view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_photo,viewGroup,false);
        holder.name=(TextView)view.findViewById(R.id.listview_photo_name);
        holder.type=(TextView)view.findViewById(R.id.listview_photo_type);
        holder.photo=(ImageView)view.findViewById(R.id.listview_photo_photo);
        holder.name.setText(photoClass.getTxt2());
        holder.photo.setImageResource(photoClass.getInt1());
        holder.type.setText(photoClass.getTxt1());
        holder.photo.setOnClickListener(listener);
        holder.photo.setTag(i);
        return view;
    }

    class ViewHolder {
        TextView  name;
        TextView type;
        ImageView photo;
    }

}
