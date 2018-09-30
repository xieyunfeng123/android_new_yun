package com.ityun.zhihuiyun.me.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.bean.ItemFile;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018/6/15 0015.
 */

public class ImageGridAdapter extends BaseAdapter {

    private Context context;

    private List<ItemFile> mlist;

    boolean isShow;

    public ImageGridAdapter(Context context) {
        this.context = context;
    }

    public void isShowView(boolean isShow) {
        this.isShow = isShow;
    }

    public void setData(List<ItemFile> mlist) {
        this.mlist = mlist;
    }

    @Override
    public int getCount() {
        return null != mlist ? mlist.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return mlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_image_grid, null);
            holder = new Holder();
            holder.bg = (LinearLayout) convertView.findViewById(R.id.itm_manage_grid_bg);
            holder.img = (ImageView) convertView.findViewById(R.id.itm_manage_grid_img);
            holder.video = (ImageView) convertView.findViewById(R.id.itm_manage_grid_video);
            holder.checked = (ImageView) convertView.findViewById(R.id.itm_manage_grid_checked);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        if (mlist.get(position).getName().contains(".mp4")) {
            holder.video.setVisibility(View.VISIBLE);
        } else {
            holder.video.setVisibility(View.GONE);
        }

        if (mlist.get(position).isChoose()) {
            holder.checked.setImageResource(R.mipmap.qcenter_otherpeople_select);
            holder.bg.setVisibility(View.VISIBLE);
            holder.checked.setVisibility(View.VISIBLE);
        } else {
            if (isShow) {
                holder.checked.setImageResource(R.mipmap.qcenter_otherpeople_unselect);
            } else {
                holder.checked.setVisibility(View.GONE);
            }
            holder.bg.setVisibility(View.GONE);
        }

        Glide.with(context).load(new File(mlist.get(position).getName())).
                into(holder.img);

        return convertView;
    }

    class Holder {

        LinearLayout bg;

        ImageView img;

        ImageView video;

        ImageView checked;
    }
}
