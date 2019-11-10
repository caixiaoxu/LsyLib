package com.lsy.viewlib.weight.audio;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lsy.viewlib.R;
import com.lsy.viewlib.utils.DensityUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MusicLayout extends FrameLayout implements MusicSlideRView.OnMusicSlideRListener, MusicSlideLView.OnMusicSlideLListener {

    //左右边距，拉伸图宽度
    private int pl = getContext().getResources().getDimensionPixelSize(R.dimen.time_line_ml);
    private int pr = getContext().getResources().getDimensionPixelSize(R.dimen.time_line_mr);

    private int radius = DensityUtil.dp2PxInt(getContext(), 3);

    public int textColor = Color.parseColor("#999999");
    public int textSize = DensityUtil.sp2px(getContext(), 12);

    private Paint bgPaint;
    private MusicSlideLView ivSL;
    private MusicSlideRView ivSR;

    private MusicTouchListener musicTouchListener;

    private List<View> audioSpectrums = new ArrayList<>();

    private TextPaint textPaint;

    public MusicLayout(@NonNull Context context) {
        super(context);

        initView();
    }

    public MusicLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    public MusicLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        //禁止多点触控
        setMotionEventSplittingEnabled(false);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.parseColor("#333333"));

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.LEFT);

        //左右
        ivSL = new MusicSlideLView(getContext());
        ivSL.setOnMusicSlideLListener(this);
        ivSR = new MusicSlideRView(getContext());
        ivSR.setOnMusicSlideRListener(this);
    }

    public void setWidth(int mWidth) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.width = mWidth + pl + pr;
        setLayoutParams(lp);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawRoundRect(pl, 0, getMeasuredWidth() - pr, getMeasuredHeight(), radius, radius, bgPaint);

        if (audioSpectrums.size() == 0) {
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
            float baseline = getHeight() * 1.0f / 2 + distance;
            canvas.drawText("点击添加音乐", pl + 20, baseline, textPaint);
        }
        super.dispatchDraw(canvas);
    }

    public void addChildView(int length, int ml, List<Float> list) {
        int width = getWidth();
        AudioSpectrum audioSpectrum = new AudioSpectrum(getContext());
        LayoutParams lp = new LayoutParams(length, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.leftMargin = pl + ml;
        audioSpectrum.setLayoutParams(lp);
        audioSpectrum.setParentWidth(width - pl - pr);
        audioSpectrum.setValues(list);
        audioSpectrum.setTag(ml);
        addView(audioSpectrum);
        audioSpectrums.add(audioSpectrum);

        hideStroke();

        Collections.sort(audioSpectrums, new Comparator<View>() {
            @Override
            public int compare(View view, View t1) {
                FrameLayout.LayoutParams lp1 = (LayoutParams) view.getLayoutParams();
                FrameLayout.LayoutParams lp2 = (LayoutParams) t1.getLayoutParams();
                return lp1.leftMargin - lp2.leftMargin;
            }
        });
    }

    //当前点击的View和索引
    private View currentView;
    private int currentIndex = -1;

    //是否可移动
    private boolean canTouch = false;

    private float lastX;
    //是否长按
    private boolean isClick = false;
    //是否长按
    private boolean isLong = false;
    //是否可以长按
    private boolean canLong = false;

    //滑动累加
    private int scrollLength = 0;

    //事件类型
    private int touchType = 0;

    private Handler handler = new Handler();
    private Runnable longPressRun = new Runnable() {
        @Override
        public void run() {
            if (canLong && canTouch) {
                vibrator();
                isLong = true;
                isClick = false;
                touchType = 3;
            }
        }
    };

    private Vibrator vibrator;

    /**
     * 振动
     */
    private void vibrator() {
        if (null == vibrator)
            vibrator = (Vibrator) getContext().getSystemService(getContext().VIBRATOR_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, 200));
            }
        } else {
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(100);
            }
        }
    }


    /**
     * 判断是否在child中
     *
     * @param view
     * @param raxX
     * @param raxY
     * @return
     */
    private boolean isConstains(View view, float raxX, float raxY) {
        int[] location = new int[2];
        // 获取控件在屏幕中的位置，返回的数组分别为控件左顶点的 x、y 的值
        view.getLocationOnScreen(location);
        RectF rectF = new RectF(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight());
        return rectF.contains(raxX, raxY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getRawX();
                float raxY = event.getRawY();

                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if (child instanceof AudioSpectrum && isConstains(child, lastX, raxY)) {
                        this.currentView = child;
                        this.currentIndex = audioSpectrums.indexOf(currentView);
                        break;
                    }
                }
                Log.e("currentView", "*******" + currentView);
                if (null != currentView) {
                    isLong = false;
                    canLong = true;
                    isClick = true;

                    touchType = 0;
                    handler.postDelayed(longPressRun, ViewConfiguration.getLongPressTimeout());

                    if (null != musicTouchListener) {
                        musicTouchListener.onTouchStart();
                    }
                } else {
                    isClick = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!canTouch || null == currentView || -1 == currentIndex) {
                    return false;
                }

                float moveX = event.getRawX();
                int dx = (int) (moveX - lastX);

                //防止误滑
                if (Math.abs(dx) > 10) {
                    handler.removeCallbacks(longPressRun);
                    canLong = false;
                    isClick = false;
                }
                if (3 == touchType) {
                    //通过设置边距移动
                    FrameLayout.LayoutParams lp = (LayoutParams) currentView.getLayoutParams();
                    if (lp == null) {
                        lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    }
                    //计算可移动距离
                    int moveLeft = (lp.leftMargin - pl) + dx;
                    if (moveLeft < 0) {
                        moveLeft = 0;
                    } else {
                        //判断可移动距离 TODO
//                        long inPoint = MusicTractHelper.getInstance().isOverlap(currentIndex, mapTimelinePosFromScrollerX(moveLeft));
//                        moveLeft = mapTimelinePosFromTs(inPoint);
                    }

                    lp.leftMargin = pl + moveLeft;
                    currentView.setLayoutParams(lp);
                    refreshSlideLR(currentView);
                    if (null != musicTouchListener) {
                        musicTouchListener.move(currentIndex, mapTimelinePosFromScrollerX(moveLeft));
                    }
                }
                lastX = moveX;
                break;
            case MotionEvent.ACTION_UP:
                handler.removeCallbacks(longPressRun);
                if (isClick) {
                    if (-1 != currentIndex && null != currentView) {
                        showSlideLR(currentIndex, currentView);
                    }
                    if (null != musicTouchListener) {
                        musicTouchListener.onTap(currentIndex);
                    }
                }
                touchType = 0;

                isClick = false;
                isLong = false;
                canLong = false;

                if (null != musicTouchListener) {
                    musicTouchListener.onTouchEnd();
                }

                break;
        }
        return true;
    }

    @Override
    public void onSlideL(int dx) {
        if (!canTouch || null == currentView || -1 == currentIndex) {
            return;
        }

        Log.e("onSlideL", "************dx:" + dx);

        //防止误滑
        if (Math.abs(dx) > 10) {
            handler.removeCallbacks(longPressRun);
            canLong = false;
            isClick = false;
        }

        //重置布局
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) currentView.getLayoutParams();
        if (lp == null) {
            lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        //新的宽度
        Log.e("onSlideL", "************getWidth:" + currentView.getWidth());
        int newWidth = currentView.getWidth() - dx;
        Log.e("onSlideL", "************newWidth:" + newWidth);
        int timeLength = getWidth() - pl - pr - (lp.leftMargin - pl);
        Log.e("timeLength", "************timeLength:" + timeLength);

        //计算当前显示的宽度对应的时长
        long showLength = mapTimelinePosFromScrollerX(newWidth);
        //吸附
//        showLength = MusicTractHelper.getInstance().getLeftToPointerShowLength(showLength);

        //判断显示时长不小于1秒
        if (showLength < 1000000) {
            showLength = 1000000;
        }
        Log.e("showLength", "************showLength:" + showLength);
        //判断是否重叠 TODO
//        showLength = MusicTractHelper.getInstance().isInPointOverlap(currentIndex, showLength);
//        Log.e("showLength", "************showLength:" + showLength);

        //重新计算dx;
        dx = currentView.getWidth() - mapTimelinePosFromTs(showLength);
        Log.e("dx", "************dx:" + dx);
        //滚动
        scrollLength += dx;
        currentView.scrollTo(scrollLength, 0);

        lp.width = mapTimelinePosFromTs(showLength);
        lp.leftMargin = lp.leftMargin + dx;
        currentView.setLayoutParams(lp);
        //重新加载右拉伸图片
        showSlideL(currentView);
        //回调
        if (null != musicTouchListener) {
            musicTouchListener.slideLeft(currentIndex, showLength);
        }
    }

    @Override
    public void onSlideR(int dx) {
        if (!canTouch || null == currentView || -1 == currentIndex) {
            return;
        }

        Log.e("onSlideR", "************dx:" + dx);

        //防止误滑
        if (Math.abs(dx) > 10) {
            handler.removeCallbacks(longPressRun);
            canLong = false;
            isClick = false;
        }

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) currentView.getLayoutParams();
        if (lp == null) {
            lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        //新的宽度
        int newWidth = currentView.getWidth() + dx;
        int timeLength = getWidth() - pl - pr - (lp.leftMargin - pl);
        //判断宽度不小于0，不大于父控件的宽度
        int minLength = mapTimelinePosFromTs(1000000);
        if (newWidth < minLength) {
            newWidth = minLength;
        } else if (newWidth > timeLength) {
            newWidth = timeLength;
        }
        //计算当前显示的宽度对应的时长
        long showLength = mapTimelinePosFromScrollerX(newWidth);
        //判断是否重叠 TODO
//        showLength = MusicTractHelper.getInstance().isOutPointOverlap(currentIndex, showLength);
        //重置布局

        lp.width = mapTimelinePosFromTs(showLength);
        currentView.setLayoutParams(lp);
        //重新加载右拉伸图片
        showSlideR(currentView);
        //回调
        if (null != musicTouchListener) {
            musicTouchListener.slideRight(currentIndex, showLength);
        }
    }

    /**
     * 显示左右拉伸图标
     *
     * @param currentIndex
     * @param currentView
     */
    private void showSlideLR(int currentIndex, View currentView) {
        //设置当前索引 TODO
//        MusicTractHelper.getInstance().setCurrentMusicIndex(currentIndex);
        //切换音频状态
        changeSpectrumState(true, currentIndex);

        //刷新位置
        refreshSlideLR(currentView);
        if (!canTouch) {
            this.canTouch = true;
            //重新添加
            addView(ivSL);
            addView(ivSR);
        }
    }

    /**
     * 显示左右拉伸图标
     *
     * @param currentView
     */
    private void refreshSlideLR(View currentView) {
        showSlideL(currentView);
        showSlideR(currentView);
    }

    public void hideStroke() {
        //切换音频状态
        changeSpectrumState(false, currentIndex);
        //取消当前索引 TODO
//        MusicTractHelper.getInstance().setCurrentMusicIndex(-1);
        //清除当前View和索引
        currentView = null;
        currentIndex = -1;
        //取消移动
        this.canTouch = false;
        //取消左右图标
        removeView(ivSL);
        removeView(ivSR);
    }

    /**
     * 切换音频图状态
     *
     * @param isFoucs
     * @param currentIndex
     */
    private void changeSpectrumState(boolean isFoucs, int currentIndex) {
        for (int i = 0; i < audioSpectrums.size(); i++) {
            View childView = audioSpectrums.get(i);
            if (childView instanceof AudioSpectrum) {
                ((AudioSpectrum) childView).setCheckState(isFoucs && i == currentIndex);
            }
        }
    }

    /**
     * 显示左拉伸图
     *
     * @param currentView
     */
    private void showSlideL(final View currentView) {
        currentView.post(new Runnable() {
            @Override
            public void run() {
                LayoutParams leftLp = (LayoutParams) ivSL.getLayoutParams();
                if (null == leftLp) {
                    leftLp = new LayoutParams(pl, ViewGroup.LayoutParams.MATCH_PARENT);
                }
                int left = currentView.getLeft() - pl;
                if (left != leftLp.leftMargin) {
                    leftLp.leftMargin = currentView.getLeft() - pl;
                    ivSL.setLayoutParams(leftLp);
                }
            }
        });
    }

    /**
     * 显示右拉伸图
     *
     * @param currentView
     */
    private void showSlideR(final View currentView) {
        currentView.post(new Runnable() {
            @Override
            public void run() {
                LayoutParams rightLp = (LayoutParams) ivSR.getLayoutParams();
                if (null == rightLp) {
                    rightLp = new LayoutParams(pr, ViewGroup.LayoutParams.MATCH_PARENT);
                }
                int left = currentView.getRight();
                if (left != rightLp.leftMargin) {
                    rightLp.leftMargin = left;
                    ivSR.setLayoutParams(rightLp);
                }
            }
        });
    }

    private double getPixelPerMicrosecond() {
        long durationPerScreen = 1000000 * 14;
        int width = DensityUtil.getScreenWidth(getContext());
        double pixelMicrosecond = width / (double) durationPerScreen;
        return pixelMicrosecond;
    }

    public long mapTimelinePosFromScrollerX(int scrollX) {
        return (long) (scrollX / getPixelPerMicrosecond());
    }

    public int mapTimelinePosFromTs(long t) {
        return (int) (t * getPixelPerMicrosecond());
    }

    public void setMusicTouchListener(MusicTouchListener musicTouchListener) {
        this.musicTouchListener = musicTouchListener;
    }

    public void removeChildView(int index) {
        View childView = audioSpectrums.get(index);
        removeView(childView);
        audioSpectrums.remove(index);
        hideStroke();
    }

    public void removeAllChildView() {
        audioSpectrums.clear();
        removeAllViews();
    }

    public interface MusicTouchListener {
        void onTouchStart();

        void slideLeft(int index, long showLength);

        void move(int index, long inPoint);

        void slideRight(int index, long showLength);

        void onTap(int currentIndex);

        void onTouchEnd();
    }
}
