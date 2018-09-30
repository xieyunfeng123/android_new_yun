package com.ityun.zhihuiyun.locate;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.google.gson.Gson;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.db.IMUtil;
import com.ityun.zhihuiyun.event.ReceiveMessageEvent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by Administrator on 2018/7/25 0025.
 */

public class BaiduMapUtil {

    private static BaiduMapUtil getinstance;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationClient mLocClient;// 定位相关

    public static BaiduMapUtil getInstance() {
        if (getinstance == null)
            getinstance = new BaiduMapUtil();
        return getinstance;
    }


    public void init(Context context) {
        mLocClient = new LocationClient(context);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setScanSpan(3000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    OnLocationListener onLocationListener;

    public void setLocationListener(OnLocationListener onLocationListener) {
        this.onLocationListener = onLocationListener;
    }

    public interface OnLocationListener {
        void callBack(BDLocation location);
    }


    /**
     * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            if (onLocationListener != null) {
                onLocationListener.callBack(location);
            }
            uploadLocation(location);
        }
    }


    private void uploadLocation(BDLocation bdLocation) {
        SysConfBean bean = App.getInstance().getSysConfBean();
        String url = "http://" + bean.getImsvrip() + ":" + bean.getImsvrhttpport() + "/?";
        int userid = bean.getAccountid();
        SendLocation sendLocation = new SendLocation();
        sendLocation.setLongitude(bdLocation.getLongitude());
        sendLocation.setLatitude(bdLocation.getLatitude());
        sendLocation.setAddress(bdLocation.getAddrStr());
        sendLocation.setTime(new Date().getTime()/1000);
        sendLocation.setContent("");
        Gson gson = new Gson();
        String sendInfo = gson.toJson(sendLocation);
        OkHttpUtils
                .postString()
                .url(url)
                .content("msgid=259&userid="+userid+"&location="+sendInfo)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
            }

            @Override
            public void onResponse(String s, int i) {
                Log.e("insert",s);
            }
        });
    }
}
