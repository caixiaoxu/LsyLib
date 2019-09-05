package com.lsy.viewlib.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.lsy.viewlib.R;

public class ZanTextView extends View {

    public static final int DEFAULT_TEXTCOLOR = Color.BLACK;
    public static final int DEFAULT_TEXTSIZE  = 36;

    private Paint           paint;
    private TextPaint       newTextPaint, oldTextPaint;

    private int             textColor;
    private int             textSize;
    private int             num;

    public ZanTextView(Context context) {
        this(context, null);
    }

    public ZanTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ZanTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZanTextView);
        textColor = typedArray.getColor(R.styleable.ZanTextView_textColor, DEFAULT_TEXTCOLOR);
        textSize = typedArray.getDimensionPixelSize(R.styleable.ZanTextView_textSize,
                DEFAULT_TEXTSIZE);
        num = typedArray.getInt(R.styleable.ZanTextView_number, 0);

        typedArray.recycle();

        init();
    }

    /**
     * 初始化
     */
    private void init() {

        initPaint();

    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        newTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        newTextPaint.setStyle(Paint.Style.FILL);
        newTextPaint.setTextSize(textSize);
        newTextPaint.setColor(textColor);

        oldTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        oldTextPaint.set(newTextPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                widthSize = (int) newTextPaint.measureText(String.valueOf(num)) + 10;
                break;
            case MeasureSpec.EXACTLY:
                break;
        }

        int heightMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                heightSize = textSize*2 + 10;
                break;
            case MeasureSpec.EXACTLY:
                break;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        canvas.drawColor(Color.RED);
        canvas.drawText(String.valueOf(num), 0, textSize, newTextPaint);
        canvas.drawText(String.valueOf(num), 0, textSize*2, oldTextPaint);
    }
}
