package com.lsy.viewlib.weight;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.lsy.viewlib.utils.DensityUtil;

public class SubwayBoardView extends View {

    private Paint bgPaint, tbPaint, centerBgPaint, centerRingPaint, centerCirclePaint, centerCircleRingPaint;

    private TextPaint centerTextPaint;

    private float barHeight = DensityUtil.dp2Px(getContext(), 20);

    private float centerCircleWidth;
    private float centerRingWidth;
    private float centerCircleRingStrokeWidth = DensityUtil.dp2Px(getContext(), 5);
    private float centerRingStrokeWidth = DensityUtil.dp2Px(getContext(), 36);

    private float centerCircleRingSweepAngle = 0f;
    private ObjectAnimator centerCircleRingAnim;

    public SubwayBoardView(Context context) {
        super(context);
        initView();
    }

    public SubwayBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SubwayBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        //全背景
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.parseColor("#85919a"));

        //上下边栏
        tbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tbPaint.setStyle(Paint.Style.FILL);
        tbPaint.setColor(Color.parseColor("#c21b2c"));

        //中间栏
        centerBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerBgPaint.setStyle(Paint.Style.FILL);
        centerBgPaint.setColor(Color.parseColor("#92a3d1"));

        //中间空白圆环区域
        centerRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerRingPaint.setStyle(Paint.Style.STROKE);
        centerRingPaint.setStrokeWidth(centerRingStrokeWidth);
        centerRingPaint.setColor(Color.parseColor("#85919a"));

        //中间圆
        centerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerCirclePaint.setStyle(Paint.Style.FILL);
        centerCirclePaint.setColor(Color.parseColor("#c21b2c"));

        //中间圆边上的圆环
        centerCircleRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerCircleRingPaint.setStyle(Paint.Style.STROKE);
        centerCircleRingPaint.setStrokeWidth(centerCircleRingStrokeWidth);
        centerCircleRingPaint.setStrokeCap(Paint.Cap.ROUND);
        centerCircleRingPaint.setColor(Color.parseColor("#6e8ca6"));

        //中间文字
        centerTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        centerTextPaint.setStyle(Paint.Style.FILL);
        centerTextPaint.setFakeBoldText(true);
        centerTextPaint.setColor(Color.parseColor("#333333"));
        centerTextPaint.setTextAlign(Paint.Align.CENTER);
        centerTextPaint.setShadowLayer(3, 3, 3, Color.parseColor("#6e8ca6"));
        centerTextPaint.setTextSize(DensityUtil.sp2px(getContext(), 24));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        int centerX = width / 2;
        int centerY = height / 2;

        //计算中间空白圆形宽度
        if (0 == centerRingWidth) {
            centerRingWidth = (height - DensityUtil.dp2Px(getContext(), 12)) * 1f / 2;
        }
        //计算中间圆的半径
        if (0 == centerCircleWidth) {
            centerCircleWidth = centerRingWidth - DensityUtil.dp2Px(getContext(), 8);
        }

        //背景
        canvas.drawRect(0, 0, width, height, bgPaint);

        //上下栏
        canvas.drawRect(0, 0, width, barHeight, tbPaint);
        canvas.drawRect(0, height - barHeight, width, height, tbPaint);

        //中间圆环空白区域
        canvas.drawCircle(centerX, centerY, centerRingWidth, centerRingPaint);

        //中间栏
        float centerLineT = barHeight + DensityUtil.dp2Px(getContext(), 10);
        float centerLineB = height - barHeight - DensityUtil.dp2Px(getContext(), 10);
        canvas.drawRect(0, centerLineT, width, centerLineB, centerBgPaint);

        //中间圆
        canvas.drawCircle(centerX, centerY, centerCircleWidth, centerCirclePaint);

        //中间圆环
        if (centerCircleRingSweepAngle > 0) {
            canvas.drawArc(centerX - centerCircleWidth - (centerCircleRingStrokeWidth / 2), centerY - centerCircleWidth - (centerCircleRingStrokeWidth / 2), centerX + centerCircleWidth + (centerCircleRingStrokeWidth / 2), centerY + centerCircleWidth + (centerCircleRingStrokeWidth / 2), -90f, centerCircleRingSweepAngle, false, centerCircleRingPaint);
        }

        //中间文字
        Paint.FontMetrics fontMetrics = centerTextPaint.getFontMetrics();
        float dx = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        canvas.drawText("杭州站", centerX, centerY + dx, centerTextPaint);
    }

    public void setCenterCircleRingSweepAngle(float centerCircleRingSweepAngle) {
        this.centerCircleRingSweepAngle = centerCircleRingSweepAngle;
        invalidate();
    }

    /**
     * 开始中间圆动画
     */
    public void animCenterCircleRing() {
        if (null == centerCircleRingAnim) {
            centerCircleRingAnim = ObjectAnimator.ofFloat(this, "centerCircleRingSweepAngle", 0f, 360f);
            centerCircleRingAnim.setDuration(3000);
            centerCircleRingAnim.setInterpolator(new LinearInterpolator());
            centerCircleRingAnim.setRepeatCount(ValueAnimator.INFINITE);
            centerCircleRingAnim.setRepeatMode(ValueAnimator.RESTART);
        }
        centerCircleRingAnim.start();
    }

    /**
     * 停止
     */
    public void stopAnimCenterCircleRing() {
        if (null != centerCircleRingAnim) {
            centerCircleRingAnim.cancel();
        }
        setCenterCircleRingSweepAngle(0);
    }
}
