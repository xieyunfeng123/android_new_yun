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
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.group.adapter.AddorDeleteAdapter;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.util.pin.Pinyin;
import com.ityun.zhihuiyun.util.screen.Eyes;
import com.ityun.zhihuiyun.view.SideBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/7/4 0004.
 */

public class AddOrDeleteActivity extends BaseActivity {

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

    @BindView(R.id.addordelete_top_name)
    TextView addordelete_top_name;

    private AddorDeleteAdapter adapter;

    //0 添加 1删除
    private int type;

    private List<Account> allAccounts;

    private List<Account> containsAccounts;

    private List<Account> mlist = new ArrayList<>();

    private List<Account> screenList = new ArrayList<>();

    private List<Account> chooseList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_addordelete);
        ButterKnife.bind(this);
        adapter = new AddorDeleteAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        select_user_list.setLayoutManager(manager);
        select_user_list.setAdapter(adapter);
        type = getIntent().getIntExtra("type", 0);
        allAccounts = App.accountList;
        containsAccounts = (List<Account>) getIntent().getSerializableExtra("account");
        if(containsAccounts!=null)
        {

        }
        User user = SpUtil.getUser();
        for (Account account : containsAccounts) {
            if (user.getId() == account.getId()) {
                containsAccounts.remove(account);
                break;
            }
        }
        String name = getIntent().getStringExtra("name");
        if (name != null) {
            if (name.equals("intercom")) {
                if (type == 0) {
                    addordelete_top_name.setText("添加成员");
                    GroupInfo groupInfo = (GroupInfo) getIntent().getSerializableExtra("group");
                    List<Account> list = App.accountList;
                    // 获取组内所有成员
                    for (Member member : groupInfo.getMember()) {
                        for (Account account : list) {
                            if (member.getId() == account.getId()) {
                                account.setChoose(false);
                                mlist.add(account);
                            }
                        }
                    }
                    List<Account> removeAccount = new ArrayList<>();
                    if (containsAccounts != null) {
                        for (Account account : mlist) {
                            for (Account a : containsAccounts) {
                                if (account.getId() == a.getId()) {
                                    removeAccount.add(account);
                                }
                            }
                        }
                    }
                    mlist.removeAll(removeAccount);
                } else if (type == 1) {
                    addordelete_top_name.setText("删除成员");
                    if (containsAccounts != null) {
                        for (Account account : containsAccounts) {
                            account.setChoose(false);
                        }
                        mlist.addAll(containsAccounts);
                    }
                }
            }
        } else {
            if (type == 0) {
                addordelete_top_name.setText("添加成员");
                mlist.addAll(allAccounts);
                List<Account> removeAccount = new ArrayList<>();
                for (Account account : mlist) {
                    account.setChoose(false);
                    for (Account account1 : containsAccounts) {
                        account1.setChoose(false);
                        if (account.getId() == account1.getId()) {
                            removeAccount.add(account);
                        }
                    }
                }
                mlist.removeAll(removeAccount);
            } else {
                addordelete_top_name.setText("删除成员");
                if (containsAccounts != null && containsAccounts.size() > 0) {
                    for (Account account : containsAccounts) {
                        account.setChoose(false);
                    }
                    mlist.addAll(containsAccounts);
                }
            }
        }
        addPin.start();
        adapter.setOnItemChooseListener(new AddorDeleteAdapter.OnItemOnClick() {
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
            for (Account account : mlist) {
                if (account.getName().contains(string)) {
                    screenList.add(account);
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
                finish();
                break;
            case R.id.sure_select_button:
                Intent intent = getIntent();
                intent.putExtra("accounts", (Serializable) chooseList);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
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
            if (allAccounts != null && allAccounts.size() != 0) {
                //排序
                Collections.sort(mlist, nameComparator);
                List<String> sort = new ArrayList<>();
                for (Account account : mlist) {
                    String username = account.getName();
                    if (username != null) {
                        String pinyin = Pinyin.toPinyin(account.getName().charAt(0));
                        String aString = pinyin.substring(0, 1).toUpperCase();
                        if (aString.matches("[A-Z]")) {
                            if (!sort.contains(aString)) {
                                sort.add(aString);
                            }
                            account.setSort(aString);
                        } else {
                            if (!sort.contains("#")) {
                                sort.add("#");
                            }
                            account.setSort("#");
                        }
                    }
                }
            }
            runOnUiThread(() -> {
                adapter.setData(mlist);
                adapter.notifyDataSetChanged();
            });
        }
    };

    private Comparator nameComparator = (Comparator<Account>) (o, t1) -> {
        String pinyin = Pinyin.toPinyin(o.getName().charAt(0));
        String aString = pinyin.substring(0, 1).toUpperCase();
        if (!aString.matches("[A-Z]")) {
            aString = "#";
        }
        String pinyin1 = Pinyin.toPinyin(t1.getName().charAt(0));
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
    };
}
