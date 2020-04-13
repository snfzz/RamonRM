package com.ramon.ramonrm;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.ramon.ramonrm.home.HomeActivity;
import com.ramon.ramonrm.model.LocPoint;

public class MapSession {
    private static MapView mMapView;

    public static MapView getMapView() {
        return mMapView;
    }

    public static void setMapView(MapView view) {
        mMapView = view;
    }

    private static BaiduMap mBaiduMap;

    public static BaiduMap getBaiduMap() {
        return mBaiduMap;
    }

    public static void setBaiduMap(BaiduMap map) {
        mBaiduMap = map;
    }

    private static MapView mTempMapView;

    public static MapView getTempMapView() {
        return mTempMapView;
    }

    public static void setTempMapView(MapView view) {
        mTempMapView = view;
    }

    private static BaiduMap mTempBaiduMap;

    public static BaiduMap getTempBaiduMap() {
        return mTempBaiduMap;
    }

    public static void setTempBaiduMap(BaiduMap map) {
        mTempBaiduMap = map;
    }

    private static LocationClient mLocationClient;

    public static LocationClient getLocationClient() {
        return mLocationClient;
    }

    public static void setLocationClient(LocationClient client) {
        mLocationClient = client;
    }

    private static LocPoint mLocPoint;

    public static LocPoint getLocPoint() {
        return mLocPoint;
    }

    public static void setLocPoint(LocPoint lPoint) {
        mLocPoint = lPoint;
    }

    private static HomeActivity mHome;

    public static HomeActivity getHome() {
        return mHome;
    }

    public static void setHome(HomeActivity home) {
        mHome = home;
    }

    private static BDLocation mLocation;

    public static BDLocation getLocation() {
        return mLocation;
    }

    public static void setLocation(BDLocation location) {
        mLocation = location;
    }
}
