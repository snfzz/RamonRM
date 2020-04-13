package com.ramon.ramonrm.device;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.format.bg.ICellBackgroundFormat;
import com.bin.david.form.data.format.draw.MultiLineDrawFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.style.LineStyle;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.blankj.utilcode.util.GsonUtils;
import com.github.abel533.echarts.json.GsonUtil;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.adapter.DeviceStatusAdapter;
import com.ramon.ramonrm.model.DeviceStatus;
import com.ramon.ramonrm.model.TableBean;
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

public class CollectDevFragment extends Fragment {

    private Activity mActivity;
    private ImageView btnSearch;
    private EditText txtInput;
    private ICellBackgroundFormat<CellInfo> backgroundFormat;

    private SmartTable sTabResult;
    private List<Column> sTabColumns;
    private List<TableBean> sTabDatas;
    private int sTabSelectedIndex = 0;

    private ListView lvDevStatus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //不同的Activity对应不同的布局
        View view = inflater.inflate(R.layout.fragment_collectdev, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        mActivity = getActivity();
        btnSearch = (ImageView) mActivity.findViewById(R.id.fragment_collectdev_btnsearch);
        btnSearch.setOnClickListener(new MyOnClickLisener());
        txtInput = (EditText) mActivity.findViewById(R.id.fragment_collectdev_mingcheng);
        sTabResult = (SmartTable) mActivity.findViewById(R.id.fragment_collectdev_stabresult);
        sTabColumns = new ArrayList<>();
        sTabColumns.add(new Column<String>("类型", "Bean1"));
        sTabColumns.add(new Column<String>("名称", "Bean2"));
        sTabColumns.get(1).setDrawFormat(new MultiLineDrawFormat(mActivity, 230));//设置最大宽度
        sTabColumns.add(new Column<String>("收藏", "Bean3"));
        backgroundFormat = new BaseCellBackgroundFormat<CellInfo>() {
            @Override
            public int getBackGroundColor(CellInfo cellInfo) {
                if (cellInfo.row == sTabSelectedIndex) {
                    return ContextCompat.getColor(getContext(), R.color.colorTitleMain);
                }
                return TableConfig.INVALID_COLOR;
            }
        };
        for (int i = 0; i < sTabColumns.size(); i++) {
            sTabColumns.get(i).setOnColumnItemClickListener(new OnColumnItemClickListener() {
                @Override
                public void onClick(Column column, String value, Object o, int position) {
                    if (column.getFieldName() == "Bean3") {
                        //点击删除收藏按钮
                        TableBean sBean = sTabDatas.get(position);
                        String sNo = sBean.Bean6;
                        removeInfoToShouCang(sNo);
                    } else {
                        if (sTabSelectedIndex != position) {
                            sTabSelectedIndex = position;
                            sTabResult.getConfig().setContentCellBackgroundFormat(backgroundFormat);
                            if (column.getFieldName() == "Bean1" || column.getFieldName() == "Bean2") {
                                //选中一行搜索结果
                                loadSelectedDev(sTabDatas.get(sTabSelectedIndex));
                            }
                        }
                    }
                    sTabResult.refreshDrawableState();
                    sTabResult.invalidate();
                }
            });
        }

        lvDevStatus = (ListView) mActivity.findViewById(R.id.fragment_collectdev_lvdev);
        searchDev("");
    }

