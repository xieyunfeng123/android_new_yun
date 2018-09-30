package com.ityun.zhihuiyun.group.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.chat.ChatActivity;
import com.ityun.zhihuiyun.view.CircleTextView;
import com.zhy.clientsdk.WMGroupInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;


/**
 * Created by Administrator on 2018/5/24 0024.
 */

public class GroupAdapter extends BaseAdapter {
    private Context context;

    private List<GroupInfo> mlist;

    public GroupAdapter(Context context) {
        this.context = context;
    }


    public void setData(List<GroupInfo> mlist) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
            groupHolder = new GroupHolder();
            groupHolder.item_group_name = convertView.findViewById(R.id.item_group_name);
            groupHolder.item_group_ll = convertView.findViewById(R.id.item_group_ll);
            groupHolder.item_group_img = convertView.findViewById(R.id.item_group_img);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        if (mlist.get(position).getName() != null) {
            try {
                String name = URLDecoder.decode(mlist.get(position).getName(), "utf-8");
                groupHolder.item_group_img.setTextString(name);
                groupHolder.item_group_name.setText(name);
            } catch (UnsupportedEncodingException e) {
                groupHolder.item_group_name.setText("");
            }
        } else {
            groupHolder.item_group_name.setText("");
        }
        groupHolder.item_group_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("groupinfo", mlist.get(position));
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class GroupHolder {

        public TextView item_group_name;

        public CircleTextView item_group_img;

        public LinearLayout item_group_ll;

    }
}
