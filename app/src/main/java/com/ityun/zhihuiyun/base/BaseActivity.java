package com.ityun.zhihuiyun.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
/**
 * Created by Administrator on 2018/5/18 0018.
 */

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    public void Tost(String message) {
        if (!this.isDestroyed())
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }




}
