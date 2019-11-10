package com.lsy.viewlib.weight.audio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.lsy.viewlib.R;

public class MusicSlideLView extends View {
    private Paint paint;

    private int pl = getContext().getResources().getDimensionPixelSize(R.dimen.time_line_ml);

    private OnMusicSlideLListener onMusicSlideLListener;

    public MusicSlideLView(Context context) {
        super(context);
        initView();
    }

    public MusicSlideLView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MusicSlideLView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(pl, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(lp);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Bitmap slideL = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_slide_l);
        float ratio = slideL.getWidth() * 1f / slideL.getHeight();
        int dstw = (int) (ratio * getHeight());
        Rect src = new Rect(0, 0, slideL.getWidth(), slideL.getHeight());
        Rect dst;
        if (pl < dstw) {
            dst = new Rect(0, 0, pl, getHeight());
        } else {
            dst = new Rect(pl - dstw, 0, pl, getHeight());
        }
        canvas.drawBitmap(slideL, src, dst, paint);
    }

    private float lastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getRawX();
                // 让父类不要拦截该view的事件
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getRawX();
                int dx = (int) (moveX - lastX);

                //防止误滑
                if (Math.abs(dx) > 5) {
                    if (null != onMusicSlideLListener) {
                        onMusicSlideLListener.onSlideL(dx);
                    }
                    lastX = moveX;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    public void setOnMusicSlideLListener(OnMusicSlideLListener onMusicSlideLListener) {
        this.onMusicSlideLListener = onMusicSlideLListener;
    }

    public interface OnMusicSlideLListener {
        void onSlideL(int dx);
    }
}
