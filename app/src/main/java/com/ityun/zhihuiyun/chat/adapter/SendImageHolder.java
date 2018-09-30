package com.ityun.zhihuiyun.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseViewHolder;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.chat.ChatPicPreviewActivity;
import com.ityun.zhihuiyun.view.CircleTextView;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/5/28 0028.
 */

public class SendImageHolder extends BaseViewHolder<IMMessage> {

    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.iv_avatar)
    CircleTextView iv_avatar;


    @BindView(R.id.iv_fail_resend)
    ImageView iv_fail_resend;

    @BindView(R.id.progress_load)
    ProgressBar progress_load;

    @BindView(R.id.iv_picture)
    ImageView iv_picture;

    public SendImageHolder(Context context, ViewGroup root, int layoutRes) {
        super(context, root, layoutRes);
        this.context = context;
    }

    @Override
    public void bindData(final IMMessage imMessage) {
        if (imMessage.getSendState() == 1) {
            progress_load.setVisibility(View.VISIBLE);
            iv_fail_resend.setVisibility(View.GONE);
        } else if (imMessage.getSendState() == 2) {
            progress_load.setVisibility(View.INVISIBLE);
            iv_fail_resend.setVisibility(View.VISIBLE);
        } else {
            progress_load.setVisibility(View.INVISIBLE);
            iv_fail_resend.setVisibility(View.GONE);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        String time = dateFormat.format(imMessage.getCreateTime());
        tv_time.setText(time);
        Glide.with(context).load(new File(imMessage.getImagePath())).thumbnail(0.5f).error(R.mipmap.chat_extra_bg).into(iv_picture);
        iv_picture.setOnClickListener(v -> {
            String path = imMessage.getImagePath();
            File file = new File(imMessage.getImagePath());
            if (file.exists()) {
                Intent intent = new Intent(context, ChatPicPreviewActivity.class);
                intent.putExtra("path", path);
                context.startActivity(intent);
            } else {
                Toast.makeText(getContext(), "文件已删除！", Toast.LENGTH_SHORT).show();
            }
        });
        bindOnRestart(iv_fail_resend);
        bindClick(iv_picture);
    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void setUserName(String name) {
        iv_avatar.setTextString(name);
    }
}
