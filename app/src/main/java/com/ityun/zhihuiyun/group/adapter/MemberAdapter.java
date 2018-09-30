package com.ityun.zhihuiyun.group.adapter;

import android.content.Context;
import android.text.TextUtils;
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
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.view.CircleBgImageView;

import java.util.List;

/**
 * Created by Administrator on 2018/7/4 0004.
 */

public class MemberAdapter extends BaseAdapter {

    private Context context;

    private GroupInfo groupInfo;

    private List<Account> mlist;

    private SysConfBean bean;

    public MemberAdapter(Context context) {
        this.context = context;
        bean = App.getInstance().getSysConfBean();
    }

    public void setData(GroupInfo groupInfo, List<Account> mlist) {
        this.groupInfo = groupInfo;
        this.mlist = mlist;
    }

    @Override
    public int getCount() {
        int size;
        if (groupInfo.getOwner() == bean.getAccountid()) {
            size = null != mlist ? mlist.size() : 0;
            size = size + 2;
            return (size > 7) ? 7 : size;
        } else {
            size = null != mlist ? mlist.size() : 0;
            size = size + 1;
            return size;
        }

    }

    @Override
    public Object getItem(int position) {
        if (position >= mlist.size()) {
            return null;
        } else {
            return mlist.get(position);
        }
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
            holder.item_user_img = convertView.findViewById(R.id.item_user_img);
            holder.item_user_name = convertView.findViewById(R.id.item_user_name);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.item_user_img.settText("");
        if (groupInfo.getOwner() == bean.getAccountid()) {
            if (position == (getCount() - 1)) {
                holder.item_user_img.setImageResource(R.mipmap.group_sbtruct_people);
                holder.item_user_name.setText("");
            } else if (position == (getCount() - 2)) {
                holder.item_user_img.setImageResource(R.mipmap.group_add_people);
                holder.item_user_name.setText("");
            } else {
                if (!TextUtils.isEmpty(mlist.get(position).getRemarkName())) {
                    holder.item_user_img.settText(mlist.get(position).getRemarkName());
                    holder.item_user_name.setText(mlist.get(position).getRemarkName());
                } else {
                    holder.item_user_img.settText(mlist.get(position).getName());
                    holder.item_user_name.setText(mlist.get(position).getName());
                }
            }
        } else {
            if (position == (getCount() - 1)) {
                holder.item_user_img.setImageResource(R.mipmap.group_add_people);
                holder.item_user_name.setText("");
            } else {
                if (!TextUtils.isEmpty(mlist.get(position).getRemarkName())) {
                    holder.item_user_img.settText(mlist.get(position).getRemarkName());
                    holder.item_user_name.setText(mlist.get(position).getRemarkName());
                } else {
                    holder.item_user_img.settText(mlist.get(position).getName());
                    holder.item_user_name.setText(mlist.get(position).getName());
                }
            }
        }
        return convertView;
    }

    class Holder {
        CircleBgImageView item_user_img;
        TextView item_user_name;
    }
}
