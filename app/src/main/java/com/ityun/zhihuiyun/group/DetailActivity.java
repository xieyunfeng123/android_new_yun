package com.ityun.zhihuiyun.group;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.HomeMessage;
import com.ityun.zhihuiyun.chat.ChatActivity;
import com.ityun.zhihuiyun.chat.RemarkActivity;
import com.ityun.zhihuiyun.db.HomeMessageUtil;
import com.ityun.zhihuiyun.db.IMUtil;
import com.ityun.zhihuiyun.event.ReceiveMessageEvent;
import com.ityun.zhihuiyun.view.CircleTextView;
import com.ityun.zhihuiyun.view.ProgressDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends BaseActivity {

    @BindView(R.id.username_img)
    CircleTextView cv_user;

    @BindView(R.id.username_text)
    TextView tv_user;

    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.tv_prename)
    TextView preName;

    private GroupInfo groupInfo;

    private Account account;

    private String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        account = (Account) getIntent().getSerializableExtra("account");
        groupInfo = (GroupInfo) getIntent().getSerializableExtra("groupInfo");
        if (!TextUtils.isEmpty(account.getRemarkName())) {
            cv_user.setTextString(account.getRemarkName());
            tv_user.setText(account.getRemarkName());
        } else {
            cv_user.setTextString(account.getName());
            tv_user.setText(account.getName());
        }
        preName.setText("原始昵称：" + account.getName());
    }

    @OnClick({R.id.ll_alter, R.id.ll_clear, R.id.iv_back, R.id.chat_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_alter:
                Intent i = new Intent(this, RemarkActivity.class);
                i.putExtra("accountid", account.getId());
                i.putExtra("type", 1);
                startActivityForResult(i, 1);
                break;
            case R.id.ll_clear:
                clear();
                break;
            case R.id.iv_back:
                Intent intent1 = getIntent();
                intent1.putExtra("name", name);
                setResult(1, intent1);
                finish();
                break;
            case R.id.chat_button:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("account", account);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 清除聊天记录
     */
    private void clear() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定清空聊天记录吗？");
        builder.setTitle("提示");
        builder.setNegativeButton("取消", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setPositiveButton("确定", (dialog, which) -> {
            final Dialog loadingDialog = ProgressDialog.create(this,
                    "正在清理……");
            loadingDialog.show();
            IMUtil.getInstance().deleteDB(App.getInstance().getSysConfBean().getAccountid(), groupInfo.getId());
            HomeMessage homeMessage = HomeMessageUtil.getInstance().selectMessageById(App.getInstance().getSysConfBean().getAccountid(), groupInfo.getId());
            HomeMessageUtil.getInstance().deleteMessage(homeMessage);
            EventBus.getDefault().post(new ReceiveMessageEvent(null));
            iv_back.postDelayed(() -> {
                if (loadingDialog != null)
                    loadingDialog.dismiss();
            }, 1500);
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1 && resultCode == 1) {
            name = data.getStringExtra("name");
            cv_user.setTextString(name);
            tv_user.setText(name);
        }
    }
}
