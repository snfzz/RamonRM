package com.ramon.ramonrm.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.AnZhuangProjectAdapter;
import com.ramon.ramonrm.model.ProjectInfo;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.PageReqData;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnZhuangProjectActivity extends BaseActivity implements View.OnClickListener {

    private String[] statusCode = new String[]{"", "QuYuZP", "WanCheng", "ZhiXingZhong"};
    private String[] statusTitle = new String[]{"全部任务", "指派区域", "完成", "执行中"};
    private String selectedStatus = "";

    private PageReqData taskReqData;
    private ImageButton imgBack;
    private ImageView imgDown, imgQuery;

    private EditText txtMingCheng;

    private ListView lvTask;
    private List<ProjectInfo> listTask;
    private AnZhuangProjectAdapter mTaskAdapter = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anzhuangproject);
        taskReqData = new PageReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable, Session.SessionId, Session.ValidMD5);
        listTask = new ArrayList<>();
        initView();
    }

    private void initView() {
        imgBack = findViewById(R.id.activity_anzhuangproject_imgback);
        imgBack.setOnClickListener(this);
        imgDown = findViewById(R.id.activity_anzhuangproject_imgdropdown);
        imgDown.setOnClickListener(this);
        imgQuery = findViewById(R.id.activity_anzhuangproject_btnsearch);
        imgQuery.setOnClickListener(this);

        txtMingCheng = findViewById(R.id.activity_anzhuangproject_txtmingcheng);
        lvTask = findViewById(R.id.activity_anzhuangproject_lvtask);
        //lvTask.setOnLoadListener(this);
        listTask = new ArrayList<>();
        mTaskAdapter = new AnZhuangProjectAdapter(context,R.layout.listitem_anzhuangproject,listTask);
        mTaskAdapter.setOnItemClickListener(new AnZhuangProjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object item, View view,int position) {
                try {
                    if (listTask != null) {
                        int oldIndex = -1;
                        int newIndex = position;
                        for (int i = 0; i < listTask.size(); i++) {
                            if(listTask.get(i).IsSelected){
                                oldIndex = i;
                            }
                            if (listTask.get(i).equals(item)) {
                                listTask.get(i).IsSelected = true;
                            } else {
                                listTask.get(i).IsSelected = false;
                            }
                        }
                        updateItem(oldIndex);
                        updateItem(newIndex);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MethodUtil.showToast(e.getMessage(), context);
                }
            }
        });
        mTaskAdapter.setOnItemLongClickListener(new AnZhuangProjectAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Object item, View view, int position) {
                try {
                    ProjectInfo projectInfo = (ProjectInfo) item;
                    Intent intent = new Intent(AnZhuangProjectActivity.this, ProjectDetailActivity.class);
                    intent.putExtra("ProjSNo", projectInfo.SNo);
                    intent.putExtra("ProjType",projectInfo.TaskType);
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    MethodUtil.showToast(ex.getMessage(), context);
                }
            }
        });
//        lvTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                try {
//                    if (listTask != null) {
//                        int oldIndex = -1;
//                        int newIndex = position;
//                        for (int i = 0; i < listTask.size(); i++) {
//                            if(listTask.get(i).IsSelected){
//                                oldIndex = i;
//                            }
//                            if (i==position) {
//                                listTask.get(i).IsSelected = true;
//                            } else {
//                                listTask.get(i).IsSelected = false;
//                            }
//                        }
//                        updateItem(oldIndex);
//                        updateItem(newIndex);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    MethodUtil.showToast(e.getMessage(), context);
//                }
//            }
//        });
        lvTask.setAdapter(mTaskAdapter);
    }
    private void updateItem(int position) {
        /**第一个可见的位置**/
        int firstVisiblePosition = lvTask.getFirstVisiblePosition();
        /**最后一个可见的位置**/
        int lastVisiblePosition = lvTask.getLastVisiblePosition();

        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            /**获取指定位置view对象**/
            View view = lvTask.getChildAt(position - firstVisiblePosition);
            mTaskAdapter.getView(position, view, lvTask);
        }

    }
    @Override
    public void onClick(View v) {
        try {
            MethodUtil.hideSoftInputFromActivity(this);
            int vId = v.getId();
            if (vId == R.id.activity_anzhuangproject_imgback) {
                AppManagerUtil.instance().finishActivity(this);
            }
            if (vId == R.id.activity_anzhuangproject_imgdropdown) {

            }
            if (vId == R.id.activity_anzhuangproject_btnsearch) {
                taskReqData.PageIndex = 1;
                listTask.clear();
                loadTaskData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), context);
        }
    }

    private void loadTaskData() {
        String input = txtMingCheng.getText().toString().trim();
        ReqData reqData = taskReqData.getReqData();
        reqData.ExtParams.put("spName", "RW_GCProj_LieBiao_SP");
        reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("renwuLB", "AZRW");
        reqData.ExtParams.put("keHuMC", "");
        reqData.ExtParams.put("keyword", input);
        reqData.ExtParams.put("status", selectedStatus);

        try {
            DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(context, reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadTaskView(resData.DataTable);
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, context);
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), context);
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

    private void loadTaskView(HashMap<String, String>[] hashData) {
        for (int i = 0; i < hashData.length; i++) {
            HashMap<String,String>hashMap = hashData[i];
            ProjectInfo projectInfo = new ProjectInfo();
            projectInfo.IsSelected = false;
            if(i == 0) projectInfo.IsSelected = true;
            projectInfo.SUM_Total = Integer.parseInt(hashMap.get("SUM_Total"));
            projectInfo.SUM_WanCheng = Integer.parseInt(hashMap.get("SUM_WanCheng"));
            projectInfo.SNo = hashMap.get("SNo").trim();
            projectInfo.ChanPinJC = hashMap.get("ChanPinJC").trim();
            projectInfo.ChanPinMC = hashMap.get("ChanPinMC").trim();
            projectInfo.HTH = hashMap.get("HTH").trim();
            projectInfo.KeHuJC = hashMap.get("KeHuJC").trim();
            projectInfo.KeHuMC = hashMap.get("KeHuMC").trim();
            projectInfo.Name = hashMap.get("Name").trim();
            projectInfo.PhaseName = hashMap.get("PhaseName").trim();
            projectInfo.PrdtCode = hashMap.get("PrdtCode").trim();
            projectInfo.RegionEmpID = hashMap.get("RegionEmpID").trim();
            projectInfo.RegionName = hashMap.get("RegionName").trim();
            projectInfo.RenWuStatus = hashMap.get("RenWuStatus").trim();
            projectInfo.TaskType = hashMap.get("TaskType").trim();
            projectInfo.XingMing = hashMap.get("XingMing") == null?"":hashMap.get("XingMing").trim();
            listTask.add(projectInfo);
        }
        lvTask.setAdapter(mTaskAdapter);
    }

//    public void onLoad(){
//        taskReqData.PageIndex = 1;
//        listTask.clear();
//        loadTaskData();
//    }
//
//    public void onPullLoad(){
//        taskReqData.PageIndex++;
//        loadTaskData();
//    }
}
