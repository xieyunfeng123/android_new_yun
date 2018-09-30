package com.ityun.zhihuiyun.chat.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseViewHolder;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.util.MediaUtil;
import com.ityun.zhihuiyun.util.VoiceUtil;
import com.ityun.zhihuiyun.view.CircleTextView;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/5/28 0028.
 */

public class SendVoiceHolder extends BaseViewHolder<IMMessage> {

    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.iv_avatar)
    CircleTextView iv_avatar;

    @BindView(R.id.layout_voice)
    RelativeLayout layout_voice;

    @BindView(R.id.iv_voice)
    ImageView iv_voice;

    @BindView(R.id.tv_voice_length)
    TextView tv_voice_length;

    @BindView(R.id.iv_fail_resend)
    ImageView iv_fail_resend;

    @BindView(R.id.progress_load)
    ProgressBar progress_load;


    AnimationDrawable animationDrawable;




    public SendVoiceHolder(Context context, ViewGroup root, int layoutRes) {
        super(context, root, layoutRes);
        this.context = context;
    }

    @Override
    public void bindData(final IMMessage imMessage) {

        if (imMessage.getSendState() == 1) {
            progress_load.setVisibility(View.VISIBLE);
            iv_fail_resend.setVisibility(View.INVISIBLE);
        } else if (imMessage.getSendState() == 2) {
            progress_load.setVisibility(View.INVISIBLE);
            iv_fail_resend.setVisibility(View.VISIBLE);
        } else {
            progress_load.setVisibility(View.INVISIBLE);
            iv_fail_resend.setVisibility(View.INVISIBLE);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        String time = dateFormat.format(imMessage.getCreateTime());
        tv_time.setText(time);
        File file = new File(imMessage.getVoicePath());
        if (file.exists()) {
            double voiceLength = MediaUtil.getVoiceLength(imMessage.getVoicePath());
            tv_voice_length.setText((int) (voiceLength / 1000) + "\"");
        }
        layout_voice.setOnClickListener(v -> VoiceUtil.getInstance().startPlay(context, iv_voice,R.drawable.chat_right_voice,imMessage.getVoicePath(), new VoiceUtil.OnVoiceListener() {
            @Override
            public void onStart() {
                iv_voice.setImageResource(R.drawable.chat_right_voice);
                animationDrawable = ((AnimationDrawable) iv_voice.getDrawable());
                animationDrawable.start();
            }

            @Override
            public void onEnd() {
                iv_voice.setImageResource(R.drawable.chat_right_voice);
                AnimationDrawable animationDrawable = (AnimationDrawable) iv_voice.getDrawable();
                animationDrawable.stop();
            }
        }));
        bindClick(layout_voice);
        bindOnRestart(iv_fail_resend);
    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }


    public void setUserName(String name) {
        iv_avatar.setTextString(name);
    }
}
