package com.ityun.zhihuiyun.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.util.screen.Eyes;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/6/20 0020.
 */

public class ChatPicPreviewActivity extends BaseActivity {

    @BindView(R.id.chat_img_preview)
    ImageView chat_img_preview;

    private String  path;

    private SysConfBean bean;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.black));
        setContentView(R.layout.activity_chat_pic_preview);
        ButterKnife.bind(this);
        bean = App.getInstance().getSysConfBean();
        path = getIntent().getStringExtra("path");
        if (path!=null) {
            Glide.with(this).load(new File(path)).into(chat_img_preview);
        } else {
            path=getIntent().getStringExtra("url");
            Glide.with(this).load( path).into(chat_img_preview);
        }

        chat_img_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
