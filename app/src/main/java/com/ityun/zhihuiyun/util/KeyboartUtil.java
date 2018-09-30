package com.ityun.zhihuiyun.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Administrator on 2018/6/27 0027.
 */

public class KeyboartUtil {
    /**
     * 隐藏软键盘
     *
     * @param context
     * @param view
     */
    public static void hide(Context context, EditText view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
        if (isOpen) {
//            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            //隐藏软键盘 //
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
