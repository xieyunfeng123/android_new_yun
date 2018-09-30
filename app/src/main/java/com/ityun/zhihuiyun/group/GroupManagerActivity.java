package com.ityun.zhihuiyun.group;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.HomeMessage;
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.bean.NewAccountInfo;
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.db.HomeMessageUtil;
import com.ityun.zhihuiyun.db.IMUtil;
import com.ityun.zhihuiyun.department.AddOrDeleteDepartmentActivity;
import com.ityun.zhihuiyun.event.FinishEvent;
import com.ityun.zhihuiyun.event.ManagerGroupEvent;
import com.ityun.zhihuiyun.event.ReceiveMessageEvent;
import com.ityun.zhihuiyun.group.adapter.MemberAdapter;
import com.ityun.zhihuiyun.group.adapter.ShowMemberAdapter;
import com.ityun.zhihuiyun.home.HomeActivity;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.util.screen.Eyes;
import com.ityun.zhihuiyun.view.CircleTextView;
import com.ityun.zhihuiyun.view.ProgressDialog;
import com.wm.WMIMSdk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/7/4 0004.
 */

public class GroupManagerActivity extends BaseActivity {

    @BindView(R.id.change_group_back)
    ImageView change_group_back;

    @BindView(R.id.change_group_name)
    TextView change_group_name;

    @BindView(R.id.group_name_circle)
    CircleTextView group_name_circle;

    @BindView(R.id.group_user_gridview)
    GridView group_user_gridview;

    @BindView(R.id.selected_user_num_txt)
    TextView selected_user_num_txt;

    @BindView(R.id.delete_group_button)
    Button delete_group_button;

    @BindView(R.id.ll_to_next)
    LinearLayout to_next;

    private GroupInfo groupInfo;

    private List<Account> accounts;

    private MemberAdapter adapter;

    private User user;

