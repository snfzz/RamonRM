package com.ramon.ramonrm.project.fragment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.InstallationTaskAdapter;
import com.ramon.ramonrm.listclass.AllListClass;
import com.ramon.ramonrm.model.InstallationTaskinfo;
import com.ramon.ramonrm.model.ProjectInfo;
import com.ramon.ramonrm.project.AnZhuangProjectActivity;
import com.ramon.ramonrm.project.ProjectDetailActivity;
import com.ramon.ramonrm.project.RenWuAuditActivity;
import com.ramon.ramonrm.renyuan.RenWuDiaryActivity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstallationTaskActivity extends BaseActivity implements View.OnClickListener {
    private int tag=-1;//用于记录当前点击的位置
    private ImageButton imgbtnback;
    private ImageView btnsearch;
    private GridView gridView;
    private List<Map<String,Object>> gridlist;//存放名称与图片的集合
    private SimpleAdapter simpleAdapter;//适配器
    private String [] gridname={"区域指派","人员指派","进度跟踪","任务审核","任务日志"};//gridview内容下标名称
    private int []gridphoto={R.mipmap.qyzp,R.mipmap.ryzp,R.mipmap.jdgz,R.mipmap.wjlb,R.mipmap.gzrz};//gridview下标图片
    private String [] griddm={"qyzp","ryzp","jdgz","rwsh","rwrz"};
    private ListView listview;
    private InstallationTaskAdapter installationTaskAdapter;
    private List <AllListClass> allListClass;//test
    private ImageView imgcenter;
    private EditText editname;
    private String status="";
    private String[] resno;
    private String[] regionEmpID;
    //用于跳转到任务日志界面后显示顶端公司名称与产品
    private String [] titl1;
    private String [] titl2;
    private String [] ProjSNo;
    private String [] ProjType;
    InstallationTaskinfo installationTaskinfo=new InstallationTaskinfo();

    private String [] QYJLname;
    private String qyjlname="";
    private String [] renwuID;
    private String [] QYJLSNo;
    private String qyjlsno="";
    private String [] QYJLempreg;
    private String qyjlempreg;
    //安装任务
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installationtask);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//防止软键盘弹出
        initView();
        LoadGridView();
        GetQYJL();
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(InstallationTaskActivity.this, ProjectDetailActivity.class);
                intent.putExtra("ProjSNo", ProjSNo[i]);
                intent.putExtra("ProjType",ProjType[i]);
                intent.putExtra("ZPorJD","DJ");
                startActivity(intent);
                return true;
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                installationTaskAdapter.setCurrentItem(i);
                //通知ListView改变状态
                installationTaskAdapter.notifyDataSetChanged();
                tag=i;
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (tag !=-1) {
                    installationTaskinfo.RenWuBH=resno[tag];
                    installationTaskinfo.RegionEmpID=regionEmpID[tag];
                    installationTaskinfo.Title1=titl1[tag];
                    installationTaskinfo.Title2=titl2[tag];
                    Intent intent;
                    switch (i) {
                        case 0:
                            qyjlempreg="";
                            qyjlname="";
                            qyjlsno="";
                            QYZPShowDalog();
                            break;
                        case 1:
                            intent = new Intent(InstallationTaskActivity.this, ProjectDetailActivity.class);
                            intent.putExtra("ProjSNo", ProjSNo[tag]);
                            intent.putExtra("ProjType",ProjType[tag]);
                            intent.putExtra("ZPorJD","ZP");
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(InstallationTaskActivity.this, ProjectDetailActivity.class);
                            intent.putExtra("ProjSNo", ProjSNo[tag]);
                            intent.putExtra("ProjType",ProjType[tag]);
                            intent.putExtra("ZPorJD","JD");
                            startActivity(intent);
                            break;
                        case 3:
                            intent=new Intent(InstallationTaskActivity.this, RenWuAuditActivity.class);
                            intent.putExtra("judge","nonull");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(intent);
                            break;
                        case 4:
                            intent=new Intent(InstallationTaskActivity.this, RenWuDiaryActivity.class);
                            startActivity(intent);
                            break;
                        default:
                            break;
                    }
                }else {
                    Toast.makeText(InstallationTaskActivity.this,"请选择一项任务",Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    private void initView(){
        imgbtnback = (ImageButton)findViewById(R.id.activity_installationtask_imgback);
        imgbtnback.setOnClickListener(this);
        gridView = (GridView)findViewById(R.id.activity_installationtask_gridview);
        listview=(ListView)findViewById(R.id.activity_installationtask_list);
        imgcenter=(ImageView)findViewById(R.id.activity_installationtask_down);
        imgcenter.setOnClickListener(this);
        editname=(EditText)findViewById(R.id.activity_installationtask_mingcheng);
        btnsearch=(ImageView) findViewById(R.id.activity_installationtask_btnsearch);
        btnsearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId();
        if (vId == R.id.activity_installationtask_imgback){
            AppManagerUtil.instance().finishActivity(InstallationTaskActivity.this);
        }
        if (vId == R.id.listview_installationtask_txt8){
            final int i= (int) view.getTag();
            Intent intent = new Intent(InstallationTaskActivity.this, ProjectDetailActivity.class);
            intent.putExtra("ProjSNo", ProjSNo[i]);
            intent.putExtra("ProjType",ProjType[i]);
            intent.putExtra("ZPorJD","DJ");
            startActivity(intent);
        }
        if (vId == R.id.activity_installationtask_down){
            showDialog();
        }
        if (vId == R.id.activity_installationtask_btnsearch){
            GetAnZhuangRW(editname.getText().toString());
        }
    }

    private void LoadGridView(){
        gridlist=new ArrayList<Map<String,Object>>();
        String [] from={"image","text"};
        int[] image = {R.id.func_menuitem_img, R.id.func_menuitem_txt};
        for (int i=0;i<gridname.length;i++){
            Map<String,Object>map=new HashMap<>();
            map.put("image",gridphoto[i]);
            map.put("text",gridname[i]);
            map.put("code",griddm[i]);
            gridlist.add(map);
        }
        simpleAdapter=new SimpleAdapter(InstallationTaskActivity.this,gridlist,R.layout.installationtask_menuitem,from,image);
        gridView.setAdapter(simpleAdapter);
    }


    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.showdalog_installationtask,
                null);
        final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
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
        dialog.getWindow().findViewById(R.id.showdalog_t1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status="";
                dialog.hide();
                GetAnZhuangRW("");
            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_t2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status="QuYuZP";
                dialog.hide();
                GetAnZhuangRW("");
            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_t3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status="WanCheng";
                dialog.hide();
                GetAnZhuangRW("");
            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_t4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status="ZhiXingZhong";
                dialog.hide();
                GetAnZhuangRW("");
            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
    }

    private void GetAnZhuangRW(String keyword){
        String key1="";
        String key2="";
        if (vd(keyword)==true){
            key1=keyword;
            key2="";
        }else {
            key1="";
            key2=keyword;
        }
        allListClass=new ArrayList<>();

        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable", Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RW_GCProj_LieBiao_SP");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        reqData.ExtParams.put("renwuLB","AZRW");
        reqData.ExtParams.put("KeHuMC",key1);
        reqData.ExtParams.put("status",status);
        reqData.ExtParams.put("keyword",key2);
        reqData.ExtParams.put("recordTotal","99");
        reqData.ExtParams.put("pageIndex","1");
        try {
            DialogUitl.showProgressDialog(InstallationTaskActivity.this, reqData.CmdID, "正在获取数据");
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(InstallationTaskActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try{
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    loadTaskView(resData.DataTable);//参数传递
                                    resno=new String[resData.DataTable.length];
                                    regionEmpID=new String[resData.DataTable.length];
                                    titl1=new String[resData.DataTable.length];
                                    titl2=new String[resData.DataTable.length];
                                    renwuID=new String[resData.DataTable.length];
                                    if (resData.DataTable.length==0){
                                        Toast.makeText(InstallationTaskActivity.this,"暂无数据",Toast.LENGTH_LONG).show();
                                    }else {
                                        for (int i = 0; i < resData.DataTable.length; i++) {
                                            installationTaskinfo.HTH = resData.DataTable[i].get("HTH");
                                            installationTaskinfo.KeHuMC = resData.DataTable[i].get("KeHuMC");
                                            installationTaskinfo.ChanPinMC = resData.DataTable[i].get("ChanPinMC");
                                            String QYJL = resData.DataTable[i].get("XingMing") + "-" + resData.DataTable[i].get("RegionName");
                                            if (resData.DataTable[i].get("XingMing")==null){
                                                QYJL="-";
                                            }
                                            String RenWuStatus = resData.DataTable[i].get("RenWuStatus");
                                            installationTaskinfo.SUM_Total = resData.DataTable[i].get("SUM_Total");
                                            installationTaskinfo.SUM_WanCheng = resData.DataTable[i].get("SUM_WanCheng");
                                            String JDTJ = "完成数:" + installationTaskinfo.SUM_WanCheng + " 总数:" + installationTaskinfo.SUM_Total;
                                            installationTaskinfo.Name = resData.DataTable[i].get("Name");
                                            titl1[i]=resData.DataTable[i].get("KeHuMC");
                                            titl2[i]=resData.DataTable[i].get("ChanPinMC");
                                            resno[i]=resData.DataTable[i].get("RenWuBH");
                                            regionEmpID[i]=resData.DataTable[i].get("RegionEmpID");
                                            renwuID[i]=resData.DataTable[i].get("SNo");
                                            allListClass.add(new AllListClass(installationTaskinfo.HTH,installationTaskinfo.KeHuMC,installationTaskinfo.ChanPinMC, QYJL, RenWuStatus,JDTJ,installationTaskinfo.Name));
                                        }
                                    }
                                    installationTaskAdapter=new InstallationTaskAdapter(InstallationTaskActivity.this,allListClass);
                                    listview.setAdapter(installationTaskAdapter);
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

    private void loadTaskView(HashMap<String, String>[] hashData) {
        ProjSNo=new String[hashData.length];
        ProjType=new String[hashData.length];
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
            ProjSNo[i] = hashMap.get("SNo").trim();
            ProjType[i]= hashMap.get("TaskType").trim();
            projectInfo.XingMing = hashMap.get("XingMing") == null?"":hashMap.get("XingMing").trim();
        }
    }

    //判断搜索合同号还是公司名称,公司名称返回true,合同号返回fales
    private boolean vd(String str){
        char[] chars=str.toCharArray();
        boolean isGB2312=false;
        for(int i=0;i<chars.length;i++){
            byte[] bytes=(""+chars[i]).getBytes();
            if(bytes.length==2){
                int[] ints=new int[2];
                ints[0]=bytes[0]& 0xff;
                ints[1]=bytes[1]& 0xff;

                if(ints[0]>=0x81 && ints[0]<=0xFE &&
                        ints[1]>=0x40 && ints[1]<=0xFE){
                    isGB2312=true;
                    break;
                }
            }
        }
        return isGB2312;
    }

    private void QYZPShowDalog() {
        View view = getLayoutInflater().inflate(R.layout.showdalog_qyzp,
                null);
        final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
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
        dialog.getWindow().findViewById(R.id.showdalog_qyzp_miss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextView hthtxt=dialog.getWindow().findViewById(R.id.showdalog_qyzp_hth);
        TextView gettxt1=listview.getChildAt(tag - listview.getFirstVisiblePosition()).findViewById(R.id.listview_installationtask_txt1);
        hthtxt.setText(gettxt1.getText().toString());
        final TextView qyjl=dialog.getWindow().findViewById(R.id.showdalog_qyzp_qyjl);
        qyjl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowSelect(qyjl);
            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_qyzp_qdzp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SureToAssign(qyjlsno,qyjlempreg);
            }
        });
    }

    //任务类型
    private void ShowSelect(final TextView textView) {
        View view = getLayoutInflater().inflate(R.layout.dialog_one,
                null);
        final Dialog dialog = new Dialog(InstallationTaskActivity.this, R.style.MyUsualDialog);
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
        numberPicker.setMaxValue(QYJLname.length-1);
        numberPicker.setDisplayedValues(QYJLname);
        numberPicker.setValue(0);
        //关闭编辑模式
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //设置滑动监听
        TextView onename=dialog.getWindow().findViewById(R.id.dialog_onename);
        onename.setText("区域指派");
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
                textView.setText(QYJLname[numberPicker.getValue()]);
                qyjlname=QYJLname[numberPicker.getValue()];
                qyjlempreg=QYJLempreg[numberPicker.getValue()];
                qyjlsno=QYJLSNo[numberPicker.getValue()];
                dialog.dismiss();
            }
        });
    }

    private void GetQYJL(){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecForTable",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RS_Staff_GetQuYuJL_SP");
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        try {
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(InstallationTaskActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try{
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){
                                    QYJLname=new String[resData.DataTable.length];
                                    QYJLSNo=new String[resData.DataTable.length];
                                    QYJLempreg=new String[resData.DataTable.length];
                                    for (int i=0;i<resData.DataTable.length;i++){
                                        String Name=resData.DataTable[i].get("Name");
                                        String EmpReg=resData.DataTable[i].get("EmpReg");
                                        QYJLname[i]=Name+"-"+EmpReg;
                                        QYJLSNo[i]=resData.DataTable[i].get("SNo");
                                        QYJLempreg[i]=resData.DataTable[i].get("EmpReg");
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

    //确定指派
    private void SureToAssign(String regionEmpID,String regionName){
        ReqData reqData=new ReqData("SQLExec","BizSql","SPExecNoRtn",Session.SessionId,Session.ValidMD5);
        reqData.ExtParams.put("spName","RW_GCProj_ZhiPai_SP");
        reqData.ExtParams.put("renwuID",renwuID[tag]);
        reqData.ExtParams.put("regionEmpID",regionEmpID);
        reqData.ExtParams.put("regionName",regionName);
        reqData.ExtParams.put("cjSNo",Session.CurrUser.YongHuSNo);
        try {
            VolleyRequestUtil.RequestPost(context, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(InstallationTaskActivity.this,reqData) {
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try{
                                ResData resData= GsonUtils.fromJson(result,ResData.class);
                                if (resData.RstValue == 0){

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
