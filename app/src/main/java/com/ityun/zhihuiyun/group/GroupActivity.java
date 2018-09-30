package com.ityun.zhihuiyun.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.group.adapter.GroupAdapter;
import com.ityun.zhihuiyun.home.HomeActivity;
import com.ityun.zhihuiyun.util.KeyboartUtil;
import com.ityun.zhihuiyun.util.screen.Eyes;
import com.ityun.zhihuiyun.view.NoScrollListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/24 0024.
 */
public class GroupActivity extends BaseActivity {

    @BindView(R.id.group_back)
    public ImageView group_back;

    @BindView(R.id.add_group)
    public ImageView add_group;

    @BindView(R.id.search_group)
    public EditText search_group;

    @BindView(R.id.group_list)
    public NoScrollListView group_list;

    @BindView(R.id.empty_rl)
    RelativeLayout empty_rl;

    private List<GroupInfo> mlist = new ArrayList<>();

    private List<GroupInfo> screenMlist = new ArrayList<>();

    private GroupAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);
        adapter = new GroupAdapter(this);
        adapter.setData(mlist);
        group_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        search_group.addTextChangedListener(new TextWatcher() {
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

    /**
     * 筛选群组
     *
     * @param string
     */
    private void screenList(String string) {
        if (TextUtils.isEmpty(string)) {
            screenMlist.clear();
            adapter.setData(mlist);
            if (mlist.size() == 0) {
                empty_rl.setVisibility(View.VISIBLE);
                group_list.setVisibility(View.GONE);
            } else {
                empty_rl.setVisibility(View.GONE);
                group_list.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();
        } else {
            screenMlist.clear();
            for (GroupInfo groupInfo : mlist) {
                if (groupInfo.getDecodeNmae().contains(string)) {
                    screenMlist.add(groupInfo);
                }
            }
            adapter.setData(screenMlist);
            adapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.group_back, R.id.add_group})
    public void intentOnClick(View view) {
        switch (view.getId()) {
            case R.id.group_back:
                KeyboartUtil.hide(GroupActivity.this, search_group);
                finish();
                break;
            case R.id.add_group:
//                Intent intent = new Intent(this, SelectUserActiviity.class);
                Intent intent = new Intent(this, SelectDepartmentActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mlist.clear();
        mlist.addAll(HomeActivity.groupInfos);
        if (mlist.size() == 0) {
            empty_rl.setVisibility(View.VISIBLE);
            group_list.setVisibility(View.GONE);
        } else {
            empty_rl.setVisibility(View.GONE);
            group_list.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        KeyboartUtil.hide(GroupActivity.this, search_group);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
