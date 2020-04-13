package com.ramon.ramonrm.volley;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ramon.ramonrm.webapi.ReqData;

/**
 * Created by user on 2016/11/19.
 */

public abstract class VolleyListenerInterface {//volley框架接口封装

    public Context mContext;
    public ReqData reqData;
    public Response.Listener<String> mListener;
    public Response.ErrorListener mErrorListener;

    public VolleyListenerInterface(Context context, ReqData reqData) {
        this.mContext = context;
        this.reqData = reqData;
    }

    // 请求成功时的回调函数
    public abstract void onMySuccess(ReqData reqData, String result);

    // 请求失败时的回调函数
    public abstract void onMyError(ReqData reqData, VolleyError error);

    // 创建请求的事件监听
    public Response.Listener<String> responseListener() {
        mListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                onMySuccess(reqData, s);
            }
        };
        return mListener;
    }

    // 创建请求失败的事件监听
    public Response.ErrorListener errorListener() {
        mErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onMyError(reqData, volleyError);
            }
        };
        return mErrorListener;
    }
}
