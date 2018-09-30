package com.ityun.zhihuiyun.group.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.bean.NewAccountInfo;
import com.ityun.zhihuiyun.department.adapter.TopNameAdaper;
import com.ityun.zhihuiyun.group.ShowMemberActivity;
import com.ityun.zhihuiyun.view.CircleBgImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowMemberAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private List<NewAccountInfo> mlist = new ArrayList<>();

    private OnItemClick onItemClick;

    public ShowMemberAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<NewAccountInfo> mlist) {
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_select_user, parent, false);
        return new MemberHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MemberHolder memberHolder = (MemberHolder)  holder;
        if (!TextUtils.isEmpty(mlist.get(position).getAccountRemarkName())) {
            memberHolder.item_user_name.setText(mlist.get(position).getAccountRemarkName());
            memberHolder.item_user_img.settText(mlist.get(position).getAccountRemarkName());
        } else {
            memberHolder.item_user_name.setText(mlist.get(position).getAccountName());
            memberHolder.item_user_img.settText(mlist.get(position).getAccountName());
        }

        memberHolder.item_user_check.setVisibility(View.GONE);
        if (getPositionForSection(position) == position) {
            memberHolder.item_top_tpye.setVisibility(View.VISIBLE);
            memberHolder.item_top_tpye.setText(mlist.get(position).getSort());
        } else {
            memberHolder.item_top_tpye.setVisibility(View.GONE);
        }
        ((MemberHolder) holder).item_select_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClick != null) {
                    onItemClick.onClick(position, mlist.get(position).isChoose());
                }
            }
        });
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < (getItemCount()); i++) {
            String sortStr = mlist.get(i).getSort();
            if (mlist != null && mlist.get(sectionIndex).getSort() != null && mlist.get(sectionIndex).getSort().equals(sortStr)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return null != mlist ? mlist.size() : 0;
    }

    class MemberHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_top_tpye)
        TextView item_top_tpye;

        @BindView(R.id.item_user_check)
        CheckBox item_user_check;

        @BindView(R.id.item_user_img)
        CircleBgImageView item_user_img;

        @BindView(R.id.item_user_name)
        TextView item_user_name;

        @BindView(R.id.item_select_ll)
        LinearLayout item_select_ll;

        public MemberHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick{
        void onClick(int position, boolean isChoose);
    }
}
