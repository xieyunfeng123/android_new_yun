package com.ityun.zhihuiyun.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;

/**
 * Created by Administrator on 2018/5/22 0022.
 */

public class ProgressDialog extends Dialog {

    private static LinearLayout lLayout_bg;
    private static Display display;

    public ProgressDialog(@NonNull Context context) {
        super(context);
    }

    public static Dialog create(Context context, String message) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.dialog_layout, null);
        TextView progress_text = v.findViewById(R.id.progress_text);
        if (!TextUtils.isEmpty(message)) {
            progress_text.setText(message);
        }
        lLayout_bg = v.findViewById(R.id.lLayout_bg);
        Dialog dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(v);

        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.5), LinearLayout.LayoutParams.WRAP_CONTENT));

        return dialog;
    }
}
