package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.model.YongHuSQ;

import java.util.List;

public class YongHuSQAdapter extends ArrayAdapter {

    private Context mContext;
    private int mResource;

    public YongHuSQAdapter(Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        mContext = getContext();
        mResource = resource;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        YongHuSQ uInfo = (YongHuSQ) getItem(position);
        ViewHolder myView;
        if (convertView == null) {
            myView = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            myView.lblDeptName = (TextView) convertView.findViewById(R.id.listitem_yonghusq_lbldeptname);
            myView.lblXingMing = (TextView) convertView.findViewById(R.id.listitem_yonghusq_lblxingming);
            myView.lblBZ = (TextView) convertView.findViewById(R.id.listitem_yonghusq_lblbz);
            convertView.setTag(myView);
        } else {
            myView = (ViewHolder) convertView.getTag();
        }
        myView.YongHuSQ = uInfo;
        myView.lblDeptName.setText(uInfo.BMMingCheng);
        myView.lblXingMing.setText(uInfo.XingMing);
        myView.lblBZ.setText(uInfo.ShenQingDMSNoMC);
        return convertView;
    }

    public class ViewHolder {
        private TextView lblDeptName, lblXingMing, lblBZ;
        public YongHuSQ YongHuSQ;
    }
}
