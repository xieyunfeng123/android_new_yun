package com.ityun.zhihuiyun.fragment.adapter;

import android.content.Context;
import android.media.Image;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.RemarkName;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.db.RemarkUtil;
import com.ityun.zhihuiyun.view.CircleBgImageView;
import com.ityun.zhihuiyun.view.CircleTextView;
import com.zhy.clientsdk.Constants;
import com.zhy.clientsdk.WMAccountInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/5/23 0023.
 */

public class FriendAdapter extends BaseAdapter {

    private Context context;

    private List<Account> mlist;

    private SysConfBean bean;

    public FriendAdapter(Context context) {
        this.context = context;
        bean = App.getInstance().getSysConfBean();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friend, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.item_friend_name.setText(mlist.get(position).getName());
        holder.item_friend_img.setTextString(mlist.get(position).getName());
        if (mlist.get(position).getStatus() == 1) {
            holder.item_friend_state.setImageResource(R.mipmap.tongxun_online);
        } else {
            holder.item_friend_state.setImageResource(R.mipmap.tongxun_outline);
        }
        return convertView;
    }

    class Holder {
        @BindView(R.id.item_friend_img)
        CircleTextView item_friend_img;

        @BindView(R.id.item_friend_name)
        TextView item_friend_name;

        @BindView(R.id.item_friend_state)
        ImageView item_friend_state;

        View view;

        public Holder(View view) {
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}
