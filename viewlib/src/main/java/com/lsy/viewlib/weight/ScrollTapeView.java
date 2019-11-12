package com.lsy.viewlib.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.lsy.viewlib.utils.DensityUtil;

public class ScrollTapeView extends View {

    private Paint scalePaint, curScalePaint, bgScalePaint;
    private TextPaint valuePaint, curValuePaint, unitPaint;

    private float tapeSpace = DensityUtil.dp2Px(getContext(), 5);
    private float tapeWidth = DensityUtil.dp2Px(getContext(), 1);
    private float curTapeWidth = DensityUtil.dp2Px(getContext(), 5);
    private float tapeAreaHeight = DensityUtil.dp2Px(getContext(), 100);
    private float curValuePB = DensityUtil.dp2Px(getContext(), 30);
    private float curValueHeight;

    private float tapeValue = 0.0f;

    public ScrollTapeView(Context context) {
        super(context);

        initView();
    }

    public ScrollTapeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ScrollTapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        //初始化画笔
        //刻度
        scalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scalePaint.setStyle(Paint.Style.STROKE);
        scalePaint.setStrokeCap(Paint.Cap.ROUND);
        scalePaint.setColor(Color.parseColor("#999999"));
        //当前刻度
        curScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        curScalePaint.setStyle(Paint.Style.STROKE);
        curScalePaint.setStrokeCap(Paint.Cap.ROUND);
        curScalePaint.setStrokeWidth(curTapeWidth);
        curScalePaint.setColor(Color.parseColor("#48ba75"));
        //刻度值
        valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setColor(Color.parseColor("#333333"));
        valuePaint.setStyle(Paint.Style.FILL);
        valuePaint.setTextSize(DensityUtil.sp2px(getContext(), 15));
        //当前刻度值
        curValuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        curValuePaint.setColor(Color.parseColor("#48ba75"));
        curValuePaint.setStyle(Paint.Style.FILL);
        curValuePaint.setTextAlign(Paint.Align.CENTER);
        curValuePaint.setTextSize(DensityUtil.sp2px(getContext(), 36));
        //单位
        unitPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        unitPaint.setColor(Color.parseColor("#48ba75"));
        unitPaint.setStyle(Paint.Style.FILL);
        unitPaint.setTextSize(DensityUtil.sp2px(getContext(), 18));
        unitPaint.setTextAlign(Paint.Align.LEFT);

        Paint.FontMetrics fontMetrics = curValuePaint.getFontMetrics();
        curValueHeight = fontMetrics.bottom - fontMetrics.top;

        //背景
        bgScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setStyle(Paint.Style.FILL);
        bgScalePaint.setColor(Color.parseColor("#f6f9f6"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (MeasureSpec.AT_MOST == heightMode) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(widthSize, (int) Math.ceil(curValueHeight + curValuePB + tapeAreaHeight));
        }
    }

    public void setTapeValue(float tapeValue) {
        this.tapeValue = tapeValue;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        //画当前刻度值
        Paint.FontMetrics fontMetrics = curValuePaint.getFontMetrics();
        float valueHeight = fontMetrics.bottom - fontMetrics.top;
        float dx = valueHeight / 2 - fontMetrics.bottom;
        String curValue = String.format("%.1f", tapeValue);
        canvas.drawText(curValue, getScrollX() + width / 2, curValueHeight / 2 + dx, curValuePaint);
        //单位
        canvas.drawText("kg", getScrollX() + width / 2 + curValuePaint.measureText(curValue) / 2, valueHeight / 4 + dx, unitPaint);

        //背景
        float top = height - tapeAreaHeight;
        float bottom = height;
        canvas.drawRect(0, top, width + tapeSpace * 100, bottom, bgScalePaint);

        //刻度
        Paint.FontMetrics fontMetrics1 = valuePaint.getFontMetrics();
        float dx1 = (fontMetrics1.bottom - fontMetrics1.top) / 2 - fontMetrics1.bottom;
        for (int i = 0; i <= 100; i++) {
            //按十绘值
            if (0 == i % 10) {
                scalePaint.setStrokeWidth(tapeWidth + 1);
                if (0 == i) {
                    valuePaint.setTextAlign(Paint.Align.LEFT);
                } else if (100 == i) {
                    valuePaint.setTextAlign(Paint.Align.RIGHT);
                } else {
                    valuePaint.setTextAlign(Paint.Align.CENTER);
                }
                canvas.drawText(i + "", width / 2 + i * tapeSpace, bottom - tapeAreaHeight / 4 + dx1, valuePaint);
            } else {
                scalePaint.setStrokeWidth(tapeWidth);
            }
            //+2留点空隙
            canvas.drawLine(width / 2 + i * tapeSpace + 2, top, width / 2 + i * tapeSpace + 2, top + tapeAreaHeight / (0 == i % 10 ? 2 : 4), scalePaint);
        }

        //当前刻度
        canvas.drawLine(getScrollX() + getWidth() / 2, top, getScrollX() + getWidth() / 2, top + tapeAreaHeight / 2, curScalePaint);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, top - curValuePB, width + tapeSpace * 100, top, paint);
    }

    private float lastX = 0;
    private int scrollx = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                int dx = (int) (moveX - lastX);
                int sx = scrollx - dx;
                //判断临界值
                if (sx < 0) {
                    sx = 0;
                } else if (sx > tapeSpace * 100) {
                    sx = (int) (tapeSpace * 100);
                }
                //滚动
                scrollTo(sx, 0);
                //重置取值
                setTapeValue(sx / tapeSpace);
                scrollx = sx;
                lastX = moveX;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }
}