    private Account a;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_group_manager);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        groupInfo = (GroupInfo) getIntent().getSerializableExtra("group");
        if (groupInfo.getOwner() == App.getInstance().getSysConfBean().getAccountid()) {
            delete_group_button.setText("解散群组");
        }
        //群组聊天
        String name = "";
        try {
            name = URLDecoder.decode(groupInfo.getName(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        group_name_circle.setTextString(name);
        change_group_name.setText(name);
        accounts = new ArrayList<>();
        adapter = new MemberAdapter(this);
        List<Account> accountList = App.accountList;
        for (Member member : groupInfo.getMember()) {
            for (Account account : accountList) {
                if (account.getId() == member.getId()) {
                    account.setChoose(false);
                    accounts.add(account);
                }
            }
        }
        user = SpUtil.getUser();
        a = new Account();
        a.setName(user.getUserName());
        a.setId(user.getId());
        accounts.add(a);
        selected_user_num_txt.setText(accounts.size() + "人");
        adapter.setData(groupInfo, accounts);
        group_user_gridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        group_user_gridview.setOnItemClickListener((parent, view, position, id) -> {
            for (Account account : accounts) {
                account.setChoose(true);
            }
            if (groupInfo.getOwner() == App.getInstance().getSysConfBean().getAccountid()) {
                if (accounts.size() <= 5) {
                    if (position == accounts.size()) {
                        Intent intent3 = new Intent(GroupManagerActivity.this, GroupManagerAccountActivity.class);
                        intent3.putExtra("type", 0);
                        intent3.putExtra("account", (Serializable) accounts);
                        startActivityForResult(intent3, 0);
                    } else if (position == (accounts.size() + 1)) {
                        Intent intent5 = new Intent(this, AddOrDeleteActivity.class);
                        intent5.putExtra("type", 1);
                        intent5.putExtra("account", (Serializable) accounts);
                        startActivityForResult(intent5, 1);
                    } else {
                        startIntent(position);
                    }
                } else {
                    if (position == 5) {
                        Intent intent2 = new Intent(GroupManagerActivity.this, GroupManagerAccountActivity.class);
                        intent2.putExtra("type", 0);
                        intent2.putExtra("account", (Serializable) accounts);
                        startActivityForResult(intent2, 0);
                    } else if (position == 6) {
                        Intent intent6 = new Intent(this, AddOrDeleteActivity.class);
                        intent6.putExtra("type", 1);
                        intent6.putExtra("account", (Serializable) accounts);
                        startActivityForResult(intent6, 1);
                    } else {
                        startIntent(position);
                    }
                }
            } else {
                if (accounts.size() <= 5) {
                    if (position == accounts.size()) {
                        Intent intent4 = new Intent(GroupManagerActivity.this, GroupManagerAccountActivity.class);
                        intent4.putExtra("type", 0);
                        intent4.putExtra("account", (Serializable) accounts);
                        startActivityForResult(intent4, 0);
                    } else {
                        startIntent(position);
                    }
                } else {
                    if (position == 6) {
                        Intent intent1 = new Intent(GroupManagerActivity.this, GroupManagerAccountActivity.class);
                        intent1.putExtra("type", 0);
                        intent1.putExtra("account", (Serializable) accounts);
                        startActivityForResult(intent1, 0);
                    } else {
                        startIntent(position);
                    }
                }
            }
        });
    }

    @OnClick({R.id.delete_all_message, R.id.change_group_back, R.id.delete_group_button, R.id.ll_to_next})
    public void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.delete_all_message:
                cleanDB();
                break;
            case R.id.change_group_back:
                closeActivity();
                break;
            case R.id.delete_group_button:
                outGroup();
                break;
            case R.id.ll_to_next:
                Intent intent = new Intent(this, ShowMemberActivity.class);
                List<NewAccountInfo> infos = new ArrayList<>();
                for (Account account : accounts) {
                    NewAccountInfo info = new NewAccountInfo(account);
                    infos.add(info);
                }
                intent.putExtra("mlist", (Serializable) infos);
                startActivityForResult(intent, 2);
                break;
            default:
                break;
        }
    }

    /**
     * 跳转到成员详情页
     */

    private void startIntent(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("account", accounts.get(position));
        intent.putExtra("groupInfo", groupInfo);
        startActivity(intent);
    }


    /**
     * 退出群组
     */
    private void outGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupManagerActivity.this);
        if (groupInfo.getOwner() == App.getInstance().getSysConfBean().getAccountid()) {
            builder.setMessage("确定解散群组");
        } else {
            builder.setMessage("确定退出群组");
        }
        builder.setTitle("提示");
        builder.setNegativeButton("取消", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setPositiveButton("确定", (dialog, which) -> {
            int result = -1;
            if (groupInfo.getOwner() == App.getInstance().getSysConfBean().getAccountid()) {
                result = WMIMSdk.getInstance().GroupDissolve(groupInfo.getId());
            } else {
                result = WMIMSdk.getInstance().GroupHadLeft(groupInfo.getId());
            }
            if (result == 0) {
                for (GroupInfo group : HomeActivity.groupInfos) {
                    if (group.getId() == groupInfo.getId()) {
                        HomeActivity.groupInfos.remove(group);
                        break;
                    }
                }
                IMUtil.getInstance().deleteDB(App.getInstance().getSysConfBean().getAccountid(), groupInfo.getId());
                HomeMessage homeMessage = HomeMessageUtil.getInstance().selectMessageById(App.getInstance().getSysConfBean().getAccountid(), groupInfo.getId());
                HomeMessageUtil.getInstance().deleteMessage(homeMessage);
                EventBus.getDefault().post(new FinishEvent("ChatActivity"));
                finish();
            } else {
                Tost("操作失败");
            }
        });
        builder.create().show();
    }

    /**
     * 清除message
     */
    private void cleanDB() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupManagerActivity.this);
        builder.setMessage("确定清空聊天记录吗？");
        builder.setTitle("提示");
        builder.setNegativeButton("取消", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setPositiveButton("确定", (dialog, which) -> {
            final Dialog loadingDialog = ProgressDialog.create(GroupManagerActivity.this,
                    "正在清理……");
            loadingDialog.show();
            IMUtil.getInstance().deleteDB(App.getInstance().getSysConfBean().getAccountid(), groupInfo.getId());
            HomeMessage homeMessage = HomeMessageUtil.getInstance().selectMessageById(App.getInstance().getSysConfBean().getAccountid(), groupInfo.getId());
            HomeMessageUtil.getInstance().deleteMessage(homeMessage);
            EventBus.getDefault().post(new ReceiveMessageEvent(null));
            change_group_back.postDelayed(() -> {
                if (loadingDialog != null)
                    loadingDialog.dismiss();
            }, 1500);
        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void managerAccount(ManagerGroupEvent event) {
        if (event.getAccounts() != null) {
            String sendJson = "{\"userids\":[";
            int m = 0;
            for (Account account : event.getAccounts()) {
                if (m == (event.getAccounts().size() - 1)) {
                    sendJson = sendJson + "{\"userid\":" + account.getId() + "}";
                } else {
                    sendJson = sendJson + "{\"userid\":" + account.getId() + "},";
                }
                m++;
            }
            sendJson = sendJson + "]}";
            int result = WMIMSdk.getInstance().GroupInviteMember(groupInfo.getId(), sendJson);
            Log.e("tag", "----result---" + result);
            if (result == 0) {
                List<Member> members = new ArrayList<>();
                for (Account account : event.getAccounts()) {
                    Member member = new Member(account);
                    members.add(member);
                }
                groupInfo.getMember().addAll(members);
                for (GroupInfo group : HomeActivity.groupInfos) {
                    if (group.getId() == groupInfo.getId()) {
                        group.setMember(groupInfo.getMember());
                    }
                }
                accounts.addAll(event.getAccounts());
                adapter.setData(groupInfo, accounts);
                adapter.notifyDataSetInvalidated();
                selected_user_num_txt.setText(accounts.size() + "人");
            }
        }
    }

    private void closeActivity() {
        Intent intent = getIntent();
        intent.putExtra("groups", groupInfo);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            List<Account> returnAccount = (List<Account>) data.getSerializableExtra("accounts");
            switch (requestCode) {
                case 0:
                    if (returnAccount != null) {
                        String sendJson = "{\"userids\":[";
                        int m = 0;
                        for (Account account : returnAccount) {
                            if (m == (returnAccount.size() - 1)) {
                                sendJson = sendJson + "{\"userid\":" + account.getId() + "}";
                            } else {
                                sendJson = sendJson + "{\"userid\":" + account.getId() + "},";
                            }
                            m++;
                        }
                        sendJson = sendJson + "]}";
                        int result = WMIMSdk.getInstance().GroupInviteMember(groupInfo.getId(), sendJson);
                        if (result == 0) {
                            List<Member> members = new ArrayList<>();
                            for (Account account : returnAccount) {
                                Member member = new Member(account);
                                members.add(member);
                            }
                            groupInfo.getMember().addAll(members);
                            for (GroupInfo group : HomeActivity.groupInfos) {
                                if (group.getId() == groupInfo.getId()) {
                                    group.setMember(groupInfo.getMember());
                                }
                            }
                            accounts.addAll(returnAccount);
                            adapter.notifyDataSetChanged();
                            selected_user_num_txt.setText(accounts.size() + "人");
                        }
                    }
                    break;
                case 1:
                    if (returnAccount != null) {
                        List<Account> deleteAccount = new ArrayList<>();
                        List<Member> members = new ArrayList<>();
                        for (Account account : returnAccount) {
                            for (Account account1 : accounts) {
                                if (account.getId() == account1.getId()) {
                                    deleteAccount.add(account1);
                                }
                            }
                            WMIMSdk.getInstance().GroupKickOut(groupInfo.getId(), account.getId());
                            for (Member member : groupInfo.getMember()) {
                                if (member.getId() == account.getId()) {
                                    members.add(member);
                                }
                            }
                        }
                        for (GroupInfo group : HomeActivity.groupInfos) {
                            if (group.getId() == groupInfo.getId()) {
                                groupInfo.getMember().removeAll(members);
                                group.setMember(groupInfo.getMember());
                            }
                        }
                        accounts.removeAll(deleteAccount);
                        adapter.notifyDataSetChanged();
                        selected_user_num_txt.setText(accounts.size() + "人");
                    }
                    break;
                case 2:
                    adapter.notifyDataSetInvalidated();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetInvalidated();
    }


}
