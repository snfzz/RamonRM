package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.model.ShowText;
import com.ramon.ramonrm.util.ImageUitl;

import java.util.List;

public class GridViewImageAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private int mResource;
    private List<ShowText>mImages;

    private class GridHolder {
        ImageView imgView;
    }

    public GridViewImageAdapter(Context c, int resource, List<ShowText>images){
        mContext = c;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
        mImages = images;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridHolder holder;
        ShowText sText = mImages.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(mResource, null);
            holder = new GridHolder();
            holder.imgView = convertView.findViewById(R.id.griditem_imageview_imgview);
            convertView.setTag(holder);
        } else {
            holder = (GridHolder) convertView.getTag();
        }
//        Bitmap img = ImageUitl.getImage(mContext, sText.Title, sText.Key, R.mipmap.noimage);
//        holder.imgView.setImageBitmap(img);
        Glide.with(mContext).load(sText.Key).override(250,250).into(holder.imgView);
        return convertView;
    }
}
