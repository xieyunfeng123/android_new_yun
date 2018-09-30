package com.ityun.zhihuiyun.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.NewAccountInfo;
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.group.adapter.ShowMemberAdapter;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.util.pin.Pinyin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 点击人数显示所有成员列表
 */
public class ShowMemberActivity extends BaseActivity {

    @BindView(R.id.rv_show_member)
    RecyclerView memberList;

    private ShowMemberAdapter adapter;

    private List<NewAccountInfo> mlist = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_member);
        ButterKnife.bind(this);

        mlist = (List<NewAccountInfo>) getIntent().getSerializableExtra("mlist");
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        memberList.setLayoutManager(manager);
        adapter = new ShowMemberAdapter(this);
        adapter.setData(mlist);
        memberList.setAdapter(adapter);
//        addPin.start();
        adapter.notifyDataSetChanged();
        adapter.setOnItemClick(new ShowMemberAdapter.OnItemClick() {
            @Override
            public void onClick(int position, boolean isChoose) {
                NewAccountInfo accountInfo = mlist.get(position);
                Account account = new Account();
                account.setId(accountInfo.getAccountId());
                account.setName(accountInfo.getAccountName());
                Intent intent = new Intent(ShowMemberActivity.this, DetailActivity.class);
                intent.putExtra("account", account);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.iv_back})
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.iv_back:
                Intent intent = getIntent();
                setResult(1, intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            String name = data.getStringExtra("name");
            adapter.notifyDataSetChanged();
        }
    }

    private Thread addPin = new Thread() {
        @Override
        public void run() {
            super.run();
                //排序
                Collections.sort(mlist, nameComparator);
                List<String> sort = new ArrayList<>();
                for (NewAccountInfo account : mlist) {
                    String username = account.getAccountName();
                    if (username != null) {
                        String pinyin = Pinyin.toPinyin(account.getAccountName().charAt(0));
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
