package com.ramon.ramonrm.home.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.SaiBangTJ.SaiBangTJActivity;
import com.ramon.ramonrm.SaiBangTJ.SaiBangTJBarChartActivity;
import com.ramon.ramonrm.project.AnZhuangProjectActivity;
import com.ramon.ramonrm.project.RenWuAuditActivity;
import com.ramon.ramonrm.project.fragment.InstallationTaskActivity;
import com.ramon.ramonrm.renyuan.BeiJianGuanliActivity;
import com.ramon.ramonrm.renyuan.FunctionalCollection;
import com.ramon.ramonrm.renyuan.KaoQinDKActivity;
import com.ramon.ramonrm.renyuan.PowerAdministrationActivity;
import com.ramon.ramonrm.renyuan.WorkMsgActivity;
import com.ramon.ramonrm.renyuan.YinHuanDengJiActivity;
import com.ramon.ramonrm.video.JiShuZCActivity;
import com.ramon.ramonrm.weihuproject.WeiHuProjectActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuncFragment extends Fragment {
    //第一部分GridView,日常工作
    private GridView gvFuncpage1;
    private List<Map<String,Object>> funList1;
    private SimpleAdapter simpleAdapter1;
    private int icon1 []={R.mipmap.azrw,R.mipmap.whrw,
            R.mipmap.bjgl,R.mipmap.gzrz,R.mipmap.pt_myblue,R.mipmap.gzrz,R.mipmap.alk};
    private String funtext1[]={"安装任务","维护任务","备件管理","工作日志","任务审核","隐患登记","塞棒统计"};
    private String[] strFuncs1 = {"azrw", "whrw", "bjgl", "gzrz","rwsh","yhdj","sbtj"};
    //第2部分GridView,常用工具
    private GridView gvFuncpage2;
    private List<Map<String,Object>> funList2;
    private SimpleAdapter simpleAdapter2;
    private int icon2 []={R.mipmap.jszc,R.mipmap.kqdk,
            R.mipmap.gnsc,R.mipmap.qxgl};
    private String funtext2[]={"技术支持","考勤打卡","功能收藏","权限管理"};
    private String[] strFuncs2 = {"jszc", "kqdk", "gnsc", "qxgl"};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //不同的Activity对应不同的布局
        View view = inflater.inflate(R.layout.fragment_funcpage, container, false);
        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**
         * TODO 实现底部菜单对应布局控件事件
         * */
        init();
        loadGridView1();
        loadGridView2();
    }

    private void init() {
        gvFuncpage1=getActivity().findViewById(R.id.fragment_funcpage_gridviewrcgz);
        funList1=new ArrayList<Map<String, Object>>();

        gvFuncpage2=getActivity().findViewById(R.id.fragment_funcpage_gridviewcygj);
        funList2=new ArrayList<Map<String, Object>>();
    }

    private void loadGridView1(){
        //加载适配器
        String[] form = {"image", "text"};
        int[] image = {R.id.func_menuitem_img, R.id.func_menuitem_txt};

        for (int i = 0; i < icon1.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon1[i]);
            map.put("text", funtext1[i]);
            map.put("code",strFuncs1[i]);
            funList1.add(map);
        }

        simpleAdapter1=new SimpleAdapter(getActivity(),funList1,R.layout.func_menuitem,form,image);
        gvFuncpage1.setAdapter(simpleAdapter1);
        gvFuncpage1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> funcMap = (Map<String, Object>) parent.getAdapter().getItem(position);
                String funcCode = funcMap.get("code").toString().toLowerCase();
                Intent intent;
                switch (funcCode)
                {
                    case "azrw":
                        intent=new Intent(getActivity(), InstallationTaskActivity.class);
                        startActivity(intent);
                        break;

                    case "whrw":
                        intent=new Intent(getActivity(), WeiHuProjectActivity.class);
                        startActivity(intent);
                        break;

                    case "bjgl":
                        intent=new Intent(getActivity(), BeiJianGuanliActivity.class);
                        startActivity(intent);
                        break;

                    case "gzrz":
                        intent = new Intent(getActivity(), WorkMsgActivity.class);
                        getActivity().startActivity(intent);
                        break;

                    case "rwsh":
                        intent=new Intent(getActivity(), RenWuAuditActivity.class);
                        intent.putExtra("judge","null");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                        break;

                    case "yhdj":
                        intent=new Intent(getActivity(), YinHuanDengJiActivity.class);
                        startActivity(intent);
                        break;

                    case "sbtj":
                        intent=new Intent(getActivity(), SaiBangTJBarChartActivity.class);
                        startActivity(intent);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void loadGridView2(){
        //加载适配器
        String[] form = {"image", "text"};
        int[] image = {R.id.func_menuitem_img, R.id.func_menuitem_txt};

        for (int i = 0; i < icon2.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon2[i]);
            map.put("text", funtext2[i]);
            map.put("code",strFuncs2[i]);
            funList2.add(map);
        }

        simpleAdapter2=new SimpleAdapter(getActivity(),funList2,R.layout.func_menuitem,form,image);
        gvFuncpage2.setAdapter(simpleAdapter2);
        gvFuncpage2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> funcMap = (Map<String, Object>) parent.getAdapter().getItem(position);
                String funcCode = funcMap.get("code").toString().toLowerCase();
                Intent intent;
                switch (funcCode)
                {
                    case "jszc":
                        intent= new Intent(getActivity(), JiShuZCActivity.class);
                        getActivity().startActivity(intent);
                        break;

                    case "kqdk":
                        intent= new Intent(getActivity(), KaoQinDKActivity.class);
                        getActivity().startActivity(intent);
                        break;

                    case "gnsc":
                        intent=new Intent(getActivity(), FunctionalCollection.class);
                        startActivity(intent);
                        break;

                    case "qxgl":
                        intent=new Intent(getActivity(), PowerAdministrationActivity.class);
                        startActivity(intent);
                        break;

                    default:
                        break;
                }
            }
        });
    }
}
