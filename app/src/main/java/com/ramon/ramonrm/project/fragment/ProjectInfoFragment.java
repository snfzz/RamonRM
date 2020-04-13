package com.ramon.ramonrm.project.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.draw.MultiLineDrawFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.style.LineStyle;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.model.PlanReqInfo;
import com.ramon.ramonrm.model.TableBean;
import com.ramon.ramonrm.model.XinXiTianXieinfo;
import com.ramon.ramonrm.project.PlanShenHeActivity;
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

public class ProjectInfoFragment extends Fragment {

    private Activity mActivity;
    private String mProjSNo = "";
    private String mProjType = "AZRW";

    private SmartTable sTabResult;
    private List<Column> sTabColumns;
    private List<TableBean> sTabDatas;

    private RelativeLayout rlayShenHe;

    private TextView lblHTH, lblKeHuMC, lblChanPinMC, lblQuYuJL, lblJieDianTJ, lblStatus, lblNeiRong;

    public ProjectInfoFragment(String projSNo, String projType) {
        mProjSNo = projSNo;
        mProjType = projType;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //不同的Activity对应不同的布局
        View view = inflater.inflate(R.layout.fragment_projectinfo, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        initView();
        loadProjectData();
        loadShenHeData();
    }

    private void initView() {
        lblHTH = mActivity.findViewById(R.id.fragment_projectinfo_lblhth);
        lblKeHuMC = mActivity.findViewById(R.id.fragment_projectinfo_lblkehumc);
        lblChanPinMC = mActivity.findViewById(R.id.fragment_projectinfo_lblchanpinmc);
        lblQuYuJL = mActivity.findViewById(R.id.fragment_projectinfo_lblquyujingli);
        lblJieDianTJ = mActivity.findViewById(R.id.fragment_projectinfo_lbljiedian);
        lblStatus = mActivity.findViewById(R.id.fragment_projectinfo_lblstatus);
        lblNeiRong = mActivity.findViewById(R.id.fragment_projectinfo_lblneirong);
        sTabResult = mActivity.findViewById(R.id.fragment_projectinfo_stabresult);
        rlayShenHe = mActivity.findViewById(R.id.fragment_projectinfo_rlayshenhe);
        sTabColumns = new ArrayList<>();
        sTabColumns.add(new Column<String>("名称", "Bean1"));
        sTabColumns.add(new Column<String>("审核", "Bean2"));
        sTabColumns.add(new Column<String>("复审", "Bean3"));
        for (int i = 0; i < sTabColumns.size(); i++) {
            sTabColumns.get(i).setOnColumnItemClickListener(new OnColumnItemClickListener() {
                @Override
                public void onClick(Column column, String value, Object o, int position) {
                    try {
                        if (position >= 0 && position < sTabDatas.size()) {
                            Object obj = sTabDatas.get(position).Tag;
                            Intent intent = new Intent(mActivity, PlanShenHeActivity.class);
                            intent.putExtra("PlanReqInfo", GsonUtils.toJson(obj));
                            startActivityForResult(intent, 10);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MethodUtil.showToast(e.getMessage(), mActivity);
                    }
                }
            });
        }
    }

    private void loadProjectData() {
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "RW_GCProj_LieBiao_SP");
        reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("renwuLB", mProjType);
        reqData.ExtParams.put("projSNo", mProjSNo);
        reqData.ExtParams.put("keyword", "");
        reqData.ExtParams.put("status", "");
        reqData.ExtParams.put("outParams","recordtotal");
        reqData.ExtParams.put("recordtotal","1");
        try {
            DialogUitl.showProgressDialog(mActivity, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(mActivity, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);

                                if (resData.RstValue == 0) {
                                    if (resData.DataTable.length > 0) {
                                        XinXiTianXieinfo xinXiTianXieinfo=new XinXiTianXieinfo();
                                        xinXiTianXieinfo.TaskType=resData.DataTable[0].get("TaskType");
                                        xinXiTianXieinfo.RegionEmpID=resData.DataTable[0].get("RegionEmpID");
                                        xinXiTianXieinfo.ProjSNo=resData.DataTable[0].get("SNo");


                                        HashMap<String, String> hashMap = resData.DataTable[0];
                                        lblHTH.setText(hashMap.get("HTH"));
                                        lblKeHuMC.setText(hashMap.get("KeHuMC"));
                                        lblChanPinMC.setText(hashMap.get("ChanPinMC"));
                                        String qyjlMC = hashMap.get("XingMing");
                                        String qyMC = hashMap.get("RegionName");
                                        lblQuYuJL.setText(qyjlMC + "-" + qyMC);
                                        int sum_Total = Integer.parseInt(hashMap.get("SUM_Total"));
                                        int sum_WanCheng = Integer.parseInt(hashMap.get("SUM_WanCheng"));
                                        lblJieDianTJ.setText("完成数：" + sum_WanCheng  + " 总数：" + sum_Total);
                                        String rwZT = "";
                                        if (sum_Total > 0 && sum_Total == sum_WanCheng) {
                                            rwZT = "完成";
                                        } else if (sum_Total == 0) {
                                            rwZT = "区域指派";
                                        } else {
                                            rwZT = "执行中";
                                        }
                                        lblStatus.setText(rwZT);
                                        lblNeiRong.setText(hashMap.get("Name"));
                                    }
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

    private void loadShenHeData(){
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName", "RW_GCProjPlanFshReq_LieBiao_SP");
        reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("projSNo", mProjSNo);
        reqData.ExtParams.put("outParams","recordtotal");
        reqData.ExtParams.put("recordtotal","1");
        try {
            DialogUitl.showProgressDialog(mActivity, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(mActivity, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                Log.e("fwwwweqe",result);
                                if (resData.RstValue == 0) {
                                    loadShenHeView(resData.DataTable);
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

    private void loadShenHeView(HashMap<String,String>[]hashData) {
        sTabDatas = new ArrayList<>();
        for (int i = 0; i < hashData.length; i++) {
            HashMap<String, String> hashMap = hashData[i];
            PlanReqInfo planReq = new PlanReqInfo();
            planReq.HTH = hashMap.get("HTH");
            planReq.SNo = hashMap.get("SNo");
            planReq.ReqDT = hashMap.get("ReqDT");
            planReq.ShenQingRenMC = hashMap.get("ShenQingRenMC");
            planReq.ShenHeRenMC = hashMap.get("ShenHeRenMC");
            planReq.FuShenHeRenMC = hashMap.get("FuShenHeRenMC");
            planReq.PlanSNo = hashMap.get("PlanSNo");
            planReq.ReAdtContent = hashMap.get("ReAdtContent");
            planReq.ReAdtRst = hashMap.get("ReAdtRst");
            planReq.ReAdtDT = hashMap.get("ReAdtDT");
            planReq.ReAdtEmpID = hashMap.get("ReAdtEmpID");
            planReq.ReqEmpID = hashMap.get("ReqEmpID");
            planReq.KeHuJC = hashMap.get("KeHuJC");
            planReq.KeHuMC = hashMap.get("KeHuMC");
            planReq.RegionName = hashMap.get("RegionName");
            planReq.ReqType = hashMap.get("ReqType");
            planReq.RWName = hashMap.get("RWName");
            planReq.AdtContent = hashMap.get("AdtContent");
            planReq.AdtRst = hashMap.get("AdtRst");
            planReq.AdtDT = hashMap.get("AdtDT");
            planReq.AdtEmpID = hashMap.get("AdtEmpID");
            planReq.ChanPinMC = hashMap.get("ChanPinMC");
            planReq.SQName = hashMap.get("SQName");
            planReq.ProjSNo = hashMap.get("ProjSNo");
            String adtRst = hashMap.get("AdtRst").toLowerCase().trim();
            String title = "未审核";
            if (adtRst.equals("tgshenhe")) {
                title = "通过";
            } else if (adtRst.equals("wtgshenhe")) {
                title = "未通过";
            }
            planReq.AdtRstTitle = title;
            adtRst = hashMap.get("ReAdtRst").toLowerCase().trim();
            title = "未审核";
            if (adtRst.equals("tgshenhe")) {
                title = "通过";
            } else if (adtRst.equals("wtgshenhe")) {
                title = "未通过";
            }
            planReq.ReAdtRstTitle = title;
            TableBean tBean = new TableBean();
            tBean.Bean1 = planReq.SQName;
            tBean.Bean2 = planReq.AdtRstTitle;
            tBean.Bean3 = planReq.ReAdtRstTitle;
            tBean.Bean4 = planReq.SNo;
            tBean.Tag = planReq;
            sTabDatas.add(tBean);
        }
        TableData<TableBean> tableData = new TableData<TableBean>("审核列表", sTabDatas, sTabColumns);
        sTabResult.getConfig().setColumnTitleStyle(new FontStyle(45, Color.parseColor("#4f94cd")));
        sTabResult.getConfig().setContentStyle(new FontStyle(45, Color.WHITE));
        sTabResult.getConfig().setContentGridStyle(new LineStyle(mActivity, 0, Color.parseColor("#FF2C2C2C")));
        sTabResult.getConfig().setColumnTitleGridStyle(new LineStyle(mActivity, 0, Color.parseColor("#FF2C2C2C")));
        sTabResult.getConfig().setShowYSequence(false);
        sTabResult.getConfig().setShowXSequence(false);
        sTabResult.getConfig().setShowTableTitle(false);
        sTabResult.setTableData(tableData);
        sTabResult.getConfig().setMinTableWidth(100);
        sTabResult.getConfig().setMinTableWidth(rlayShenHe.getWidth());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode){
                case 10:
                {
                    if(resultCode!=0){
                        loadShenHeData();
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MethodUtil.showToast(ex.getMessage(), mActivity);
        }
    }
}
