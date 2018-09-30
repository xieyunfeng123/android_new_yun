package com.ityun.zhihuiyun.me;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.TeamViewMember;
import com.ityun.zhihuiyun.me.adapter.MyAdapter;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/7/12 0012.
 * 发送远程协助视频界面
 */

public class SendActivity extends BaseActivity {

    /* 接听 */
    @BindView(R.id.rl_receive)
    RelativeLayout receive;

    /* 发送视频头布局 */
    @BindView(R.id.rl_send_head)
    RelativeLayout send_head;


    /* 发送视频界面文本信息 */
    @BindView(R.id.tv_send_text)
    TextView send_text;

    /* 发送视频界面底部布局 */
    @BindView(R.id.ll_send_foot)
    LinearLayout send_foot;

    @BindView(R.id.receive_head)
    RelativeLayout receive_head;

    @BindView(R.id.receive_foot)
    RelativeLayout receive_foot;

//    @BindView(R.id.receive_text)
//    LinearLayout receive_text;

    @BindView(R.id.teamviewer_memeber)
    RecyclerView member;

    /* 摄像头 */
    @BindView(R.id.iv_camera)
    ImageView camera;

    @BindView(R.id.video_portrait)
    RelativeLayout video_portrait;


    private int top_width;

    private int top_height;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.item_send_teamviewer);
//        ButterKnife.bind(this);
    }

    @OnClick({R.id.rl_receive, R.id.iv_camera})
    public void onClick(View v) {
        switch (v.getId()) {
            // 点击接听
            case R.id.rl_receive:
                send_head.setVisibility(View.GONE);
                send_foot.setVisibility(View.GONE);
                send_text.setVisibility(View.GONE);
                receive_head.setVisibility(View.VISIBLE);
                receive_foot.setVisibility(View.GONE);
//                receive_text.setVisibility(View.VISIBLE);
                initRecy();
                break;
            // 点击摄像头
            case R.id.iv_camera:
//                receive_text.setVisibility(View.INVISIBLE);
                video_portrait.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }


    public void initRecy() {

        List<TeamViewMember> list = new ArrayList<>();
        TeamViewMember tm = new TeamViewMember();
        for (int i = 0; i < 7; i++) {
            tm.setImageName("txt0" + i);
            tm.setTextName("txt0" + i);
            list.add(tm);
        }
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        member.setLayoutManager(lm);
//        member.setAdapter(new MyAdapter(list));
        member.postDelayed(new Runnable() {
            @Override
            public void run() {
                top_width = receive_head.getWidth();
                top_height = receive_head.getHeight();
            }
        }, 300);
    }

    /**
     * 横竖屏切换
     */
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            receive_head.setLayoutParams(new RelativeLayout.LayoutParams(top_width, top_height));

        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            receive_head.setRotation(360.0f);
//            receive_foot.setRotation(360.0f);
            receive_head.setLayoutParams(new RelativeLayout.LayoutParams(top_width, top_height));
        }
    }
}
