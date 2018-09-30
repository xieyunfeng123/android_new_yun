package com.ityun.zhihuiyun.intercom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.group.AddOrDeleteActivity;
import com.ityun.zhihuiyun.home.HomeActivity;
import com.ityun.zhihuiyun.intercom.adapter.IntercomAdapter;
import com.ityun.zhihuiyun.talk.GroupTalkActivity;
import com.ityun.zhihuiyun.talk.RobTalkActivity;
import com.ityun.zhihuiyun.talk.StrongPullTalkActivity;
import com.ityun.zhihuiyun.talk.TrailRobActivity;
import com.ityun.zhihuiyun.util.screen.Eyes;
import com.wm.WMIMSdk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntercomActivity extends BaseActivity {

    @BindView(R.id.gl_member)
    GridView gv_member;

    @BindView(R.id.tv_num)
    TextView tv_num;

    @BindView(R.id.tv_normal)
    TextView tv_normal;

    @BindView(R.id.tv_trail)
    TextView tv_trail;

    @BindView(R.id.tv_free)
    TextView tv_free;

    @BindView(R.id.tv_rob)
    TextView tv_rob;

    @BindView(R.id.iv_return)
    ImageView iv_return;

    @BindView(R.id.tv_launch)
    TextView tv_launch;

    private IntercomAdapter adapter;

    private List<Account> accounts;

    private GroupInfo groupInfo;

    private boolean isNoraml = true;

    private boolean isTrail = false;

    private boolean isFree = true;

    private boolean isRob = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercom);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.main_color));
        ButterKnife.bind(this);
        groupInfo = (GroupInfo) getIntent().getSerializableExtra("group");
        initView();
    }

    private void initView() {
        accounts = new ArrayList<>();
        adapter = new IntercomAdapter(this);
        if (accounts.size() == 0) {
            tv_num.setText(0 + "人");
            adapter.setData(groupInfo, null);
        } else {
            tv_num.setText(accounts.size() + "人");
            adapter.setData(groupInfo, accounts);
        }
        gv_member.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        gv_member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = "intercom";
                Intent intent = new Intent(IntercomActivity.this, AddOrDeleteActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("group", groupInfo);

                if (position == adapter.getCount() - 2) {
                    intent.putExtra("type", 0);
                    intent.putExtra("account", (Serializable) accounts);
                    startActivityForResult(intent, 0);
                } else if (position == adapter.getCount() - 1) {
                    intent.putExtra("type", 1);
                    intent.putExtra("account", (Serializable) accounts);
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    @OnClick({R.id.tv_normal, R.id.tv_trail, R.id.tv_free, R.id.tv_rob, R.id.iv_return, R.id.tv_launch})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_normal:
                if (isNoraml) {
                    trail();
                } else {
                    normal();
                }
                break;
            case R.id.tv_trail:
                if (isTrail) {
                    normal();
                } else {
                    trail();
                }
                break;
            case R.id.tv_free:
                if (isFree) {
                    rob();
                } else {
                    free();
                }
                break;
            case R.id.tv_rob:
                if (isRob) {
                    free();
                } else {
                    rob();
                }
                break;
            case R.id.iv_return:
                finish();
                break;
            case R.id.tv_launch:
                if (accounts.size() == 0) {
                    Tost("请选择成员");
                } else {
                    List<Member> members = new ArrayList<>();
                    for (Member member : groupInfo.getMember()) {
                        for (Account account : accounts) {
                            if (member.getId() == account.getId()) {
                                members.add(member);
                            }
                        }
                    }
//                    groupInfo.setMember(members);
                    //语音
                    if (isNoraml && isFree) {
                        Intent intent = new Intent(IntercomActivity.this, GroupTalkActivity.class);
                        intent.putExtra("group", groupInfo);
                        intent.putExtra("members", (Serializable) members);
                        startActivity(intent);
                        finish();
                        // 抢麦
                    } else if (isNoraml && isRob) {
                        Intent intent = new Intent(IntercomActivity.this, RobTalkActivity.class);
                        intent.putExtra("group", groupInfo);
                        intent.putExtra("members", (Serializable) members);
                        startActivity(intent);
                        finish();
                        // 强拉
                    } else if (isTrail && isFree) {
                        Intent intentStrong = new Intent(this, StrongPullTalkActivity.class);
                        intentStrong.putExtra("group", groupInfo);
                        intentStrong.putExtra("members", (Serializable) members);
                        startActivity(intentStrong);
                        finish();
                        // 强拉抢麦
                    } else if (isTrail && isRob) {
                        Intent intent = new Intent(IntercomActivity.this, TrailRobActivity.class);
                        intent.putExtra("group", groupInfo);
                        intent.putExtra("members", (Serializable) members);
                        startActivity(intent);
                        finish();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 邀请方式-普通
     */
    private void normal() {
        isNoraml = true;
        tv_normal.setBackgroundResource(R.drawable.intercom_bg_left_checked);
        tv_normal.setTextColor(getResources().getColor(R.color.white));
        isTrail = false;
        tv_trail.setBackgroundResource(R.drawable.intercom_bg_right_unchecked);
        tv_trail.setTextColor(getResources().getColor(R.color.main_color));
    }

    /**
     * 邀请方式-强拉
     */
    private void trail() {
        isNoraml = false;
        tv_normal.setBackgroundResource(R.drawable.intercom_bg_left_unchecked);
        tv_normal.setTextColor(getResources().getColor(R.color.main_color));
        isTrail = true;
        tv_trail.setBackgroundResource(R.drawable.intercom_bg_right_checked);
        tv_trail.setTextColor(getResources().getColor(R.color.white));
    }

    /**
     * 通话模式-自由
     */
    private void free() {
        isFree = true;
        tv_free.setBackgroundResource(R.drawable.intercom_bg_left_checked);
        tv_free.setTextColor(getResources().getColor(R.color.white));
        isRob = false;
        tv_rob.setBackgroundResource(R.drawable.intercom_bg_right_unchecked);
        tv_rob.setTextColor(getResources().getColor(R.color.main_color));
    }

    /**
     * 通话模式-抢麦
     */
    private void rob() {
        isFree = false;
        tv_free.setBackgroundResource(R.drawable.intercom_bg_left_unchecked);
        tv_free.setTextColor(getResources().getColor(R.color.main_color));
        isRob = true;
        tv_rob.setBackgroundResource(R.drawable.intercom_bg_right_checked);
        tv_rob.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        List<Account> accountList = (List<Account>) data.getSerializableExtra("accounts");
        if (requestCode == 0) {
            if (accountList != null) {
//                String sendJson = "{\"userids\":[";
//                int m = 0;
//                for (Account account : accountList) {
//                    if (m == (accountList.size() - 1)) {
//                        sendJson = sendJson + "{\"userid\":" + account.getId() + "}";
//                    } else {
//                        sendJson = sendJson + "{\"userid\":" + account.getId() + "},";
//                    }
//                    m++;
//                }
//                sendJson = sendJson + "]}";
//                int result = WMIMSdk.getInstance().GroupInviteMember(groupInfo.getId(), sendJson);
//                if (result == 0) {
//                List<Member> members = new ArrayList<>();
//                for (Account account : accountList) {
//                    Member member = new Member(account);
//                    members.add(member);
//                }
//                groupInfo.getMember().addAll(members);
//                for (GroupInfo group : HomeActivity.groupInfos) {
//                    if (group.getId() == groupInfo.getId()) {
//                        group.setMember(groupInfo.getMember());
//                    }
//                }
                accounts.addAll(accountList);
                adapter.setData(groupInfo, accounts);
                adapter.notifyDataSetChanged();
                tv_num.setText(accounts.size() + "人");
            } else {
                Tost("请重新选择！");
            }
        } else if (requestCode == 1) {
            if (accountList != null) {
                List<Account> deleteAccount = new ArrayList<>();
                List<Member> members = new ArrayList<>();
                for (Account account : accountList) {
                    for (Account account1 : accounts) {
                        if (account.getId() == account1.getId()) {
                            deleteAccount.add(account1);
                        }
                    }
//                    WMIMSdk.getInstance().GroupKickOut(groupInfo.getId(), account.getId());
//                    for (Member member : groupInfo.getMember()) {
//                        if (member.getId() == account.getId()) {
//                            members.add(member);
//                        }
//                    }
                }
//                for (GroupInfo group : HomeActivity.groupInfos) {
//                    if (group.getId() == groupInfo.getId()) {
//                        groupInfo.getMember().removeAll(members);
//                        group.setMember(groupInfo.getMember());
//                    }
//                }
                accounts.removeAll(deleteAccount);
                adapter.setData(groupInfo, accounts);
                adapter.notifyDataSetChanged();
                tv_num.setText(accounts.size() + "人");
            }
        }
    }
}

