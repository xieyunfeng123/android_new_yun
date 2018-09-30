package com.ityun.zhihuiyun.department;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.Department;
import com.ityun.zhihuiyun.chat.ChatActivity;
import com.ityun.zhihuiyun.department.adapter.TopNameAdaper;
import com.ityun.zhihuiyun.fragment.adapter.DepartmentAdapter;
import com.ityun.zhihuiyun.fragment.adapter.FriendAdapter;
import com.ityun.zhihuiyun.util.screen.Eyes;
import com.ityun.zhihuiyun.view.NoScrollListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/8/30 0030.
 */
public class DepartmentActivity extends BaseActivity {

    @BindView(R.id.department_back)
    ImageView department_back;

    @BindView(R.id.department_name)
    TextView department_name;

    @BindView(R.id.department_name_list)
    RecyclerView department_name_list;

    @BindView(R.id.department_list)
    NoScrollListView department_list;

    @BindView(R.id.account_list)
    NoScrollListView account_list;

    private Department department;

    private List<Department> names = new ArrayList<>();

    private TopNameAdaper topNameAdaper;

    private DepartmentAdapter departmentAdapter;

    private FriendAdapter friendAdapter;

    private Department nowDepartment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_department);
        ButterKnife.bind(this);
        department = (Department) getIntent().getSerializableExtra("department");
        nowDepartment = department;
        department_name.setText(nowDepartment.getName());
        names.add(nowDepartment);
        topNameAdaper = new TopNameAdaper(this);
        department_name_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        topNameAdaper.setData(names);
        department_name_list.setAdapter(topNameAdaper);

        topNameAdaper.setOnItemClickListener(new TopNameAdaper.OnItemClick() {
            @Override
            public void onClik(int position) {
                changeDepartment(names.get(position));
            }
        });

        departmentAdapter = new DepartmentAdapter(this);
        departmentAdapter.setData(nowDepartment.getDepartments());
        department_list.setAdapter(departmentAdapter);
        department_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeDepartment(nowDepartment.getDepartments().get(position));
            }
        });

        friendAdapter = new FriendAdapter(this);
        friendAdapter.setData(nowDepartment.getAccounts());
        account_list.setAdapter(friendAdapter);
        friendAdapter.notifyDataSetChanged();

        account_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DepartmentActivity.this, ChatActivity.class);
                intent.putExtra("account", nowDepartment.getAccounts().get(position));
                intent.putExtra("type", 0);
                startActivity(intent);
            }
        });
        department_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void changeDepartment(Department changeDepartment) {
        nowDepartment = changeDepartment;
        department_name.setText(nowDepartment.getName());
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
        departmentAdapter.setData(nowDepartment.getDepartments());
        friendAdapter.setData(nowDepartment.getAccounts());

        topNameAdaper.notifyDataSetChanged();
        departmentAdapter.notifyDataSetChanged();
        friendAdapter.notifyDataSetChanged();
    }
}
