package com.ramon.ramonrm.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ramon.ramonrm.R;

/**
 * Created by Administrator on 2019/10/28.
 */

public class HomeMenuItem extends LinearLayout {
    private ImageView btnImage;
    private TextView lblTitle;
    private Context mContxt;
    private AttributeSet mAttrs;
    private boolean isChecked = false;
    private TypedArray mTypedArray;

    public HomeMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.home_menuitem, this);
        mContxt = context;
        mAttrs = attrs;
        lblTitle =  findViewById(R.id.home_menuitem_text);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HomeMenuItem);
        mTypedArray = ta;
        lblTitle.setText(ta.getString(R.styleable.HomeMenuItem_title));
        btnImage = findViewById(R.id.home_menuitem_button);
        boolean isChecked = ta.getBoolean(R.styleable.HomeMenuItem_ischecked, false);
        setIsChecked(isChecked);
    }

    public boolean getIsChecked() {
        return this.isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
        btnImage.setImageResource(mTypedArray.getResourceId(isChecked ? R.styleable.HomeMenuItem_selected_src : R.styleable.HomeMenuItem_src, R.mipmap.pt_homeblue));
        int colorId = isChecked ? R.color.colorSelected : R.color.colorUnSelected;
        lblTitle.setTextColor(mContxt.getResources().getColor(colorId));
    }

    public String getTitle(){
        return lblTitle.getText().toString();
    }
}