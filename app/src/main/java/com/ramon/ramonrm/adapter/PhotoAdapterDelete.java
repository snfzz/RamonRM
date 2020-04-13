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

public class PhotoAdapterDelete extends BaseAdapter {

    private final View.OnClickListener listener;
    private final List<AllListClass> dataList;

    public PhotoAdapterDelete(View.OnClickListener listener, List<AllListClass> dataList) {
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
        ViewHolderDelete viewHolderDelete=new ViewHolderDelete();
        AllListClass photoClass= (AllListClass) getItem(i);
        view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_delete,viewGroup,false);
        viewHolderDelete.name=(TextView)view.findViewById(R.id.listview_delete_name);
        viewHolderDelete.type=(TextView)view.findViewById(R.id.listview_delete_type);
        viewHolderDelete.photo=(ImageView)view.findViewById(R.id.listview_delete_photo);
        viewHolderDelete.name.setText(photoClass.getTxt2());
        viewHolderDelete.photo.setImageResource(photoClass.getInt1());
        viewHolderDelete.type.setText(photoClass.getTxt1());
        viewHolderDelete.photo.setOnClickListener(listener);
        viewHolderDelete.photo.setTag(i);
        return view;
    }

    class ViewHolderDelete {
        TextView name;
        TextView type;
        ImageView photo;
    }
}
