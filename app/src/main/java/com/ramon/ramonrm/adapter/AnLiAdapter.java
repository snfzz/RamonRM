package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.model.AnLiInfo;

import java.util.List;

public class AnLiAdapter extends ArrayAdapter {
    private Context mContext;
    private int mResource;

    public AnLiAdapter(Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        mContext = getContext();
        mResource = resource;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        AnLiInfo alInfo = (AnLiInfo)getItem(position);
        ViewHolder myView = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            myView = new ViewHolder();
            myView.lblChanPinMC = convertView.findViewById(R.id.listitem_anliinfo_lblchanpinmc);
            myView.lblChuLiCS = convertView.findViewById(R.id.listitem_anliinfo_lblchulics);
            myView.lblDianPingCS = convertView.findViewById(R.id.listitem_anliinfo_lbldianpingcs);
            myView.lblWenTiMS = convertView.findViewById(R.id.listitem_anliinfo_lblwentims);
            convertView.setTag(myView);
        }
        else{
            myView = (ViewHolder)convertView.getTag();
        }
        myView.lblWenTiMS.setText(alInfo.WenTiMS);
        myView.lblDianPingCS.setText("评论次数："+alInfo.DianPingCS);
        myView.lblChuLiCS.setText(alInfo.ChuLiCS);
        myView.lblChanPinMC.setText(alInfo.ChanPinMC);
        return convertView;
    }

    class ViewHolder {
        public TextView lblChanPinMC,lblWenTiMS,lblChuLiCS,lblDianPingCS;
    }
}
