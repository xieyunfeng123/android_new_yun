package com.ityun.zhihuiyun.locate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.locate.adapter.ShowPlaceAdapter;
import com.ityun.zhihuiyun.view.ClearEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/7/20 0020.
 */

public class MapSearchActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.et_keyWord)
    ClearEditText keyWord;

    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.ls_showPlace)
    ListView listView;

    private PoiSearch mPoiSearch = PoiSearch.newInstance();

    private ShowPlaceAdapter adapter;

    private List<PoiInfo> datas;

    private int pageNum = 0;

    private int preCheckedPosition = 0;

    private int num = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
        ButterKnife.bind(this);
        datas = new ArrayList<>();
        adapter = new ShowPlaceAdapter(MapSearchActivity.this, datas, R.layout.item_showplace);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MyItemClickListener());
        handler.sendEmptyMessage(0);
        keyWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                datas.clear();
                if (s != null) {
                    poiSearch(s.toString());
                } else {
                    Tost("请输入名称");
                }
                adapter.notifyDataSetChanged();
            }
        });

        keyWord.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    num++;
                    //在这里加判断的原因是点击一次软键盘的删除键,会触发两次回调事件
                    if (num % 2 != 0) {
                        String s = keyWord.getText().toString();
                        if (!TextUtils.isEmpty(s)) {
                            keyWord.setText("" + s.substring(0, s.length() - 1));
                            //将光标移到最后
                            keyWord.setSelection(keyWord.getText().length());
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    initSearchListener();
                    break;
            }
        }
    };

    private void initSearchListener() {
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                datas.clear();
                if (poiResult != null && poiResult.getAllPoi() != null && poiResult.getAllPoi().size() != 0) {
                    datas.addAll(poiResult.getAllPoi());
                    adapter.notifyDataSetChanged();
                } else {
                    Tost("暂无搜索结果");
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            }
        });
    }

    private void poiSearch(String keyWord) {
        mPoiSearch.searchInCity(new PoiCitySearchOption().city("南京").keyword(keyWord).pageNum(pageNum));
    }

    private class MyItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            adapter.setSelection(position);
            View view1 = listView.getChildAt(preCheckedPosition - listView.getFirstVisiblePosition());
            ImageView checked = null;
            if (view1 != null) {
                checked = view1.findViewById(R.id.isChecked);
                checked.setVisibility(View.GONE);
            }
            preCheckedPosition = position;
            PoiInfo poiInfo = datas.get(position);
            if (poiInfo != null) {
                LatLng latLng = poiInfo.location;
                checked = view.findViewById(R.id.isChecked);
                checked.setVisibility(View.VISIBLE);
                Intent intent = getIntent();
                intent.putExtra("position", position);
                intent.putExtra("latLng", latLng);
                intent.putExtra("list", (Serializable) datas);
                Bundle bundle = new Bundle();
                bundle.putParcelable("poiInfo", poiInfo);
                intent.putExtra("bundle", bundle);
                setResult(1, intent);
                finish();
            } else {
                Tost("暂无搜索结果");
            }
        }
    }


    @OnClick({R.id.iv_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
