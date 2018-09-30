package com.ityun.zhihuiyun.department.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.bean.Department;
import com.ityun.zhihuiyun.group.SelectDepartmentActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/8/30 0030.
 */

public class TopNameAdaper extends RecyclerView.Adapter<TopNameAdaper.DePartmentNameHolder> {

    Context context;

    private List<Department> mlist;

    public TopNameAdaper(Context context) {
        this.context = context;
    }

    public void setData(List<Department> mlist) {
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public DePartmentNameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DePartmentNameHolder(LayoutInflater.from(context).inflate(R.layout.item_department_name, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DePartmentNameHolder holder, int position) {
        if (position == 0) {
            holder.item_department_name.setText("联系人");
        } else {
            holder.item_department_name.setText(mlist.get(position - 1).getName());
        }

        if ((position - 1) == (mlist.size() - 1)) {
            holder.item_department_name.setTextColor(context.getResources().getColor(R.color.text_color));
            holder.item_department_right.setVisibility(View.GONE);
        } else {
            holder.item_department_name.setTextColor(context.getResources().getColor(R.color.main_color));
            holder.item_department_right.setVisibility(View.VISIBLE);
        }

        /*holder.item_department_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.item_department_name.getText().toString().contains("联系人")) {
                    Intent intent = new Intent(context, SelectDepartmentActivity.class);
                    context.startActivity(intent);
                }
            }
        });*/

        holder.item_department_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0 && position != (mlist.size())) {
                    if (onItemClick != null)
                        onItemClick.onClik(position - 1);
                }
            }
        });

       /* holder.item_department_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNameClick.onClick(holder.item_department_name.getText().toString());
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return null != mlist ? (mlist.size() + 1) : 1;
    }

    class DePartmentNameHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_department_name)
        TextView item_department_name;
        @BindView(R.id.item_department_right)
        ImageView item_department_right;
        @BindView(R.id.item_department_ll)
        LinearLayout item_department_ll;

        public DePartmentNameHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    OnItemClick onItemClick;

//    OnNameClick onNameClick;

    public void setOnItemClickListener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    /*public void setOnNameClick(OnNameClick onNameClick) {
        this.onNameClick = onNameClick;
    }*/


    public interface OnItemClick {
        void onClik(int position);
    }

 /*   public interface OnNameClick {
        void onClick(String name);
    }*/
}
