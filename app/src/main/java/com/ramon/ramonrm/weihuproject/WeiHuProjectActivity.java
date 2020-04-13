package com.ramon.ramonrm.weihuproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.project.fragment.InstallationTaskActivity;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeiHuProjectActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton imgbtn;
    private ImageView downimage;
    private GridView gridView;
    private List<Map<String,Object>> gridlist;//存放名称与图片的集合
    private SimpleAdapter simpleAdapter;//适配器
    private String [] gridname={"区域指派","人员指派","进度跟踪","任务审核","任务日志"};//gridview内容下标名称
    private int []gridphoto={R.mipmap.qyzp,R.mipmap.ryzp,R.mipmap.jdgz,R.mipmap.wjlb,R.mipmap.gzrz};//gridview下标图片
    private String [] griddm={"qyzp","ryzp","jdgz","rwsh","rwrz"};
    private int Tag=-1;//用于标记初始的被点击条目
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weihuproject);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//防止软键盘弹出
        initView();
        LoadGridView();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (Tag!=-1){
                    switch (i){
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        default:
                            break;
                    }
                }else {
                    Toast.makeText(WeiHuProjectActivity.this,"请选择一项任务",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initView(){
        imgbtn=findViewById(R.id.activity_weihuproject_imgback);
        imgbtn.setOnClickListener(this);
        downimage=findViewById(R.id.activity_weihuproject_down);
        downimage.setOnClickListener(this);
        gridView=findViewById(R.id.activity_weihuproject_gridview);
    }

    @Override
    public void onClick(View view) {
        int vId=view.getId();
        if (vId == R.id.activity_weihuproject_imgback){
            AppManagerUtil.instance().finishActivity(WeiHuProjectActivity.this);
        }
        if (vId == R.id.activity_weihuproject_down){
            showDialog();
        }
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

                dialog.hide();

            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_t2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.hide();

            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_t3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.hide();

            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_t4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.hide();

            }
        });
        dialog.getWindow().findViewById(R.id.showdalog_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.hide();
            }
        });
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
        simpleAdapter=new SimpleAdapter(WeiHuProjectActivity.this,gridlist,R.layout.installationtask_menuitem,from,image);
        gridView.setAdapter(simpleAdapter);
    }
}
