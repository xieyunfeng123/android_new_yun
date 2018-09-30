package com.ityun.zhihuiyun.talk;


import android.os.Bundle;
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
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.bean.NewAccountInfo;
import com.ityun.zhihuiyun.talk.adapter.SelectMemberAdapter;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.util.pin.Pinyin;
import com.ityun.zhihuiyun.view.SideBar;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;

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

public class SelectMemberActiviity extends BaseActivity {

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

    private SelectMemberAdapter adapter;

    private List<Member> mlist = new ArrayList<>();

    private List<Member> chooseList = new ArrayList<>();

    private List<Member> screenList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectuser);
        ButterKnife.bind(this);
        adapter = new SelectMemberAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        select_user_list.setLayoutManager(manager);
        select_user_list.setAdapter(adapter);
        adapter.setData(mlist);
        adapter.notifyDataSetChanged();
//        addPin.start();
        adapter.setOnItemChooseListener(new SelectMemberAdapter.OnItemOnClick() {
            @Override
            public void onClick(int position, boolean isChoose) {
//                adapter.notifyDataSetChanged();
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

        List<Member> selectUsers = (List<Member>) getIntent().getSerializableExtra("members");
        if (selectUsers != null && selectUsers.size() != 0) {
            List<String> sort = new ArrayList<>();
            //重组新对象
            for (Member accountInfo : selectUsers) {
                if(accountInfo.getId()!= SpUtil.getUser().getId())
                {
                    mlist.add(accountInfo);
                }
            }
            //排序
            Collections.sort(mlist, nameComparator);
            for (Member newAccountInfo : mlist) {
                String username = newAccountInfo.getName();
                if (username != null) {
                    String pinyin = Pinyin.toPinyin(newAccountInfo.getName().charAt(0));
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
            adapter.notifyDataSetChanged();
        }
    }

    private void screenList(String string) {
        if (TextUtils.isEmpty(string)) {
            screenList.clear();
            adapter.setData(mlist);
            adapter.notifyDataSetChanged();
        } else {
            screenList.clear();
            for (Member newAccountInfo : mlist) {
                if (newAccountInfo.getName().contains(string)) {
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
                finish();
                break;
            case R.id.sure_select_button:
                EventBus.getDefault().postSticky(chooseList);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private Thread addPin = new Thread() {
        @Override
        public void run() {
            super.run();
            mlist = (List<Member>) getIntent().getSerializableExtra("members");

            if (mlist != null && mlist.size() != 0) {
                List<String> sort = new ArrayList<>();
                //排序
                Collections.sort(mlist, new Comparator<Member>() {
                    @Override
                    public int compare(Member o1, Member o2) {
                        String  name1=o1.getName();
                        String  name2=o2.getName();
                        String pinyin = Pinyin.toPinyin(name1.charAt(0));
                        String aString = pinyin.substring(0, 1).toUpperCase();
                        if (!aString.matches("[A-Z]")) {
                            aString = "#";
                        }
                        String pinyin1 = Pinyin.toPinyin(name2.charAt(0));
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
                });
                for (Member newAccountInfo : mlist) {
                    String username = newAccountInfo.getName();
                    if (username != null) {
                        String pinyin = Pinyin.toPinyin(newAccountInfo.getName().charAt(0));
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
        }
    };

    private Comparator nameComparator = new Comparator<Member>() {
        @Override
        public int compare(Member o, Member t1) {
            Log.e("insert","==================="+o.getName());
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
        }
    };
}
