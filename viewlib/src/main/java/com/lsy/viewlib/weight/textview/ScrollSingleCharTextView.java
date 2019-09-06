package com.lsy.viewlib.weight.textview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lsy.viewlib.R;

public class ScrollSingleCharTextView extends View {

    public static final int DEFAULT_TEXTCOLOR = Color.BLACK;
    public static final int DEFAULT_TEXTSIZE = 36;

    private TextPaint newTextPaint, oldTextPaint;

    private AnimatorSet addAnimator;
    private AnimatorSet minusAnimator;

    private int measureWidth;
    private int measureHeight;

    private int textColor = DEFAULT_TEXTCOLOR;
    private int textSize = DEFAULT_TEXTSIZE;

    private int num;
    private int oldNum;
    private int newNum;

    private int animatorOldY;
    private float animatorOldAlpha = 1;

    private int animatorNewY;
    private float animatorNewAlpha = 0;
    private int baseline;

    public ScrollSingleCharTextView(Context context) {
        super(context);

        init();
    }

    public ScrollSingleCharTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initAttr(context, attrs);

        init();
    }

    public ScrollSingleCharTextView(Context context, @Nullable AttributeSet attrs,
                                    int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttr(context, attrs);

        init();
    }

    /**
     * 初始化属性
     *
     * @param context
     * @param attrs
     */
    private void initAttr(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.ScrollSingleCharTextView);
        textColor = typedArray.getColor(R.styleable.ScrollSingleCharTextView_textColor,
                DEFAULT_TEXTCOLOR);
        textSize = typedArray.getDimensionPixelSize(R.styleable.ScrollSingleCharTextView_textSize,
                DEFAULT_TEXTSIZE);
        num = typedArray.getInt(R.styleable.ScrollSingleCharTextView_number, 0);
        if (0 > num || num > 10) {
            throw new IllegalArgumentException("Number is only 0-9");
        }
        oldNum = num;

        typedArray.recycle();
    }

    /**
     * 初始化
     */
    private void init() {
        initPaints();
        initParams();
    }

    /**
     * 初始化画笔
     */
    private void initPaints() {
        newTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        newTextPaint.setStyle(Paint.Style.FILL);
        newTextPaint.setTextSize(textSize);
        newTextPaint.setColor(textColor);
        newTextPaint.setTextAlign(Paint.Align.CENTER);

        oldTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        oldTextPaint.set(newTextPaint);
    }

    /**
     * 初始化参数
     */
    private void initParams() {
        Paint.FontMetrics fontMetrics = newTextPaint.getFontMetrics();
        measureWidth = (int) newTextPaint.measureText(String.valueOf(num));
        measureHeight = (int) (fontMetrics.bottom - fontMetrics.top);

        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        baseline = (int) (measureHeight * 1.0f / 2 + distance);

        animatorOldY = baseline;
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
                widthSize = measureWidth;
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
                heightSize = measureHeight;
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
        oldTextPaint.setAlpha((int) (255 * animatorOldAlpha));
        canvas.drawText(String.valueOf(oldNum), width / 2, animatorOldY, oldTextPaint);

        newTextPaint.setAlpha((int) (255 * animatorNewAlpha));
        canvas.drawText(String.valueOf(newNum), width / 2, animatorNewY, newTextPaint);
    }

    public void setAnimatorOldY(int animatorOldY) {
        this.animatorOldY = animatorOldY;
        invalidate();
    }

    public void setAnimatorOldAlpha(float animatorOldAlpha) {
        this.animatorOldAlpha = animatorOldAlpha;
        invalidate();
    }

    public void setAnimatorNewY(int animatorNewY) {
        this.animatorNewY = animatorNewY;
        invalidate();
    }

    public void setAnimatorNewAlpha(float animatorNewAlpha) {
        this.animatorNewAlpha = animatorNewAlpha;
        invalidate();
    }

    public void setNum(int num) {
        this.num = num;
        if (0 > num || num > 10) {
            throw new IllegalArgumentException("Number is only 0-9");
        }
        oldNum = num;
        invalidate();
    }

    public void add() {
        ObjectAnimator oldYAnimator = ObjectAnimator.ofInt(this, "animatorOldY", baseline, 0);
        ObjectAnimator oldAlphaAnimator = ObjectAnimator.ofFloat(this, "animatorOldAlpha", 1, 0);
        ObjectAnimator newYAnimator = ObjectAnimator.ofInt(this, "animatorNewY", baseline * 2,
                baseline);
        ObjectAnimator newAlphaAnimator = ObjectAnimator.ofFloat(this, "animatorNewAlpha", 0, 1);

        addAnimator = new AnimatorSet();
        addAnimator.playTogether(oldYAnimator, oldAlphaAnimator, newYAnimator, newAlphaAnimator);
        addAnimator.setInterpolator(new LinearInterpolator());
        addAnimator.setDuration(300);
        addAnimator.start();
    }

    public void minus() {
        ObjectAnimator oldYAnimator = ObjectAnimator.ofInt(this, "animatorOldY", baseline,
                baseline * 2);
        ObjectAnimator oldAlphaAnimator = ObjectAnimator.ofFloat(this, "animatorOldAlpha", 1, 0);
        ObjectAnimator newYAnimator = ObjectAnimator.ofInt(this, "animatorNewY", 0, baseline);
        ObjectAnimator newAlphaAnimator = ObjectAnimator.ofFloat(this, "animatorNewAlpha", 0, 1);

        minusAnimator = new AnimatorSet();
        minusAnimator.playTogether(oldYAnimator, oldAlphaAnimator, newYAnimator, newAlphaAnimator);
        minusAnimator.setInterpolator(new LinearInterpolator());
        minusAnimator.setDuration(300);
        minusAnimator.start();
    }


    public void change(boolean isAdd) {
        if (isAdd) {
            if (null != addAnimator && addAnimator.isStarted()) {
                addAnimator.cancel();
            }
            if (null != minusAnimator && minusAnimator.isStarted()) {
                return;
            }
            sumNum(false);
            minus();
        } else {
            if (null != minusAnimator && minusAnimator.isStarted()) {
                minusAnimator.cancel();
            }
            if (null != addAnimator && addAnimator.isStarted()) {
                return;
            }
            sumNum(true);
            add();
        }
    }


    /**
     * 重新计算绘画的值
     *
     * @param isAdd
     */
    private void sumNum(boolean isAdd) {
        oldNum = num;
        newNum = num + (isAdd ? 1 : -1);
        if (newNum < 0) {
            newNum = 9;
        } else if (newNum > 9) {
            newNum = 0;
        }
        num = newNum;
    }
}
