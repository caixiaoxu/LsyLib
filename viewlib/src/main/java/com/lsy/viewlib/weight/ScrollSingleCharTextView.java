package com.lsy.viewlib.weight;

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
    public static final int DEFAULT_TEXTSIZE  = 36;

    private Paint           paint;
    private TextPaint       newTextPaint, oldTextPaint;

    private boolean         isAdd             = false;
    private AnimatorSet     addAnimator;
    private AnimatorSet     minusAnimator;

    private int             measureWidth;
    private int             measureHeight;

    private int             textColor;
    private int             textSize;

    private int             num;
    private int             oldNum;
    private int             newNum;

    private int             animatorOldY;
    private float           animatorOldAlpha  = 1;

    private int             animatorNewY;
    private float           animatorNewAlpha  = 0;
    private int             baseline;

    public ScrollSingleCharTextView(Context context) {
        this(context, null);
    }

    public ScrollSingleCharTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ScrollSingleCharTextView(Context context, @Nullable AttributeSet attrs,
                                    int defStyleAttr) {
        super(context, attrs, defStyleAttr);

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

        init();
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
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

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
        measureWidth = (int) (newTextPaint.measureText(String.valueOf(num)) + 4);
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
        canvas.drawColor(Color.RED);
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

    public void setAdd(boolean add) {
        isAdd = add;
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
        addAnimator.setDuration(1000);
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
        minusAnimator.setDuration(1000);
        minusAnimator.start();
    }

    private boolean   click      = false;

    private final int MOHUFANWEI = 10;

    private float     lastX      = 0;
    private float     lastY      = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (1 == event.getPointerCount()) {
                    click = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (click) {
                    onClick();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(lastX - x) > MOHUFANWEI || Math.abs(lastY - y) > MOHUFANWEI) {
                    click = false;
                }
                break;
        }

        lastX = x;
        lastY = y;
        return true;
    }

    private void onClick() {
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
            isAdd = true;
            oldNum = num;
            newNum = num + 1;
            num = newNum;
            add();
        }
    }

    /**
     * 重新计算绘画的值
     *
     * @param add
     */
    private void sumNum(boolean add) {
        isAdd = add;
        oldNum = num;
        newNum = num + (add ? 1 : -1);
        if (newNum < 0) {
            newNum = 9;
        } else if (newNum > 9) {
            newNum = 0;
        }
        num = newNum;
    }
}
