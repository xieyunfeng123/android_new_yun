package com.ityun.zhihuiyun.locate.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.ityun.zhihuiyun.R;

import java.util.List;

/**
 * Created by Administrator on 2018/7/22 0022.
 * 搜索关键字显示地理位置
 */

public class ShowPlaceAdapter extends CommonAdapter<PoiInfo> {

    private int selectPosition;

    public ShowPlaceAdapter(Context context, List<PoiInfo> datas, int layoutId) {
        super(context, datas, layoutId);
        Log.e("insert", "-----adapter-list---" + datas.size());
    }

    @Override
    public void convert(ViewHolder holder, PoiInfo poiInfo, int position) {
        TextView name = holder.getView(R.id.show_name);
        TextView address = holder.getView(R.id.show_address);
        ImageView checked = holder.getView(R.id.isChecked);
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
