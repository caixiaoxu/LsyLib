package com.lsy.viewlib.weight.audio;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.lsy.viewlib.R;
import com.lsy.viewlib.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class AudioSpectrum extends View {

    private Paint paint, bgPaint;

    private int lineWidth = getContext().getResources().getDimensionPixelSize(R.dimen.music_space);
    private int lineColor = Color.parseColor("#999999");
    private int lineCheckColoe = Color.parseColor("#FF0000");

    private int radius = DensityUtil.dp2PxInt(getContext(), 3);

    private boolean isCheckState = false;

    private List<Float> values = new ArrayList<>();

    private int parentWidth;

    public AudioSpectrum(Context context) {
        super(context);

        initView();
    }

    public AudioSpectrum(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    public AudioSpectrum(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(lineWidth);
        paint.setStrokeJoin(Paint.Join.ROUND);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.parseColor("#666666"));
    }

    /**
     * 返回振幅数
     *
     * @return
     */
    public int getValueCount() {
        if (null != values)
            return values.size();

        return 0;
    }

    /**
     * 设置振幅值
     *
     * @param values
     */
    public void setValues(List<Float> values) {
        this.values = values;
        requestLayout();
    }

    public void setCheckState(boolean checkState) {
        isCheckState = checkState;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(getScrollX(), 0, getScrollX() + getWidth(), getHeight(), radius, radius, bgPaint);
        if (isCheckState) {
            paint.setColor(lineCheckColoe);
        } else {
            paint.setColor(lineColor);
        }

        //记录起点位置
        float bsHeight = getHeight() * 1f / 2 - 2;
        PathPoint lastPath = new PathPoint(0, bsHeight);

        //循环，分段绘制
        List<PathPoint> src = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            //保存
            float pointX = i * lineWidth;
            float pointY = bsHeight - bsHeight * values.get(i);
            PathPoint pathPoint = new PathPoint(pointX, pointY);
            src.add(pathPoint);
            //每100条绘制一次，或者到最后了绘制
            if (i != 0 && (i % 100 == 0) || (i == values.size() - 1)) {
                //移动到上一次位置
                Path path = new Path();
                path.moveTo(lastPath.pointX, lastPath.pointY);
                //绘制当前Path
                for (int j = 0; j < src.size(); j++) {
                    PathPoint pp = src.get(j);
                    path.lineTo(pp.pointX, pp.pointY);
                }
                //反向
                for (int j = (src.size() - 1); j >= 0; j--) {
                    PathPoint pp = src.get(j);
                    path.lineTo(pp.pointX, getHeight() - pp.pointY);
                }
                //闭合
                path.lineTo(lastPath.pointX, getHeight() - lastPath.pointY);
                path.close();
                //绘制
                canvas.drawPath(path, paint);
                //记录结束的位置
                lastPath = pathPoint;
                //清空
                src.clear();
            }
        }
    }

    public void setUnitLength(int unitLength) {
        this.lineWidth = unitLength;
    }

    public void setParentWidth(int width) {
        this.parentWidth = width;
    }

    public static class PathPoint {
        public float pointX;
        public float pointY;

        public PathPoint(float pointX, float pointY) {
            this.pointX = pointX;
            this.pointY = pointY;
        }

        @Override
        public String toString() {
            return "******pointX:" + pointX + "******pointY:" + pointY;
        }
    }
}
