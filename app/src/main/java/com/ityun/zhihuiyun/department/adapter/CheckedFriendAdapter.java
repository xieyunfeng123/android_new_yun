package com.ityun.zhihuiyun.department.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.view.CircleTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/23 0023.
 */

public class CheckedFriendAdapter extends BaseAdapter {

    private Context context;

    private List<Account> mlist;

    public CheckedFriendAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Account> mlist) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_select_friend, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.item_user_img.setTextString(mlist.get(position).getName());
        holder.item_user_name.setText(mlist.get(position).getName());
//        mlist.get(position).setChoose(true);
        holder.item_user_check.setChecked(mlist.get(position).isChoose());
        holder.item_department_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });
        holder.item_user_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onClick.onCheckedClick(position, isChecked);
            }
        });

        return convertView;
    }

    class Holder {
        @BindView(R.id.item_department_ll)
        LinearLayout item_department_ll;

        @BindView(R.id.item_user_check)
        CheckBox item_user_check;

        @BindView(R.id.item_user_img)
        CircleTextView item_user_img;

        @BindView(R.id.item_user_name)
        TextView item_user_name;

        View view;

        public Holder(View view) {
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }

    OnClick onClick;

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    public interface OnClick {
        void onItemClick(int position);
        void onCheckedClick(int position, boolean isCheck);
    }
}
