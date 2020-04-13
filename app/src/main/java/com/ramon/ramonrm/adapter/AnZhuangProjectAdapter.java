package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.model.ProjectInfo;

import java.util.List;

public class AnZhuangProjectAdapter extends ArrayAdapter {

    private Context mContext;
    private int mResource;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public AnZhuangProjectAdapter(Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        mContext = getContext();
        mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ProjectInfo projectInfo = (ProjectInfo) getItem(position);
        ViewHolder myView;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            myView = new ViewHolder();
            myView.lblHeTongH = convertView.findViewById(R.id.listitem_anzhuangtask_lblhth);
            myView.lblKeHuMC = convertView.findViewById(R.id.listitem_anzhuangtask_lblkhmc);
            myView.lblChanPinMC = convertView.findViewById(R.id.listitem_anzhuangtask_lblcpmc);
            myView.lblQuYuJL = convertView.findViewById(R.id.listitem_anzhuangtask_lblqyjl);
            myView.lblRenWuZT = convertView.findViewById(R.id.listitem_anzhuangtask_lblrwzt);
            myView.lblJieDianTJ = convertView.findViewById(R.id.listitem_anzhuangtask_lbljdtj);
            myView.lblXiangXiNR = convertView.findViewById(R.id.listitem_anzhuangtask_lblxxnr);
            myView.lblDetailTask = convertView.findViewById(R.id.listitem_anzhuangtask_lbltodetail);
            myView.lblDetailTask.setOnClickListener(new MyOnClickListener(projectInfo,position));
            myView.lyMain = convertView.findViewById(R.id.listitem_azhuangtask_lymain);
            myView.lyMain.setOnClickListener(new MyOnClickListener(projectInfo, position));
            myView.lyMain.setOnLongClickListener(new MyOnLongClickListener(myView.lyMain, projectInfo, position));
            convertView.setTag(myView);
        } else {
            myView = (ViewHolder) convertView.getTag();
        }
        myView.lblHeTongH.setText(projectInfo.HTH);
        myView.lblKeHuMC.setText(projectInfo.KeHuMC);
        myView.lblChanPinMC.setText(projectInfo.ChanPinMC);
        if (projectInfo.XingMing == null || projectInfo.XingMing.length() == 0) {
            myView.lblQuYuJL.setText("-");
        } else {
            myView.lblQuYuJL.setText(projectInfo.XingMing + "-" + projectInfo.RegionName);
        }
        String rwZT = "";
        if (projectInfo.SUM_Total > 0 && projectInfo.SUM_Total == projectInfo.SUM_WanCheng) {
            rwZT = "完成";
        } else if (projectInfo.SUM_Total == 0) {
            rwZT = "区域指派";
        } else {
            rwZT = "执行中";
        }
        myView.lblRenWuZT.setText(rwZT);
        myView.lblJieDianTJ.setText("完成数：" + projectInfo.SUM_WanCheng + "  总数:" + projectInfo.SUM_Total);
        myView.lblXiangXiNR.setText(projectInfo.Name);
        myView.ProjectInfo = projectInfo;
        myView.lyMain.setBackgroundResource(projectInfo.IsSelected ? R.color.colorPrimaryGray : R.color.colorPrimaryDark);
        return convertView;
    }

    public class MyOnClickListener implements View.OnClickListener {

        private Object objItem;
        private int position;

        public MyOnClickListener(Object item, int id) {
            this.objItem = item;
            this.position = id;
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(objItem, view, position);
            }

            if (view.getId() == R.id.listitem_anzhuangtask_lbltodetail) {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onItemLongClick(objItem, view, position);
                }
            }
        }
    }

    public class MyOnLongClickListener implements View.OnLongClickListener {

        private Object objItem;
        private int position;

        public MyOnLongClickListener(View view, Object item, int id) {
            this.objItem = item;
            this.position = id;
        }

        @Override
        public boolean onLongClick(View view) {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(objItem, view, position);
            }
            return false;
        }

    }

    public interface OnItemClickListener {
        void onItemClick(Object item, View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Object item, View view, int position);
    }

    public class ViewHolder {
        private TextView lblHeTongH, lblKeHuMC, lblChanPinMC, lblQuYuJL, lblRenWuZT, lblJieDianTJ, lblXiangXiNR;
        private TextView lblDetailTask;
        private LinearLayout lyMain;
        public ProjectInfo ProjectInfo;
    }
}

