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

public class MusicSlideRView extends View {
    private Paint paint;

    private int pr = getContext().getResources().getDimensionPixelSize(R.dimen.time_line_mr);

    private OnMusicSlideRListener onMusicSlideRListener;

    public MusicSlideRView(Context context) {
        super(context);
        initView();
    }

    public MusicSlideRView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MusicSlideRView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(pr, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(lp);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Bitmap slideR = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_slide_r);
        float ratio = slideR.getWidth() * 1f / slideR.getHeight();
        int dstw = (int) (ratio * getHeight());
        Rect src = new Rect(0, 0, slideR.getWidth(), slideR.getHeight());
        Rect dst;
        if (pr < dstw) {
            dst = new Rect(getWidth() - pr, 0, getWidth(), getHeight());
        } else {
            dst = new Rect(0, 0, dstw, getHeight());
        }
        canvas.drawBitmap(slideR, src, dst, paint);
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
                    if (null != onMusicSlideRListener) {
                        onMusicSlideRListener.onSlideR(dx);
                    }
                    lastX = moveX;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    public void setOnMusicSlideRListener(OnMusicSlideRListener onMusicSlideRListener) {
        this.onMusicSlideRListener = onMusicSlideRListener;
    }

    public interface OnMusicSlideRListener {
        void onSlideR(int dx);
    }
}
