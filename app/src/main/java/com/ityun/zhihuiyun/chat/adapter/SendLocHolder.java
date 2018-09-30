package com.ityun.zhihuiyun.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseViewHolder;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.locate.MapDetailActivity;
import com.ityun.zhihuiyun.util.CameraUtil;
import com.ityun.zhihuiyun.view.CircleTextView;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/6/26 0026.
 */

public class SendLocHolder extends BaseViewHolder<IMMessage> {

    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.iv_avatar)
    CircleTextView iv_avatar;


    @BindView(R.id.iv_fail_resend)
    ImageView iv_fail_resend;

    @BindView(R.id.progress_load)
    ProgressBar progress_load;

    @BindView(R.id.iv_loc)
    ImageView iv_loc;

    @BindView(R.id.sendloc_address)
    TextView address;


    public SendLocHolder(Context context, ViewGroup root, int layoutRes) {
        super(context, root, layoutRes);
        this.context = context;
    }
    public void bindData(final IMMessage imMessage) {
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        String time = dateFormat.format(imMessage.getCreateTime());
        tv_time.setText(time);
        String message = imMessage.getMessage();
        String content[] = message.split("-");
        double longitude = Double.parseDouble(content[0]);
        double latitude = Double.parseDouble(content[1]);
        Bitmap bitmap = CameraUtil.convertStringToIcon(content[3]);
        String add = content[2];
        iv_loc.setImageBitmap(bitmap);
        address.setText(add);
        iv_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapDetailActivity.class);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude",latitude);
                intent.putExtra("address", add);
                getContext().startActivity(intent);
            }
        });
        bindOnRestart(iv_fail_resend);
        bindClick(iv_loc);
    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void setUserName(String name) {
        iv_avatar.setTextString(name);
    }
}