    private class MyOnClickLisener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            try {
                if (view.getId() == R.id.fragment_collectdev_btnsearch) {
                    //搜索
                    MethodUtil.hideSoftInputFromFragment(getView());
                    String key = txtInput.getText().toString().trim();
                    searchDev(key);
                }
            } catch (Exception ex) {
                MethodUtil.showToast(ex.getMessage(), mActivity);
            }
        }
    }

    private void searchDev(String key) {
        sTabSelectedIndex = 0;
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        try {
            reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
            reqData.ExtParams.put("yonghuSNo", Session.CurrUser.YongHuSNo);
            reqData.ExtParams.put("spName", "WX_ShouCang_LieBiao_SP");
            reqData.ExtParams.put("mingCheng", key);
            reqData.ExtParams.put("outParams", "recordTotal");
            reqData.ExtParams.put("recordTotal", "0");
            DialogUitl.showProgressDialog(mActivity, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(mActivity, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    refreshTable(resData.DataTable);
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, mActivity);
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), mActivity);
                            } finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        // Volley请求失败时调用的函数
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

    private void refreshTable(HashMap<String, String>[] hashData) {
        if (hashData == null) return;
        sTabDatas = new ArrayList<>();
        for (int i = 0; i < hashData.length; i++) {
            TableBean bean = new TableBean();
            bean.Bean3 = "-";
            bean.Bean4 = hashData[i].get("ShouCangSNo");
            bean.Bean5 = hashData[i].get("LeiBieDM");
            bean.Bean1 = bean.Bean5.equals("SB") ? "设备" : "客户";
            bean.Bean2 = bean.Bean5.equals("SB") ? hashData[i].get("SBMingCheng") : hashData[i].get("MingCheng");
            bean.Bean6 = hashData[i].get("SNo");
            sTabDatas.add(bean);
        }
        TableData<TableBean> tableData = new TableData<TableBean>("收藏列表", sTabDatas, sTabColumns);
        sTabResult.getConfig().setColumnTitleStyle(new FontStyle(50, Color.parseColor("#4f94cd")));
        sTabResult.getConfig().setContentStyle(new FontStyle(45, Color.WHITE));
        sTabResult.getConfig().setContentGridStyle(new LineStyle(mActivity, 1, Color.parseColor("#FF2C2C2C")));
        sTabResult.getConfig().setColumnTitleGridStyle(new LineStyle(mActivity, 1, Color.parseColor("#FF2C2C2C")));
        sTabResult.getConfig().setShowYSequence(false);
        sTabResult.getConfig().setShowXSequence(false);
        sTabResult.getConfig().setShowTableTitle(false);
        sTabResult.setTableData(tableData);
        sTabResult.getConfig().setMinTableWidth(100);
        sTabResult.getConfig().setContentCellBackgroundFormat(backgroundFormat);
        if (sTabDatas.size() > 0)
            loadSelectedDev(sTabDatas.get(sTabSelectedIndex));
    }

    private void loadSelectedDev(TableBean selectedBean) {
        if (selectedBean == null) return;
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecForTable,Session.SessionId,Session.ValidMD5);
        try {
            reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
            reqData.ExtParams.put("spName", "WX_SB_SheBei_Status_SP");
            if (selectedBean.Bean5.equals("SB")) {
                reqData.ExtParams.put("sheBeiSNo", selectedBean.Bean4);
            } else {
                reqData.ExtParams.put("keHuSNo", selectedBean.Bean4);
            }
            reqData.ExtParams.put("shouCang", "0");
            DialogUitl.showProgressDialog(mActivity, reqData.CmdID, "正在加载数据……");
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(mActivity, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    loadDevData(resData.DataTable);
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, mActivity);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                MethodUtil.showToast(ex.getMessage(), mActivity);
                            } finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        // Volley请求失败时调用的函数
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

    private void loadDevData(HashMap<String, String>[] hashData) {
        List<DeviceStatus> listDev = new ArrayList<>();
        for (int i = 0; i < hashData.length; i++) {
            HashMap<String, String> hashMap = hashData[i];
            DeviceStatus devStatus = new DeviceStatus();
            devStatus.AllowAdd = false;
            devStatus.CurrAlarmNum = Integer.parseInt(hashMap.get("CurrAlarmNum"));
            devStatus.DevName = hashMap.get("SBMingCheng");
            devStatus.DevStatus = Integer.parseInt(hashMap.get("devStatus"));
            devStatus.DevSNo = hashMap.get("SBSNo");
            devStatus.DevStatusName = hashMap.get("devName");
            devStatus.LastDataTime = hashMap.get("LastDataTime");
            devStatus.NetStatus = Integer.parseInt(hashMap.get("netStatus"));
            devStatus.NetStatusName = devStatus.NetStatus == 0 ? "网络异常" : "网络正常";
            devStatus.ChanPinSNo = hashMap.get("ChanPinSNo");
            devStatus.KeHuMC = hashMap.get("KeHuMC");
            devStatus.KeHuJC = hashMap.get("KeHuJC");
            devStatus.ChanPinMC = hashMap.get("CPMingCheng");
            devStatus.ChanPinJC = hashMap.get("CPJianCheng");
            devStatus.LiuShu = Integer.parseInt(hashMap.get("LiuShu"));
            listDev.add(devStatus);
        }
        DeviceStatusAdapter adapter = new DeviceStatusAdapter(mActivity, R.layout.listitem_devstatus, listDev);
        adapter.setOnItemClickListener(new DeviceStatusAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object item, View view) {
                try {
                    DeviceStatus devStatus = (DeviceStatus) item;
                    Intent intent = new Intent(mActivity, DevDetailActivity.class);
                    intent.putExtra("SBSNo", devStatus.DevSNo);
                    intent.putExtra("CPSNo", devStatus.ChanPinSNo);
                    intent.putExtra("SBMingCheng", devStatus.DevName);
                    intent.putExtra("KeHuMC", devStatus.KeHuMC);
                    intent.putExtra("LiuShu", devStatus.LiuShu);
                    String sbInfo = GsonUtils.toJson(devStatus);
                    intent.putExtra("SBInfo",sbInfo);
                    startActivity(intent);
                } catch (Exception e) {
                    MethodUtil.showToast(e.getMessage(), mActivity);
                    e.printStackTrace();
                }
            }
        });
        lvDevStatus.setAdapter(adapter);
    }

    private void removeInfoToShouCang(String sNo) {
        ReqData reqData = ReqData.createReqData(ReqData.ReqType.T_SQLExec_BizSql_SPExecNoRtn,Session.SessionId,Session.ValidMD5);
        try {
            reqData.ExtParams.put("cjSNo", Session.CurrUser.YongHuSNo);
            reqData.ExtParams.put("spName", "WX_ShouCang_ShanChu_SP");
            reqData.ExtParams.put("sno", sNo);
            DialogUitl.showProgressDialog(mActivity, reqData.CmdID, "正在移除收藏……");
            VolleyRequestUtil.RequestPost(mActivity, APIConfig.APIHOST + "/api/Req", reqData,
                    new VolleyListenerInterface(mActivity, reqData) {
                        // Volley请求成功时调用的函数
                        @Override
                        public void onMySuccess(ReqData reqData, String result) {
                            try {
                                ResData resData = GsonUtils.fromJson(result, ResData.class);
                                if (resData.RstValue == 0) {
                                    String key = txtInput.getText().toString().trim();
                                    searchDev(key);
                                } else {
                                    MethodUtil.showToast(resData.RstMsg, mActivity);
                                }
                            } catch (Exception ex) {
                                MethodUtil.showToast(ex.getMessage(), mActivity);
                            } finally {
                                DialogUitl.dismissProgressDialog(reqData.CmdID);
                            }
                        }

                        // Volley请求失败时调用的函数
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
}