package com.ityun.zhihuiyun.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseViewHolder;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.chat.ChatPicPreviewActivity;
import com.ityun.zhihuiyun.view.CircleTextView;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/5/28 0028.
 */

public class ReceiveImageHolder extends BaseViewHolder<IMMessage> {

    @BindView(R.id.tv_time)
    public TextView tv_time;

    @BindView(R.id.iv_avatar)
    public CircleTextView iv_avatar;

    @BindView(R.id.iv_picture)
    public ImageView iv_picture;

    private Context context;

    SysConfBean bean;

    public ReceiveImageHolder(Context context, ViewGroup root, int layoutRes) {
        super(context, root, layoutRes);
        this.context = context;
        bean = App.getInstance().getSysConfBean();
    }

    @Override
    public void bindData(final IMMessage imMessage) {
        iv_avatar.setTextString(imMessage.getMemberName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        String time = dateFormat.format(imMessage.getCreateTime());
        tv_time.setText(time);
        String path = "";
        if (imMessage.getImType() == 0) {
            path = "http://" + bean.getVfilesvrip() + ":" + bean.getVfilesvrdownport() + "/" + imMessage.getImId() + "/" + imMessage.getFileUrl();
        } else {
            path = "http://" + bean.getVfilesvrip() + ":" + bean.getVfilesvrdownport() + "/" + imMessage.getMessageSendId() + "/" + imMessage.getFileUrl();
        }
        Glide.with(context).load(path).placeholder(R.mipmap.chat_extra_bg).error(R.mipmap.chat_extra_bg).into(iv_picture);
        final String intentPath = path;
        iv_picture.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatPicPreviewActivity.class);
            intent.putExtra("url", intentPath);
            context.startActivity(intent);
        });
        bindClick(iv_picture);
    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}

