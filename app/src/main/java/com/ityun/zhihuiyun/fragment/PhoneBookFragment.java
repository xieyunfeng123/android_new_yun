package com.ityun.zhihuiyun.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.Department;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.chat.ChatActivity;
import com.ityun.zhihuiyun.department.DepartmentActivity;
import com.ityun.zhihuiyun.event.FriendEvent;
import com.ityun.zhihuiyun.event.GroupEvent;
import com.ityun.zhihuiyun.event.UserOnLineEvent;
import com.ityun.zhihuiyun.fragment.adapter.DepartmentAdapter;
import com.ityun.zhihuiyun.fragment.adapter.FriendAdapter;
import com.ityun.zhihuiyun.group.GroupActivity;
import com.ityun.zhihuiyun.home.HomeActivity;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.view.NoScrollListView;
import com.ityun.zhihuiyun.window.IMService;
import com.zhy.clientsdk.WMAccountInfo;
import com.zhy.clientsdk.WMGroupInfo;
import com.zhy.clientsdk.ZHYClientSdk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/22 0022.
 * 通讯录
 */

public class PhoneBookFragment extends Fragment {

    @BindView(R.id.friend_list)
    NoScrollListView friend_list;

    @BindView(R.id.group_num)
    TextView group_num;

    @BindView(R.id.layout_to_group)
    LinearLayout layout_to_group;

    @BindView(R.id.search_user)
    EditText search_user;

    @BindView(R.id.department_list)
    NoScrollListView department_list;

    private static FriendAdapter adapter;

    private List<Account> mlist = new ArrayList<>();

//    private boolean isFirst = true;

    private List<Account> screenMlist = new ArrayList<>();

    private Department department;

    private DepartmentAdapter departmentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phonebook, null);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        adapter = new FriendAdapter(getActivity());
        friend_list.setAdapter(adapter);
        department = App.getInstance().getDepartment();
        departmentAdapter = new DepartmentAdapter(getActivity());
        departmentAdapter.setData(department.getDepartments());
        department_list.setAdapter(departmentAdapter);
        department_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DepartmentActivity.class);
                intent.putExtra("department", department.getDepartments().get(position));
                startActivity(intent);
            }
        });

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

        friend_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                if (screenMlist.size() != 0) {
                    intent.putExtra("account", screenMlist.get(position));
                } else {
                    intent.putExtra("account", mlist.get(position));
                }
                intent.putExtra("type", 0);
                startActivity(intent);
            }
        });
        return view;
    }

    @OnClick(R.id.layout_to_group)
    public void onViewClick() {
        Intent intent = new Intent(getActivity(), GroupActivity.class);
        startActivity(intent);
    }

    /**
     * 筛选
     *
     * @param inputString
     */
    private void screenList(String inputString) {
        if (TextUtils.isEmpty(inputString)) {
            screenMlist.clear();
            adapter.setData(mlist);
            adapter.notifyDataSetChanged();
        } else {
            screenMlist.clear();
            for (Account accountInfo : mlist) {
                if (accountInfo.getName().contains(inputString)) {
                    screenMlist.add(accountInfo);
                }
            }
            adapter.setData(screenMlist);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取联系人
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessage(FriendEvent event) {
//        mlist.clear();
//        mlist.addAll(App.accountList);
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                adapter.setData(mlist);
//                adapter.notifyDataSetChanged();
//            }
//        });
    }

    /**
     * 获取到群组
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupMessage(GroupEvent event) {
        List<GroupInfo> groupInfos = HomeActivity.groupInfos;
        if (groupInfos != null) {
            group_num.setText("(" + groupInfos.size() + ")");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            List<GroupInfo> groupInfos = HomeActivity.groupInfos;
            if (groupInfos != null) {
                group_num.setText("(" + groupInfos.size() + ")");
            }
        }
    }


    /**
     * 刷新上下线通知
     */
    public static void upDataOnline() {
        if (handler != null) {
            handler.sendEmptyMessage(0);
        }
//        EventBus.getDefault().post(new UserOnLineEvent());
    }

    @SuppressLint("HandlerLeak")
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
    };


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new FriendEvent());
        EventBus.getDefault().post(new GroupEvent());
    }


}
