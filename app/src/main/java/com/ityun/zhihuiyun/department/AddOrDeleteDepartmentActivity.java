package com.ityun.zhihuiyun.department;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.Department;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.bean.NewAccountInfo;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.chat.ChatActivity;
import com.ityun.zhihuiyun.department.adapter.CheckedAdapter;
import com.ityun.zhihuiyun.department.adapter.CheckedFriendAdapter;
import com.ityun.zhihuiyun.department.adapter.TopNameAdaper;
import com.ityun.zhihuiyun.event.AddEvent;
import com.ityun.zhihuiyun.event.AddOrDeleteEvent;
import com.ityun.zhihuiyun.event.CreateGroupManagerEvent;
import com.ityun.zhihuiyun.event.FinishEvent;
import com.ityun.zhihuiyun.event.ManagerGroupEvent;
import com.ityun.zhihuiyun.group.CreateGroupActivity;
import com.ityun.zhihuiyun.group.GroupActivity;
import com.ityun.zhihuiyun.group.SelectDepartmentActivity;
import com.ityun.zhihuiyun.util.KeyboartUtil;
import com.ityun.zhihuiyun.view.NoScrollListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddOrDeleteDepartmentActivity extends BaseActivity {

    @BindView(R.id.department_name)
    TextView departName;

    @BindView(R.id.account_list)
    NoScrollListView account_list;

    @BindView(R.id.department_list)
    NoScrollListView department_list;

    @BindView(R.id.department_name_list)
    RecyclerView department_name_list;

    @BindView(R.id.tv_number)
    TextView tv_number;

    @BindView(R.id.search_user)
    EditText search_user;

    private Department department;

    private CheckedFriendAdapter friendAdapter;

    private CheckedAdapter checkedAdapter;

    private TopNameAdaper topNameAdapter;

    private List<Department> names = new ArrayList<>();

    private Department nowDepartment;

    private List<Department> departList = new ArrayList<>();

    private List<Account> accountList = new ArrayList<>();

    private boolean child = false;

    private int clickPosition;

    private int clickAccountPosition;

    private List<Account> screeAccount = new ArrayList<>();

    private List<Department> screenDepart = new ArrayList<>();

    private boolean isDeapartmentNameClick = false;

    private int isManager;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_checked);
        ButterKnife.bind(this);
        accountList.clear();
        department = (Department) getIntent().getSerializableExtra("department");
        isManager = getIntent().getIntExtra("isManager", 0);
        getStatus(department);
        if (accountList != null && accountList.size() != 0) {
            tv_number.setText("已选择" + accountList.size() + "人");
        }
        departName.setText(department.getName().toString());
        nowDepartment = department;
        names.add(department);
        departList.add(department);
        checkedAdapter = new CheckedAdapter(this);
        checkedAdapter.setData(nowDepartment.getDepartments());
        department_list.setAdapter(checkedAdapter);
        checkedAdapter.setOnClick(new CheckedAdapter.OnClick() {
            @Override
            public void onItemClick(int position) {
//                checkedAdapter.setClickPosition(position);
                nowDepartment.getDepartments().get(position).setCheck(!nowDepartment.getDepartments().get(position).isCheck());
                checkedAdapter.notifyDataSetChanged();
                clickPosition = position;
            }

            @Override
            public void onDepartmentClick(int position) {
                child = true;
                // 进入下一级
                changeDepartment(nowDepartment.getDepartments().get(position));
            }

            @Override
            public void onCheckBoxChange(int position, boolean isCheck) {
                // 点击item的时候返回是否选中 如果选中就add or delete
                if (clickPosition == position) {
//                    if (isDeapartmentNameClick) {
//                        isDeapartmentNameClick = false;
//                        return;
//                    }
                    if (isCheck) {
                        getAccounts(nowDepartment.getDepartments().get(position));
                    } else {
                        removeAccouts(nowDepartment.getDepartments().get(position));
                    }
                }
                tv_number.setText("已选择" + accountList.size() + "人");
            }
        });

        friendAdapter = new CheckedFriendAdapter(this);
        friendAdapter.setData(nowDepartment.getAccounts());
        account_list.setAdapter(friendAdapter);
        friendAdapter.setOnClick(new CheckedFriendAdapter.OnClick() {
            @Override
            public void onItemClick(int position) {
                clickAccountPosition = position;
                nowDepartment.getAccounts().get(position).setChoose(!nowDepartment.getAccounts().get(position).getChoose());
                friendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCheckedClick(int position, boolean isCheck) {
                if (child) {
                    child = false;
                    return;
                }
                if (clickAccountPosition == position) {
                    if (isCheck) {
                        if (accountList.size() != 0) {
                            boolean hasAccount = false;
                            for (Account account : accountList) {
                                if (account.getId() == nowDepartment.getAccounts().get(position).getId()) {
                                    hasAccount = true;
                                }
                            }
                            if (!hasAccount) {
                                accountList.add(nowDepartment.getAccounts().get(position));
                            }
                        } else {
                            accountList.add(nowDepartment.getAccounts().get(position));
                        }
                    } else {
                        accountList.remove(nowDepartment.getAccounts().get(position));
                    }
                }
                tv_number.setText("已选择" + accountList.size() + "人");
            }
        });
        topNameAdapter = new TopNameAdaper(this);
        department_name_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        topNameAdapter.setData(names);
        department_name_list.setAdapter(topNameAdapter);
        topNameAdapter.setOnItemClickListener(new TopNameAdaper.OnItemClick() {
            @Override
            public void onClik(int position) {
                isDeapartmentNameClick = true;
                changeDepartment(names.get(position));
            }
        });

        friendAdapter.notifyDataSetChanged();
        checkedAdapter.notifyDataSetChanged();
        topNameAdapter.notifyDataSetChanged();

        search_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                screenList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.tv_sure, R.id.iv_back, R.id.tv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sure:
                EventBus.getDefault().post(new FinishEvent("GroupManagerAccountActivity"));
                EventBus.getDefault().post(new ManagerGroupEvent(accountList));
                finish();
                break;
            case R.id.tv_back:
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
     * 如果成员是选中状态，添加成员
     */
    private void getStatus(Department department) {
        if (department.getAccounts() != null && department.getAccounts().size() != 0) {
            for (Account account : department.getAccounts()) {
                if (account.isChoose()) {
                    accountList.add(account);
                }
            }
        }
        if (department.getDepartments() != null && department.getDepartments().size() != 0) {
            for (Department department1 : department.getDepartments()) {
                getStatus(department1);
            }
        }

    }

    private void changeDepartment(Department changeDepartment) {
        nowDepartment = changeDepartment;
        departName.setText(nowDepartment.getName());
        if (names.contains(nowDepartment)) {
            boolean remove = false;
            List<Department> removeDepartments = new ArrayList<>();
            for (int i = 0; i < names.size(); i++) {
                if (remove) {
                    removeDepartments.add(names.get(i));
                }
                if (names.get(i).getId() == changeDepartment.getId()) {
                    remove = true;
                }
            }
            names.removeAll(removeDepartments);
        } else {
            names.add(nowDepartment);
        }
        checkedAdapter.setData(nowDepartment.getDepartments());
        friendAdapter.setData(nowDepartment.getAccounts());
        topNameAdapter.notifyDataSetChanged();
        checkedAdapter.notifyDataSetChanged();
        friendAdapter.notifyDataSetChanged();
        tv_number.setText("已选择" + accountList.size() + "人");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void test(AddEvent event) {
    }

    /**
     * 获取所有成员
     *
     * @param department
     */
    private void getAccounts(Department department) {
        if (department.getAccounts() != null) {
            for (Account account : department.getAccounts()) {
                account.setChoose(true);
            }

            List<Account> list = new ArrayList<>();
            if (accountList != null && department.getAccounts() != null) {
                for (Account acc : department.getAccounts()) {
                    boolean hasAccount = false;
                    for (Account a : accountList) {
                        if (acc.getId() == a.getId()) {
                            hasAccount = true;
                        }
                    }
                    if (!hasAccount) {
                        list.add(acc);
                    }
                }
                accountList.addAll(list);
            }
        }
        if (department.getDepartments() != null) {
            for (Department department1 : department.getDepartments()) {
                getAccounts(department1);
            }
        }
    }

    /**
     * 清除选中人员
     *
     * @param department
     */
    private void removeAccouts(Department department) {
        if (department.getAccounts() != null) {
            for (Account account : department.getAccounts()) {
                account.setChoose(false);
            }
            accountList.removeAll(department.getAccounts());
        }
        if (department.getDepartments() != null) {
            for (Department department1 : department.getDepartments()) {
                department1.setCheck(false);
                removeAccouts(department1);
            }
        }
    }

    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * 筛选群组和用户
     *
     * @param string
     */
    private void screenList(String string) {
        if (TextUtils.isEmpty(string)) {
            screenDepart.clear();
            screeAccount.clear();
            friendAdapter.setData(accountList);
            checkedAdapter.setData(nowDepartment.getDepartments());
            if (accountList.size() == 0 || nowDepartment.getDepartments().size() == 0) {
                department_list.setVisibility(View.VISIBLE);
                account_list.setVisibility(View.VISIBLE);
            } else {
                department_list.setVisibility(View.GONE);
                account_list.setVisibility(View.GONE);
            }
            friendAdapter.notifyDataSetChanged();
            checkedAdapter.notifyDataSetChanged();
        } else {
            screenDepart.clear();
            screeAccount.clear();
            for (Account account : accountList) {
                if (account.getName().contains(string)) {
                    screeAccount.add(account);
                }
            }
            for (Department department : nowDepartment.getDepartments()) {
                if (department.getName().contains(string)) {
                    screenDepart.add(department);
                }
            }
            friendAdapter.setData(screeAccount);
            checkedAdapter.setData(screenDepart);
            friendAdapter.notifyDataSetChanged();
            checkedAdapter.notifyDataSetChanged();
        }
    }
}
