package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.model.DeviceStatus;
import com.ramon.ramonrm.util.MethodUtil;

import java.util.List;

import lecho.lib.hellocharts.model.Line;

public class DeviceStatusAdapter  extends ArrayAdapter {
    int colorNormal;
    int colorError;

    Context mContext;
    int mResource;
    OnItemClickListener mItemClickListener;
    OnItemClickListener mImageClickListener;

    public DeviceStatusAdapter(Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        mContext = getContext();
        mResource = resource;
        colorNormal = mContext.getResources().getColor(R.color.colorStatusNormal);
        colorError = mContext.getResources().getColor(R.color.colorStatusError);
    }

    public void setOnImageClickListener(OnItemClickListener listener) {
        this.mImageClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceStatus sbzt = (DeviceStatus) getItem(position);
        ViewHolder myView;
        if (convertView == null) {
            myView = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            myView.lyMain = (LinearLayout) convertView.findViewById(R.id.listitem_devstatus_lymain);
            myView.lblTitle = (TextView) convertView.findViewById(R.id.listitem_devstatus_lbltitle);
            myView.lblTitle.setOnClickListener(new MyOnClickLisener(sbzt));
            myView.lblTime = (TextView) convertView.findViewById(R.id.listitem_devstatus_lbldatatime);
            myView.lblTime.setOnClickListener(new MyOnClickLisener(sbzt));
            myView.lblNet = (TextView) convertView.findViewById(R.id.listitem_devstatus_lblnetstatus);
            myView.lblNet.setOnClickListener(new MyOnClickLisener(sbzt));
            myView.lblButton = (ImageView) convertView.findViewById(R.id.listitem_devstatus_lblbutton);
            myView.lblButton.setOnClickListener(new MyOnClickLisener(sbzt));
            myView.lblAlarm = (ImageView) convertView.findViewById(R.id.listitem_devstatus_imgalarm);
            myView.lblAlarm.setOnClickListener(new MyOnClickLisener(sbzt));
            convertView.setTag(myView);
        }
        else{
            myView = (ViewHolder)convertView.getTag();
        }
        myView.lblTitle.setText(sbzt.DevName);
        myView.lblTime.setText(sbzt.LastDataTime);
        myView.lblTime.setTextColor(sbzt.DevStatus == 1 ? colorNormal : colorError);
        myView.lblNet.setText(sbzt.NetStatusName);
        myView.lblNet.setTextColor(sbzt.NetStatus == 1 ? colorNormal : colorError);
        if (sbzt.AllowAdd)
            myView.lblButton.setBackgroundResource(R.mipmap.add);
        else
            myView.lblButton.setVisibility(View.GONE);
        myView.lblAlarm.setBackgroundResource(sbzt.CurrAlarmNum == 0 ? R.mipmap.green : R.mipmap.red);
        return convertView;
    }

    private class MyOnClickLisener implements View.OnClickListener {
        private Object clickItem=null;
        public MyOnClickLisener(Object item){
            clickItem= item;
        }
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.listitem_devstatus_lblbutton) {
                if (mImageClickListener != null) {
                    mImageClickListener.onItemClick(clickItem, view);
                }
            }
            if (view.getId() == R.id.listitem_devstatus_imgalarm || view.getId() == R.id.listitem_devstatus_lbldatatime || view.getId() == R.id.listitem_devstatus_lblnetstatus || view.getId() == R.id.listitem_devstatus_lbltitle) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(clickItem, view);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Object item, View view);
    }

    class ViewHolder{
        TextView lblTitle, lblTime, lblNet;
        ImageView lblButton, lblAlarm;
        LinearLayout lyMain;
        Object mItem;
    }
}
