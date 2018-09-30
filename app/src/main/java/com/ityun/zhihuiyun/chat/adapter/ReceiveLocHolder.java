package com.ityun.zhihuiyun.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseViewHolder;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.locate.MapDetailActivity;
import com.ityun.zhihuiyun.util.CameraUtil;
import com.ityun.zhihuiyun.view.CircleTextView;

import java.text.SimpleDateFormat;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/6/27 0027.
 * 接收位置消息
 */

public class ReceiveLocHolder  extends BaseViewHolder<IMMessage> implements View.OnClickListener {

    @BindView(R.id.tv_time)
    public TextView tv_time;

    @BindView(R.id.iv_avatar)
    public CircleTextView iv_avatar;

    @BindView(R.id.iv_loc)
    public ImageView iv_loc;

    @BindView(R.id.receiveloc_address)
    public TextView address;


    private Context context;

    public ReceiveLocHolder(Context context, ViewGroup root, int layoutRes) {
        super(context, root, layoutRes);
        this.context = context;
    }

    @Override
    public void bindData(IMMessage imMessage) {
        Log.e("insert", "--------receive loc holder------------");
        iv_avatar.setTextString(imMessage.getMemberName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        String time = dateFormat.format(imMessage.getCreateTime());
        tv_time.setText(time);
        String message = imMessage.getMessage();
        String content[] = message.split("-");
        double longitude = Double.parseDouble(content[0]);
        double latitude = Double.parseDouble(content[1]);
        String add = content[2];
        Bitmap bitmap = CameraUtil.convertStringToIcon(content[3]);
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
        bindClick(iv_loc);
    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
