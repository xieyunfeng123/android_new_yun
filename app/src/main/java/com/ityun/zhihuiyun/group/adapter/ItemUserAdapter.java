package com.ityun.zhihuiyun.group.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.bean.NewAccountInfo;
import com.ityun.zhihuiyun.view.CircleBgImageView;

import java.util.List;

/**
 * Created by Administrator on 2018/5/27 0027.
 */
public class ItemUserAdapter extends BaseAdapter {

    private Context context;

    private List<NewAccountInfo> mlist;

    public ItemUserAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<NewAccountInfo> mlist) {
        this.mlist = mlist;
    }

    @Override
    public int getCount() {

        if (mlist == null) {
            return 2;
        } else if (mlist.size() >= 5) {
            return 7;
        } else {
            return mlist.size() + 2;
        }
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user_img, null);
            holder = new ImageHolder();
            holder.item_user_img = convertView.findViewById(R.id.item_user_img);
            convertView.setTag(holder);
        } else {
            holder = (ImageHolder) convertView.getTag();
        }
        if (position == (getCount() - 1)) {
            holder.item_user_img.setImageResource(R.mipmap.group_sbtruct_people);
        } else if (position == (getCount() - 2)) {
            holder.item_user_img.setImageResource(R.mipmap.group_add_people);
        } else {
            holder.item_user_img.settText(mlist.get(position).getAccountName());
        }
        return convertView;
    }

    class ImageHolder {
        CircleBgImageView item_user_img;
    }
}
