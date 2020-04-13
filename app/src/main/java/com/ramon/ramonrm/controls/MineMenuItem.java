package com.ramon.ramonrm.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ramon.ramonrm.R;

public class MineMenuItem extends LinearLayout {
    private ImageView imgView;
    private TextView lblTitle;
    private Context mContxt;
    private TypedArray mTypedArray;
    public MineMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.mine_menuitem, this);
        mContxt = context;
        imgView = findViewById(R.id.mine_menuitem_imgview);
        lblTitle = findViewById(R.id.mine_menuitem_lbltitle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MineMenuItem);
        mTypedArray = ta;
        lblTitle.setText(ta.getString(R.styleable.MineMenuItem_title));
        imgView.setImageResource(mTypedArray.getResourceId(R.styleable.MineMenuItem_src, R.mipmap.gzk));
    }
}
