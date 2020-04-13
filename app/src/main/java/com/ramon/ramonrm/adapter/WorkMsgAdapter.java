package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.model.WorkMsgInfo;

import java.util.List;

public class WorkMsgAdapter extends ArrayAdapter {

    private Context mContext;
    private int mResource;

    public WorkMsgAdapter(Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        mContext = getContext();
        mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        WorkMsgInfo msgInfo = (WorkMsgInfo) getItem(position);
        ViewHolder myView = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            myView = new ViewHolder();
            myView.lblContent = (TextView) convertView.findViewById(R.id.listitem_workmsg_lblcontent);
            myView.lblInputDT = (TextView) convertView.findViewById(R.id.listitem_workmsg_lblinputdt);
            myView.lblLoc = (TextView) convertView.findViewById(R.id.listitem_workmsg_lblloc);
            myView.lblTask = (TextView) convertView.findViewById(R.id.listitem_workmsg_lbltask);
            myView.lblEmpInfo = (TextView) convertView.findViewById(R.id.listitem_workmsg_lblempinfo);
            convertView.setTag(myView);
        } else {
            myView = (ViewHolder) convertView.getTag();
        }
        myView.lblContent.setText(msgInfo.Content);
        myView.lblInputDT.setText(msgInfo.InputDT);
        myView.lblLoc.setText(msgInfo.Loc);
        myView.lblTask.setText(msgInfo.Task);
        myView.lblEmpInfo.setText(msgInfo.EmpName + " - " + msgInfo.RegName + " - " + msgInfo.Mobile);
        return convertView;
    }

    class ViewHolder {
        public TextView lblEmpInfo, lblInputDT, lblLoc, lblTask, lblContent;
    }
}
