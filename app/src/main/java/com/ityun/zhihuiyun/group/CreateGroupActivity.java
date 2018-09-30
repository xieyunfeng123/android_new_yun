package com.ityun.zhihuiyun.group;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.bean.NewAccountInfo;
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.department.AddOrDeleteDepartmentActivity;
import com.ityun.zhihuiyun.event.AddEvent;
import com.ityun.zhihuiyun.event.AddOrDeleteEvent;
import com.ityun.zhihuiyun.event.CreateGroupManagerEvent;
import com.ityun.zhihuiyun.group.adapter.ItemUserAdapter;
import com.ityun.zhihuiyun.home.HomeActivity;
import com.ityun.zhihuiyun.util.KeyboartUtil;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.util.screen.Eyes;
import com.ityun.zhihuiyun.view.ProgressDialog;
import com.wm.WMIMSdk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/25 0025.
 */

public class CreateGroupActivity extends BaseActivity {

    @BindView(R.id.add_group_back)
    public ImageView add_group_back;

    @BindView(R.id.create_group_name)
    public EditText create_group_name;

    @BindView(R.id.selected_user_num_txt)
    public TextView selected_user_num_txt;

    @BindView(R.id.create_group_button)
    public Button create_group_button;

    @BindView(R.id.group_user_gridview)
    public GridView group_user_gridview;

    private List<NewAccountInfo> mlist = new ArrayList<>();

    private ItemUserAdapter adapter;

    private Dialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);

        dialog = ProgressDialog.create(this, "创建中...");
        initAdapter();
    }

    private void initAdapter() {
        adapter = new ItemUserAdapter(this);
        group_user_gridview.setAdapter(adapter);
        adapter.setData(mlist);
        adapter.notifyDataSetChanged();
        group_user_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CreateGroupActivity.this, SelectDepartmentActivity.class);
                intent.putExtra("isManager", 2);
                if (position == (adapter.getCount() - 1)) {
                    intent.putExtra("type", 1);
                    intent.putExtra("selectuser", (Serializable) mlist);
                }
                if (position == (adapter.getCount() - 2)) {
                    intent.putExtra("type", 0);
                    intent.putExtra("selectuser", (Serializable) mlist);
                }
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        KeyboartUtil.hide(CreateGroupActivity.this, create_group_name);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void managerAccount(CreateGroupManagerEvent event) {
        Log.e("tag", "-----create event----" + event.getAccounts().size());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void selectMessage(AddOrDeleteEvent event) {
        if (event.type == -1) {
            mlist.clear();
            User user = SpUtil.getUser();
            Account account = new Account();
            account.setId(user.getId());
            account.setName(user.getUserName());
            NewAccountInfo accountInfoUser = new NewAccountInfo(account);
            mlist.add(accountInfoUser);
            mlist.addAll(event.mlist);
        } else if (event.type == 0) {
//            mlist.addAll(event.mlist);
        } else if (event.type == 1) {
            List<NewAccountInfo> deleteMlist = new ArrayList<>();
            if (mlist.size() != 0 && event.mlist != null && event.mlist.size() != 0) {
                for (NewAccountInfo newAccountInfo : mlist) {
                    for (NewAccountInfo accountInfo : event.mlist) {
                        if (accountInfo.getAccountId() == newAccountInfo.getAccountId()) {
                            deleteMlist.add(newAccountInfo);
                        }
                    }
                }
            }
            mlist.removeAll(deleteMlist);
        }
        initAdapter();
        selected_user_num_txt.setText(mlist.size() + "人");
    }

    @OnClick({R.id.add_group_back, R.id.create_group_button, R.id.iv_list})
    public void viewOnClick(View v) {
        switch (v.getId()) {
            case R.id.iv_list:
                Intent intent = new Intent(this, ShowMemberActivity.class);
                intent.putExtra("mlist", (Serializable) mlist);
                startActivity(intent);
                break;
            case R.id.create_group_button:
                dialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String json = "";
                        if (mlist.size() > 0) {
                            for (int i = 0; i < mlist.size(); i++) {
                                if (i == mlist.size() - 1) {
                                    json = json + "{\"userid\":" + mlist.get(i).getAccountId() + "}";
                                } else {
                                    json = json + "{\"userid\":" + mlist.get(i).getAccountId() + "},";
                                }
                            }
                            String sendJson = "{\"userids\":[" + json + "]}";
                            String groupName = create_group_name.getText().toString().trim();
                            List<GroupInfo> groupInfos = HomeActivity.groupInfos;
                            try {
                                if (groupInfos != null) {
                                    for (int x = 0; x < groupInfos.size(); x++) {
                                        String userName = java.net.URLDecoder.decode(groupInfos.get(x).getName(), "utf-8");
                                        if (userName.equals(groupName)) {
                                            toast("组名已存在,换个试试？");
                                            return;
                                        }
                                    }
                                }
                            } catch (UnsupportedEncodingException e) {
                                toast("");
                            }
                            if (groupName.isEmpty()) {
                                toast("组名不能为空");
                            } else {
                                //调用接口创建群组
                                //int addGroup = ZHYClientSdk.getInstance().addGroup(groupName, json);
                                String groupNames = null;
                                try {
                                    groupNames = java.net.URLEncoder.encode(groupName, "utf-8");

                                } catch (UnsupportedEncodingException e) {
//                                    e.printStackTrace();
                                    toast("");
                                }
                                if (groupNames != null) {
                                    int addGroupId = WMIMSdk.getInstance().GroupCreat(groupNames);
                                    if (addGroupId == -1) {
                                        toast("创建失败");
                                    } else {
                                        int start = WMIMSdk.getInstance().GroupInviteMember(addGroupId, sendJson);
                                        if (start != 0) {
                                            toast("添加失败");
                                        } else {
                                            //创建的组添加到集合中去
                                            User user = SpUtil.getUser();
                                            GroupInfo groupInfo = new GroupInfo();
                                            groupInfo.setId(addGroupId);
                                            groupInfo.setName(groupNames);
                                            groupInfo.setOwner(user.getId());
                                            List<Member> memberList = new ArrayList<>();
                                            for (NewAccountInfo accountInfo : mlist) {
                                                accountInfo.setChoose(false);
                                                memberList.add(new Member(accountInfo));
                                            }
                                            groupInfo.setMember(memberList);
                                            HomeActivity.groupInfos.add(groupInfo);
                                            toast("创建成功");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
//                                                    finish();
                                                    Intent intent = new Intent(CreateGroupActivity.this, GroupActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    }
                }).start();
                break;
            case R.id.add_group_back:
                KeyboartUtil.hide(CreateGroupActivity.this, create_group_name);
                finish();
                break;
        }
    }

    private void toast(final String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (!TextUtils.isEmpty(message)) {
                    Toast.makeText(CreateGroupActivity.this, message, Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
    }
}
