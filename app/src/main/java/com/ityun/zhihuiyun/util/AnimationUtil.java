package com.ityun.zhihuiyun.util;

import android.view.View;
import android.view.animation.TranslateAnimation;

/**
 * Created by Administrator on 2018/6/15 0015.
 * 动画
 */

public class AnimationUtil {

    /**
     * 从下到上
     * @param view
     */
    public static void buttomToTop(View view) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 100, 0);
        animation.setDuration(500);
        view.setAnimation(animation);
    }

    /**
     * 从上到下
     * @param view
     */
    public static void topToBottom(View view) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 100);
        animation.setDuration(500);
        view.setAnimation(animation);
    }


}
