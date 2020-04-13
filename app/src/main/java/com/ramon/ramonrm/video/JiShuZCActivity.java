package com.ramon.ramonrm.video;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.blankj.utilcode.util.GsonUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.ContactAdapter;
import com.ramon.ramonrm.model.UserInfo;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.DialogUitl;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.util.RoomUtil;
import com.ramon.ramonrm.volley.VolleyListenerInterface;
import com.ramon.ramonrm.volley.VolleyRequestUtil;
import com.ramon.ramonrm.webapi.APIConfig;
import com.ramon.ramonrm.webapi.ReqData;
import com.ramon.ramonrm.webapi.ResData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JiShuZCActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<UserInfo> allUsers;
    private ImageView imgBack, btnSearch;
    private ListView lvContacts;
    private EditText txtKey;

    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_jishuzc);
            initView();
            loadUserInfo();
        } catch (Exception e) {
            MethodUtil.showToast(e.getMessage(), context);
            e.printStackTrace();
        }
    }

    private void initView() {
        imgBack = (ImageView) findViewById(R.id.activity_jishuzc_imgback);
        imgBack.setOnClickListener(this);
        lvContacts = (ListView) findViewById(R.id.activity_jishuzc_lvcontacts);
        btnSearch = (ImageView) findViewById(R.id.activity_jishuzc_btnsearch);
        btnSearch.setOnClickListener(this);
        txtKey = (EditText) findViewById(R.id.activity_jishuzc_mingcheng);
    }

    @Override
    public void onClick(View v) {
        try {
            int vId = v.getId();
            if (vId == R.id.activity_jishuzc_imgback) {
                AppManagerUtil.instance().finishActivity(JiShuZCActivity.this);
            } else if (vId == R.id.activity_jishuzc_btnsearch) {
                MethodUtil.hideSoftInputFromActivity(this);
                String key = txtKey.getText().toString();
                loadListView(key);
            }
        } catch (Exception e) {
            MethodUtil.showToast(e.getMessage(), context);
            e.printStackTrace();
        }
    }

    private void loadUserInfo() {
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_SysSql_SPExecForTable,"","");
        try {
            DialogUitl.showProgressDialog(this, reqData.CmdID, "正在加载数据……");
            reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
            reqData.ExtParams.put("spName", "RS_YuanGong_ZhuangTaiLB_SP");
            reqData.ExtParams.put("type", "0");
            VolleyRequestUtil.RequestPost(this, APIConfig.APIHOST + "/api/Req", reqData, new VolleyListenerInterface(this, reqData) {
                @Override
                public void onMySuccess(ReqData reqData, String result) {
                    try {
                        ResData resData = GsonUtils.fromJson(result, ResData.class);
                        if (resData.RstValue == 0) {
                            allUsers = new ArrayList<>();
                            for (int i = 0; i < resData.DataTable.length; i++) {
                                HashMap<String, String> hashMap = resData.DataTable[i];
                                UserInfo userInfo = new UserInfo();
                                userInfo.DeptSNames = hashMap.get("DeptSNames");
                                userInfo.DeptSNos = hashMap.get("DeptSNos");
                                userInfo.YongHuSNo = hashMap.get("YongHuSNo");
                                userInfo.YuanGongSNo = hashMap.get("SNo");
                                userInfo.Name = hashMap.get("Name");
                                userInfo.Mobile = hashMap.get("Mobile");
                                allUsers.add(userInfo);
                            }
                            loadListView("");
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
        }
    }

    private void loadListView(String key) {
        List<UserInfo> listUser = new ArrayList<>();
        for (int i = 0; i < allUsers.size(); i++) {
            UserInfo uInfo = allUsers.get(i);
            if (key.length() == 0 || uInfo.Name.contains(key) || uInfo.DeptSNames.contains(key)) {
                listUser.add(uInfo);
            }
        }
        ContactAdapter adapter = new ContactAdapter(context, R.layout.listitem_contact, listUser);
        adapter.setOnImageClickListener(new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object item, View view, int callType) {
                try {
                    if (callType == ContactAdapter.CALLTYPE_PHONE) {
                        //电话 - 启动系统电话
                        UserInfo uInfo = (UserInfo)item;
                        Intent dialIntent =  new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + uInfo.Mobile));//跳转到拨号界面，同时传递电话号码
                        startActivity(dialIntent);
                    } else if (callType == ContactAdapter.CALLTYPE_VIDEO) {
                        //视频 - 启动视频
                        UserInfo uInfo = (UserInfo) item;
                        String fromUser = uInfo.YongHuSNo;
                        String fromUserName = uInfo.Name;
                        String fromUserDept = uInfo.DeptSNames;
                        int roomId = RoomUtil.createRoomId();
                        sendTIMMessage(fromUser, "video:roomid:" + roomId + ":" + Session.CurrUser.DeptSNames + ":" + Session.CurrUser.Name);
                        Intent intent1 = new Intent(getApplicationContext(), VideoCallActivity.class);
                        intent1.putExtra("data", "video");
                        intent1.putExtra("fromUser", fromUser);
                        intent1.putExtra("fromUserName", fromUserName);
                        intent1.putExtra("fromUserDept", fromUserDept);
                        intent1.putExtra("roomID", roomId);
                        intent1.putExtra("role", 20);
                        intent1.putExtra("callType",VideoCallActivity.CALLOUT);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MethodUtil.showToast(e.getMessage(), context);
                }
            }
        });
        lvContacts.setAdapter(adapter);
    }
}
