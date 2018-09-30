package com.ityun.zhihuiyun.department.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.bean.Department;
import com.ityun.zhihuiyun.view.CircleTextView;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/5/23 0023.
 */

public class CheckedAdapter extends BaseAdapter {

    private Context context;

    private List<Department> mlist;


    public CheckedAdapter(Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_department_check, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        Holder finalHolder = holder;
        holder.to_next_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!finalHolder.cb_check.isChecked()) {
                    onClick.onDepartmentClick(position);
                }
            }
        });
//        holder.item_user_img.setTextString(mlist.get(position).getName());
        holder.item_user_name.setText(mlist.get(position).getName());
        holder.cb_check.setChecked(mlist.get(position).isCheck());
        holder.cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onClick.onCheckBoxChange(position, isChecked);
                if (isChecked) {
                    finalHolder.tv_next.setTextColor(context.getResources().getColor(R.color.gray));
                } else {
                    finalHolder.tv_next.setTextColor(context.getResources().getColor(R.color.main_color));
                }
            }
        });
        holder.item_select_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });
        return convertView;
    }

    public static class Holder {

        @BindView(R.id.item_select_ll)
        LinearLayout item_select_ll;

       /* @BindView(R.id.item_user_img)
        CircleTextView item_user_img;*/

        @BindView(R.id.item_user_name)
        TextView item_user_name;

        @BindView(R.id.cb_check)
        CheckBox cb_check;

        @BindView(R.id.to_next_ll)
        LinearLayout to_next_ll;

        @BindView(R.id.tv_next)
        TextView tv_next;

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

        void onDepartmentClick(int position);

        void onCheckBoxChange(int position, boolean isCheck);
    }
}
