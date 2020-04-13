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

public class WoringProductAdapter extends ArrayAdapter {
    public WoringProductAdapter(Context context, int resource, List<AllListClass> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AllListClass allListClass = (AllListClass) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.listview_woringproduct, null);

        TextView txt1 = (TextView) view.findViewById(R.id.listview_woringproduct_txt1);
        TextView txt2 = (TextView) view.findViewById(R.id.listview_woringproduct_txt2);
        TextView txt3 = (TextView) view.findViewById(R.id.listview_woringproduct_txt3);
        TextView txt4 = (TextView) view.findViewById(R.id.listview_woringproduct_txt4);
        TextView txt5 = (TextView) view.findViewById(R.id.listview_woringproduct_txt5);
        TextView txt6 = (TextView) view.findViewById(R.id.listview_woringproduct_txt6);
        TextView txt7 = (TextView) view.findViewById(R.id.listview_woringproduct_txt7);
        TextView txt8 = (TextView) view.findViewById(R.id.listview_woringproduct_txt8);
        TextView txt9 = (TextView) view.findViewById(R.id.listview_woringproduct_txt9);


        txt1.setText(allListClass.getTxt1());
        txt2.setText(allListClass.getTxt2());
        txt3.setText(allListClass.getTxt3());
        txt4.setText(allListClass.getTxt4());
        txt5.setText(allListClass.getTxt5());
        txt6.setText(allListClass.getTxt6());
        txt7.setText(allListClass.getTxt7());
        txt8.setText(allListClass.getTxt8());
        txt9.setText(allListClass.getTxt9());

        return view;
    }
}
