package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.listclass.AllListClass;
import com.ramon.ramonrm.listclass.UserAuditListViewclass;

import java.util.List;

public class UserAuditListViewAdapter  extends ArrayAdapter {
    //当前Item被点击的位置
    private int currentItem=-1;

    public UserAuditListViewAdapter(Context context, int resource, List<AllListClass> objects) {
        super(context, resource, objects);
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AllListClass linkeMain = (AllListClass) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.listview_useraudit, null);

        TextView userauditadapterName = (TextView)view.findViewById(R.id.listview_useraudit_name);
        TextView userauditadapterDepartment = (TextView)view.findViewById(R.id.listview_useraudit_department);
        TextView userauditadapterType = (TextView) view.findViewById(R.id.listview_useraudit_type);

        userauditadapterName.setText(linkeMain.getTxt1());
        userauditadapterDepartment.setText(linkeMain.getTxt2());
        userauditadapterType.setText(linkeMain.getTxt3());

        if (currentItem == position) {
            //如果被点击，设置当前TextView被选中
            userauditadapterName.setSelected(true);
            userauditadapterDepartment.setSelected(true);
            userauditadapterType.setSelected(true);
        } else {
            //如果没有被点击，设置当前TextView未被选中
            userauditadapterName.setSelected(false);
            userauditadapterDepartment.setSelected(false);
            userauditadapterType.setSelected(false);
        }

        return view;
    }

}
