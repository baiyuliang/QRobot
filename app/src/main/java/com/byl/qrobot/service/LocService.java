package com.byl.qrobot.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.byl.qrobot.config.Const;
import com.byl.qrobot.util.LogUtil;
import com.byl.qrobot.util.PreferencesUtils;


/**
 * 后台定位Service
 * @author 白玉梁
 * @date 2015-7-6 下午3:15:30
 */
public class LocService extends Service {

    private static LocService mInstance = null;
    // 定位相关
    LocationClient mLocClient;
    GeoCoder mSearch;
    MyLocationListenner myLocationListenner = null;

    private double mlongitude = Const.LOC_LONGITUDE;
    private double mlatitude = Const.LOC_LATITUDE;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.e("Service启动");
        initLocation();        //启动定位
        mInstance = this;
    }

    public static LocService getInstance() {
        return mInstance;
    }

    /**
     * 初始化定位
     */
    public void initLocation() {
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {//反地理编码
                if (arg0 == null || arg0.error != SearchResult.ERRORNO.NO_ERROR) {
                    return;
                }
                LogUtil.e("当前位置：" +arg0.getAddress());
                PreferencesUtils.putSharePre(LocService.this, Const.ADDRESS, arg0.getAddress());
                PreferencesUtils.putSharePre(LocService.this, Const.CITY, arg0.getAddressDetail().city);

            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult arg0) {

            }
        });
        myLocationListenner = new MyLocationListenner();
        mLocClient = new LocationClient(this);// 定位初始化
        mLocClient.registerLocationListener(myLocationListenner);//注册定位监听
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);//设置定位模式
        option.setOpenGps(true);// 打开gps
        option.setIsNeedAddress(true);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5 * 60 * 1000);//设置定位请求间隔(此处为5min定位一次)
        mLocClient.setLocOption(option);
        mLocClient.start();
    }


    /**
     * 定位监听
     *
     * @author 白玉梁
     * @date 2015-7-6 下午3:18:45
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) return;
            mlongitude = location.getLongitude();//经度
            mlatitude = location.getLatitude();//维度
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(mlatitude, mlongitude)));
            LogUtil.e("当前坐标：" + mlongitude + "," + mlatitude);
            PreferencesUtils.putSharePre(LocService.this, Const.LOCTION, mlongitude + "," + mlatitude);
        }

    }


    @Override
    public void onDestroy() {
        mInstance = null;
        // 退出时销毁定位
        if (mLocClient != null) {
            mLocClient.unRegisterLocationListener(myLocationListenner);
            myLocationListenner = null;
            mLocClient.stop();
            mLocClient = null;
        }
        LogUtil.e("Service销毁");
        super.onDestroy();
    }

}
