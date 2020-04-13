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
import com.ramon.ramonrm.model.ProjTaskInfo;

import java.util.List;

public class ProjTaskAdapter  extends ArrayAdapter {

    private Context mContext;
    private int mResource;
    private View.OnClickListener listener;
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.onItemClickListener = itemClickListener;
    }

    public ProjTaskAdapter(Context context, int resource, @NonNull List objects,View.OnClickListener listener) {
        super(context, resource, objects);
        mContext = getContext();
        mResource = resource;
        this.listener = listener;
    }



    public View getView(int position, View convertView, ViewGroup parent) {
        ProjTaskInfo projTask = (ProjTaskInfo) getItem(position);
        ViewHolder myView;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            myView = new ViewHolder();
            myView.lblYuanGongMC = convertView.findViewById(R.id.listitem_projtask_lblygxm);
            myView.lblRenWuLB = convertView.findViewById(R.id.listitem_projtask_lblrwlx);
            myView.lblZhuFuZR = convertView.findViewById(R.id.listitem_projtask_lblzfzr);
            myView.lblRenWuZT = convertView.findViewById(R.id.listitem_projtask_lblrwzt);
            myView.lblYaoQiuDD = convertView.findViewById(R.id.listitem_projtask_lblyqdd);
            myView.lblYaoQiuWC = convertView.findViewById(R.id.listitem_projtask_lblyqwc);
            myView.btnDelete = convertView.findViewById(R.id.listitem_projtask_btndelete);
            myView.btnDelete.setOnClickListener(listener);
            myView.btnDelete.setTag(position);
            convertView.setTag(myView);
        } else {
            myView = (ViewHolder) convertView.getTag();
        }
        myView.lblYuanGongMC.setText(projTask.XingMing);
        myView.lblRenWuLB.setText(projTask.TaskTypeTitle);
        myView.lblZhuFuZR.setText(projTask.MainEmp.toLowerCase() == "true"?"是":"否");
        myView.lblRenWuZT.setText(projTask.StatusTitle);
        myView.lblYaoQiuDD.setText(projTask.ReqArriveDT);
        myView.lblYaoQiuWC.setText(projTask.ReqFinishDT);
        return convertView;
    }

    private class MyOnClickListener implements View.OnClickListener {
        private Object mItem;

        public  MyOnClickListener(Object item){
            mItem = item;
        }
        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(mItem, v);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Object item, View view);
    }
    private class ViewHolder {
        private TextView lblYuanGongMC,lblRenWuLB,lblZhuFuZR,lblRenWuZT,lblYaoQiuDD,lblYaoQiuWC;
        private ImageView btnDelete;
    }
}
