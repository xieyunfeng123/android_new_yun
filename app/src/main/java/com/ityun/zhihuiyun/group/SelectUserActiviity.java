package com.ityun.zhihuiyun.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.NewAccountInfo;
import com.ityun.zhihuiyun.event.AddOrDeleteEvent;
import com.ityun.zhihuiyun.event.FinishEvent;
import com.ityun.zhihuiyun.group.adapter.SelectUserAdapter;
import com.ityun.zhihuiyun.home.HomeActivity;
import com.ityun.zhihuiyun.util.KeyboartUtil;
import com.ityun.zhihuiyun.util.pin.Pinyin;
import com.ityun.zhihuiyun.util.screen.Eyes;
import com.ityun.zhihuiyun.view.SideBar;
import com.zhy.clientsdk.WMAccountInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/24 0024.
 */

public class SelectUserActiviity extends BaseActivity {

    @BindView(R.id.select_back)
    ImageView select_back;

    @BindView(R.id.selected_user_num)
    TextView selected_user_num;

    @BindView(R.id.sure_select_button)
    TextView sure_select_button;

    @BindView(R.id.select_user_list)
    RecyclerView select_user_list;

    @BindView(R.id.select_sidebar)
    SideBar select_sidebar;

    @BindView(R.id.top_name)
    TextView top_name;

    private SelectUserAdapter adapter;

    private List<NewAccountInfo> mlist = new ArrayList<>();

    private List<Account> list = new ArrayList<>();

    private List<NewAccountInfo> chooseList = new ArrayList<>();

    private List<NewAccountInfo> screenList = new ArrayList<>();

    private boolean isIntent = true;

    int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_selectuser);
        ButterKnife.bind(this);
        adapter = new SelectUserAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        select_user_list.setLayoutManager(manager);
        select_user_list.setAdapter(adapter);
        adapter.setData(mlist);
        adapter.notifyDataSetChanged();
        addPin.start();
        adapter.setOnItemChooseListener(new SelectUserAdapter.OnItemOnClick() {
            @Override
            public void onClick(int position, boolean isChoose) {
                if (screenList.size() != 0) {
                    screenList.get(position).setChoose(isChoose);
                } else {
                    mlist.get(position).setChoose(isChoose);
                }
                adapter.notifyDataSetChanged();
                if (isChoose && !chooseList.contains(mlist.get(position))) {
                    chooseList.add(mlist.get(position));
                }
                if (!isChoose && chooseList.contains(mlist.get(position))) {
                    chooseList.remove(mlist.get(position));
                }
                selected_user_num.setText("已选择" + chooseList.size() + "人");
            }

            @Override
            public void onTextChanged(String string) {
                screenList(string);
            }
        });
    }

    private void screenList(String string) {
        if (TextUtils.isEmpty(string)) {
            screenList.clear();
            adapter.setData(mlist);
            adapter.notifyDataSetChanged();
        } else {
            screenList.clear();
            for (NewAccountInfo newAccountInfo : mlist) {
                if (newAccountInfo.getAccountName().contains(string)) {
                    screenList.add(newAccountInfo);
                }
            }
            adapter.setData(screenList);
            adapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.select_back, R.id.sure_select_button})
    public void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.select_back:
                KeyboartUtil.hide(SelectUserActiviity.this, adapter.getEdit());
                finish();
                break;
            case R.id.sure_select_button:
                if (isIntent) {
                    Intent intent = new Intent(this, CreateGroupActivity.class);

                    startActivity(intent);
                }
                EventBus.getDefault().postSticky(new AddOrDeleteEvent(type, chooseList, 1));
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finishOnMessage(FinishEvent finishEvent) {
        if (finishEvent.activity.equals("SelectUserActiviity")) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private Thread addPin = new Thread() {
        @Override
        public void run() {
            super.run();
            List<Account> accountInfos = App.accountList;
            if (accountInfos != null && accountInfos.size() != 0) {
                List<String> sort = new ArrayList<>();
                //重组新对象
                for (Account accountInfo : accountInfos) {
                    NewAccountInfo newAccountInfo = new NewAccountInfo(accountInfo);
                    mlist.add(newAccountInfo);
                }
                //排序
                Collections.sort(mlist, nameComparator);
                for (NewAccountInfo newAccountInfo : mlist) {
                    String username = newAccountInfo.getAccountName();
                    if (username != null) {
                        String pinyin = Pinyin.toPinyin(newAccountInfo.getAccountName().charAt(0));
                        String aString = pinyin.substring(0, 1).toUpperCase();
                        if (aString.matches("[A-Z]")) {
                            if (!sort.contains(aString)) {
                                sort.add(aString);
                            }
                            newAccountInfo.setSort(aString);
                        } else {
                            if (!sort.contains("#")) {
                                sort.add("#");
                            }
                            newAccountInfo.setSort("#");
                        }
                    }
                }
            }
            runOnUiThread(() -> {
                List<NewAccountInfo> selectUsers = (List<NewAccountInfo>) getIntent().getSerializableExtra("selectuser");
                type = getIntent().getIntExtra("type", 0);

                if (selectUsers != null) {
                    for (NewAccountInfo accountInfo : selectUsers) {
                        accountInfo.setChoose(false);
                    }
                    isIntent = false;
                }
                if (type == 0) {
                    top_name.setText("添加成员");
                } else {
                    top_name.setText("删除成员");
                }
                if (selectUsers != null && selectUsers.size() != 0) {
                    if (type == 0) {
                        for (NewAccountInfo accountInfo : selectUsers) {
                            for (NewAccountInfo newAccountInfo : mlist) {
                                if (newAccountInfo.getAccountId() == accountInfo.getAccountId()) {
                                    chooseList.add(newAccountInfo);
                                }
                            }
                        }
                        mlist.removeAll(chooseList);
                        chooseList.clear();
                    } else {
                        mlist.clear();
                        mlist.addAll(selectUsers);
                    }
                }
                adapter.notifyDataSetChanged();
            });
        }
    };


    private Comparator nameComparator = new Comparator<NewAccountInfo>() {
        @Override
        public int compare(NewAccountInfo o, NewAccountInfo t1) {
            String pinyin = Pinyin.toPinyin(o.getAccountName().charAt(0));
            String aString = pinyin.substring(0, 1).toUpperCase();
            if (!aString.matches("[A-Z]")) {
                aString = "#";
            }
            String pinyin1 = Pinyin.toPinyin(t1.getAccountName().charAt(0));
            String aString1 = pinyin1.substring(0, 1).toUpperCase();
            if (!aString1.matches("[A-Z]")) {
                aString1 = "#";
            }
            int flag = aString.compareTo(aString1);
            if (flag == 0) {
                return aString.compareTo(aString1);
            } else {
                if (aString.equals("#") || aString1.equals("#")) {
                    return -flag;
                }
                return flag;
            }
        }
    };
}
