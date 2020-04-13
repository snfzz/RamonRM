package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.listclass.AllListClass;

import java.util.List;

public class WoringProductAdapter2 extends ArrayAdapter {
    public WoringProductAdapter2(Context context, int resource, List<AllListClass> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AllListClass linkeMain = (AllListClass) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.listview_woringproduct2, null);

        TextView userauditadapterName = (TextView)view.findViewById(R.id.listview_woringproduct2_type);
        TextView userauditadapterNum = (TextView) view.findViewById(R.id.listview_woringproduct2_name);

        userauditadapterName.setText(linkeMain.getTxt1());
        userauditadapterNum.setText(linkeMain.getTxt2());
        return view;
    }
}
