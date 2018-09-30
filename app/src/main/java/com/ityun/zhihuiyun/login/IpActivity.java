package com.ityun.zhihuiyun.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.IpInfo;
import com.ityun.zhihuiyun.login.adapter.PortAdapter;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.util.screen.Eyes;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class IpActivity extends BaseActivity {

    @BindView(R.id.setting_return)
    public LinearLayout setting_return;

    @BindView(R.id.et_port)
    public EditText et_port;

    @BindView(R.id.tv_lishi)
    public TextView tv_lishi;

    @BindView(R.id.iv_ok)
    public LinearLayout iv_ok;

    @BindView(R.id.iv_detele)
    public ImageView iv_detele;

    @BindView(R.id.lv_ip)
    public ListView lv_ip;
    private List<IpInfo> mlists;

    private IpInfo ipInfo;

    private PortAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.light_top_color));
        setContentView(R.layout.activity_ip);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        ipInfo = SpUtil.getIp();
        showIp(ipInfo);
        mlists = SpUtil.getListIp();
        adapter = new PortAdapter(this, mlists);
        lv_ip.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        lv_ip.setOnItemClickListener((parent, view, position, id) -> {
            ipInfo = mlists.get(position);
            showIp(ipInfo);
        });
    }

    private void showIp(IpInfo ipInfo) {
        if (ipInfo == null || TextUtils.isEmpty(ipInfo.getIp())) {
            ipInfo = new IpInfo();
            ipInfo.setIp(App.old_ip);
            ipInfo.setPort(App.old_oprt);
        }
        String ip = ipInfo.getIp();
        int port = ipInfo.getPort();
        et_port.setText(ip + ":" + port);
    }

    @OnClick({R.id.setting_return, R.id.iv_detele, R.id.iv_ok})
    public void viewSetOnClick(View view) {
        switch (view.getId()) {
            case R.id.setting_return:
                finish();
                break;
            case R.id.iv_detele:
                et_port.setText("");
                break;
            case R.id.iv_ok:
                if (TextUtils.isEmpty(et_port.getText().toString())) {
                    Tost("IP不能为空!");
                    return;
                }
                String[] ips = et_port.getText().toString().split(":");
                if (ips.length != 2) {
                    Tost("请输入正确的IP!");
                    return;
                }
                App.old_ip = ips[0];
                App.old_oprt=Integer.parseInt(ips[1]);
                Tost("IP设置成功!");
                finish();
                break;
            default:
                break;
        }
    }

}
