package com.ramon.ramonrm.renyuan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.BeiJianGuanLiAdapter1;
import com.ramon.ramonrm.adapter.BeiJianGuanLiAdapter2;
import com.ramon.ramonrm.listclass.AllListClass;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
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

public class BeiJianGuanliActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton imgbtn;
    private ListView listView1;
    private ListView listView2;

    private BeiJianGuanLiAdapter2 beiJianGuanLiAdapter2;
    private BeiJianGuanLiAdapter1 beiJianGuanLiAdapter1;

    private List<AllListClass> list1;
    private List<AllListClass> list2;
    private EditText editname;
    private ImageView imgsearch;

    private TextView txt1;
    private TextView txt2;
    private RelativeLayout r1;
    private RelativeLayout r2;

    String [] FKID;
    private String [] xiangxisno;
    private String [] xiangxiname;
    private String [] xiangxitype;//型号
    private String [] xiangxifnum;//初期数量
    private String [] xiangxilnum;//期末数量
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beijianguanli);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        GetGangChan("");
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                beiJianGuanLiAdapter1.setCurrentItem(i);
                beiJianGuanLiAdapter1.notifyDataSetChanged();
                GetQingDan(FKID[i]);
            }
        });
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                beiJianGuanLiAdapter2.setCurrentItem(i);
                beiJianGuanLiAdapter2.notifyDataSetChanged();
                Intent intent=new Intent(BeiJianGuanliActivity.this,BeiJianXiangXiActivity.class);
                intent.putExtra("txt1",xiangxisno[i]);
                intent.putExtra("txt2",xiangxiname[i]);
                intent.putExtra("txt3",xiangxitype[i]);
                intent.putExtra("txt4",xiangxifnum[i]);
                intent.putExtra("txt5",xiangxilnum[i]);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        imgbtn=(ImageButton)findViewById(R.id.activity_beijianguanli_imgback);
        imgbtn.setOnClickListener(this);
        listView1=(ListView)findViewById(R.id.activity_changzhugongchang_listview);
        listView2=(ListView)findViewById(R.id.activity_beijianguanli_listview);
        editname=(EditText)findViewById(R.id.activity_beijianguanli_mingcheng);
        imgsearch=(ImageView)findViewById(R.id.activity_beijianguanli_btnsearch);
        imgsearch.setOnClickListener(this);
        txt1=(TextView)findViewById(R.id.activity_beijianguanli_txt1);
        txt2=(TextView)findViewById(R.id.activity_beijianguanli_txt2);
        r1=(RelativeLayout)findViewById(R.id.activity_beijianguanli_rr1);
        r2=(RelativeLayout)findViewById(R.id.activity_beijianguanli_rr2);

    }

    @Override
    public void onClick(View view) {
        int vId =view.getId();
        if (vId == R.id.activity_beijianguanli_imgback){
            AppManagerUtil.instance().finishActivity(BeiJianGuanliActivity.this);
        }
        if (vId == R.id.activity_beijianguanli_btnsearch){
            GetGangChan(editname.getText().toString());
        }
    }

    private void GetGangChan(String mingcheng){
        list1=new ArrayList<>();
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","KH_KeHu_LieBiao_Page_SP");
        reqData.ExtParams.put("recordTotal","1");
        reqData.ExtParams.put("mingcheng",mingcheng);
        reqData.ExtParams.put("outParams","recordTotal");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("quyu","常驻部");
        try {
            DialogUitl.showProgressDialog(BeiJianGuanliActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(BeiJianGuanliActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try{
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                FKID=new String[resData.DataTable.length];
                                if (resData.RstValue == 0) {
                                    if (resData.DataTable.length == 0) {
                                        txt2.setVisibility(View.INVISIBLE);
                                        r2.setVisibility(View.INVISIBLE);
                                        listView2.setVisibility(View.INVISIBLE);
                                        txt1.setVisibility(View.INVISIBLE);
                                        r1.setVisibility(View.INVISIBLE);
                                        listView1.setVisibility(View.INVISIBLE);
                                    } else {
                                        txt1.setVisibility(View.VISIBLE);
                                        r1.setVisibility(View.VISIBLE);
                                        listView1.setVisibility(View.VISIBLE);
                                        for (int i = 0; i < resData.DataTable.length; i++) {
                                            String MingCheng = resData.DataTable[i].get("MingCheng");
                                            String LeiBieSNo = resData.DataTable[i].get("LeiBieSNo");
                                            FKID[i]=resData.DataTable[i].get("FKID");
                                            if (LeiBieSNo.equals("GC")) {
                                                list1.add(new AllListClass("钢厂", MingCheng));
                                            } else {
                                                list1.add(new AllListClass("", MingCheng));
                                            }
                                        }
                                    }
                                    beiJianGuanLiAdapter1=new BeiJianGuanLiAdapter1(context,R.layout.listview_changzhugangchang,list1);
                                    listView1.setAdapter(beiJianGuanLiAdapter1);
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

    private void GetQingDan(String khID){
        list2=new ArrayList<>();
        ReqData reqData=new ReqData("SQLExec","X5sql","SPExecForTable",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RM_KHSB_ChangZhuPrdt_GetList");
        reqData.ExtParams.put("outParams","");
        reqData.ExtParams.put("khID",khID);
        try{
            DialogUitl.showProgressDialog(BeiJianGuanliActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(BeiJianGuanliActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData=GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    if (resData.DataTable.length==0){
                                        txt2.setVisibility(View.INVISIBLE);
                                        r2.setVisibility(View.INVISIBLE);
                                        listView2.setVisibility(View.INVISIBLE);
                                        Toast.makeText(BeiJianGuanliActivity.this,"暂无数据",Toast.LENGTH_LONG).show();
                                    }else {
                                        txt2.setVisibility(View.VISIBLE);
                                        r2.setVisibility(View.VISIBLE);
                                        listView2.setVisibility(View.VISIBLE);
                                        xiangxisno=new String[resData.DataTable.length];
                                        xiangxiname=new String[resData.DataTable.length];
                                        xiangxitype=new String[resData.DataTable.length];
                                        xiangxifnum=new String[resData.DataTable.length];
                                        xiangxilnum=new String[resData.DataTable.length];
                                        for (int i=0;i<resData.DataTable.length;i++){
                                            String Name=resData.DataTable[i].get("Name");
                                            String InitQty=resData.DataTable[i].get("InitQty");
                                            String RemainQty=resData.DataTable[i].get("RemainQty");
                                            list2.add(new AllListClass(Name,InitQty,RemainQty));
                                            xiangxisno[i]=resData.DataTable[i].get("PrdNO");
                                            xiangxiname[i]=resData.DataTable[i].get("Name");
                                            xiangxitype[i]=resData.DataTable[i].get("Model");
                                            xiangxifnum[i]=resData.DataTable[i].get("InitQty");
                                            xiangxilnum[i]=resData.DataTable[i].get("RemainQty");
                                        }
                                    }
                                    beiJianGuanLiAdapter2=new BeiJianGuanLiAdapter2(context,R.layout.listview_beijianqingdan,list2);
                                    listView2.setAdapter(beiJianGuanLiAdapter2);
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
