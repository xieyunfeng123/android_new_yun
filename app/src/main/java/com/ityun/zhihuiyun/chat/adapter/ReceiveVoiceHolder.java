package com.ityun.zhihuiyun.chat.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseViewHolder;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.db.IMUtil;
import com.ityun.zhihuiyun.util.MediaUtil;
import com.ityun.zhihuiyun.util.VoiceUtil;
import com.ityun.zhihuiyun.view.CircleTextView;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/5/28 0028.
 */

public class ReceiveVoiceHolder extends BaseViewHolder<IMMessage> {

    @BindView(R.id.tv_time)
    public TextView tv_time;

    @BindView(R.id.iv_avatar)
    public CircleTextView iv_avatar;

    @BindView(R.id.layout_voice)
    public LinearLayout layout_voice;
    @BindView(R.id.iv_voice)
    public ImageView iv_voice;
    @BindView(R.id.open_state)
    public View open_state;

    @BindView(R.id.tv_voice_length)
    public TextView tv_voice_length;

    AnimationDrawable animationDrawable;


    public ReceiveVoiceHolder(Context context, ViewGroup root, int layoutRes) {
        super(context, root, layoutRes);
        this.context = context;
    }

    @Override
    public void bindData(final IMMessage imMessage) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        String time = dateFormat.format(imMessage.getCreateTime());
        tv_time.setText(time);
        File file = new File(imMessage.getVoicePath());
        if (file.exists()) {
            double voiceLength = MediaUtil.getVoiceLength(imMessage.getVoicePath());
            tv_voice_length.setText((int) (voiceLength / 1000) + "\"");
        }
        if (imMessage.getVoiceHasOpen() == 1) {
            open_state.setVisibility(View.GONE);
        } else {
            open_state.setVisibility(View.VISIBLE);
        }
        iv_avatar.setTextString(imMessage.getMemberName());

        layout_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceUtil.getInstance().startPlay(context, iv_voice,R.drawable.chat_left_voice,imMessage.getVoicePath(), new VoiceUtil.OnVoiceListener() {
                    @Override
                    public void onStart() {
                        iv_voice.setImageResource(R.drawable.chat_left_voice);
                        animationDrawable = ((AnimationDrawable) iv_voice.getDrawable());
                        animationDrawable.start();
                        imMessage.setVoiceHasOpen(1);
                        IMUtil.getInstance().upDataVoiceOpen(imMessage);
                        open_state.setVisibility(View.GONE);
                    }

                    @Override
                    public void onEnd() {
                        iv_voice.setImageResource(R.drawable.chat_left_voice);
                        AnimationDrawable animationDrawable = (AnimationDrawable) iv_voice.getDrawable();
                        animationDrawable.stop();
                    }
                });
            }
        });
        bindClick(layout_voice);
    }




    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
