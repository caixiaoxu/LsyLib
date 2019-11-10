package com.lsy.viewlib.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.lsy.viewlib.R;

/**
 * 滑动选择开始点
 */
public class StartPointView extends View {

    private Scroller mScroller;
    private Paint paint;

    //线高
    private float lineHeight;
    //线长
    private int maxLength;
    //线的颜色
    private int lineColor;
    //线的底色
    private int lineBgColor;

    private StartPointCallBack callBack;

    /**
     * 根据时长，设置长度
     *
     * @param time 单位：秒second
     */
    public void setLength(long time) {
        maxLength = (int) (time * 10);
        invalidate();
    }

    public void setLineHeight(float lineHeight) {
        this.lineHeight = lineHeight;
        invalidate();
    }

    public StartPointView(Context context) {
        super(context);
        init();
    }

    public StartPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StartPointView);
        lineHeight = typedArray.getDimensionPixelSize(R.styleable.StartPointView_lineHeight, 3);
        lineColor = typedArray.getColor(R.styleable.StartPointView_lineColor, Color.parseColor("#FF0000"));
        lineBgColor = typedArray.getColor(R.styleable.StartPointView_lineBgColor, Color.parseColor("#000000"));
        int time = typedArray.getInt(R.styleable.StartPointView_time, 0);
        setLength(time);
        typedArray.recycle();
        init();
    }

    public StartPointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StartPointView);
        lineHeight = typedArray.getDimensionPixelSize(R.styleable.StartPointView_lineHeight, 3);
        lineColor = typedArray.getColor(R.styleable.StartPointView_lineColor, Color.parseColor("#FF0000"));
        lineBgColor = typedArray.getColor(R.styleable.StartPointView_lineBgColor, Color.parseColor("#000000"));
        int time = typedArray.getInt(R.styleable.StartPointView_time, 0);
        setLength(time);
        typedArray.recycle();
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        mScroller = new Scroller(getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        paint.setStrokeWidth(lineHeight);
        //画底线，长度为线长+控件的宽
        paint.setColor(lineBgColor);
        canvas.drawLine(0, height / 2, maxLength + width, height / 2, paint);
        //画线
        paint.setColor(lineColor);
        canvas.drawLine(0, height / 2, maxLength, height / 2, paint);
    }

    private float lastMoveX;
    private float lastMoveY;
    //开始点
    private int start;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastMoveX = (int) event.getX();
                lastMoveY = (int) event.getY();
                Log.e("按下坐标:", lastMoveX + "," + lastMoveY);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                Log.e("移动坐标:", moveX + "," + moveY);
                int dx = (int) (lastMoveX - moveX);
                int dy = (int) (lastMoveY - moveY);
                Log.e("dx,dy:", dx + "," + dy);
                //横向偏移
                if (Math.abs(dx) > Math.abs(dy)) {
                    //判断偏移后，起始点不小于0，且不超过最大长度
                    int temp = start + dx;
                    if (temp < 0) {
                        dx = -start;
                    } else if (temp > maxLength) {
                        dx = maxLength - start;
                    }

                    Log.e("偏移值坐标:", dx + "");
                    Log.e("start坐标:", start + "");
                    //移动
                    beginScroll(start, dx);
                    //刷新开始点
                    start += dx;

                    if (null != callBack) {
                        callBack.startPoint(start*1f / maxLength);
                    }

                    //记录最后移动坐标
                    lastMoveX = moveX;
                    lastMoveY = moveY;
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * 移动
     *
     * @param start
     * @param dx
     */
    public void beginScroll(int start, int dx) {
        mScroller.startScroll(start, 0, dx, 0, 2000);
        invalidate();
    }

    public void setCallBack(StartPointCallBack callBack) {
        this.callBack = callBack;
    }

    public interface StartPointCallBack {
        void startPoint(float percent);
    }
}
