package com.ramon.ramonrm;

import com.android.volley.VolleyError;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.ramon.ramonrm.model.LocPoint;
import com.ramon.ramonrm.util.DateTimeUtil;

import java.util.HashMap;
import java.util.Map;

public class MyLocationListener extends BDAbstractLocationListener {
    @Override
    public void onReceiveLocation(BDLocation location) {
        MapView mMapView = MapSession.getMapView();
        BaiduMap mBaiduMap = MapSession.getBaiduMap();
        //mapView 销毁后不在处理新接收的位置
        if (location == null || mMapView == null || mBaiduMap == null) {
            return;
        }
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(location.getDirection()).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();

        LocPoint lPoint = new LocPoint();
        lPoint.LocTime = DateTimeUtil.Now();
        lPoint.Lng = location.getLongitude();
        lPoint.Lat = location.getLatitude();
        MapSession.setLocPoint(lPoint);
        MapSession.setLocation(location);
    }
}