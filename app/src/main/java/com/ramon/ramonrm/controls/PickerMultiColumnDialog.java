package com.ramon.ramonrm.controls;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.model.PickerMultiColumnData;

import java.util.ArrayList;
import java.util.List;

public class PickerMultiColumnDialog {
    private static String Tag = "PickerMultiColumnDialog";

    private Activity mContext;
    private NumberPicker mPicker1,mPicker2,mPicker3;
    private List<PickerMultiColumnData>mDataList;

    private List<PickerMultiColumnData>mData1;
    private List<PickerMultiColumnData>mData2;
    private List<PickerMultiColumnData>mData3;
    private String[] mDataTitle1;
    private String[] mDataTitle2;
    private String[] mDataTitle3;

    private PickedListener mListener;

    private AlertDialog mAlertDlg;

    public PickerMultiColumnDialog(Activity context,List<PickerMultiColumnData>listData ,PickedListener listener) {
        this.mContext = context;
        this.mListener = listener;
        this.mDataList = listData;
        mData1 = new ArrayList<>();
        mData2 = new ArrayList<>();
        mData3 = new ArrayList<>();
        for (int i = 0; i < listData.size(); i++) {
            PickerMultiColumnData data = listData.get(i);
            if (data.Level == 0) {
                mData1.add(data);
            }
        }
        if(mData1.size()>0){
            for(int i=0;i<mData1.get(0).Children.size();i++) {
                mData2.add(mData1.get(0).Children.get(i));
            }
        }
        if(mData2.size()>0){
            for(int i=0;i<mData2.get(0).Children.size();i++) {
                mData3.add(mData2.get(0).Children.get(i));
            }
        }
        mDataTitle1 = new String[mData1.size()];
        for (int i = 0; i < mData1.size(); i++) {
            mDataTitle1[i] = mData1.get(i).Title;
        }

        mDataTitle2 = new String[mData2.size()];
        for (int i = 0; i < mData2.size(); i++) {
            mDataTitle2[i] = mData2.get(i).Title;
        }

        mDataTitle3 = new String[mData3.size()];
        for (int i = 0; i < mData3.size(); i++) {
            mDataTitle3[i] = mData3.get(i).Title;
        }
    }

    public AlertDialog showDialog() {
        FrameLayout locatePickLayout = (FrameLayout) mContext.getLayoutInflater().inflate(R.layout.dialog_pickermulticolumn, null);
        mPicker1 = locatePickLayout.findViewById(R.id.dialog_pickermulticolumn_npicker1);
        mPicker2 = locatePickLayout.findViewById(R.id.dialog_pickermulticolumn_npicker2);
        mPicker3 = locatePickLayout.findViewById(R.id.dialog_pickermulticolumn_npicker3);
        initView();
        mAlertDlg = new AlertDialog.Builder(mContext).setView(locatePickLayout).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PickerMultiColumnData colData1 = mData1.get(mPicker1.getValue());
                PickerMultiColumnData colData2 = mPicker2.getValue() >= 0 ? mData2.get(mPicker2.getValue()) : null;
                PickerMultiColumnData colData3 = mPicker3.getValue() >= 0 ? mData3.get(mPicker3.getValue()) : null;
                if (mListener != null) {
                    mListener.onAddressPicked(colData1, colData2, colData3);
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
        return mAlertDlg;
    }

    private void initView(){
        mPicker1.setWrapSelectorWheel(false);
        setPickerTextSize(mPicker1,mDataTitle1);
        mPicker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                PickerMultiColumnData colData1 = mData1.get(newVal);
                mData2 = new ArrayList<>();
                for (int i = 0; i < colData1.Children.size(); i++) {
                    mData2.add(colData1.Children.get(i));
                }
                mDataTitle2 = new String[mData2.size()];
                for (int i = 0; i < mData2.size(); i++) {
                    mDataTitle2[i] = mData2.get(i).Title;
                }
                mPicker2.setValue(-1);
                setPickerTextSize(mPicker2, mDataTitle2);
            }
        });
        mPicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mPicker2.setDisplayedValues(null);
        setPickerTextSize(mPicker2,mDataTitle2);
        mPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                if(newVal<0)return;
                PickerMultiColumnData colData2 = mData2.get(newVal);
                mData3 = new ArrayList<>();
                for(int i=0;i<colData2.Children.size();i++) {
                    mData3.add(colData2.Children.get(i));
                }
                mDataTitle3 = new String[mData3.size()];
                for (int i = 0; i < mData3.size(); i++) {
                    mDataTitle3[i] = mData3.get(i).Title;
                }
                mPicker3.setDisplayedValues(null);
                setPickerTextSize(mPicker3, mDataTitle3);
            }
        });
        mPicker3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mPicker3.setDisplayedValues(null);
        setPickerTextSize(mPicker3,mDataTitle3);
    }

    private void setPickerTextSize(NumberPicker picker,String[]strVals) {
        picker.setDisplayedValues(strVals);
        picker.setMinValue(0);
        if (strVals.length > 0) {
            picker.setMaxValue(strVals.length - 1);
        } else {
            picker.setMaxValue(0);
        }
    }

    public interface PickedListener {
        abstract void onAddressPicked(PickerMultiColumnData data1,PickerMultiColumnData data2,PickerMultiColumnData data3);
    }
}
