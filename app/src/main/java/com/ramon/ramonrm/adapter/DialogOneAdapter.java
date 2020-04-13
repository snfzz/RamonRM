package com.ramon.ramonrm.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import androidx.core.content.ContextCompat;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.controls.PickerMultiColumnDialog;
import com.ramon.ramonrm.model.PickerMultiColumnData;

import java.util.ArrayList;
import java.util.List;

public class DialogOneAdapter extends NumberPicker {

    /**
     * 构造方法 NumberPicker
     * */

    public DialogOneAdapter(Context context) {
        super(context);
    }

    public DialogOneAdapter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogOneAdapter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * addView方法 ViewGroup
     * */

    @Override
    public void addView(View child) {
        super.addView(child);
        setNumberPickerView(child);
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        setNumberPickerView(child);
    }

    @Override
    public void addView(View child, int width, int height) {
        super.addView(child, width, height);
        setNumberPickerView(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        setNumberPickerView(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setNumberPickerView(child);
    }

    public void setNumberPickerView(View view) {
        if (view instanceof EditText) {
            ((EditText) view).setTextColor(ContextCompat.getColor(getContext(), R.color.colorTitle)); //字体颜色
            ((EditText) view).setTextSize(20f);//字体大小
        }
    }

}
