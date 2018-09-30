package com.ityun.zhihuiyun.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.Department;
import com.ityun.zhihuiyun.bean.NewAccountInfo;
import com.ityun.zhihuiyun.department.DepartmentCheckedActivity;
import com.ityun.zhihuiyun.event.AddOrDeleteEvent;
import com.ityun.zhihuiyun.event.FinishEvent;
import com.ityun.zhihuiyun.fragment.adapter.DepartmentAdapter;
import com.ityun.zhihuiyun.fragment.adapter.FriendAdapter;
import com.ityun.zhihuiyun.view.NoScrollListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectDepartmentActivity extends BaseActivity {

    @BindView(R.id.select_department)
    NoScrollListView depart;

    @BindView(R.id.select_friend)
    NoScrollListView friend;

    @BindView(R.id.tv_number)
    TextView tv_number;

    @BindView(R.id.tv_title)
    TextView tv_title;

    private FriendAdapter friendAdapter;

    private DepartmentAdapter departmentAdapter;

    private Department department;

    private List<Account> accountList = new ArrayList<>();

    private List<NewAccountInfo> hasAccount = new ArrayList<>();

    private List<Account> managerAccounts = new ArrayList<>();

    private List<Account> accounts = new ArrayList<>();
    // 0 初始化 1 已有群加减人 2 创建群加减人
    private int isManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectdepartment);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        accountList.addAll(App.accountList);
        friendAdapter = new FriendAdapter(this);
        department = App.getInstance().getDepartment();
        departmentAdapter = new DepartmentAdapter(this);
        departmentAdapter.setData(department.getDepartments());
        isManager = getIntent().getIntExtra("isManager", 0);
        // 创建群组状态下加减人
        hasAccount = (List<NewAccountInfo>) getIntent().getSerializableExtra("selectuser");
        managerAccounts = (List<Account>) getIntent().getSerializableExtra("account");
        if (hasAccount != null && hasAccount.size() != 0) {
            accounts.clear();
            tv_title.setText("选择联系人");
            for (NewAccountInfo accountInfo : hasAccount) {
                Account account = new Account();
                account.setName(accountInfo.getAccountName());
                account.setId(accountInfo.getAccountId());
                accounts.add(account);
            }
            int size = accounts.size() - 1;
            tv_number.setText("已选择" + size + "人");
            department = App.getInstance().getDepartment();
            getStatus(department, accounts);
        }
        /*else if (managerAccounts != null && managerAccounts.size() != 0) {
            accounts.clear();
            accounts.addAll(managerAccounts);
            int size = accounts.size() - 1;
            tv_number.setText("已选择" + size + "人");
            department = App.getInstance().getDepartment();
            setStatus(department, accounts);
        }*/
        else {
            clearStatus(department);
        }
       /* if (managerAccounts != null && managerAccounts.size() != 0) {
            accounts.clear();
            accounts.addAll(managerAccounts);
            int size = accounts.size() - 1;
            tv_number.setText("已选择" + size + "人");
            department = App.getInstance().getDepartment();
            getStatus(department, accounts);
        } else {
            clearStatus(department);
        }*/

        depart.setAdapter(departmentAdapter);
        removeAccouts(department);
        friend.setAdapter(friendAdapter);
        friendAdapter.setData(accountList);
        friendAdapter.notifyDataSetChanged();
        departmentAdapter.notifyDataSetChanged();
        depart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SelectDepartmentActivity.this, DepartmentCheckedActivity.class);
                intent.putExtra("department", department.getDepartments().get(position));
                intent.putExtra("isManager", isManager);
                startActivity(intent);
            }
        });
    }

    private void removeAccouts(Department department) {
        if (department.getAccounts() != null) {
            accountList.removeAll(department.getAccounts());
        }
        if (department.getDepartments() != null) {
            for (Department department1 : department.getDepartments()) {
                removeAccouts(department1);
            }
        }
    }

    @OnClick({R.id.tv_sure, R.id.iv_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sure:
                Intent intent = new Intent(this, CreateGroupActivity.class);
                startActivity(intent);
                List<NewAccountInfo> list = new ArrayList<>();
                for (Account account : accountList) {
                    NewAccountInfo info = new NewAccountInfo(account);
                    list.add(info);
                }
                EventBus.getDefault().postSticky(new AddOrDeleteEvent(-1, list, 0));
                finish();
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * @param department
     * @param accounts
     */
    private void getStatus(Department department, List<Account> accounts) {
        if (department.getAccounts() != null && accounts.size() != 0) {
            for (Account account : department.getAccounts()) {
                boolean has = false;
                for (Account hasAccount : accounts) {
                    if (account.getId() == hasAccount.getId()) {
                        has = true;
                    }
                }
                if (has) {
                    account.setChoose(true);
                }
            }
        }
        if (department.getDepartments() != null) {
            for (Department department1 : department.getDepartments()) {
                getStatus(department1, accounts);
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
//        clearStatus(App.getInstance().getDepartment());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finshActivity(FinishEvent finishEvent) {
        if (finishEvent.activity.equals("SelectDepartmentActivity")) {
            finish();
        }
    }
    /**
     * 初始化所有成员状态
     */
    private void clearStatus(Department department) {
        Log.e("tag", "------clear status--------");
        department.setCheck(false);
        if (department.getAccounts() != null && department.getAccounts().size() != 0) {
            for (Account account : department.getAccounts()) {
                account.setChoose(false);
            }
        }
        if (department.getDepartments() != null && department.getDepartments().size() != 0) {
            for (Department department1 : department.getDepartments()) {
                clearStatus(department1);
            }
        }

    }
}
