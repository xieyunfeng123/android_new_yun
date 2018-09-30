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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseViewHolder;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.chat.ChatPicPreviewActivity;
import com.ityun.zhihuiyun.chat.ReceiveVideoDetailActivity;
import com.ityun.zhihuiyun.view.CircleTextView;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/5/28 0028.
 * 接收视频
 */

class ReceiveVideoHolder extends BaseViewHolder<IMMessage> {

    @BindView(R.id.tv_time)
    TextView tv_time;

    @BindView(R.id.iv_avatar)
    CircleTextView iv_avatar;

    @BindView(R.id.play_pause)
    ImageView playPause;

    @BindView(R.id.iv_video)
    ImageView iv_video;

    @BindView(R.id.video_rl)
    RelativeLayout video_rl;
    private Context context;


    String path = "";

    SysConfBean bean;

    public ReceiveVideoHolder(Context context, ViewGroup root, int layoutRes) {
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
        if (imMessage.getImType() == 0) {
            path = "http://" + bean.getVfilesvrip() + ":" + bean.getVfilesvrdownport() + "/" + imMessage.getImId() + "/" + imMessage.getFileUrl();
        } else {
            path = "http://" + bean.getVfilesvrip() + ":" + bean.getVfilesvrdownport() + "/" + imMessage.getMessageSendId() + "/" + imMessage.getFileUrl();
        }
        initImage();
        playPause.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReceiveVideoDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("imMessage", imMessage);
            bundle.putString("path", path);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
        bindClick(video_rl);
    }

    /**
     * 初始化页面显示为视频第一帧
     */
    private void initImage() {
        iv_video.setVisibility(View.VISIBLE);
        new Thread(() -> {
            Bitmap bitmap = getNetVideoBitmap(path);
            Message message = new Message();
            message.obj = bitmap;
            handler.sendMessage(message);
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (iv_video != null) {
                Bitmap bitmap = (Bitmap) msg.obj;
                if (bitmap != null) {
                    iv_video.setImageBitmap(bitmap);
                } else {
                    iv_video.setImageResource(R.color.white);
                }
            }
        }
    };


    public Bitmap getNetVideoBitmap(String videoUrl) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据url获取缩略图
            retriever.setDataSource(videoUrl, new HashMap());
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }


    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}

