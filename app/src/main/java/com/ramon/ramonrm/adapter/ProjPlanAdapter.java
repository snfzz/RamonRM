package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.ShenQingDialog.base.Config;
import com.ramon.ramonrm.ShenQingDialog.component.ShowImagesDialog;
import com.ramon.ramonrm.model.ProjPlanInfo;
import com.ramon.ramonrm.model.ShowText;

import java.util.ArrayList;
import java.util.List;

public class ProjPlanAdapter extends ArrayAdapter {

    public final static int CODE_SUBMIT = 0;
    public final static int CODE_DETAIL = 1;
    public final static int CODE_NONE = -1;

    private Context mContext;
    private int mResource;

    private OnItemClickListener onItemClickListener;
    private View.OnClickListener listener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ProjPlanAdapter(Context context, int resource, @NonNull List objects,View.OnClickListener listener) {
        super(context, resource, objects);
        mContext = getContext();
        mResource = resource;
        this.listener=listener;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ProjPlanInfo planInfo = (ProjPlanInfo) getItem(position);
        final ViewHolder myView;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            myView = new ViewHolder();
            myView.gvImage = convertView.findViewById(R.id.listitem_projplan_gridimage);


            myView.btnHelp = convertView.findViewById(R.id.listitem_projplan_imghelp);
            myView.btnHelp.setOnClickListener(listener);
            myView.btnHelp.setTag(position);
            //myView.btnHelp.setOnClickListener(new MyOnClickListener(planInfo, position));

            myView.btnSubmit = convertView.findViewById(R.id.listitem_projplan_btnsubmit);
            //myView.btnSubmit.setOnClickListener(new MyOnClickListener(planInfo, position));
            myView.btnSubmit.setOnClickListener(listener);
            myView.btnSubmit.setTag(position);

            myView.btnDown = convertView.findViewById(R.id.listitem_projplan_imgdown);
            myView.btnDown.setTag(myView);
            myView.btnDown.setOnClickListener(new MyOnClickListener(planInfo, position));
            myView.lblPlanFile = convertView.findViewById(R.id.listitem_projplan_lblplanfile);
            myView.lblPlanFile.setOnClickListener(new MyOnClickListener(planInfo,position));
            myView.lblPlanZT = convertView.findViewById(R.id.listitem_projplan_lblplanzt);
            myView.lblPlanMC = convertView.findViewById(R.id.listitem_projplan_lblplanmc);
            convertView.setTag(myView);
        } else {
            myView = (ViewHolder) convertView.getTag();
        }
        myView.lblPlanZT.setText(planInfo.StatusTitle);
        myView.lblPlanMC.setText(planInfo.PhaseName);
        myView.lblPlanFile.setText(planInfo.FileNum + "");
        if (planInfo.Status.toLowerCase().trim().equals("wancheng")) {
            myView.btnSubmit.setVisibility(View.INVISIBLE);
        } else {
            myView.btnSubmit.setVisibility(View.VISIBLE);
        }
        if (myView.gvImage.getVisibility() == View.VISIBLE) {
            myView.btnDown.setImageResource(R.mipmap.dropopen);
        } else {
            myView.btnDown.setImageResource(R.mipmap.dropclosed);
        }
        List<ShowText> listSText = new ArrayList<>();
        final List<String >list=new ArrayList<>();
        for (int i = 0; i < planInfo.FileNum; i++) {
            if (planInfo.FileNames[i] != null) {
                ShowText sText = new ShowText();
                sText.Key = planInfo.FileUrls[i];
                sText.Title = planInfo.FileNames[i];
                list.add(planInfo.FileUrls[i]);
                listSText.add(sText);
            }
        }
        myView.gvImage.setTag(position);
        GridViewImageAdapter adapter = new GridViewImageAdapter(mContext, R.layout.griditem_imageview, listSText);
        myView.gvImage.setAdapter(adapter);
        myView.gvImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new ShowImagesDialog(mContext, list,i).show();
            }
        });
        return convertView;
    }

    public class MyOnClickListener implements View.OnClickListener {

        private Object objItem;
        private int position;

        public MyOnClickListener( Object item, int id) {
            this.objItem = item;
            this.position = id;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.listitem_projplan_imgdown || view.getId() == R.id.listitem_projplan_lblplanfile) {
                ViewHolder myView = (ViewHolder)view.getTag();
                if(myView.gvImage.getVisibility() == View.VISIBLE){
                    myView.gvImage.setVisibility(View.GONE);
                    myView.btnDown.setImageResource(R.mipmap.dropclosed);
                }
                else{
                    myView.gvImage.setVisibility(View.VISIBLE);
                    myView.btnDown.setImageResource(R.mipmap.dropopen);
                }
            } else {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(objItem, view, position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Object item, View view, int position);
    }

    public class ViewHolder {
        private TextView lblPlanMC,lblPlanZT,lblPlanFile;
        private Button btnSubmit;
        private ImageView btnHelp,btnDown;
        private GridView gvImage;
    }
}

