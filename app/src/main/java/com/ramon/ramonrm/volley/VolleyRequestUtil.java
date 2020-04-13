package com.ramon.ramonrm.volley;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.ramon.ramonrm.LIMSApplication;
import com.ramon.ramonrm.webapi.ReqData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2016/11/19.
 */

public class VolleyRequestUtil {//volley框架网络连接类

    //public static StringRequest stringRequest;
    public static JsonRequest jsonRequest;
    public static Context context;

    public static void RequestPost(Context context, String url, final ReqData reqData, final VolleyListenerInterface volleyListenerInterface) throws JSONException {
        // 清除请求队列中的tag标记请求
        //LIMSApplication.getRequestQueue().cancelAll(reqData.CmdID);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("reqJson", reqData);
        // 创建当前的POST请求，并将请求内容写入Map中
        jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                volleyListenerInterface.onMySuccess(reqData, jsonObject.toString());
            }
        }, volleyListenerInterface.errorListener()) {
        };
        // 为当前请求添加标记
        jsonRequest.setTag(reqData.CmdID);
        //设置请求超时时间
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, 1.0f));
        // 将当前请求添加到请求队列中
        LIMSApplication.getRequestQueue().add(jsonRequest);
    }
}
