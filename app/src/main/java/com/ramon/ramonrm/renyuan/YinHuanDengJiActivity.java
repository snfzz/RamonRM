package com.ramon.ramonrm.renyuan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.YinHuanDengJiAdapter;
import com.ramon.ramonrm.listclass.AllListClass;
import com.ramon.ramonrm.model.KeHuInfo;
import com.ramon.ramonrm.model.ProjPlanInfo;
import com.ramon.ramonrm.model.XinXiTianXieinfo;
import com.ramon.ramonrm.project.fragment.InstallationTaskActivity;
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
import com.ramon.ramonrm.yinhuan.YinHuanCKXXActivity;
import com.ramon.ramonrm.yinhuan.YinHuanTXXXActivity;
import com.ramon.ramonrm.yinhuan.YinHuanTxxxWriteActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class YinHuanDengJiActivity extends BaseActivity implements View.OnClickListener {
    private TextView yhzt;//dialog中的textview框
    private String sendYHZT="ChuLiZhongYH";//存放隐患状态的信息
    private ImageView imgbtn;

    Calendar calBegin = Calendar.getInstance(Locale.CHINA);//开始时间
    Calendar calEnd = Calendar.getInstance(Locale.CHINA);//结束时间
    private String begtime="";
    private String endtime="";

    private ListView listView;
    private YinHuanDengJiAdapter yinHuanDengJiAdapter;
    List<AllListClass>list;
    private int tag=-1;//用于记录当前点击的位置

    private ImageView write,delete,add,xinxi;
    private String [] ZT;
    private String [] SNO;

    //用于编辑中显示的内容与备注
    private String [] writebz;
    private String [] writecontent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yinhuandengji);
        initView();
        GetTime();
        GetYinHuanList("",sendYHZT,begtime,endtime);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                yinHuanDengJiAdapter.setCurrentItem(i);
                //通知ListView改变状态
                yinHuanDengJiAdapter.notifyDataSetChanged();
                delete.setVisibility(View.VISIBLE);
                if (ZT[i].equals("ChuLiZhongYH")){
                    write.setVisibility(View.VISIBLE);
                }else {
                    write.setVisibility(View.INVISIBLE);
                }
                tag=i;
            }
        });
    }

    private void GetTime(){
        long dtNow = System.currentTimeMillis();
        endtime= DateTimeUtil.DateTimeToString(dtNow, "yyyy-MM-dd");
        dtNow = dtNow+30*24 * 60 * 60 * 1000;
        begtime=DateTimeUtil.DateTimeToString(dtNow, "yyyy-MM-dd");
    }

    private void initView(){
        listView = findViewById(R.id.activity_yinhuandengji_listview);
        write=findViewById(R.id.activity_yinhuandengji_write);
        delete=findViewById(R.id.activity_yinhuandengji_delete);
        add=findViewById(R.id.activity_yinhuandengji_add);
        xinxi=findViewById(R.id.activity_yinhuandengji_xinxi);
        write.setOnClickListener(this);
        delete.setOnClickListener(this);
        add.setOnClickListener(this);
        xinxi.setOnClickListener(this);
        imgbtn=findViewById(R.id.activity_yinhuandengji_imgback);
        imgbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int vId=view.getId();
        if (vId == R.id.listview_yinhuandengji_txt9){
            int i= (int) view.getTag();
            Intent intent=new Intent(YinHuanDengJiActivity.this, YinHuanCKXXActivity.class);
            intent.putExtra("sno",SNO[i]);
            startActivity(intent);
        }
        if (vId == R.id.activity_yinhuandengji_write){
            Intent intent=new Intent(YinHuanDengJiActivity.this, YinHuanTxxxWriteActivity.class);
            intent.putExtra("sno",SNO[tag]);
            intent.putExtra("bz",writebz[tag]);
            intent.putExtra("content",writecontent[tag]);
            startActivity(intent);
        }
        if (vId == R.id.activity_yinhuandengji_delete){
            showDialog(SNO[tag]);
        }
        if (vId == R.id.activity_yinhuandengji_add){
            Intent intent=new Intent(YinHuanDengJiActivity.this, YinHuanTXXXActivity.class);
            startActivity(intent);
        }
        if (vId == R.id.activity_yinhuandengji_xinxi){
            showDialogAdd();
        }
        if (vId == R.id.activity_yinhuandengji_imgback){
            AppManagerUtil.instance().finishActivity(YinHuanDengJiActivity.this);
        }
    }

    private void GetYinHuanList(String keyword,String yhZT,String beginDate,String endDate){
        write.setVisibility(View.INVISIBLE);
        delete.setVisibility(View.INVISIBLE);
        tag=-1;
        list=new ArrayList<>();
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","KH_YinHuan_LieBiao_SP");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("yhSNo","");
        reqData.ExtParams.put("keyword",keyword);//搜索
        reqData.ExtParams.put("yhZT",yhZT);//ChuLiZhongYH,WanChengYH
        reqData.ExtParams.put("rmSBSNo","");
        reqData.ExtParams.put("beginDate",beginDate);
        reqData.ExtParams.put("endDate",endDate);
        reqData.ExtParams.put("outParams","recordTotal");
        reqData.ExtParams.put("recordTotal","1");
        try {
            DialogUitl.showProgressDialog(YinHuanDengJiActivity.this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(YinHuanDengJiActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    if (resData.DataTable.length==0){

                                    }else {
                                        ZT=new String[resData.DataTable.length];
                                        SNO=new String[resData.DataTable.length];
                                        writebz=new String[resData.DataTable.length];
                                        writecontent=new String[resData.DataTable.length];
                                        for (int i=0;i<resData.DataTable.length;i++){
                                            SNO[i]=resData.DataTable[i].get("SNo");
                                            String YinHuanDJName=resData.DataTable[i].get("YinHuanDJName");
                                            String MingCheng=resData.DataTable[i].get("MingCheng");
                                            String ChanPinMC=resData.DataTable[i].get("ChanPinMC");
                                            String RMSBMingCheng=resData.DataTable[i].get("RMSBMingCheng");
                                            if (RMSBMingCheng.length()==0){
                                                RMSBMingCheng="-";
                                            }
                                            String BZ=resData.DataTable[i].get("BZ");
                                            if (BZ.length()==0){
                                                BZ="-";
                                            }
                                            String Content=resData.DataTable[i].get("Content");
                                            if (Content.length()==0){
                                                Content="-";
                                            }
                                            writebz[i]=BZ;
                                            writecontent[i]=Content;
                                            String Status=resData.DataTable[i].get("Status");
                                            ZT[i]=Status;
                                            if (Status.equals("ChuLiZhongYH")){
                                                Status="处理中";
                                            }else {
                                                Status="已完成";
                                            }
                                            String Result=resData.DataTable[i].get("Result");
                                            if (Result.length()==0){
                                                Result="-";
                                            }
                                            list.add(new AllListClass(YinHuanDJName,MingCheng,ChanPinMC,RMSBMingCheng,BZ,Content,Status,Result));
                                        }
                                    }
                                    yinHuanDengJiAdapter=new YinHuanDengJiAdapter(YinHuanDengJiActivity.this,list);
                                    listView.setAdapter(yinHuanDengJiAdapter);
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


    private void showDialogAdd() {
        try {
            View view = getLayoutInflater().inflate(R.layout.showdalog_yinhuandengji,
                    null);
            final Dialog dialog = new Dialog(YinHuanDengJiActivity.this, R.style.MyUsualDialog);
            dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = dialog.getWindow();
            // 设置显示动画
            //window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            // 以下这两句是为了保证按钮可以水平满屏
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            wl.gravity= Gravity.CENTER;
            // 设置显示位置
            dialog.onWindowAttributesChanged(wl);
            // 设置点击外围解散
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();


            //取消的图标
            dialog.getWindow().findViewById(R.id.showdalog_yinhuandengji_miss).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            //开始时间
            final TextView begintime=dialog.getWindow().findViewById(R.id.showdalog_yinhuandengji_begtime);
            begintime.setText(begtime);
            begintime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //region 开始日期
                    new DatePickerDialog(YinHuanDengJiActivity.this,
                            // 绑定监听器
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    String strYear = year + "";
                                    String strMonth = month < 9 ? "0" + (month + 1) : "" + (month + 1);
                                    String strDay = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                                    begintime.setText(strYear + "-" + strMonth + "-" + strDay);
                                    calBegin.set(year, month, dayOfMonth);
                                }
                            }
                            , calBegin.get(Calendar.YEAR)
                            , calBegin.get(Calendar.MONTH)
                            , calBegin.get(Calendar.DAY_OF_MONTH)).show();
                    //endregion
                }
            });
            final TextView overtime=dialog.getWindow().findViewById(R.id.showdalog_yinhuandengji_endtime);
            overtime.setText(endtime);
            overtime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //region 结束日期
                    new DatePickerDialog(YinHuanDengJiActivity.this,
                            // 绑定监听器
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    String strYear = year + "";
                                    String strMonth = month < 9 ? "0" + (month + 1) : "" + (month + 1);
                                    String strDay = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                                    overtime.setText(strYear + "-" + strMonth + "-" + strDay);
                                    calEnd.set(year,month,dayOfMonth);
                                }
                            }
                            , calEnd.get(Calendar.YEAR)
                            , calEnd.get(Calendar.MONTH)
                            , calEnd.get(Calendar.DAY_OF_MONTH)).show();
                    //endregion
                }
            });

            yhzt=dialog.getWindow().findViewById(R.id.showdalog_yinhuandengji_yhzt);
            yhzt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GetYHZT();
                }
            });

            final EditText dalogedit=dialog.getWindow().findViewById(R.id.showdalog_yinhuandengji_ssnr);

            dialog.getWindow().findViewById(R.id.showdalog_renyuananpaiadd_qdzp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GetYinHuanList(dalogedit.getText().toString(),sendYHZT,begintime.getText().toString(),overtime.getText().toString());
                    dialog.dismiss();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    //隐患状态
    private void GetYHZT() {
        final String [] yhztTxT={"全部","处理中","已完成"};
        View view = getLayoutInflater().inflate(R.layout.dialog_one,
                null);
        final Dialog dialog = new Dialog(YinHuanDengJiActivity.this, R.style.MyUsualDialog);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        //window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        wl.gravity= Gravity.CENTER;
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        final NumberPicker numberPicker = dialog.getWindow().findViewById(R.id.dialog_npicker1);
        numberPicker.setWrapSelectorWheel(false);
        //设置需要显示的内容数组
        numberPicker.setDisplayedValues(null);
        numberPicker.setMaxValue(yhztTxT.length-1);
        numberPicker.setDisplayedValues(yhztTxT);
        numberPicker.setValue(0);
        //关闭编辑模式
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //分割线颜色
        setNumberPickerDividerColor(numberPicker);
        //设置滑动监听
        TextView onename=dialog.getWindow().findViewById(R.id.dialog_onename);
        onename.setText("选择状态");
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            //当NunberPicker的值发生改变时，将会激发该方法
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //  Toast.makeText(getActivity(), numbers[newVal], Toast.LENGTH_SHORT).show();
            }
        });
        dialog.getWindow().findViewById(R.id.dialog_oneno).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().findViewById(R.id.dialog_oneis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yhzt.setText(yhztTxT[numberPicker.getValue()]);
                if (yhzt.getText().toString().equals("全部")){
                    sendYHZT="";
                }
                if (yhzt.getText().toString().equals("处理中")){
                    sendYHZT="ChuLiZhongYH";
                }
                if (yhzt.getText().toString().equals("已完成")){
                    sendYHZT="WanChengYH";
                }
                dialog.dismiss();
            }
        });
    }

    private void setNumberPickerDividerColor(NumberPicker number) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(number, new ColorDrawable(ContextCompat.getColor(YinHuanDengJiActivity.this, R.color.colorPrimary)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    //删除
    private void Delete(String sno){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","KH_YinHuan_ShanChu_SP");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("sno",sno);
        try {
            DialogUitl.showProgressDialog(YinHuanDengJiActivity.this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(YinHuanDengJiActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    GetYinHuanList("",sendYHZT,begtime,endtime);
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

    private void showDialog(final String SNo) {
        View view = getLayoutInflater().inflate(R.layout.show_dalog_renyuananpai,
                null);
        final Dialog dialog = new Dialog(YinHuanDengJiActivity.this, R.style.MyUsualDialog);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        //window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        wl.gravity= Gravity.CENTER;
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        TextView message=dialog.getWindow().findViewById(R.id.message);
        message.setText("确认删除该隐患信息?");
        dialog.getWindow().findViewById(R.id.show_dalog_renyuananpai_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().findViewById(R.id.show_dalog_renyuananpai_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete(SNo);
                dialog.dismiss();
            }
        });
    }

    public void onRestart() {
        GetTime();
        GetYinHuanList("",sendYHZT,begtime,endtime);
        super.onRestart();
    }


}
