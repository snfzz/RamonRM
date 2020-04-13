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

public class SeePowerAdapter extends ArrayAdapter {
    public SeePowerAdapter(Context context, int resource, List<AllListClass> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AllListClass linkeMain = (AllListClass) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.listview_seepower, null);

        TextView userauditadapterName = (TextView)view.findViewById(R.id.listview_seepower_name);
        TextView userauditadapterType = (TextView) view.findViewById(R.id.listview_seepower_type);

        userauditadapterName.setText(linkeMain.getTxt2());
        userauditadapterType.setText(linkeMain.getTxt1());


        return view;
    }
}