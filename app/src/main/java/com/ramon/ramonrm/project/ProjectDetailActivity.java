package com.ramon.ramonrm.project;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.google.android.material.tabs.TabLayout;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.RMFragmentAdapter;
import com.ramon.ramonrm.model.ProjFileInfo;
import com.ramon.ramonrm.model.ProjPlanInfo;
import com.ramon.ramonrm.project.fragment.ProjectInfoFragment;
import com.ramon.ramonrm.project.fragment.ProjectPlanFragment;
import com.ramon.ramonrm.project.fragment.ProjectRenYuanFragment;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private String mProjSNo;
    private String mProjType;
    private String[] mHeaders = new String[]{"任务明细", "人员安排", "任务节点"};
    private Fragment[]mFragments = new Fragment[3];
    private TabLayout tlayHeader;
    private ViewPager vpFragment;
    private RMFragmentAdapter rmAdapter;
    private List<ProjFileInfo> mProjFiles = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectdetail);
        mProjSNo = getIntent().getStringExtra("ProjSNo");
        mProjType = getIntent().getStringExtra("ProjType");
        loadFile();
    }

    private void initView() {
        btnBack = findViewById(R.id.activity_projectdetail_imgback);
        btnBack.setOnClickListener(this);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        tlayHeader = findViewById(R.id.activity_projectdetail_tablay);
        vpFragment = findViewById(R.id.activity_projectdetail_viewpager);
        mFragments[0] = new ProjectInfoFragment(mProjSNo,mProjType);
        mFragments[1] = new ProjectRenYuanFragment(mProjSNo);
        mFragments[2] = new ProjectPlanFragment(mProjSNo,mProjFiles);
        rmAdapter = new RMFragmentAdapter(getSupportFragmentManager());
        rmAdapter.setDatas(mFragments, mHeaders);
        vpFragment.setAdapter(rmAdapter);
        vpFragment.setOffscreenPageLimit(0);
        tlayHeader.setupWithViewPager(vpFragment);
        if (getIntent().getStringExtra("ZPorJD").equals("ZP")){
            vpFragment.setCurrentItem(1);
        }else if (getIntent().getStringExtra("ZPorJD").equals("JD")){
            vpFragment.setCurrentItem(2);
        }else if (getIntent().getStringExtra("ZPorJD").equals("DJ")){

        }
    }

    @Override
    public void onClick(View v) {
        try {
            int vId = v.getId();
            if (vId == R.id.activity_projectdetail_imgback) {
                AppManagerUtil.instance().finishActivity(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    private void loadFile(){
            ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
            reqData.ExtParams.put("spName", "RW_GCProjFile_LieBiao_SP");
            reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
            reqData.ExtParams.put("projSNo", mProjSNo);
            reqData.ExtParams.put("orderStr", "CJSJ");
            reqData.ExtParams.put("outParams","recordtotal");
            reqData.ExtParams.put("recordtotal","1");
            try {
                DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
                VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                        new VolleyListenerInterface(context, reqData) {
                            @Override
                            public void onMySuccess(ReqData reqData, String result) {
                                try {
                                    ResData resData = GsonUtils.fromJson(result, ResData.class);

                                    if (resData.RstValue == 0) {
                                        mProjFiles = new ArrayList<>();
                                        for(int i=0;i<resData.DataTable.length;i++) {

                                            HashMap<String, String> hashMap = resData.DataTable[i];
                                            ProjFileInfo fInfo = new ProjFileInfo();
                                            fInfo.DataSNo = hashMap.get("DataSNo");
                                            fInfo.EmpSNo = hashMap.get("EmpSNo");
                                            fInfo.FileExt = hashMap.get("FileExt");
                                            fInfo.FileName = hashMap.get("FileName");
                                            fInfo.FilePath = hashMap.get("FilePath");
                                            //fInfo.FileSize = Integer.parseInt(hashMap.get("FileSize"));
                                            fInfo.PlanSNo = hashMap.get("PlanSNo");
                                            fInfo.ProjSNo = hashMap.get("ProjSNo");
                                            fInfo.SNo = hashMap.get("SNo");
                                            fInfo.TaskSNo = hashMap.get("TaskSNo");
                                            fInfo.UploadDT = hashMap.get("UploadDT");
                                            mProjFiles.add(fInfo);
                                        }
                                        initView();
                                    } else {
                                        MethodUtil.showToast(resData.RstMsg, context);
                                    }
                                } catch (Exception ex) {
                                    MethodUtil.showToast(ex.getMessage(), context);
                                    Log.e("fwwwweqe",ex.getMessage());
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
                MethodUtil.showToast(e.getMessage(), context);
            }
        }
}
