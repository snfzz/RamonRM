package com.ramon.ramonrm.project.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.github.abel533.echarts.code.X;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.ShenQingDialog.base.Config;
import com.ramon.ramonrm.adapter.ProjPlanAdapter;
import com.ramon.ramonrm.model.ProjFileInfo;
import com.ramon.ramonrm.model.ProjPlanInfo;
import com.ramon.ramonrm.model.XinXiTianXieinfo;
import com.ramon.ramonrm.project.XinXiTianXieActivity;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectPlanFragment extends Fragment implements View.OnClickListener {

    private Activity mActivity;
    private String mProjSNo = "";
    private List<ProjFileInfo>mProjFiles;

    private ListView lvPlan;
    private String [] txt1;
    private String [] txt2;
    private String [] txt3;
    private String [] txt4;
    private String [] plansno;
    public ProjectPlanFragment(String projSNo, List<ProjFileInfo>listFile) {
        mProjSNo = projSNo;
        mProjFiles= listFile;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //不同的Activity对应不同的布局
        View view = inflater.inflate(R.layout.fragment_projectplan, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        initView();
        loadPlanData();
        getDeviceDensity();

    }

    private void initView() {
        lvPlan = mActivity.findViewById(R.id.fragment_projectplan_lvplan);
    }

    private void loadPlanData() {
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "RW_GCProjPlan_LieBiao_SP");
        reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("renwuSNo", mProjSNo);
        reqData.ExtParams.put("orderStr", "CJSJ");
        reqData.ExtParams.put("outParams","recordtotal");
        reqData.ExtParams.put("recordtotal","1");
        try {
            DialogUitl.showProgressDialog(mActivity, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(mActivity, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                Log.e("fwwwweqe",result);
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadPlanDataView(resData.DataTable);
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

    private void loadPlanDataView(HashMap<String,String>[]hashData) {
        List<ProjPlanInfo> listPlan = new ArrayList<>();
        txt1=new String[hashData.length];
        txt2=new String[hashData.length];
        txt3=new String[hashData.length];
        txt4=new String[hashData.length];
        plansno=new String[hashData.length];
        for (int i = 0; i < hashData.length; i++) {
            HashMap<String, String> hashMap = hashData[i];
            ProjPlanInfo planInfo = new ProjPlanInfo();
            txt1[i]=hashMap.get("PhaseData");
            txt2[i]=hashMap.get("Content");
            txt3[i]=hashMap.get("PhaseTarget");
            txt4[i]=hashMap.get("PhaseTime");
            plansno[i]=hashMap.get("SNo");

            planInfo.Content = hashMap.get("Content");
            planInfo.FileNum = Integer.parseInt(hashMap.get("FileNum"));
            planInfo.PhaseData = hashMap.get("PhaseData");
            planInfo.PhaseModelSNo = hashMap.get("PhaseModelSNo");
            planInfo.PhaseName = hashMap.get("PhaseName");
            planInfo.PhaseTarget = hashMap.get("PhaseTarget");
            planInfo.PhaseTime = hashMap.get("PhaseTime");
            planInfo.ProjSNo = hashMap.get("ProjSNo");
            planInfo.SNo = hashMap.get("SNo");
            planInfo.Status = hashMap.get("Status");
            planInfo.StatusTitle = hashMap.get("StatusTitle");
            planInfo.FileNames = new String[planInfo.FileNum];
            planInfo.FileUrls = new String[planInfo.FileNum];
            int index = 0;
            for(int j=0;j<mProjFiles.size();j++) {
                if (mProjFiles.get(j).PlanSNo.equals(planInfo.SNo)) {
                    if (index < planInfo.FileNum) {
                        planInfo.FileNames[index] = mProjFiles.get(j).FileName;
                        planInfo.FileUrls[index] = APIConfig.APIHOST + "/" + mProjFiles.get(j).FilePath + "/" + mProjFiles.get(j).FileName;
                        index++;
                    }
                }
            }
            listPlan.add(planInfo);
        }
        ProjPlanAdapter adapter = new ProjPlanAdapter(mActivity, R.layout.listitem_projplan, listPlan,this);
        lvPlan.setAdapter(adapter);
    }
    protected void getDeviceDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Config.EXACT_SCREEN_HEIGHT = metrics.heightPixels;
        Config.EXACT_SCREEN_WIDTH = metrics.widthPixels;
    }

    @Override
    public void onClick(View view) {
        int vId=view.getId();
        if (vId == R.id.listitem_projplan_imghelp){
            final int i= (int) view.getTag();
            ShowWenHao(i);
        }
        if (vId == R.id.listitem_projplan_btnsubmit){
            final int i= (int) view.getTag();
            XinXiTianXieinfo xinXiTianXieinfo=new XinXiTianXieinfo();
            xinXiTianXieinfo.PlanSNo=plansno[i];
            Intent intent=new Intent(getActivity(), XinXiTianXieActivity.class);
            startActivity(intent);
        }
    }

    private void ShowWenHao(int i){
        View view = getLayoutInflater().inflate(R.layout.showdalog_renwuwenhao,
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
        TextView txtview1=dialog.getWindow().findViewById(R.id.showdalog_renwuwenhao_txt1);
        TextView txtview2=dialog.getWindow().findViewById(R.id.showdalog_renwuwenhao_txt2);
        TextView txtview3=dialog.getWindow().findViewById(R.id.showdalog_renwuwenhao_txt3);
        TextView txtview4=dialog.getWindow().findViewById(R.id.showdalog_renwuwenhao_txt4);
        txtview1.setText(txt1[i]);
        txtview2.setText(txt2[i]);
        txtview3.setText(txt3[i]);
        txtview4.setText(txt4[i]);
        dialog.getWindow().findViewById(R.id.showdalog_renwuwenhao_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
