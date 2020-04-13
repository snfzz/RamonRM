package com.ramon.ramonrm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.listclass.AllListClass;

import java.util.List;

public class RenWuAuditAdapter extends BaseAdapter {
    private final View.OnClickListener listener;
    private final List<AllListClass> dataList;

    public RenWuAuditAdapter(View.OnClickListener listener, List<AllListClass> dataList) {
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
        ViewHolder holder=new ViewHolder();
        AllListClass allListClass=(AllListClass)getItem(i);
        view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_renwuaudit,viewGroup,false);
        holder.SQR=(TextView)view.findViewById(R.id.listview_rentuaudit_name);
        holder.SQSJ=(TextView)view.findViewById(R.id.listview_rentuaudit_time);
        holder.SQJD=(TextView)view.findViewById(R.id.listview_rentuaudit_jiedian);
        holder.HTH=(TextView)view.findViewById(R.id.listview_rentuaudit_hth);
        holder.KHMC=(TextView)view.findViewById(R.id.listview_rentuaudit_khname);
        holder.SH=(Button)view.findViewById(R.id.listview_rentuaudit_audit);
        holder.SQR.setText(allListClass.getTxt1());
        holder.SQSJ.setText(allListClass.getTxt2());
        holder.SQJD.setText(allListClass.getTxt3());
        holder.HTH.setText(allListClass.getTxt4());
        holder.KHMC.setText(allListClass.getTxt5());
        holder.SH.setOnClickListener(listener);
        holder.SH.setTag(i);
        return view;
    }

    class ViewHolder {
        TextView SQR;
        TextView SQSJ;
        TextView SQJD;
        TextView HTH;
        TextView KHMC;
        Button SH;
    }
}
