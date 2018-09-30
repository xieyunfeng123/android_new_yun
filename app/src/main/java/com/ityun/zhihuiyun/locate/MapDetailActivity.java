package com.ityun.zhihuiyun.locate;

import android.content.Intent;
import android.location.Geocoder;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/6/28 0028.
 * 显示位置信息详情页面
 */

public class MapDetailActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bmap_detail)
    MapView mapView;

    @BindView(R.id.detail_address)
    TextView tv_address;

    @BindView(R.id.bt_return)
    Button bt_return;

    private BaiduMap baiduMap;

    private LatLng point;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_mapdetail);
        ButterKnife.bind(this);
        bt_return.setOnClickListener(this);
        init();
    }

    /**
     * 点击事件
     * @param v
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_return:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化
     */
    private void init() {
        Intent intent = getIntent();
        double li = intent.getDoubleExtra("longitude", 0);
        double la = intent.getDoubleExtra("latitude", 0);
        String address = intent.getStringExtra("address");
        tv_address.setText(address);
        point = new LatLng(la,li);
        baiduMap = mapView.getMap();
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
        OverlayOptions options = new MarkerOptions().icon(icon).position(point);
        baiduMap.addOverlay(options);

        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(point).zoom(18.0f);
        baiduMap.animateMapStatus(MapStatusUpdateFactory
                .newMapStatus(builder.build()));
    }

        @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
