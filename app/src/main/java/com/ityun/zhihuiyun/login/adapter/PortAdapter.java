package com.ityun.zhihuiyun.login.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.IpInfo;

import java.util.List;

public class PortAdapter extends BaseAdapter {
    private Context context;
    private List<IpInfo> mlist;

    public PortAdapter(Context context, List<IpInfo> mlist) {
        this.context = context;
        this.mlist = mlist;
    }

    @Override
    public int getCount() {
        return mlist.size();
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
        Holder holder ;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.prot_item, parent, false);
            holder = new Holder();
            holder.tv_ip = convertView.findViewById(R.id.tv_ip);
            holder.iv_true =  convertView.findViewById(R.id.iv_true);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_ip.setText(mlist.get(position).getIp() + ":" + (mlist.get(position).getPort() == 0 ? App.old_oprt : mlist.get(position).getPort()));
        return convertView;
    }

    class Holder {
        TextView tv_ip;
        ImageView iv_true;
    }
}
