package com.ityun.zhihuiyun.me.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.bean.ManageFile;
import com.ityun.zhihuiyun.view.NoScrollGridView;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class ShowPhotoAdapter extends RecyclerView.Adapter<ShowPhotoAdapter.MyViewHolder> {

    Context context;

    List<ManageFile> mlist;

    OnPicListener onPicListener;

    boolean isShow;

    public ShowPhotoAdapter(Context context) {
        this.context = context;
    }

    public void isShowView(boolean isShow) {
        this.isShow = isShow;
    }

    public void setData(List<ManageFile> mlist) {
        this.mlist = mlist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_showphoto, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        holder.item_manage_date.setText(format.format(new Date()));

        ImageGridAdapter adapter = new ImageGridAdapter(context);
        adapter.setData(mlist.get(position).getItemFiles());
        adapter.isShowView(isShow);
        holder.item_manage_scrollgridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        holder.item_manage_scrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int childposition, long id) {
                if (onPicListener != null) {
                    onPicListener.OnClick(position, childposition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView item_manage_date;
        NoScrollGridView item_manage_scrollgridview;

        public MyViewHolder(View view) {
            super(view);
            item_manage_date = (TextView) view.findViewById(R.id.item_showphoto_date);
            item_manage_scrollgridview = (NoScrollGridView) view.findViewById(R.id.item_showphoto);
        }
    }

    public void setOnPicListener(OnPicListener onPicListener) {
        this.onPicListener = onPicListener;
    }

    public interface OnPicListener {
        void OnClick(int groupPosition, int childPosition);
    }
}
