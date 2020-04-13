package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.model.FaHuoInfo;
import com.ramon.ramonrm.model.UserInfo;

import java.util.List;

public class FaHuoAdapter extends ArrayAdapter {
    private Context mContext;
    private int mResource;

    public FaHuoAdapter(Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        mContext = getContext();
        mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        FaHuoInfo fhInfo = (FaHuoInfo) getItem(position);
        ViewHolder myView;
        if (convertView == null) {
            myView = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            myView.lblBaoZhuangFS = (TextView) convertView.findViewById(R.id.listitem_fahuo_lblbaozhuangfs);
            myView.lblBZ = (TextView) convertView.findViewById(R.id.listitem_fahuo_lblbz);
            myView.lblFaHuoFS = (TextView) convertView.findViewById(R.id.listitem_fahuo_lblfahuofs);
            myView.lblKuaiDiDH = (TextView) convertView.findViewById(R.id.listitem_fahuo_lblkuaididh);
            myView.lblShouHuoDW = (TextView) convertView.findViewById(R.id.listitem_fahuo_lblshouhuodw);
            myView.lblShouHuoDZ = (TextView) convertView.findViewById(R.id.listitem_fahuo_lblshouhuodz);
            myView.lblSHRInfo = (TextView) convertView.findViewById(R.id.listitem_fahuo_lblshrinfo);
            convertView.setTag(myView);
        } else {
            myView = (ViewHolder) convertView.getTag();
        }
        myView.lblBaoZhuangFS.setText(fhInfo.fBZFS + "  " + fhInfo.fZL + "Kg");
        myView.lblBZ.setText(fhInfo.fBZ);
        myView.lblFaHuoFS.setText(fhInfo.fFHFS);
        myView.lblKuaiDiDH.setText(fhInfo.fFHDH);
        myView.lblShouHuoDW.setText(fhInfo.fFSHDWMC);
        myView.lblShouHuoDZ.setText(fhInfo.fSHDZ);
        myView.lblSHRInfo.setText(fhInfo.fSHR + " - " + fhInfo.fSHRDH);
        return convertView;
    }
    class ViewHolder{
        private TextView lblSHRInfo,lblShouHuoDW,lblShouHuoDZ,lblFaHuoFS,lblKuaiDiDH,lblBaoZhuangFS,lblBZ;
    }
}