package com.ityun.zhihuiyun.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.util.DensityUtil;

/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class CircleBgImageView extends ImageView {
    private int nWidth;

    private int nHeight;

    private Paint paint;

    private Paint txtPint;

    private String text = "";
    private Rect mBound;

    public CircleBgImageView(Context context) {
        super(context);
        initPaint();
    }

    public CircleBgImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public CircleBgImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    public CircleBgImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.main_color));
        txtPint = new Paint();
        txtPint.setColor(getResources().getColor(R.color.white));
        mBound = new Rect();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!TextUtils.isEmpty(text)) {

            canvas.drawCircle(nWidth / 2, nHeight / 2, nWidth / 2, paint);


            txtPint.getTextBounds(getShowText(), 0, getShowText().length(), mBound);
          /*
         * 控件宽度/2 - 文字宽度/2
		 */
            float startX = getWidth() / 2 - mBound.width() / 2;

		/*
         * 控件高度/2 + 文字高度/2,绘制文字从文字左下角开始,因此"+"
		 */
            // float startY = getHeight() / 2 + mBound.height() / 2;

            Paint.FontMetricsInt fm = txtPint.getFontMetricsInt();

            // int startY = getHeight() / 2 - fm.descent + (fm.descent - fm.ascent)
            // / 2;
//            int startY = getHeight() / 2 - fm.descent + (fm.bottom - fm.top) / 2;

            int startY=getHeight()/2 - (fm.descent - (-fm.ascent + fm.descent)/2);
            txtPint.setTextSize((float) (getWidth() / 2.5));
            // 绘制文字
            canvas.drawText(getShowText(), startX, startY, txtPint);
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        nWidth = MeasureSpec.getSize(widthMeasureSpec);
        nHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void settText(String text) {
        this.text = text;
        invalidate();
    }


    private String getShowText() {
        if (text.length() >= 2) {
            return text.substring(text.length() - 2, text.length());
        }
        return text;
    }
}
