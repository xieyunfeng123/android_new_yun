package com.ityun.zhihuiyun.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.db.RemarkUtil;
import com.ityun.zhihuiyun.util.KeyboartUtil;
import com.ityun.zhihuiyun.util.screen.Eyes;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/21 0021.
 */

public class RemarkActivity extends BaseActivity {

    @BindView(R.id.remark_back)
    ImageView remark_back;

    @BindView(R.id.add_remark_button)
    Button add_remark_button;

    @BindView(R.id.add_friend_remark)
    EditText add_friend_remark;

    private SysConfBean bean;

    private int id;

    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_remark);
        ButterKnife.bind(this);
        bean = App.getInstance().getSysConfBean();
        id = getIntent().getIntExtra("accountid", 0);
        type = getIntent().getIntExtra("type", 0);
    }

    @OnClick(R.id.remark_back)
    public void setOnFinishClick() {
        KeyboartUtil.hide(RemarkActivity.this, add_friend_remark);
        finish();
    }

    @OnClick(R.id.add_remark_button)
    public void setOnRemark() {
        if (TextUtils.isEmpty(add_friend_remark.getText().toString())) {
            RemarkUtil.getInstance().addRemark(bean.getAccountid(), id, " ");
        } else {
            RemarkUtil.getInstance().addRemark(bean.getAccountid(), id, add_friend_remark.getText().toString());
        }
//        RemarkUtil.getInstance().addRemark(bean.getAccountid(), id, add_friend_remark.getText().toString());
        if (type == 1) {
            Intent intent = getIntent();
            intent.putExtra("name", add_friend_remark.getText().toString());
            setResult(1, intent);
            finish();
        } else {
            finish();
        }
    }
    @Override
    protected void onStop() {
        KeyboartUtil.hide(RemarkActivity.this, add_friend_remark);
        super.onStop();
    }
}
