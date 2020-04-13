package com.ramon.ramonrm.home.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.controls.MineMenuItem;
import com.ramon.ramonrm.home.AboutActivity;
import com.ramon.ramonrm.home.HomeActivity;
import com.ramon.ramonrm.home.LoginActivity;
import com.ramon.ramonrm.softupdate.UpdateManager;
import com.ramon.ramonrm.user.AuditUserActivity;
import com.ramon.ramonrm.user.PasswordActivity;
import com.ramon.ramonrm.user.PowerApplyActivity;
import com.ramon.ramonrm.user.QuanXianSHActivity;
import com.ramon.ramonrm.user.SeePowerActivity;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.MethodUtil;

public class MineFragment extends Fragment implements View.OnClickListener {

    private Activity mActivity;

    private TextView lblName, lblDeptName;
    private MineMenuItem mItemModifyPsw, mItemLogout, mItemAbout,mItemYongHuSH,mItemPower,mItenApply,mItenSee;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //不同的Activity对应不同的布局
        View view = inflater.inflate(R.layout.fragment_minepage, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        lblName = mActivity.findViewById(R.id.fragment_minepage_lblname);
        lblName.setText(Session.CurrUser.Name);
        lblDeptName = mActivity.findViewById(R.id.fragment_minepage_lbldeptname);
        lblDeptName.setText(Session.CurrUser.DeptSNames);
        mItemModifyPsw = mActivity.findViewById(R.id.fragment_minepage_mitempassword);
        mItemModifyPsw.setOnClickListener(this);
        mItemLogout = mActivity.findViewById(R.id.fragment_minepage_mitemzhuxiao);
        mItemLogout.setOnClickListener(this);
        mItemAbout = mActivity.findViewById(R.id.fragment_minepage_mitemabout);
        mItemAbout.setOnClickListener(this);
        mItemYongHuSH = mActivity.findViewById(R.id.fragment_minepage_mitemyonghush);
        mItemYongHuSH.setOnClickListener(this);
        mItemPower = mActivity.findViewById(R.id.fragment_minepage_mitemquanxiansh);
        mItemPower.setOnClickListener(this);
        mItenApply = mActivity.findViewById(R.id.fragment_minepage_mitemquanxiansq);
        mItenApply.setOnClickListener(this);
        mItenSee = mActivity.findViewById(R.id.fragment_minepage_mitemquanxianck);
        mItenSee.setOnClickListener(this);

    }

    //修改密码，注销登陆，关于，用户审核
    @Override
    public void onClick(View v) {
        try {
            int vId = v.getId();
            if (vId == R.id.fragment_minepage_mitempassword) {
                Intent intent = new Intent(mActivity, PasswordActivity.class);
                mActivity.startActivity(intent);
            }
            if (vId == R.id.fragment_minepage_mitemzhuxiao) {
                new AlertDialog.Builder(mActivity)
                        .setTitle("镭目云运维")
                        .setMessage("是否注销？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        AppManagerUtil.instance().AppExit(mActivity);
                                        Intent intent = new Intent(mActivity, LoginActivity.class);
                                        intent.putExtra("IsLogout", true);
                                        mActivity.startActivity(intent);
                                    }
                                })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface,
                                    int i) {
                            }
                        }).show();
            }
            if (vId == R.id.fragment_minepage_mitemabout) {
                Intent intent = new Intent(mActivity, AboutActivity.class);
                mActivity.startActivity(intent);
            }
            if(vId == R.id.fragment_minepage_mitemyonghush){
                Intent intent = new Intent(mActivity, AuditUserActivity.class);
                mActivity.startActivity(intent);
            }
            if(vId == R.id.fragment_minepage_mitemquanxiansh){
                Intent intent=new Intent(mActivity, QuanXianSHActivity.class);
                mActivity.startActivity(intent);
            }
            if (vId == R.id.fragment_minepage_mitemquanxiansq){
                Intent intent=new Intent(mActivity, PowerApplyActivity.class);
                mActivity.startActivity(intent);
            }
            if (vId == R.id.fragment_minepage_mitemquanxianck){
                Intent intent=new Intent(mActivity, SeePowerActivity.class);
                mActivity.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MethodUtil.showToast(e.getMessage(), mActivity);
        }
    }
}
