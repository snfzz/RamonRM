package com.ramon.ramonrm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.listclass.AllListClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickPowerAuditAdapter extends BaseAdapter {
    private final View.OnClickListener listener;
    private final List<AllListClass> dataList;
    private Map<Integer,Boolean> map=new HashMap<>();

    public ClickPowerAuditAdapter(View.OnClickListener listener, List<AllListClass> dataList) {
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder=new ViewHolder();
        final AllListClass test=(AllListClass)getItem(i);
        view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_clickpoweraudit,viewGroup,false);
        holder.type=(TextView)view.findViewById(R.id.listview_clickpoweraudit_department);
        holder.name=(TextView)view.findViewById(R.id.listview_clickpoweraudit_name);
        holder.rad=(CheckBox)view.findViewById(R.id.listview_clickpoweraudit_type);
        holder.type.setText(test.getTxt1());
        holder.name.setText(test.getTxt2());
        if (test.getBolean1()==true){
            holder.rad.setChecked(true);
        }else {
            holder.rad.setChecked(false);
        }
        holder.rad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.rad.isChecked()){
                    map.put(i,true);
                }else {
                    map.remove(i);
                }
                if(map!=null&&map.containsKey(i)){
                    holder.rad.setChecked(true);
                }else {
                    holder.rad.setChecked(false);
                }
            }
        });
        holder.rad.setOnClickListener(listener);
        holder.rad.setTag(i);
        return view;
    }

    class ViewHolder {
        TextView name;
        TextView type;
        CheckBox rad;
    }
}
