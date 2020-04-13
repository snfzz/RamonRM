package com.ramon.ramonrm.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Looper;

import java.util.HashMap;

/**
 * Created by user on 2016/11/22.
 */

public class DialogUitl {//简单加载动画
    private static HashMap<String, ProgressDialog> mHashMap = new HashMap<>();

    /**
     * Show a progress prompt dialog
     *
     * @param msg : show message text content
     */
    public static void showProgressDialog(final Context ct, final String key, final String msg) {
        if (!mHashMap.containsKey(key)) {
            mHashMap.put(key, null);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    ProgressDialog mProgressDialog = null;
                    mProgressDialog = new ProgressDialog(ct);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.setMessage(msg);
                    mProgressDialog.show();
                    if (mHashMap.containsKey(key)) {
                        mHashMap.remove(key);
                        mHashMap.put(key, mProgressDialog);
                    }
                    else{
                        mProgressDialog.dismiss();
                    }
                    Looper.loop();
                }
            }).start();
        }
    }

    /**
     * Dismiss a progress prompt dialog box
     */
    public static void dismissProgressDialog(String key) {
        if (mHashMap.containsKey(key)) {
            ProgressDialog mProgressDialog = mHashMap.get(key);
            mHashMap.remove(key);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }
}