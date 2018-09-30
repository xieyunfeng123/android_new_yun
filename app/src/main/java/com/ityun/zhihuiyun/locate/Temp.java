package com.ityun.zhihuiyun.locate;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.fragment.adapter.HomeMessageAdapter;
import com.ityun.zhihuiyun.locate.adapter.BaiduMapAdapter;
import com.ityun.zhihuiyun.view.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by Administrator on 2018/6/21 0021.
 * 定位
 */

public class Temp extends Activity implements SensorEventListener {

    @BindView(R.id.bmap_View)
    MapView mapView;

    @BindView(R.id.bmap_listview)
    ListView listView;

    @BindView(R.id.bt_send)
    Button bt_send;

    @BindView(R.id.bmap_refresh)
    TextView tv_refresh;

    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private List<PoiInfo> datas;
    private BaiduMapAdapter adapter;
    BaiduMap mBaiduMap;
    private PoiSearch mPoiSearch; // 兴趣点
    private GeoCoder geoCoder = null; // 反地理编码（根据经纬度确认街道地址）
    private PoiInfo poiInfo;
    private boolean isGeoCoderFinished;
    private boolean isSearchFinished;
    private int preCheckedPosition = 0; // 点击的前一个位置
    private MapStatusUpdate update;
    private boolean changeState = false; // 滑动地图时进行附近搜索
    private LatLng originalLL, currentLL;//初始化时的经纬度和地图滑动时屏幕中央的经纬度
    private ProgressDialog progressDialog;
    // UI 相关
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private BaiduSDKReceiver mBaiduReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_baidumap);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

        datas = new ArrayList<>();
        adapter = new BaiduMapAdapter(this, datas, R.layout.item_baidumap);
        listView.setAdapter(adapter);
        mBaiduMap = mapView.getMap();

        // 设置地图比例尺
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);

        mPoiSearch = PoiSearch.newInstance();
        mapView.setLongClickable(true);

        geoCoder = GeoCoder.newInstance();

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);

        // 隐藏百度logo
        int count = mapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mapView.getChildAt(i);
            if (child instanceof ImageView || child instanceof ZoomControls) {
                child.setVisibility(View.INVISIBLE);
            }
        }

        // 隐藏比例尺
        mapView.showScaleControl(false);
        if (latitude == 0) {
            mapView = new MapView(this, new BaiduMapOptions());
            mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                    mCurrentMode, true, null
            ));
            mBaiduMap.setMyLocationEnabled(true);
            locate();
            setOnClick();
        } else {
            double longitude = intent.getDoubleExtra("longitude", 0);
            String address = intent.getStringExtra("address");
            LatLng p = new LatLng(latitude, longitude);
            mapView = new MapView(this, new BaiduMapOptions()
                    .mapStatus(new MapStatus.Builder().target(p).build()));
            listView.setVisibility(View.GONE);
            showMap(latitude, longitude, address.split("|")[1]);
        }




        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mBaiduReceiver = new BaiduSDKReceiver();
        registerReceiver(mBaiduReceiver, iFilter);
    }

    /**
     * 开启定位
     */
    private void locate() {

        String str = "正在刷新";
        ProgressDialog.create(this, str);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                finish();
            }
        });

        progressDialog.show();

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(10000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 点击事件
     */
    private void setOnClick() {
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                changeState = true;
            }
        });
        listView.setOnItemClickListener(new MyItemListener());
        mPoiSearch.setOnGetPoiSearchResultListener(new MyGetPoiSearchResult());
        geoCoder.setOnGetGeoCodeResultListener(new MyGetGeoCoderResultListener());
        mBaiduMap.setOnMapStatusChangeListener(new MyMapStatusChangeListener());
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * 监听位置发生了变化
     */
    private class MyMapStatusChangeListener implements BaiduMap.OnMapStatusChangeListener {

        public void onMapStatusChangeStart(MapStatus mapStatus) {
            if (changeState) {
                datas.clear();
                listView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            if (changeState) {
                boolean isFirstLoad = true;
                if (isFirstLoad) {
                    originalLL = mapStatus.target;
                }
                currentLL = mapStatus.target;
                // 反Geo搜索
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(currentLL));
                mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword("小区")
                        .location(currentLL).radius(1000));
            }
        }
    }


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }

            if (poiInfo != null) {
                return;
            }

            poiInfo = new PoiInfo();
            mBaiduMap.clear();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

            poiInfo.location = ll;
            poiInfo.address = location.getAddrStr();
            poiInfo.name = "[位置]";

            LatLng latLng = new LatLng(location.getLatitude() - 0.0002, location.getLongitude());
            CoordinateConverter converter = new CoordinateConverter(); // 坐标转换工具
            converter.coord(latLng); // 设置原坐标数据
            converter.from(CoordinateConverter.CoordType.COMMON); // 设置原坐标类型
            LatLng convertLatLng = converter.convert();
            OverlayOptions options = new MarkerOptions().position(convertLatLng)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.mipmap.icon_yourself_lication))
                    .zIndex(4).draggable(true);
            mBaiduMap.addOverlay(options);
            update = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
            mBaiduMap.animateMapStatus(update);
        }
        public void onReceivePoi(BDLocation poiLocation) {

        }
    }

    /**
     * 根据经纬度进行反地理编码确认街道地址
     */
    public class MyGetGeoCoderResultListener implements OnGetGeoCoderResultListener {

        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
            Log.e("insert", "---onGetGeoCodeResult------");
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            Log.e("insert", "-----onGetReverseGeoCodeResult----------");
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Log.e("insert", "------result null-----");
                return;
            }

            if (poiInfo != null) {
                poiInfo.address = result.getAddress();
                poiInfo.location = result.getLocation();
                poiInfo.name = "[位置]";
                Log.e("insert", "------poiInfo.address-----" + poiInfo.address);
                datas.add(poiInfo);
            }

            preCheckedPosition = 0;
            adapter.setSelection(0);
            isGeoCoderFinished = true;
            refreshAdapter();
        }
    }

    private void refreshAdapter() {
        Log.e("insert", "------refreshAdapter-----" + poiInfo.address);
        adapter.notifyDataSetChanged();
        if (isSearchFinished && isGeoCoderFinished) {
            isGeoCoderFinished = false;
            isSearchFinished = false;
        }
    }

    /**
     * 点击相应的位置，移动到该位置
     */
    private class MyItemListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (preCheckedPosition != position) {
                adapter.setSelection(position);
                View view1 = listView.getChildAt(preCheckedPosition - listView.getFirstVisiblePosition());
                ImageView checked = null;
                if (view1 != null) {
                    checked = (ImageView) view1.findViewById(R.id.bmap_location_checked);
                    checked.setVisibility(View.GONE);
                }
                preCheckedPosition = position;
                changeState = false;
                PoiInfo info = datas.get(position);
                LatLng llA = info.location;
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 17.0f);
                mBaiduMap.animateMapStatus(u);
                poiInfo = info;
                checked = (ImageView) view.findViewById(R.id.bmap_location_checked);
                checked.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 根据关键字查找附近的位置信息
     */
    private class MyGetPoiSearchResult implements OnGetPoiSearchResultListener {

        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if(datas != null&& poiResult != null&&poiResult.getAllPoi()!=null){
                datas.addAll(poiResult.getAllPoi());
            }else{
//                Toast.makeText(BaiduMapActivity.this,"请打开位置信息",Toast.LENGTH_LONG).show();
            }
            preCheckedPosition = 0;
            isSearchFinished = true;

            refreshAdapter();
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult arg0) {
            // TODO Auto-generated method stub

        }
    }

    /**
     *
     *  查看已经发送或者接受的位置信息
     * @param latitude 纬度
     * @param longitude 经度
     * @param address 地址
     */
    private void showMap(double latitude, double longitude, String address) {

    }

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class  BaiduSDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            String st1 = "Network error";
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {

                String st2 = "key validation error!Please on AndroidManifest.xml file " +
                        "check the key set";
//                Toast.makeText(BaiduMapActivity.this, st2, Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
//                Toast.makeText(BaiduMapActivity.this, st1, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mLocClient != null) {
            mLocClient.stop();
        }
        mapView.onDestroy();
        unregisterReceiver(mBaiduReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        if (mLocClient != null) {
            mLocClient.stop();
        }
        super.onPause();
    }
}
