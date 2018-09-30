package com.ityun.zhihuiyun.talk.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.view.CircleTextView;

import java.util.List;

/**
 * Created by Administrator on 2018/6/6 0006.
 */
public class MemberAdapter extends BaseAdapter {

    private Context context;

    private List<Member> mlist;

    private User user;

    public MemberAdapter(Context context) {
        this.context = context;
        user = SpUtil.getUser();
    }

    public void setData(List<Member> mlist) {
        this.mlist = mlist;
    }

    @Override
    public int getCount() {
        return null != mlist ? mlist.size() : 0;
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
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_talk_group_member, null);
            holder = new Holder();
            holder.item_talk_group_member_img = convertView.findViewById(R.id.item_talk_group_member_img);
            holder.item_talk_group_member_name = convertView.findViewById(R.id.item_talk_group_member_name);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (mlist.get(position).getId() == user.getId()) {
            holder.item_talk_group_member_img.setFillColor(context.getResources().getColor(R.color.main_color));
            holder.item_talk_group_member_img.setTextString(user.getUserName());
            holder.item_talk_group_member_name.setText(user.getUserName());
        } else {
            if (mlist.get(position).getMemberState() == 0) {
                holder.item_talk_group_member_img.setFillColor(context.getResources().getColor(R.color.biantai_gray));
                holder.item_talk_group_member_img.setTextColor(context.getResources().getColor(R.color.white));
                holder.item_talk_group_member_img.setText("未通话");
            } else if (mlist.get(position).getMemberState() == 1) {
                holder.item_talk_group_member_img.setFillColor(context.getResources().getColor(R.color.main_color));
                holder.item_talk_group_member_img.setTextColor(context.getResources().getColor(R.color.white));
                holder.item_talk_group_member_img.setTextString(mlist.get(position).getName());
            } else if (mlist.get(position).getMemberState() == 2) {
                holder.item_talk_group_member_img.setFillColor(context.getResources().getColor(R.color.biantai_gray));
                holder.item_talk_group_member_img.setTextColor(context.getResources().getColor(R.color.white));
                holder.item_talk_group_member_img.setText("已挂断");
            } else {
                holder.item_talk_group_member_img.setFillColor(context.getResources().getColor(R.color.biantai_gray));
                holder.item_talk_group_member_img.setTextColor(context.getResources().getColor(R.color.white));
                holder.item_talk_group_member_img.setText("未通话");
            }
            holder.item_talk_group_member_name.setText(mlist.get(position).getName());
        }
        return convertView;
    }

    class Holder {
        CircleTextView item_talk_group_member_img;
        TextView item_talk_group_member_name;
    }
}
