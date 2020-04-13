package com.ramon.ramonrm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.listclass.AllListClass;

import java.util.List;

public class InstallationTaskAdapter extends BaseAdapter  {

    //当前Item被点击的位置
    private int currentItem=-1;


    private final View.OnClickListener listener;
    private final List<AllListClass> dataList;

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    public InstallationTaskAdapter(View.OnClickListener listener, List<AllListClass> dataList) {
        this.listener = listener;
        this.dataList = dataList;
    }
    @Override
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
        ViewHolderDelete viewHolderDelete=new ViewHolderDelete();
        AllListClass allListClass=(AllListClass)getItem(i);
        view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_installationtask,viewGroup,false);
        viewHolderDelete.hth=(TextView)view.findViewById(R.id.listview_installationtask_txt1);
        viewHolderDelete.khmc=(TextView)view.findViewById(R.id.listview_installationtask_txt2);
        viewHolderDelete.cpmc=(TextView)view.findViewById(R.id.listview_installationtask_txt3);
        viewHolderDelete.qyjl=(TextView)view.findViewById(R.id.listview_installationtask_txt4);
        viewHolderDelete.rwzt=(TextView)view.findViewById(R.id.listview_installationtask_txt5);
        viewHolderDelete.jdtj=(TextView)view.findViewById(R.id.listview_installationtask_txt6);
        viewHolderDelete.xxnr=(TextView)view.findViewById(R.id.listview_installationtask_txt7);
        viewHolderDelete.ckxx=(TextView)view.findViewById(R.id.listview_installationtask_txt8);
        viewHolderDelete.l1=(LinearLayout)view.findViewById(R.id.listview_installationtask_l1);

        viewHolderDelete.hth.setText(allListClass.getTxt1());
        viewHolderDelete.khmc.setText(allListClass.getTxt2());
        viewHolderDelete.cpmc.setText(allListClass.getTxt3());
        viewHolderDelete.qyjl.setText(allListClass.getTxt4());
        viewHolderDelete.rwzt.setText(allListClass.getTxt5());
        viewHolderDelete.jdtj.setText(allListClass.getTxt6());
        viewHolderDelete.xxnr.setText(allListClass.getTxt7());
        //viewHolderDelete.ckxx.setText(allListClass.getTxt8());
        viewHolderDelete.ckxx.setOnClickListener(listener);
        viewHolderDelete.ckxx.setTag(i);
        if (currentItem == i) {
            view.setBackgroundResource(R.color.colorBackground);
        }
        return view;
    }

    class ViewHolderDelete {
        TextView hth;
        TextView khmc;
        TextView cpmc;
        TextView qyjl;
        TextView rwzt;
        TextView jdtj;
        TextView xxnr;
        TextView ckxx;
        LinearLayout l1;
    }
}
