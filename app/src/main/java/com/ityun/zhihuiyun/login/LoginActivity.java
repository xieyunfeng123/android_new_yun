package com.ityun.zhihuiyun.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.base.ConstantAuthenticate;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.AccountBean;
import com.ityun.zhihuiyun.bean.Department;
import com.ityun.zhihuiyun.bean.DepartmentBean;
import com.ityun.zhihuiyun.bean.IpInfo;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.home.HomeActivity;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.view.ProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2018/5/18 0018.
 */
public class LoginActivity extends BaseActivity {

    // 登录按钮
    @BindView(R.id.login_button)
    public Button login_button;

    // 登录名
    @BindView(R.id.login_name)
    public EditText login_name;

    // 登录密码
    @BindView(R.id.login_password)
    public EditText login_password;

    @BindView(R.id.bt_fuwu)
    public Button bt_fuwu;

    @BindView(R.id.login_maccode)
    public TextView login_maccode;

    private User user;

    private int LOGIN_BACK = -1;

    //private MyDialog myDialog;
    private Handler handler;
    private Mythread mythread;

    private boolean hasInit;

    //所需要申请的权限数组
    private static final String[] permissionsArray = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE
    };
    //还需申请的权限列表
    private List<String> permissionsList = new ArrayList<String>();
    //申请权限后的返回码
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private static final int REQUEST_CODE = 1;

    private Dialog dialog;

