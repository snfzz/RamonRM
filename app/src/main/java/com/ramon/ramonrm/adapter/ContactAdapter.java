package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.model.UserInfo;

import java.util.List;

public class ContactAdapter extends ArrayAdapter {

    public static final int CALLTYPE_PHONE = 0;
    public static final int CALLTYPE_VIDEO = 1;

    private Context mContext;
    private int mResource;
    private Object mItem;

    public ContactAdapter(Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        mContext = getContext();
        mResource = resource;
    }

    OnItemClickListener mImageClickListener;
    public void setOnImageClickListener(OnItemClickListener listener) {
        this.mImageClickListener = listener;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        UserInfo uInfo = (UserInfo) getItem(position);
        mItem = getItem(position);
        ViewHolder myView;
        if (convertView == null) {
            myView = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            myView.lblName = (TextView)convertView.findViewById(R.id.listitem_contact_lblname);
            myView.lblDept = (TextView)convertView.findViewById(R.id.listitem_contact_lbldpet);
            myView.lblPhone = (TextView)convertView.findViewById(R.id.listitem_contact_lblphone);
            myView.imgPhoneCall = (ImageView)convertView.findViewById(R.id.listitem_contact_imgphonecall);
            myView.imgPhoneCall.setOnClickListener(new MyOnClickLisener(mItem));
            myView.imgVideoCall = (ImageView)convertView.findViewById(R.id.listitem_contact_imgvideocall);
            myView.imgVideoCall.setOnClickListener(new MyOnClickLisener(mItem));
            myView.imgHeader = (ImageView)convertView.findViewById(R.id.listitem_contact_imgheader);
            convertView.setTag(myView);
        }
        else{
            myView = (ViewHolder)convertView.getTag();
        }
        myView.lblPhone.setText(uInfo.Mobile);
        myView.lblDept.setText(uInfo.DeptSNames);
        myView.lblName.setText(uInfo.Name);
        myView.imgPhoneCall.setBackgroundResource(R.mipmap.phonecall);
        myView.imgVideoCall.setBackgroundResource(R.mipmap.jieting);
        myView.imgHeader.setBackgroundResource(R.mipmap.head_icon);
        return convertView;
    }

    private class MyOnClickLisener implements View.OnClickListener {
        private Object clickItem=null;
        public MyOnClickLisener(Object item){
            clickItem= item;
        }
        @Override
        public void onClick(View view) {
            int callType = CALLTYPE_PHONE;
            if (view.getId() == R.id.listitem_contact_imgphonecall) {
                callType = CALLTYPE_PHONE;
            } else {
                callType = CALLTYPE_VIDEO;
            }
            if (mImageClickListener != null) {
                mImageClickListener.onItemClick(clickItem, view, callType);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Object item, View view, int callType);
    }

    class ViewHolder{
        private TextView lblName,lblDept,lblPhone;
        private ImageView imgPhoneCall,imgVideoCall;
        private ImageView imgHeader;
    }
}
