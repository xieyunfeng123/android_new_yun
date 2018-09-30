package com.ityun.zhihuiyun.chat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.IMMessage;

import java.io.BufferedOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/7/4 0004.
 * 播放视频详情页面
 */

public class ReceiveVideoDetailActivity extends BaseActivity {

    @BindView(R.id.vv_video_detail)
    VideoView video;

    @BindView(R.id.playPause)
    LinearLayout playPause;

    @BindView(R.id.videoPauseImg)
    ImageView iv_playPause;

    @BindView(R.id.videoTotalTime)
    TextView totalTime;

    @BindView(R.id.videoSeekBar)
    SeekBar seekBar;

    @BindView(R.id.videoCurTime)
    TextView curTime;


    private TimerTask timerTask;

    private final int UPDATE_SEEKBAR = 1000;

    private Timer timer = new Timer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_receive_video_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        IMMessage imMessage = (IMMessage) intent.getSerializableExtra("imMessage");
        String path = intent.getStringExtra("path");
        playVideo(imMessage, path);
    }

    /**
     * 播放视频
     *
     * @param imMessage
     */
    public void playVideo(IMMessage imMessage, String path) {
        video.setOnPreparedListener(mp -> {
            fullScreen();
            video.start();
            int duration = video.getDuration();
            int[] time = getMinuteAndSecond(duration);
            totalTime.setText(String.format("%02d:%02d", time[0], time[1]));
            seekBar.setMax(duration);
//                seekBar.setProgress(video.getCurrentPosition());
            // 监听器 监听进度条
            if (timerTask != null) {
                timerTask.cancel();
            }
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    videoHandler.sendEmptyMessage(UPDATE_SEEKBAR);
                }
            };
            timer.schedule(timerTask, 0, 1000);
        });
        video.setOnCompletionListener(mp -> {
//            video.seekTo(0);
            seekBar.setProgress(0);
            iv_playPause.setImageResource(R.mipmap.icon_video_play);
//            video.stopPlayback();
        });

        video.setOnErrorListener((mp, what, extra) -> {
            video.stopPlayback();
            return true;
        });

        Uri uri = Uri.parse(path);
        video.setVideoURI(uri);

        playPause.setOnClickListener(v -> {
            if (video.isPlaying()) {
                iv_playPause.setImageResource(R.mipmap.icon_video_play);
                video.pause();
            } else {
                iv_playPause.setImageResource(R.mipmap.icon_video_pause);
                video.start();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int[] time = getMinuteAndSecond(progress);
                curTime.setText(String.format("%02d:%02d", time[0], time[1]));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                video.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                video.seekTo(seekBar.getProgress());
                video.start();
            }
        });
    }


    private Handler videoHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_SEEKBAR:
                    if (video.isPlaying()) {
                        seekBar.setProgress(video.getCurrentPosition());
                    }
                    break;
            }
        }
    };


    /**
     * 获取播放时间的分和秒
     *
     * @param mils
     * @return
     */
    private int[] getMinuteAndSecond(int mils) {
        mils /= 1000;
        int[] time = new int[2];
        time[0] = mils / 60;
        time[1] = mils % 60;
        return time;
    }


    /**
     * 设置全屏
     */
    private void fullScreen() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        video.setLayoutParams(layoutParams);
    }
}
