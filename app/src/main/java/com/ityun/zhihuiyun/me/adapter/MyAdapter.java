package com.ityun.zhihuiyun.me.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.view.CircleTextView;

import java.util.List;

/**
 * Created by Administrator on 2018/7/12 0012.
 * 远程协助中recyclerview适配器
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


    private Context context;

    private List<Member> list;

    private User user;

    public MyAdapter(List<Member> list, Context context) {
        this.list = list;
        this.context=context;
        user = SpUtil.getUser();
    }


    /**
     * 加载item布局，创建viewholer实例
     *
     * @param parent
     * @param viewType
     * @return
     */
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teamviewer_member, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    /**
     * 对RecyclerView子项数据进行赋值
     * @param holder
     * @param position
     */
    public void onBindViewHolder(ViewHolder holder, int position) {
        Member member = list.get(position);
        holder.cv_member.setText(member.getName());
        holder.tv_member.setText(member.getName());

        // 设置当前用户样式
        if (list.get(position).getId() == user.getId()) {
            holder.cv_member.setText(user.getUserName());
            holder.tv_member.setText(user.getUserName());
        } else {
            if (list.get(position).getMemberState() == 0) {
                holder.cv_member.setFillColor(context.getResources().getColor(R.color.trans_grey));
                holder.cv_member.setTextColor(context.getResources().getColor(R.color.main_color));
                holder.cv_member.setText("未接通");
            } else if (list.get(position).getMemberState() == 1) {
                holder.cv_member.setFillColor(context.getResources().getColor(R.color.main_color));
                holder.cv_member.setText(list.get(position).getName());
            } else if (list.get(position).getMemberState() == 2) {
                holder.cv_member.setFillColor(context.getResources().getColor(R.color.trans_grey));
                holder.cv_member.setTextColor(context.getResources().getColor(R.color.main_color));
                holder.cv_member.setText("已挂断");
            } else {
                holder.cv_member.setFillColor(context.getResources().getColor(R.color.talking_bg));
                holder.cv_member.setText("讲话中");
            }
            holder.tv_member.setText(list.get(position).getName());
        }
    }

    /**
     * @return 返回子项个数
     */
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleTextView cv_member;
        TextView tv_member;

        public ViewHolder(View view) {
            super(view);
            cv_member = (CircleTextView) view.findViewById(R.id.cv_member);
            tv_member = (TextView) view.findViewById(R.id.tv_member);
        }
    }
}
