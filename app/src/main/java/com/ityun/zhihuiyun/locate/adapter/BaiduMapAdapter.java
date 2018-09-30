package com.ityun.zhihuiyun.locate.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.ityun.zhihuiyun.R;

import java.util.List;

/**
 * Created by Administrator on 2018/6/21 0021.
 */

public class BaiduMapAdapter extends CommonAdapter<PoiInfo> {

    private int selectPosition;

    public BaiduMapAdapter(Context context, List<PoiInfo> datas, int LayoutId) {
        super(context, datas, LayoutId);
    }

    public void convert(ViewHolder holder, PoiInfo poiInfo, int position) {
        TextView name = holder.getView(R.id.bmap_location);
        TextView address = holder.getView(R.id.bmap_address);
        ImageView checked = holder.getView(R.id.bmap_location_checked);
        if (position == selectPosition) {
            checked.setVisibility(View.VISIBLE);
        } else {
            checked.setVisibility(View.GONE);
        }
        name.setText(poiInfo.name);
        address.setText(poiInfo.address);
    }

    public void setSelection(int selectPosition) {
        this.selectPosition = selectPosition;
    }
}
