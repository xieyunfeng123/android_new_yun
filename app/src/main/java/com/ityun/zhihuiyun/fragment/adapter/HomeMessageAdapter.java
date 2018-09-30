package com.ityun.zhihuiyun.fragment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.HomeMessage;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.chat.gif.AnimatedGifDrawable;
import com.ityun.zhihuiyun.chat.gif.AnimatedImageSpan;
import com.ityun.zhihuiyun.db.IMUtil;
import com.ityun.zhihuiyun.fragment.MessageFragment;
import com.ityun.zhihuiyun.util.DensityUtil;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.view.CircleTextView;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/5/22 0022.
 */

public class HomeMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private List<HomeMessage> mlist;

    private final MessageFragment.OnListFragmentInteractionListener mListener;

    public HomeMessageAdapter(Context context, MessageFragment.OnListFragmentInteractionListener listener) {
        this.context = context;
        this.mListener = listener;
    }

    public void setData(List<HomeMessage> mlist) {
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_message, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final MessageHolder messageHolder = (MessageHolder) holder;
        messageHolder.message_img.setTextString(mlist.get(position).getName());
        SpannableStringBuilder sb = handler(messageHolder.message_content, mlist.get(position).getContent());
        messageHolder.message_content.setText(sb);
        long current = System.currentTimeMillis();
        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        if (zero <= mlist.get(position).getTime()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String time = dateFormat.format(new Date(mlist.get(position).getTime()));
            messageHolder.message_time.setText(time);
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
            String time = dateFormat.format(new Date(mlist.get(position).getTime()));
            messageHolder.message_time.setText(time);
        }

        messageHolder.message_name.setText(mlist.get(position).getName());
        messageHolder.item_home_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.OnClick(position);
            }
        });
        messageHolder.item_home_message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null) {
                    mListener.onListFragmentInteraction(mlist.get(position));
                }
                return true;
            }
        });

        List<IMMessage> messages = IMUtil.getInstance().selectMeaageByRead(App.getInstance().getSysConfBean().getAccountid(), mlist.get(position).getId(), 0);

        if (messages != null && messages.size() != 0) {
            for (IMMessage message : messages) {
                if (message.getIsSelf() == 1) {
                    message.setIsRead(1);
                }
            }
            if (messages.size() > 99) {
                messageHolder.read_num.setText("99+");
            } else {
                messageHolder.read_num.setText(messages.size() + "");
            }
            messageHolder.read_num.setVisibility(View.VISIBLE);
        } else {
            messageHolder.read_num.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return null != mlist ? mlist.size() : 0;
    }


    class MessageHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.message_img)
        CircleTextView message_img;

        @BindView(R.id.message_name)
        TextView message_name;

        @BindView(R.id.message_content)
        TextView message_content;

        @BindView(R.id.message_time)
        TextView message_time;

        @BindView(R.id.item_home_message)
        LinearLayout item_home_message;

        @BindView(R.id.read_num)
        CircleTextView read_num;

        public MessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    OnItemClickListener onItemClickListener;
    OnItemLongClickListener onItemLongClickListener;

    public void setOnItemClick(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void OnClick(int position);
    }

    public void setOnLongItemClick(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemLongClickListener {
        void OnClick(View view, int position);
    }

    private SpannableStringBuilder handler(final TextView gifTextView, String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            String png = tempText.substring("#[".length(), tempText.length() - "]#".length());
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(png));
                int x= DensityUtil.dip2px(context,15);
                sb.setSpan(new ImageSpan(context, setImgSize(bitmap, x, x)), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return sb;
    }

    public Bitmap setImgSize(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高.
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例.
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数.
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片.
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
}
