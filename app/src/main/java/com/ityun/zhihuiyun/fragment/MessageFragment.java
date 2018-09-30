package com.ityun.zhihuiyun.fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ibeiliao.badgenumberlibrary.BadgeNumberManager;
import com.ibeiliao.badgenumberlibrary.BadgeNumberManagerXiaoMi;
import com.ibeiliao.badgenumberlibrary.MobileBrand;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.HomeMessage;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.chat.ChatActivity;
import com.ityun.zhihuiyun.db.HomeMessageUtil;
import com.ityun.zhihuiyun.db.IMUtil;
import com.ityun.zhihuiyun.event.HomeEvent;
import com.ityun.zhihuiyun.fragment.adapter.HomeMessageAdapter;
import com.ityun.zhihuiyun.home.HomeActivity;
import com.ityun.zhihuiyun.util.SpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/5/22 0022.
 * 消息
 */

public class MessageFragment extends Fragment {

    @BindView(R.id.message_list)
    RecyclerView message_list;

    private Handler mhandler;

    private List<IMMessage> messageList;

    private HomeMessageAdapter adapter;

    private List<HomeMessage> mlist = new ArrayList<>();

    private OnListFragmentInteractionListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, view);
        adapter = new HomeMessageAdapter(getActivity(), mListener);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        initBadgeNumber();
        message_list.setLayoutManager(manager);
        adapter.setData(mlist);
        message_list.setAdapter(adapter);
        message_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        adapter.setOnItemClick(position -> {
            if (mlist.get(position).getImType() == 0) {
                accountIntent(mlist.get(position).getId());
            } else {
                groupIntent(mlist.get(position).getId());
            }
        });
        return view;
    }

    /**
     * 显示图标的未读消息数量
     */
    private void initBadgeNumber() {
        mhandler = new Handler();

        SysConfBean bean = App.getInstance().getSysConfBean();
        messageList = IMUtil.getInstance().selectAllMeaageByRead(bean.getAccountid(), 0);
        if (!Build.MANUFACTURER.equalsIgnoreCase(MobileBrand.XIAOMI)) {
            BadgeNumberManager.from(getActivity()).setBadgeNumber(messageList.size());
        } else {
            Runnable runnable = () -> {
                // TODO Auto-generated method stub
                //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setXiaomiBadgeNumber();
                    }
                }, 3000);
            };

            //小米手机如果在应用内直接调用设置角标的方法，设置角标会不生效,因为在退出应用的时候角标会自动消除
            //这里先退出应用，延迟3秒后再进行角标的设置，模拟在后台收到推送并更新角标的情景
//            moveTaskToBack(true);
        }
    }

    /**
     * 小米手机图标显示未读消息数量
     */
    private void setXiaomiBadgeNumber() {
        NotificationManager notificationManager = (NotificationManager) getActivity().
                getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(getActivity().getApplicationInfo().icon)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("推送标题")
                .setContentText("我是推送内容")
                .setTicker("ticker")
                .setAutoCancel(true)
                .build();
        //相邻的两次角标设置如果数字相同的话，好像下一次会不生效
        BadgeNumberManagerXiaoMi.setBadgeNumber(notification, messageList.size());
        notificationManager.notify(1000, notification);
        Toast.makeText(getActivity(), "设置桌面角标成功", Toast.LENGTH_SHORT).show();
    }


    /**
     * 单人聊天的跳转
     *
     * @param id
     */
    private void accountIntent(int id) {
        List<Account> accounts = App.accountList;
        initBadgeNumber();
        if (accounts != null && accounts.size() != 0) {
            Account intentAccount = null;
            for (Account account : accounts) {
                if (account.getId() == id) {
                    intentAccount = account;
                }
            }
            if (intentAccount != null) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("account", intentAccount);
                startActivity(intent);
            }

        }
    }

    /**
     * 群聊的跳转
     *
     * @param id
     */
    private void groupIntent(int id) {
        initBadgeNumber();
        List<GroupInfo> groupInfos = HomeActivity.groupInfos;
        if (groupInfos != null && groupInfos.size() != 0) {
            GroupInfo intentGroup = null;
            for (GroupInfo groupInfo : groupInfos) {
                if (groupInfo.getId() == id) {
                    intentGroup = groupInfo;
                }
            }
            if (intentGroup != null) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("groupinfo", intentGroup);
                startActivity(intent);
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initBadgeNumber();
        message_list.postDelayed(new Runnable() {
            @Override
            public void run() {
                mlist.clear();
                List<HomeMessage> homeMessages = HomeMessageUtil.getInstance().selectMessage(SpUtil.getUser().getId(), SpUtil.getUser().getUserName());
                if (homeMessages != null) {
                    mlist.addAll(homeMessages);
                    clearMessage(mlist);
                }
                Collections.reverse(mlist);
                adapter.notifyDataSetChanged();
            }
        }, 300);

    }


    private void clearMessage(List<HomeMessage> homeMessageList) {
        List<HomeMessage> messages = new ArrayList<>();
        for (HomeMessage homeMessage : homeMessageList) {
            if (homeMessage.getImType() == 1) {
                boolean has = false;
                for (GroupInfo groupInfo : HomeActivity.groupInfos) {
                    if (groupInfo.getId() == homeMessage.getId()) {
                        has = true;
                    }
                }
                if (!has) {
                    IMUtil.getInstance().deleteDB(App.getInstance().getSysConfBean().getAccountid(), homeMessage.getId());
                    HomeMessageUtil.getInstance().deleteMessage(homeMessage);
                    messages.add(homeMessage);
                }
            }
        }
        homeMessageList.removeAll(messages);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(HomeEvent event) {
        initBadgeNumber();
        mlist.clear();
        List<HomeMessage> homeMessages = HomeMessageUtil.getInstance().selectMessage(SpUtil.getUser().getId(), SpUtil.getUser().getUserName());
        if (homeMessages != null) {
            mlist.addAll(homeMessages);
            clearMessage(mlist);
        }
        Collections.reverse(mlist);
        adapter.notifyDataSetChanged();
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(HomeMessage homeMessage);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
