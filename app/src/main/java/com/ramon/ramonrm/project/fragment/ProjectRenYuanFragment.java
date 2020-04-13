package com.ramon.ramonrm.project.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.DialogOneAdapter;
import com.ramon.ramonrm.adapter.ProjTaskAdapter;
import com.ramon.ramonrm.controls.PickerMultiColumnDialog;
import com.ramon.ramonrm.model.PickerMultiColumnData;
import com.ramon.ramonrm.model.ProjFileInfo;
import com.ramon.ramonrm.model.ProjTaskInfo;
import com.ramon.ramonrm.project.ProjectDetailActivity;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyErrorHelper;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ProjectRenYuanFragment extends Fragment implements View.OnClickListener {
    private Calendar calBegin = Calendar.getInstance(Locale.CHINA);
    private Calendar calEnd = Calendar.getInstance(Locale.CHINA);

    private Activity mActivity;
    private String mProjSNo = "";
    private ListView lvRenYuan;

    private ImageView btnAdd;
    private List<ProjTaskInfo> listTask = new ArrayList<>();
    public ProjectRenYuanFragment(String projSNo) {
        mProjSNo = projSNo;
    }

    private String [] RY;//人员
    private String [] RWLX={"钳工","电气","学习"};//任务类型
    private List<String> list;
    private String [] QY;//
    private String [] YGSNO;
    private String ygnum="";
    private boolean fzrbolean=true;

    private TextView xzqydlg;
    private TextView xzrydlg;
    private TextView rwlxdlg;
    private TextView begtime;
    private TextView overtime;

    private String SearchName="";//用于标记选择人员的搜索
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //不同的Activity对应不同的布局
        View view = inflater.inflate(R.layout.fragment_projectrenyuan, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        initView();
        loadRenYuanData();
        GetXZQU();
    }

    @Override
    public void onClick(View v) {
        try {
            int vId = v.getId();
            if (vId == R.id.fragment_projectrenyuan_btnadd) {
                showDialogAdd();
            }
            if (vId == R.id.listitem_projtask_btndelete){
                int i= (int) v.getTag();
                showDialog(listTask.get(i).SNo);

            }
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), mActivity);
        }
    }

    private void initView() {
        lvRenYuan = mActivity.findViewById(R.id.fragment_projectrenyuan_lvrenyuan);
        btnAdd = mActivity.findViewById(R.id.fragment_projectrenyuan_btnadd);
        btnAdd.setOnClickListener(this);
    }

    private void loadRenYuanData() {
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "RW_GCTask_LieBiao_SP");
        reqData.ExtParams.put("orderStr", "CJSJ");
        reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("renwuSNo", mProjSNo);
        reqData.ExtParams.put("outParams", "recordTotal");
        reqData.ExtParams.put("recordTotal", "1");
        try {
            DialogUitl.showProgressDialog(mActivity, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(mActivity, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadRenYuanView(resData.DataTable);
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, mActivity);
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), mActivity);
                            } finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog(reqData.CmdID);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), mActivity);
        }
    }

    private void loadRenYuanView(HashMap<String, String>[] hashData) {

        for (int i = 0; i < hashData.length; i++) {
            HashMap<String, String> hashMap = hashData[i];
            ProjTaskInfo projTask = new ProjTaskInfo();
            projTask.AssDT = hashMap.get("AssDT");
            projTask.SNo = hashMap.get("SNo");
            projTask.ReqFinishDT = hashMap.get("ReqFinishDT");
            projTask.ReqArriveDT = hashMap.get("ReqArriveDT");
            projTask.XingMing = hashMap.get("XingMing");
            projTask.DeptSNames = hashMap.get("DeptSNames");
            projTask.MainEmp = hashMap.get("MainEmp");
            projTask.TaskType = hashMap.get("TaskType");
            projTask.TaskTypeTitle = hashMap.get("TaskTypeTitle");
            projTask.EmpSNo = hashMap.get("EmpSNo");
            projTask.Status = hashMap.get("Status");
            projTask.StatusTitle = hashMap.get("StatusTitle");
            projTask.ProjSNo = hashMap.get("ProjSNo");
            listTask.add(projTask);
        }
        ProjTaskAdapter adapter = new ProjTaskAdapter(mActivity, R.layout.listitem_projtask, listTask,ProjectRenYuanFragment.this);
        adapter.setOnItemClickListener(new ProjTaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object item, View view) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                    MethodUtil.showToast(e.getMessage(), mActivity);
                }
            }
        });
        lvRenYuan.setAdapter(adapter);
    }

    private void showDialog(final String SNo) {
        View view = getLayoutInflater().inflate(R.layout.show_dalog_renyuananpai,
                null);
        final Dialog dialog = new Dialog(getActivity(), R.style.MyUsualDialog);
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
        dialog.getWindow().findViewById(R.id.show_dalog_renyuananpai_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().findViewById(R.id.show_dalog_renyuananpai_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteStall(SNo);
                dialog.dismiss();
            }
        });
    }

    //删除参与人员
    private void DeleteStall(String sno){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("spName","RW_GCTask_ShanChu_SP");
        reqData.ExtParams.put("SNo",sno);
        try {
            DialogUitl.showProgressDialog(mActivity, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(getContext(), APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(getActivity(),reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    Log.e("fwwwq",result);
                                    if (resData.DataTable.length==0){

                                    }else {

                                    }
                                }else {
                                    MethodUtil.showToast(resData.RstMsg, getContext());
                                }
                            }catch (Exception e){
                                MethodUtil.showToast(e.getMessage(), getContext());
                            }finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog(reqData.CmdID);
                            MethodUtil.showToast(VolleyErrorHelper.getMessage(error, mContext), getContext());
                        }
                    });
        }catch (Exception e){
            MethodUtil.showToast(e.getMessage(), getContext());
        }
    }

    private void showDialogAdd() {
        try {
            View view = getLayoutInflater().inflate(R.layout.showdalog_renyuananpaiadd,
                    null);
            final Dialog dialog = new Dialog(getActivity(), R.style.MyUsualDialog);
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

            dialog.getWindow().findViewById(R.id.showdalog_renyuananpaiadd_miss).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });



            //要求到达
            begtime=dialog.getWindow().findViewById(R.id.showdalog_renyuananpaiadd_begtime);
            begtime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //region 开始日期
                    new DatePickerDialog(mActivity,
                            // 绑定监听器
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    String strYear = year + "";
                                    String strMonth = month < 9 ? "0" + (month + 1) : "" + (month + 1);
                                    String strDay = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                                    begtime.setText(strYear + "-" + strMonth + "-" + strDay);
                                    calBegin.set(year, month, dayOfMonth);
                                }
                            }
                            , calBegin.get(Calendar.YEAR)
                            , calBegin.get(Calendar.MONTH)
                            , calBegin.get(Calendar.DAY_OF_MONTH)).show();
                    //endregion
                }
            });

            //要求完成
            overtime=dialog.getWindow().findViewById(R.id.showdalog_renyuananpaiadd_overtime);
            overtime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //region 结束日期
                    new DatePickerDialog(mActivity,
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

            //主负责人
            RadioGroup showdalogradgroup=dialog.getWindow().findViewById(R.id.showdalog_renyuananpaiadd_group);
            final RadioButton radis=dialog.getWindow().findViewById(R.id.showdalog_renyuananpaiadd_groupis);
            final RadioButton radno=dialog.getWindow().findViewById(R.id.showdalog_renyuananpaiadd_groupno);
            showdalogradgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    int id=radioGroup.getCheckedRadioButtonId();
                    switch (id){
                        case R.id.showdalog_renyuananpaiadd_groupis:

                            fzrbolean=true;
                            break;
                        case R.id.showdalog_renyuananpaiadd_groupno:
                            fzrbolean=false;
                            break;
                    }
                }
            });

            //确定指派
            dialog.getWindow().findViewById(R.id.showdalog_renyuananpaiadd_qdzp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AnPaiRenYuan();
                }
            });

            //任务类型
            rwlxdlg=dialog.getWindow().findViewById(R.id.showdalog_renyuananpaiadd_rwlx);
            rwlxdlg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initNumberPicker3();
                }
            });

            //选择人员
            xzrydlg=dialog.getWindow().findViewById(R.id.showdalog_renyuananpaiadd_xzry);
            xzrydlg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initNumberPicker2();
                }
            });

            //选择区域
            xzqydlg=dialog.getWindow().findViewById(R.id.showdalog_renyuananpaiadd_xzqy);
            xzqydlg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initNumberPicker1();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), mActivity);
        }
    }

    //选择区域
    private void initNumberPicker1() {
        View view = getLayoutInflater().inflate(R.layout.dialog_one,
                null);
        final Dialog dialog = new Dialog(getActivity(), R.style.MyUsualDialog);
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
        numberPicker.setMaxValue(QY.length-1);
        numberPicker.setDisplayedValues(QY);
        numberPicker.setValue(0);
        //关闭编辑模式
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //分割线颜色
        setNumberPickerDividerColor(numberPicker);
        //设置滑动监听
        TextView onename=dialog.getWindow().findViewById(R.id.dialog_onename);
        onename.setText("选择区域");
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
                xzrydlg.setText("点击选择");
                xzqydlg.setText(QY[numberPicker.getValue()]);
                SearchName=xzqydlg.getText().toString();
                ygnum="";
                GetXZQU();
                dialog.dismiss();
            }
        });
    }

    //选择人员
    private void initNumberPicker2() {
        View view = getLayoutInflater().inflate(R.layout.dialog_one,
                null);
        final Dialog dialog = new Dialog(getActivity(), R.style.MyUsualDialog);
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
        numberPicker.setMaxValue(RY.length-1);
        numberPicker.setDisplayedValues(RY);
        numberPicker.setValue(0);
        //关闭编辑模式
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //分割线颜色
        setNumberPickerDividerColor(numberPicker);
        //设置滑动监听
        TextView onename=dialog.getWindow().findViewById(R.id.dialog_onename);
        onename.setText("选择人员");
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            //当NunberPicker的值发生改变时，将会激发该方法
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
               // Toast.makeText(getActivity(), numbers[newVal], Toast.LENGTH_SHORT).show();
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
                xzrydlg.setText(RY[numberPicker.getValue()]);
                ygnum=YGSNO[numberPicker.getValue()];
                dialog.dismiss();
            }
        });
    }

    //任务类型
    private void initNumberPicker3() {
        View view = getLayoutInflater().inflate(R.layout.dialog_one,
                null);
        final Dialog dialog = new Dialog(getActivity(), R.style.MyUsualDialog);
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
        numberPicker.setMaxValue(RWLX.length-1);
        numberPicker.setDisplayedValues(RWLX);
        numberPicker.setValue(0);
        //关闭编辑模式
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //分割线颜色
        setNumberPickerDividerColor(numberPicker);
        //设置滑动监听
        TextView onename=dialog.getWindow().findViewById(R.id.dialog_onename);
        onename.setText("任务类型");
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            //当NunberPicker的值发生改变时，将会激发该方法
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
               // Toast.makeText(getActivity(), numbers[newVal], Toast.LENGTH_SHORT).show();
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
                rwlxdlg.setText(RWLX[numberPicker.getValue()]);
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
                    pf.set(number, new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void  GetXZQU(){
        list=new ArrayList<>();
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RS_Staff_GetGCYuanGong_SP");
        reqData.ExtParams.put("quyu",SearchName);
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        try {
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(getActivity(),reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                RY=new String[resData.DataTable.length];
                                YGSNO=new String[resData.DataTable.length];
                                if (resData.RstValue == 0){
                                    for (int i=0;i<resData.DataTable.length;i++){
                                        String Name=resData.DataTable[i].get("Name");
                                        String EmpReg=resData.DataTable[i].get("EmpReg");
                                        YGSNO[i]=resData.DataTable[i].get("SNo");
                                        String ry=Name+"-"+EmpReg;
                                        RY[i]=ry;
                                        list.add(EmpReg);
                                    }
                                    if (SearchName.equals("")) {
                                        List<String> newList = new ArrayList<>();
                                        for (int i = 0; i < list.size(); i++) {
                                            boolean isContains = newList.contains(list.get(i));
                                            if (!isContains) {
                                                newList.add(list.get(i));
                                            }
                                        }
                                        list.clear();
                                        list.addAll(newList);
                                        QY = new String[list.size()];
                                        for (int num = 0; num < list.size(); num++) {
                                            QY[num] = list.get(num);
                                        }
                                    }
                                }else {
                                    MethodUtil.showToast(resData.RstMsg, getContext());
                                }
                            }catch (Exception e){
                                MethodUtil.showToast(e.getMessage(), mActivity);
                            }finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog(reqData.CmdID);
                            MethodUtil.showToast(VolleyErrorHelper.getMessage(error, mContext), getContext());
                        }
                    });
        }catch (Exception e){
            MethodUtil.showToast(e.getMessage(), getContext());
        }
    }

    private void AnPaiRenYuan(){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RW_GCProj_AnPai_SP");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("projSNo",mProjSNo);
        reqData.ExtParams.put("yuangongSNo",ygnum);

        if (rwlxdlg.getText().toString().equals("钳工")){
            reqData.ExtParams.put("taskType","qiangong");

        }else if (rwlxdlg.getText().toString().equals("电气")){
            reqData.ExtParams.put("taskType","dianqi");

        }else if (rwlxdlg.getText().toString().equals("学习")){
            reqData.ExtParams.put("taskType","xuexi");

        }
        if (fzrbolean==true){
            reqData.ExtParams.put("isMain","1");

        }else {
            reqData.ExtParams.put("isMain","0");

        }

        reqData.ExtParams.put("ReqArriveDT",begtime.getText().toString());
        reqData.ExtParams.put("ReqFinishDT",overtime.getText().toString());
        try {
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(getActivity(),reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){

                                }else {
                                    MethodUtil.showToast(resData.RstMsg, getContext());
                                }
                            }catch (Exception e){
                                MethodUtil.showToast(e.getMessage(), mActivity);
                            }finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        @Override
                        public void onMyError(ReqData reqData, VolleyError error) {
                            error.printStackTrace();
                            DialogUitl.dismissProgressDialog(reqData.CmdID);
                            MethodUtil.showToast(VolleyErrorHelper.getMessage(error, mContext), getContext());
                        }
                    });
        }catch (Exception e){
            MethodUtil.showToast(e.getMessage(), getContext());
        }
    }

}
