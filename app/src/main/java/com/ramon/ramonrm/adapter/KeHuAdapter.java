package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.model.KeHuInfo;

import java.util.List;

public class KeHuAdapter extends ArrayAdapter {
    public static final int CALLTYPE_PHONE = 0;
    public static final int CALLTYPE_VIDEO = 1;

    private Context mContext;
    private int mResource;

    public KeHuAdapter(Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        mContext = getContext();
        mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        KeHuInfo khInfo = (KeHuInfo) getItem(position);
        ViewHolder myView;
        if (convertView == null) {
            myView = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            myView.lblMingCheng = (TextView)convertView.findViewById(R.id.listitem_kehuinfo_lblmingcheng);
            convertView.setTag(myView);
        }
        else{
            myView = (ViewHolder)convertView.getTag();
        }
        myView.lblMingCheng.setText(khInfo.MingCheng);
        return convertView;
    }

    class ViewHolder{
        private TextView lblMingCheng;
    }
}
