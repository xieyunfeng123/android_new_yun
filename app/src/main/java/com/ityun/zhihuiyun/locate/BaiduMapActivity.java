package com.ityun.zhihuiyun.locate;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
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
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
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
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.locate.adapter.BaiduMapAdapter;
import com.ityun.zhihuiyun.util.CameraUtil;
import com.ityun.zhihuiyun.util.screen.Eyes;

import java.util.ArrayList;
import java.util.List;

public class BaiduMapActivity extends BaseActivity {

    public final static String LATITUDE = "latitude";
    public final static String LONGITUDE = "longitude";
    public final static String ADDRESS = "address";
    public final static String NAME = "name";
    public final static String PATH = "path";
    private BaiduMapAdapter adatper;
    private LatLng originalLL, currentLL;//初始化时的经纬度和地图滑动时屏幕中央的经纬度
    static MapView mMapView = null;
    private GeoCoder mSearch = null;


    private TextView sendButton = null;
    private PoiSearch mPoiSearch, poiSearch;
    private List<PoiInfo> datas;
    private PoiInfo lastInfo = null;
    public static BaiduMapActivity instance = null;
    private ProgressDialog progressDialog;
    private ImageView iv_search;
    private BaiduMap mBaiduMap;
    private MapStatusUpdate myselfU;
    private ListView listView;
    private boolean changeState = true;//当滑动地图时再进行附近搜索
    private int preCheckedPosition = 0;//点击的前一个位置
    private TextView refreshText;
    private boolean isSearchFinished;
    private boolean isGeoCoderFinished;

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class BaiduSDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            String st1 = "网络错误！";
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                String st2 = "百度验证码错误！";
                Toast.makeText(instance, st2, Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Toast.makeText(instance, st1, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BaiduSDKReceiver mBaiduReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.main_color));
        SDKInitializer.initialize(getApplicationContext());
        instance = this;
        setContentView(R.layout.activity_baidumap);
        init();
        setTitle();

    }

    private void init() {
        listView = findViewById(R.id.bmap_listview);
        mMapView = findViewById(R.id.bmap_View);
        mSearch = GeoCoder.newInstance();
        sendButton = findViewById(R.id.bt_send);
        refreshText = findViewById(R.id.bmap_refresh);
        iv_search = findViewById(R.id.iv_search);
        datas = new ArrayList<>();
        adatper = new BaiduMapAdapter(BaiduMapActivity.this,
                datas, R.layout.item_baidumap);
        listView.setAdapter(adatper);
        LocationMode mCurrentMode = LocationMode.NORMAL;
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        mPoiSearch = PoiSearch.newInstance();
        mMapView.setLongClickable(true);
        // 隐藏百度logo ZoomControl
        int count = mMapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ImageView || child instanceof ZoomControls) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        // 隐藏比例尺
        mMapView.showScaleControl(false);
        mMapView = new MapView(this, new BaiduMapOptions());
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, null));
        mBaiduMap.setMyLocationEnabled(true);
        showMapWithLocationClient();
        setOnclick();
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mBaiduReceiver = new BaiduSDKReceiver();
        registerReceiver(mBaiduReceiver, iFilter);
    }

    /**
     * 设置点击事件
     */
    private void setOnclick() {
        mBaiduMap.setOnMapTouchListener(motionEvent -> changeState = true);
        listView.setOnItemClickListener(new MyItemClickListener());
        mPoiSearch.setOnGetPoiSearchResultListener(new MyGetPoiSearchResult());
        mBaiduMap.setOnMapStatusChangeListener(new MyMapStatusChangeListener());
        mSearch.setOnGetGeoCodeResultListener(new MyGetGeoCoderResultListener());
        iv_search.setOnClickListener(new SearchListener());
    }

    /**
     * 点击搜索
     */
    private class SearchListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(BaiduMapActivity.this, MapSearchActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    private void refreshAdapter() {
        if (isSearchFinished && isGeoCoderFinished) {
            adatper.notifyDataSetChanged();
            refreshText.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            isSearchFinished = false;
            isGeoCoderFinished = false;
        }
    }

    /**
     * 根据关键字查找附近的位置信息
     */
    private class MyGetPoiSearchResult implements OnGetPoiSearchResultListener {

        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (datas != null && poiResult != null && poiResult.getAllPoi() != null) {
                datas.addAll(poiResult.getAllPoi());
            } else {
                Toast.makeText(BaiduMapActivity.this, "请打开位置信息", Toast.LENGTH_LONG).show();
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
     * 根据经纬度进行反地理编码
     */
    private class MyGetGeoCoderResultListener implements OnGetGeoCoderResultListener {

        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            if (lastInfo != null) {
                lastInfo.address = result.getAddress();
                lastInfo.location = result.getLocation();
                lastInfo.name = "[位置]";
                datas.add(lastInfo);
            }
            preCheckedPosition = 0;
            adatper.setSelection(0);
            isGeoCoderFinished = true;
            refreshAdapter();
        }
    }

    /**
     * 监听位置发生了变化
     */
    private class MyMapStatusChangeListener implements BaiduMap.OnMapStatusChangeListener {

        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {
            if (changeState) {
                datas.clear();
                refreshText.setVisibility(View.VISIBLE);
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
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
                OverlayOptions options = new MarkerOptions().icon(icon).position(currentLL);
                mBaiduMap.clear();
                mBaiduMap.addOverlay(options);
                // 反Geo搜索
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(currentLL));
                mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword("全部")
                        .location(currentLL).radius(1000));
            }
        }
    }

    /**
     * 显示当前的位置信息
     */
    private void showMapWithLocationClient() {
        String str1 = "正在刷新";
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(str1);
        progressDialog.setOnCancelListener(arg0 -> {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            finish();
        });
        progressDialog.show();
        BaiduMapUtil.getInstance().setLocationListener(location -> {
            if (location == null) {
                return;
            }
            sendButton.setEnabled(true);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            if (lastInfo != null) {
                return;
            }
            lastInfo = new PoiInfo();
            mBaiduMap.clear();
            LatLng llA = new LatLng(location.getLatitude(), location.getLongitude());

            lastInfo.location = llA;
            lastInfo.address = location.getAddrStr();
            lastInfo.name = "[位置]";
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
            OverlayOptions options = new MarkerOptions().icon(icon).position(latLng);
            mBaiduMap.addOverlay(options);

            LatLng ll = new LatLng(location.getLatitude() - 0.0002, location.getLongitude());
            CoordinateConverter converter = new CoordinateConverter();//坐标转换工具类
            converter.coord(ll);//设置源坐标数据
            converter.from(CoordinateConverter.CoordType.COMMON);//设置源坐标类型
            LatLng convertLatLng = converter.convert();
            OverlayOptions myselfOOA = new MarkerOptions().position(convertLatLng)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.mipmap.icon_yourself_lication))
                    .zIndex(4).draggable(true);
            mBaiduMap.addOverlay(myselfOOA);
            myselfU = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
            mBaiduMap.animateMapStatus(myselfU);
        });
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
        lastInfo = null;
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        unregisterReceiver(mBaiduReceiver);
        super.onDestroy();
    }



    private void showRightWithText(String str,
                                   View.OnClickListener clickListener) {
        TextView rightText = findViewById(R.id.bt_send);
        rightText.setVisibility(View.VISIBLE);
        rightText.setText(str);

        //设置点击区域
        TextView rightClickRange = findViewById(R.id.bt_send);
        rightClickRange.setOnClickListener(clickListener);
    }

    protected void showLeftWithImage(int resId,
                                     View.OnClickListener clickListener) {
        ImageView leftImage = findViewById(R.id.left_title_image);
        leftImage.setVisibility(View.VISIBLE);
        leftImage.setImageResource(resId);

        //设置点击区域
        ImageView leftClickRange = findViewById(R.id.left_title_image);
        leftClickRange.setOnClickListener(clickListener);
    }

    public void getId() {

    }

    private String paths;

    /**
     * 发送位置信息
     */
    private void setTitle() {
        showRightWithText("发送", new View.OnClickListener() {
            private double longitude;
            private double latitude;
            @Override
            public void onClick(View v) {
                latitude = lastInfo.location.latitude;
                longitude = lastInfo.location.longitude;
                Rect rect = new Rect(200, 200, 1000, 550);
                mBaiduMap.snapshotScope(rect, bitmap -> {
                    paths = CameraUtil.saveBitmap(BaiduMapActivity.this, bitmap);
                    Intent intent = BaiduMapActivity.this.getIntent();
                    intent.putExtra(LATITUDE, latitude);
                    intent.putExtra(LONGITUDE, longitude);
                    intent.putExtra(ADDRESS, lastInfo.address.replace("-","|"));
                    intent.putExtra(NAME, lastInfo.name.replace("-","|"));
                    intent.putExtra(PATH, paths);
                    BaiduMapActivity.this.setResult(RESULT_OK, intent);
                    finish();
                });
            }
        });
        showLeftWithImage(R.mipmap.btn_back, v -> finish());
    }

    /**
     * 点击相应的位置，移动到该位置
     */
    private class MyItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (preCheckedPosition != position) {
                adatper.setSelection(position);
                View view1 = listView.getChildAt(preCheckedPosition - listView.getFirstVisiblePosition());
                ImageView checked = null;
                if (view1 != null) {
                    checked = view1.findViewById(R.id.bmap_location_checked);
                    checked.setVisibility(View.GONE);
                }
                preCheckedPosition = position;
                changeState = false;
                PoiInfo info = datas.get(position);
                LatLng llA = info.location;
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
                OverlayOptions options = new MarkerOptions().icon(icon).position(llA);
                mBaiduMap.addOverlay(options);
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 17.0f);
                mBaiduMap.animateMapStatus(u);
                lastInfo = info;
                checked = view.findViewById(R.id.bmap_location_checked);
                checked.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            datas.clear();
            LatLng latlng = data.getParcelableExtra("latLng");
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
            OverlayOptions options = new MarkerOptions().icon(icon).position(latlng);
            mBaiduMap.addOverlay(options);
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(latlng, 17.0f);
            mBaiduMap.animateMapStatus(u);
            List<PoiInfo> list = (List<PoiInfo>) data.getSerializableExtra("list");
            int position = data.getIntExtra("position", 0);
            Bundle bundle = data.getBundleExtra("bundle");
            PoiInfo info = bundle.getParcelable("poiInfo");
            lastInfo = info;
            preCheckedPosition = position;
            adatper.setSelection(preCheckedPosition);
            datas.addAll(list);
            adatper.notifyDataSetChanged();
            refreshText.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }
}
