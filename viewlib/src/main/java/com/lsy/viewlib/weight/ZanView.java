package com.lsy.viewlib.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.lsy.viewlib.weight.textview.ScrollSingleCharTextView;

import java.util.ArrayList;
import java.util.List;

public class ZanView extends LinearLayout {
    private TextPaint textPaint;
    private int measureWidth, measureHeight;

    private int textSize = ScrollSingleCharTextView.DEFAULT_TEXTSIZE;

    private int num = 399;
    private boolean isAdd = false;

    private List<ScrollSingleCharTextView> charTvs = new ArrayList<>();

    public ZanView(Context context) {
        this(context, null);
    }

    public ZanView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    /**
     * 初始化
     */
    private void init() {

        initPaints();
        initParams();
        initView();
    }

    /**
     * 初始化画笔
     */
    private void initPaints() {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
    }

    /**
     * 初始化参数
     */
    private void initParams() {
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        measureWidth = (int) textPaint.measureText(String.valueOf(num));
        measureHeight = (int) (fontMetrics.bottom - fontMetrics.top);
    }

    protected void initView() {
        charTvs.clear();
        String str_num = String.valueOf(num);
        for (int i = 0; i < str_num.length(); i++) {
            ScrollSingleCharTextView textView = new ScrollSingleCharTextView(getContext());
            int show_num = Integer.valueOf(str_num.substring(i, i + 1));
            Log.e("zanview", "show_num:" + show_num);
            textView.setNum(show_num);
            addView(textView);
            charTvs.add(textView);
        }
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

    private boolean click = false;

    private final int MOHUFANWEI = 10;

    private float lastX = 0;
    private float lastY = 0;

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
        } else {
            String str_num = String.valueOf(num);
            for (int i = (str_num.length() - 1); i >= 0; i--) {
                int chr_num = Integer.valueOf(str_num.substring(i, i + 1));
                if (chr_num++ > 9 && charTvs.size() > i) {
                    charTvs.get(i).change(false);
                }
            }
            num++;
        }
    }
}
