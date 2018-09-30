package com.ityun.zhihuiyun.me;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ibeiliao.badgenumberlibrary.BadgeNumberManager;
import com.ibeiliao.badgenumberlibrary.BadgeNumberManagerXiaoMi;
import com.ibeiliao.badgenumberlibrary.MobileBrand;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.db.IMUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/7/12 0012.
 * 远程协助界面
 */

public class TempActivity extends BaseActivity {

    /* 发送聊天请求 */
    @BindView(R.id.send)
    Button send;

    @BindView(R.id.setBadge)
    Button setBadge;

    @BindView(R.id.cleanBadge)
    Button cleanBadge;

    private List<IMMessage> messageList;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp);

        ButterKnife.bind(this);
        SysConfBean bean = App.getInstance().getSysConfBean();
        messageList = IMUtil.getInstance().selectAllMeaageByRead(bean.getAccountid(), 0);
    }

    @OnClick({R.id.send, R.id.setBadge, R.id.cleanBadge})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
//                Intent intent = new Intent(this, SendActivity.class);
//                startActivity(intent);
//                break;
            case R.id.setBadge:
                if (!Build.MANUFACTURER.equalsIgnoreCase(MobileBrand.XIAOMI)) {
                    BadgeNumberManager.from(TempActivity.this).setBadgeNumber(messageList.size());
                    Toast.makeText(TempActivity.this, "设置桌面角标成功", Toast.LENGTH_SHORT).show();
                } else {
                    setBadge.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setXiaomiBadgeNumber();
                        }
                    }, 3000);
                    //小米手机如果在应用内直接调用设置角标的方法，设置角标会不生效,因为在退出应用的时候角标会自动消除
                    //这里先退出应用，延迟3秒后再进行角标的设置，模拟在后台收到推送并更新角标的情景
                    moveTaskToBack(true);
                }
                break;
            case R.id.cleanBadge:
                BadgeNumberManager.from(TempActivity.this).setBadgeNumber(0);
                Toast.makeText(TempActivity.this, "清除桌面角标成功", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void setXiaomiBadgeNumber() {
        NotificationManager notificationManager = (NotificationManager) TempActivity.this.
                getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(TempActivity.this)
                .setSmallIcon(TempActivity.this.getApplicationInfo().icon)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("推送标题")
                .setContentText("我是推送内容")
                .setTicker("ticker")
                .setAutoCancel(true)
                .build();
        //相邻的两次角标设置如果数字相同的话，好像下一次会不生效
        BadgeNumberManagerXiaoMi.setBadgeNumber(notification, messageList.size());
        notificationManager.notify(1000, notification);
        Toast.makeText(TempActivity.this, "设置桌面角标成功", Toast.LENGTH_SHORT).show();
    }
}
