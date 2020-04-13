package com.ramon.ramonrm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.model.DaKaInfo;
import com.ramon.ramonrm.util.ImageUitl;

import java.util.List;

public class DaKaAdapter  extends ArrayAdapter {
    private Context mContext;
    private int mResource;
    OnItemClickListener mImageClickListener;

    public DaKaAdapter(Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        mContext = getContext();
        mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        DaKaInfo dkInfo = (DaKaInfo) getItem(position);
        ViewHolder myView = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            myView = new ViewHolder();
            myView.imgDaKa = (ImageView) convertView.findViewById(R.id.listitem_dakainfo_imgview);
            myView.lblDaKaTime = (TextView) convertView.findViewById(R.id.listitem_dakainfo_lbldakatime);
            myView.lblDaKaAddress = (TextView) convertView.findViewById(R.id.listitem_dakainfo_lbldakaaddress);
            myView.lblDaKaType = (TextView) convertView.findViewById(R.id.listitem_dakainfo_lbldakatype);
            myView.imgTag = (ImageView) convertView.findViewById(R.id.listitem_dakainfo_imgtag);
            myView.imgDaKa.setOnClickListener(new MyOnClickLisener(dkInfo));
            convertView.setTag(myView);
        } else {
            myView = (ViewHolder) convertView.getTag();
        }
        myView.imgTag.setImageResource(R.mipmap.loctag);
        myView.lblDaKaTime.setText(dkInfo.DaKaSJ);
        myView.lblDaKaAddress.setText(dkInfo.Address);
        myView.lblDaKaType.setText(dkInfo.LeiBieMC);
        Bitmap img = ImageUitl.getImage(mContext, dkInfo.FileName, dkInfo.ImageUrl, R.mipmap.noimage);
        myView.imgDaKa.setImageBitmap(img);
        return convertView;
    }

    private class MyOnClickLisener implements View.OnClickListener {
        private Object clickItem = null;

        public MyOnClickLisener(Object item) {
            clickItem = item;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.listitem_dakainfo_imgview) {
                if (mImageClickListener != null) {
                    mImageClickListener.onItemClick(clickItem, view);
                }
            }
        }
    }

    public void setOnImageClickListener(OnItemClickListener listener) {
        this.mImageClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Object item, View view);
    }

    class ViewHolder{
        public ImageView imgDaKa, imgTag;
        public TextView lblDaKaTime, lblDaKaAddress, lblDaKaType;
    }
}