package com.ityun.zhihuiyun.me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.util.SpUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/21 0021.
 * 选择画面质量的画面
 */

public class ImageQualityActivity extends BaseActivity {

    /* 低质量 */
    @BindView(R.id.ll_low)
    LinearLayout low;

    /* 中等质量 */
    @BindView(R.id.ll_medium)
    LinearLayout medium;

    /* 高质量 */
    @BindView(R.id.ll_high)
    LinearLayout high;

    /* 返回按钮 */
    @BindView(R.id.bt_return)
    Button bt_return;

    /* 低质量的样式 */
    @BindView(R.id.iv_low)
    ImageView iv_low;

    /* 中等质量的样式 */
    @BindView(R.id.iv_medium)
    ImageView iv_medium;

    /* 高质量的样式 */
    @BindView(R.id.iv_high)
    ImageView iv_high;

    /* 清晰度 0为标清 1为高清 2为全高清 */
    private static int clarity = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagequality);

        ButterKnife.bind(this);

        initData();
    }

    /**
     * 获取默认的分辨率
     */
    private void initData() {
        // 获取默认分辨率
        int clarity = getIntent().getIntExtra("clarity", 0);
        // 设置样式
        if(clarity == 0) {
            iv_low.setBackgroundResource(R.mipmap.selected);
            iv_medium.setBackgroundResource(R.mipmap.notselected);
            iv_high.setBackgroundResource(R.mipmap.notselected);
        } else if (clarity == 1) {
            iv_low.setBackgroundResource(R.mipmap.notselected);
            iv_medium.setBackgroundResource(R.mipmap.selected);
            iv_high.setBackgroundResource(R.mipmap.notselected);
        } else {
            iv_low.setBackgroundResource(R.mipmap.notselected);
            iv_medium.setBackgroundResource(R.mipmap.notselected);
            iv_high.setBackgroundResource(R.mipmap.selected);
        }
    }

    /**
     * 点击事件
     * @param view
     */
    @OnClick({R.id.ll_low, R.id.ll_medium, R.id.ll_high, R.id.bt_return})
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.ll_low:
                clarity = 0;
                iv_low.setBackgroundResource(R.mipmap.selected);
                iv_medium.setBackgroundResource(R.mipmap.notselected);
                iv_high.setBackgroundResource(R.mipmap.notselected);
                saveData();
                break;
            case R.id.ll_medium:
                clarity = 1;
                iv_low.setBackgroundResource(R.mipmap.notselected);
                iv_medium.setBackgroundResource(R.mipmap.selected);
                iv_high.setBackgroundResource(R.mipmap.notselected);
                saveData();
                break;
            case R.id.ll_high:
                clarity = 2;
                iv_low.setBackgroundResource(R.mipmap.notselected);
                iv_medium.setBackgroundResource(R.mipmap.notselected);
                iv_high.setBackgroundResource(R.mipmap.selected);
                saveData();
                break;
            case R.id.bt_return:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 将分辨率保存到本地
     */
    private void saveData() {
        SpUtil.setClarity(this, clarity);
    }
}
