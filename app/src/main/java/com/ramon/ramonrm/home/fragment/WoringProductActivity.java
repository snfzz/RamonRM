package com.ramon.ramonrm.home.fragment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.WoringProductAdapter;
import com.ramon.ramonrm.adapter.WoringProductAdapter2;
import com.ramon.ramonrm.listclass.AllListClass;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DateTimeUtil;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyErrorHelper;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

import java.util.ArrayList;
import java.util.List;

public class WoringProductActivity extends BaseActivity {
    private ListView listView1,listView2;
    private List<AllListClass> list2;
    private List<AllListClass>list1;
    private String [] ChanPinSNo;
    private TextView textView;
    private ImageButton imgbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_woring_product);
        GetProductWaring();
        initView();
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GetSheBeitWaring(ChanPinSNo[i]);
            }
        });
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManagerUtil.instance().finishActivity(WoringProductActivity.this);
            }
        });
    }

    private void initView(){
        listView2=(ListView)findViewById(R.id.activity_woringproduct_list2);
        listView1=(ListView)findViewById(R.id.activity_woringproduct_list1);
        textView=(TextView)findViewById(R.id.activity_woringproduct_text);
        imgbtn=(ImageButton)findViewById(R.id.activity_woringproduct_imgback);
    }

    private void GetProductWaring(){
        list1=new ArrayList<>();
        try {
            long currTime = System.currentTimeMillis();
            long beginTime = currTime;
            long endTime = currTime + 24 * 60 * 60 * 1000;
            ReqData reqData = new ReqData("SQLExec", "BizSql", "SPExecForTable", Session.SessionId, Session.ValidMD5);
            reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
            reqData.ExtParams.put("spName", "BJ_BaoJing_TongJi_Home_SP");
            reqData.ExtParams.put("tjType", "0");
            reqData.ExtParams.put("beginTime", DateTimeUtil.DateTimeToString(beginTime,"yyyy/MM/dd"));
            reqData.ExtParams.put("endTime", DateTimeUtil.DateTimeToString(endTime,"yyyy/MM/dd"));
            DialogUitl.showProgressDialog(WoringProductActivity.this,  reqData.CmdID,"正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req",  reqData,
                    new VolleyListenerInterface(WoringProductActivity.this, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData,String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    if (resData.DataTable.length == 0){

                                    }else {
                                        ChanPinSNo=new String[resData.DataTable.length];
                                        for (int i=0;i<resData.DataTable.length;i++){
                                            String txt1=resData.DataTable[i].get("MingCheng");
                                            String txt2=resData.DataTable[i].get("AlarmDevNum");
                                            ChanPinSNo[i]=resData.DataTable[i].get("ChanPinSNo");
                                            list1.add(new AllListClass(txt1,txt2));
                                        }
                                        WoringProductAdapter2 woringProductAdapter2=new WoringProductAdapter2(context,R.layout.listview_seepower,list1);
                                        listView1.setAdapter(woringProductAdapter2);
                                    }
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), context);
                            } finally {
                                DialogUitl.dismissProgressDialog( reqData.CmdID);
                            }
                        }

                        // Volley请求失败时调用的函数
                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog( reqData.CmdID);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetSheBeitWaring(String sno){
        try {
            long currTime = System.currentTimeMillis();
            long beginTime = currTime;
            long endTime = currTime + 24 * 60 * 60 * 1000;
            list2=new ArrayList<>();
            ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
            reqData.ExtParams.put("spName","BJ_BaoJing_TongJi_Home_SP");
            reqData.ExtParams.put("endTime", DateTimeUtil.DateTimeToString(endTime,"yyyy/MM/dd"));
            reqData.ExtParams.put("beginTime", DateTimeUtil.DateTimeToString(beginTime,"yyyy/MM/dd"));
            reqData.ExtParams.put("chanPinSNo",sno);
            reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
            reqData.ExtParams.put("tjType","1");
            DialogUitl.showProgressDialog(WoringProductActivity.this,  reqData.CmdID,"正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(WoringProductActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                Log.e("fwwwq",result);
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue==0){
                                    if (resData.DataTable.length == 0){
                                        textView.setVisibility(View.INVISIBLE);
                                    }else {
                                        textView.setVisibility(View.VISIBLE);
                                        for (int i=0;i<resData.DataTable.length;i++){
                                            String txt1=resData.DataTable[i].get("KeHuMC");
                                            String txt2=resData.DataTable[i].get("MingCheng");
                                            String txt3=resData.DataTable[i].get("MiaoShuGZ");
                                            String txt4=resData.DataTable[i].get("YuanYinGZ");
                                            String txt5=resData.DataTable[i].get("DengJiDM");
                                            String txt6=resData.DataTable[i].get("rn");
                                            String txt7=resData.DataTable[i].get("ChuLiGZ");
                                            String txt8=resData.DataTable[i].get("BeginTime");
                                            String txt9=resData.DataTable[i].get("ChiXuSJ");
                                            if (txt9==null){
                                                txt9="-";
                                            }else {
                                                txt9=txt9.substring(0,txt9.indexOf("."))+"s";
                                            }
                                            list2.add(new AllListClass(txt1,txt2,txt3,txt4,txt5,txt6,txt7,txt8,txt9));
                                        }
                                        WoringProductAdapter woringProductAdapter=new WoringProductAdapter(context,R.layout.listview_woringproduct,list2);
                                        listView2.setAdapter(woringProductAdapter);
                                    }
                                }else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            }catch (Exception e){
                                MethodUtil.showToast(e.getMessage(), context);
                            }finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog(reqData.CmdID);
                            MethodUtil.showToast(VolleyErrorHelper.getMessage(error, mContext), context);
                        }
                    });
        }catch (Exception e){
            MethodUtil.showToast(e.getMessage(), context);
        }
    }
}
