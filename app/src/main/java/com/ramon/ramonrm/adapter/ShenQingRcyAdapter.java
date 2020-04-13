package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ramon.ramonrm.R;

import java.util.ArrayList;

public class ShenQingRcyAdapter extends RecyclerView.Adapter<ShenQingRcyAdapter.ViewHolder> {
    private ArrayList<String> mData;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public interface OnRecyclerViewClickListener {
        void onItemClickListener(View view);
    }

    private OnRecyclerViewClickListener listener;
    public void setItemClickListener(OnRecyclerViewClickListener itemClickListener) {
        listener = itemClickListener;
    }


    public ShenQingRcyAdapter(Context context,ArrayList<String> data) {
        this.mData = data;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void updateData(ArrayList<String> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_shengqingxiangxi, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);

        //接口回调
        if(listener != null){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(v);
                }
            });

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 绑定数据
        Glide.with(mContext).load(mData.get(position)).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.listview_shengqingxiangxi_photo);
        }
    }
}
