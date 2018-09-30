package com.ityun.zhihuiyun.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.bean.Department;
import com.ityun.zhihuiyun.view.CircleTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/5/23 0023.
 */

public class DepartmentAdapter extends BaseAdapter {

    private Context context;

    private List<Department> mlist;

    public DepartmentAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Department> mlist) {
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
        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_department, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.item_friend_name.setText(mlist.get(position).getName());
//        holder.item_friend_img.setTextString(mlist.get(position).getName());
        holder.item_friend_state.setVisibility(View.GONE);
        holder.item_friend_right.setVisibility(View.VISIBLE);
        return convertView;
    }

    class Holder {
 /*       @BindView(R.id.item_friend_img)
        CircleTextView item_friend_img;*/

        @BindView(R.id.item_friend_name)
        TextView item_friend_name;

        @BindView(R.id.item_friend_state)
        ImageView item_friend_state;

        @BindView(R.id.item_friend_right)
        ImageView item_friend_right;

        View view;

        public Holder(View view) {
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}
