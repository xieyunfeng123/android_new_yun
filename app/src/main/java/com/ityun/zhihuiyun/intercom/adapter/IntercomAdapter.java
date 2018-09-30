package com.ityun.zhihuiyun.intercom.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.view.CircleBgImageView;

import java.util.List;

public class IntercomAdapter extends BaseAdapter {

    private Context context;

    private GroupInfo gruopInfo;

    private List<Account> list;

    private SysConfBean bean;

    public IntercomAdapter(Context context) {
        this.context = context;
        bean = App.getInstance().getSysConfBean();
    }

    public void setData(GroupInfo groupInfo, List<Account> list) {
        this.gruopInfo = groupInfo;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 2;
        } else if (list.size() >= 5) {
            return 7;
        } else {
            return list.size() + 2;
        }
//        int size;
//        if (gruopInfo.getOwner() == bean.getAccountid()) {
//            size = null != list ? list.size() : 0;
//            size += 2;
//            return size;
//        } else {
//            size = null != list ? list.size() : 0;
//            size += 1;
//            return size;
//        }
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group_member, null);
            holder = new Holder();
            holder.cv_name = convertView.findViewById(R.id.item_user_img);
            holder.tv_name = convertView.findViewById(R.id.item_user_name);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.cv_name.settText("");

        if (position == getCount() - 1) {
            holder.cv_name.setImageResource(R.mipmap.group_sbtruct_people);
            holder.tv_name.setText("");
        } else if (position == getCount() - 2) {
            holder.cv_name.setImageResource(R.mipmap.group_add_people);
            holder.tv_name.setText("");
        } else {
            holder.cv_name.settText(list.get(position).getName());
            holder.tv_name.setText(list.get(position).getName());
        }
//        } else {
//            if (position == getCount()) {
//                holder.cv_name.setImageResource(R.mipmap.group_add_people);
//                holder.tv_name.setText("");
//            } else {
//                holder.cv_name.settText(list.get(position).getName());
//                holder.tv_name.setText(list.get(position).getName());
//            }
//        }
        return convertView;
    }

    class Holder {
        CircleBgImageView cv_name;
        TextView tv_name;
    }
}