//    private  int

    //判断权限
    private boolean commonROMPermissionCheck(Context context) {
        Boolean result = true;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class clazz = Settings.class;
                Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                result = (Boolean) canDrawOverlays.invoke(null, context);
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return result;
    }

    //申请悬浮窗口权限
    private void requestAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE);
    }

    //申请其他需要用到的权限
    private void checkRequiredPermission(Context context) {
        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        if (permissionsList.size() != 0) {
            ActivityCompat.requestPermissions((Activity) context, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_PERMISSIONS);
        } else {
//            veye的初始化
            if (!hasInit) {
                TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String mImei = "a_" + TelephonyMgr.getDeviceId();
                login_maccode.setText(mImei);
                hasInit = true;
            }
//            initLogin();
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        dialog = ProgressDialog.create(this, "");

//        BadgeNumberManager.from(LoginActivity.this).setBadgeNumber(10);
//        Toast.makeText(LoginActivity.this, "设置桌面角标成功", Toast.LENGTH_SHORT).show();

        //获取用户名缓存
        user = SpUtil.getUser();
        if (!TextUtils.isEmpty(user.getUserName())) {
            login_name.setText(user.getUserName());
            login_password.setText(user.getPassword());
        }
        //判断是否是需要动态申请悬浮窗口权限
        if (commonROMPermissionCheck(this)) {
            checkRequiredPermission(this);
        } else {
            requestAlertWindowPermission();
            checkRequiredPermission(this);
        }
    }

    @OnClick({R.id.login_button, R.id.bt_fuwu})
    public void setOnClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
//                checkRequiredPermission(this);
                if (!hasInit) {
                    TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    String mImei = "a_" + TelephonyMgr.getDeviceId();
                    login_maccode.setText(mImei);
                    hasInit = true;
                }
                initLogin();
                break;
            case R.id.bt_fuwu:
                Intent intent = new Intent(this, IpActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    @SuppressLint("HandlerLeak")
    private void initLogin() {
        IpInfo ipInfo = new IpInfo();
        ipInfo.setIp(App.old_ip);
        ipInfo.setPort(App.old_oprt);
        SpUtil.setIp(ipInfo);
        saveListIp(ipInfo);
        if (login_name.getText().toString().isEmpty() && login_password.getText().toString().isEmpty()) {
            return;
        }
        if (login_name.getText().toString().isEmpty() || login_password.getText().toString().isEmpty()||login_maccode.getText().toString().isEmpty()) {
            Toast.makeText(this, "账号,密码或机器码不能为空", Toast.LENGTH_LONG).show();
        }
        else {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 10:
                            getLoginConstants(Integer.parseInt(msg.obj.toString()));
                            break;
                        default:
                            break;
                    }
                }
            };
            dialog.show();
            mythread = new Mythread();
            mythread.start();
        }
    }
    //+"&"+login_maccode.getText().toString()
    class Mythread extends Thread {
        @Override
        public void run() {
            super.run();
//            LOGIN_BACK = App.getInstance().login(login_name.getText().toString() + "&" + login_maccode.getText().toString() + "&" + login_maccode.getText().toString()
            LOGIN_BACK = App.getInstance().login(login_name.getText().toString() , login_password.getText().toString());
            Message message = new Message();
            message.what = 10;
            message.obj = LOGIN_BACK;
            handler.sendMessage(message);
        }
    }

    @SuppressWarnings("static-access")
    private void getLoginConstants(int LOGIN_BACK) {
        if (LOGIN_BACK == ConstantAuthenticate.WM_Authenticate_Success) {
            String name = login_name.getText().toString();
            String passWord = login_password.getText().toString();
            SpUtil.setUser(name, passWord, 0);
            getAccount();
        } else if (LOGIN_BACK == ConstantAuthenticate.WM_Authenticate_ErrorCode_ErrorPassword) {
            Toast.makeText(this, "密码错误", Toast.LENGTH_LONG).show();
        } else if (LOGIN_BACK == ConstantAuthenticate.WM_Authenticate_ErrorCode_HasLogin) {
            Toast.makeText(this, "该用户已登录", Toast.LENGTH_LONG).show();
        } else if (LOGIN_BACK == ConstantAuthenticate.WM_Authenticate_ErrorCode_NoExistUserName) {
            Toast.makeText(this, "用户名不存在", Toast.LENGTH_LONG).show();
        } else if (LOGIN_BACK == ConstantAuthenticate.WM_Authenticate_ErrorCode_ResponseTimeout) {
            Toast.makeText(this, "请检查网络!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "登录失败!", Toast.LENGTH_LONG).show();
        }
        dialog.dismiss();
    }


    private void saveListIp(IpInfo ipInfo) {
        List<IpInfo> mlist = SpUtil.getListIp();
        if (mlist != null && mlist.size() != 0) {
            IpInfo deleteIp = null;
            for (IpInfo ip : mlist) {
                if (ip.getIp().equals(ipInfo.getIp())) {
                    deleteIp = ip;
                }
            }
            if (deleteIp != null)
                mlist.remove(deleteIp);
            mlist.add(0, ipInfo);
        } else {
            mlist.add(0, ipInfo);
        }
        SpUtil.setListIp(mlist);
    }

    /**
     * 获取好友
     */
    private void getAccount() {
        bean = App.getInstance().getSysConfBean();
        OkHttpUtils
                .post()
                .url("http://" + App.old_ip + ":" + bean.getHttpport())
                .addParams("msgid", "259")
                .addParams("userid", bean.getAccountid() + "")
                .addParams("pageid", 0 + "")
                .addParams("pagecount", "0")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        AccountBean bean = gson.fromJson(s, AccountBean.class);
                        if (bean != null && bean.getResult() == 0) {
                            if (bean.getAccounts() != null && bean.getAccounts().size() != 0) {
                                for (Account account : bean.getAccounts()) {
                                    if (account.getName().equals(login_name.getText().toString())) {
                                        bean.getAccounts().remove(account);
                                        break;
                                    }
                                }
                                App.getInstance().setAccountList(bean.getAccounts());
                                runOnUiThread(() -> {
                                    getDepartment();
                                });
                            }
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "登录失败!", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            });
                        }
                    }
                });
    }

    /**
     * 获取好友
     */
    SysConfBean bean;

    /**
     * 获取组织结构
     */
    private void getDepartment() {
        OkHttpUtils
                .post()
                .url("http://" + App.old_ip + ":" + bean.getHttpport())
                .addParams("msgid", "260")
                .addParams("userid", bean.getAccountid() + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {

                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        DepartmentBean bean = gson.fromJson(s.trim(), DepartmentBean.class);
                        if (bean != null && bean.getResult() == 0) {
                            Department department = new Department();
                            sortDepartment(department, bean.getDepartments());
                            App.getInstance().setDepartment(department);
                            runOnUiThread(() -> {
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "登录失败!", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            });
                        }
                    }
                });
    }

    private void sortDepartment(Department department, List<Department> departments) {
        if (departments == null || departments.size() == 0) {
            return;
        }
        List<Department> mlist = new ArrayList<>();
        for (Department ment : departments) {
            if (ment.getParentid() == department.getId()) {
                mlist.add(ment);
            }
        }
        department.setDepartments(mlist);
        if (App.getInstance().getAccountList() != null) {
            List<Account> accounts = new ArrayList<>();
            for (Account account : App.getInstance().getAccountList()) {
                if (account.getDepartmentid() == department.getId()) {
                    if (bean != null && bean.getAccountid() != account.getId()) {
                        accounts.add(account);
                    }
                }
            }
            department.setAccounts(accounts);
        }
        if (department.getDepartments() != null && department.getDepartments().size() != 0) {
            for (Department department1 : department.getDepartments()) {
                sortDepartment(department1, departments);
            }
        } else {
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        if (!hasInit) {
                            TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            String mImei = "a_" + TelephonyMgr.getDeviceId();
                            login_maccode.setText(mImei);
                            hasInit = true;
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "权限被拒绝： " + permissions[i], Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
