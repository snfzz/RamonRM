package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.listclass.AllListClass;

import java.util.List;

public class XinXiTianXieGridAdapter extends BaseAdapter {
    private final View.OnClickListener listener;
    private final List<AllListClass> dataList;
    Context context;
    public XinXiTianXieGridAdapter(View.OnClickListener listener, List<AllListClass> dataList,Context context) {
        this.listener = listener;
        this.dataList = dataList;
        this.context=context;
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
        view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gridview_xinxitianxie,viewGroup,false);
        holder.photo=(ImageView)view.findViewById(R.id.gridview_xinxitianxie_img);
        holder.delete=(ImageView)view.findViewById(R.id.gridview_xinxitianxie_delete);
        Glide.with(context).load(photoClass.getUri()).override(250,250).into(holder.photo);
        holder.photo.setOnClickListener(listener);
        holder.photo.setTag(i);
        holder.delete.setOnClickListener(listener);
        holder.delete.setTag(i);
        return view;
    }

    class ViewHolder {
        ImageView photo;
        ImageView delete;
    }
}
