package com.ityun.zhihuiyun.group.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.NewAccountInfo;
import com.ityun.zhihuiyun.view.CircleBgImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/5/25 0025.
 */

public class AddorDeleteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int TOP = 0;

    private int ITEM = 1;

    private Context context;

    private List<Account> mlist;

    private OnItemOnClick onItemOnClick;

    public AddorDeleteAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Account> mlist) {
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TOP) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_search_edittext, parent, false);
            return new TopHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_select_user, parent, false);
            return new ItemUserHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (position == 0) {
            TopHolder topHolder = (TopHolder) holder;
            topHolder.item_search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(onItemOnClick!=null)
                            onItemOnClick.onTextChanged(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } else {
            ItemUserHolder itemUserHolder = (ItemUserHolder) holder;
            itemUserHolder.item_user_name.setText(mlist.get(position - 1).getName());
            if (getPositionForSection(position - 1) == (position - 1)) {
                itemUserHolder.item_top_tpye.setVisibility(View.VISIBLE);
                itemUserHolder.item_top_tpye.setText(mlist.get(position - 1).getSort());
            } else {
                itemUserHolder.item_top_tpye.setVisibility(View.GONE);
            }
            itemUserHolder.item_user_img.settText(mlist.get(position - 1).getName());
            itemUserHolder.item_user_check.setChecked(mlist.get(position - 1).isChoose());
            itemUserHolder.item_select_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemOnClick != null)
                        onItemOnClick.onClick(position - 1, !mlist.get(position - 1).isChoose());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TOP;
        else
            return ITEM;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < (getItemCount() - 1); i++) {
            String sortStr = mlist.get(i).getSort();
            if (mlist != null && mlist.get(sectionIndex).getSort() != null && mlist.get(sectionIndex).getSort().equals(sortStr)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return null != mlist ? (mlist.size() + 1) : 1;
    }

    class TopHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_search)
        EditText item_search;

        public TopHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ItemUserHolder extends RecyclerView.ViewHolder {
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

        public ItemUserHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnItemChooseListener(OnItemOnClick onItemOnClick) {
        this.onItemOnClick = onItemOnClick;
    }

    public interface OnItemOnClick {
        void onClick(int position, boolean isChoose);
        void onTextChanged(String str);
    }
}
