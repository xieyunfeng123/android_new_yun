package com.ityun.zhihuiyun.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseViewHolder;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.chat.ReceiveVideoDetailActivity;
import com.ityun.zhihuiyun.view.CircleTextView;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/7/2 0002.
 * 发送视频
 */

public class SendVideoHolder extends BaseViewHolder<IMMessage> {

    @BindView(R.id.tv_time)
    TextView tv_time;

    @BindView(R.id.iv_avatar)
    CircleTextView iv_avatar;

    @BindView(R.id.iv_fail_resend)
    ImageView iv_fail_resend;

    @BindView(R.id.progress_load)
    ProgressBar progress_load;

    @BindView(R.id.vv_video)
    VideoView vv_video;

    @BindView(R.id.play_pause)
    ImageView playPause;

    @BindView(R.id.send_video)
    RelativeLayout send_video;

    @BindView(R.id.iv_video)
    ImageView iv_video;

    @BindView(R.id.video_rl)
    RelativeLayout video_rl;


    public SendVideoHolder(Context context, ViewGroup root, int layoutRes) {
        super(context, root, layoutRes);
        this.context = context;
    }

    @Override
    public void bindData(IMMessage imMessage) {
        if (imMessage.getSendState() == 1) {
            progress_load.setVisibility(View.VISIBLE);
            iv_fail_resend.setVisibility(View.GONE);
        } else if (imMessage.getSendState() == 2) {
            progress_load.setVisibility(View.GONE);
            iv_fail_resend.setVisibility(View.VISIBLE);
        } else {
            progress_load.setVisibility(View.GONE);
            iv_fail_resend.setVisibility(View.GONE);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(imMessage.getCreateTime());
        tv_time.setText(time);
        initImage(imMessage);

        /**
         * 点击播放按钮跳转到播放界面
         */
        playPause.setOnClickListener(v -> {
            File file = new File(imMessage.getImagePath());
            if (file.exists()) {
                Intent intent = new Intent(context, ReceiveVideoDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("imMessage", imMessage);
                bundle.putString("path", imMessage.getImagePath());
                intent.putExtras(bundle);
                context.startActivity(intent);
            } else {
                Toast.makeText(getContext(), "文件已删除！", Toast.LENGTH_SHORT).show();
            }
        });
        bindClick(video_rl);
        bindOnRestart(iv_fail_resend);
    }

    /**
     * 初始化页面显示为视频第一帧
     */
    private void initImage(IMMessage imMessage) {
        iv_video.setVisibility(View.VISIBLE);
        vv_video.setVisibility(View.GONE);
        Glide.with(context).load(imMessage.getImagePath()).error(R.color.black).into(iv_video);
    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void setUserName(String name) {
        iv_avatar.setTextString(name);
    }
}
