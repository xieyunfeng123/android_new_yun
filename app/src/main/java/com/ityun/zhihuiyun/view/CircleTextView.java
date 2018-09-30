package com.ityun.zhihuiyun.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;

/**
 * Created by Administrator on 2018/5/30 0030.
 */

public class CircleTextView extends TextView {

    private int radius;

    private int borderWidth;

    private int borderColor;

    private int fillColor;

    public CircleTextView(Context context) {
        this(context, null);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleTextView);
        borderWidth = typedArray.getDimensionPixelSize(R.styleable.CircleTextView_borderWidth, 0);
        borderColor = typedArray.getColor(R.styleable.CircleTextView_borderColor, Color.WHITE);
        fillColor = typedArray.getColor(R.styleable.CircleTextView_fillColor, Color.WHITE);
        typedArray.recycle();
        setGravity(Gravity.CENTER);
    }


    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int max = Math.max(measuredWidth, measuredHeight);
        setMeasuredDimension(max, max);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getWidth();
        // 半径
        radius = Math.min(width, height) / 2;
        if (borderWidth > 0) {
            Paint borderPaint = new Paint();
            borderPaint.setColor(borderColor);
            canvas.drawCircle(getWidth() / 2, getWidth() / 2, radius, borderPaint);
        }
        Paint fillPaint = new Paint();
        fillPaint.setColor(fillColor);
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, radius - borderWidth, fillPaint);
        super.onDraw(canvas);
    }

//    @Override
//    public void setText(CharSequence text, BufferType type) {
//        super.setText(text, type);
//    }

    public void setTextString(String textString) {
        String text = "";
        if (textString.length() >= 2) {
            text = textString.substring(textString.length() - 2, textString.length());
        } else {
            text = textString;
        }
        setText(text);
    }

}