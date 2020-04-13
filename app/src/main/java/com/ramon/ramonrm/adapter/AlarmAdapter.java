package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.model.AlarmInfo;

import org.w3c.dom.Text;

import java.util.List;

public class AlarmAdapter extends ArrayAdapter {

    private Object mItem = null;
    private Context mContext;
    private int mResource;

    private TextView lblKeHuMC,lblGuZhangMC,lblGuZhangMS,lblGuZhangYY,lblGuZhangDJ,lblLiuHao,lblChuLiCS,lblKaiShiSJ,lblChiXuSJ;

    public AlarmAdapter(Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        mContext = getContext();
        mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AlarmInfo alarmInfo = (AlarmInfo) getItem(position);
        mItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            lblKeHuMC = (TextView) convertView.findViewById(R.id.listitem_alarminfo_lblkehumc);
            lblGuZhangMC = (TextView) convertView.findViewById(R.id.listitem_alarminfo_lblguzhangmc);
            lblGuZhangMS = (TextView) convertView.findViewById(R.id.listitem_alarminfo_lblguzhangms);
            lblGuZhangYY = (TextView) convertView.findViewById(R.id.listitem_alarminfo_lblguzhangyy);
            lblGuZhangDJ = (TextView) convertView.findViewById(R.id.listitem_alarminfo_lblguzhangdj);
            lblLiuHao = (TextView) convertView.findViewById(R.id.listitem_alarminfo_lblliuhao);
            lblChuLiCS = (TextView) convertView.findViewById(R.id.listitem_alarminfo_lblchixusj);
            lblKaiShiSJ = (TextView) convertView.findViewById(R.id.listitem_alarminfo_lblkaishisj);
            lblChiXuSJ = (TextView) convertView.findViewById(R.id.listitem_alarminfo_lblchixusj);
        }
        lblKeHuMC.setText(alarmInfo.KeHuMC);
        lblGuZhangMC.setText(alarmInfo.GuZhangMC);
        lblGuZhangMS.setText(alarmInfo.MiaoShuGZ);
        lblGuZhangYY.setText(alarmInfo.YuanYinGZ);
        lblGuZhangDJ.setText(alarmInfo.DengJiName);
        lblLiuHao.setText(Session.StrandTitles[alarmInfo.LiuHao - 1]);
        lblChuLiCS.setText(alarmInfo.ChuLiGZ);
        lblKaiShiSJ.setText(alarmInfo.BeginTime);
        if (alarmInfo.ChiXuSJ.equals("--")){
            String strInfo = "--";
//            String strInfo = alarmInfo.ChiXuSJ;
//            int index = strInfo.indexOf('.');
//            if(index>=0) {
//                strInfo = strInfo.substring(0, index);
//            }
            lblChiXuSJ.setText(strInfo);
        }else {
            String strInfo = alarmInfo.ChiXuSJ;
            int index = strInfo.indexOf('.');
            if(index>=0) {
                strInfo = strInfo.substring(0, index);
            }
            lblChiXuSJ.setText(strInfo);
        }

        return convertView;
    }
}
