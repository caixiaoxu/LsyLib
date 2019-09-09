package com.lsy.viewlib.weight.likeview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.lsy.viewlib.R;

public class LikeImageView extends View {

    private Paint imagePaint, shiningPaint;

    private int shiningMoveX;
    private int shiningMoveY;

    private int measureWidth;
    private int measureHeight;
    private Bitmap selectedBtimap;
    private Bitmap selectedShiningBtimap;
    private Bitmap unSelectedBtimap;

    private boolean isAdd = false;

    private float shiningAlpha = isAdd ? 1f : 0f;

    public LikeImageView(Context context) {
        super(context);

        init();
    }

    public LikeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public LikeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        imagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shiningPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        selectedBtimap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_like_selected);
        selectedShiningBtimap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_like_selected_shining);
        unSelectedBtimap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_like_unselected);
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
                widthSize = Math.max(selectedBtimap.getWidth(), unSelectedBtimap.getWidth());
                shiningMoveX = (int) (widthSize * 1.0f - selectedShiningBtimap.getWidth()) - 2;
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
                heightSize = Math.max(selectedBtimap.getHeight(), unSelectedBtimap.getHeight());
                shiningMoveY = (int) (selectedShiningBtimap.getHeight() * 1.0f / 3);
                heightSize += shiningMoveY;
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
        Rect src = new Rect(0, 0, width, height);
        Rect selectDst = new Rect(0, shiningMoveY, selectedBtimap.getWidth(), height);
        if (isAdd) {
            //画红赞
            canvas.drawBitmap(selectedBtimap, src, selectDst, imagePaint);
            //画阴影
            shiningPaint.setAlpha((int) (255 * shiningAlpha));
            Rect shiningDst = new Rect(shiningMoveX, 0,
                    shiningMoveX + selectedShiningBtimap.getWidth(), selectedShiningBtimap.getHeight());
            canvas.drawBitmap(selectedShiningBtimap, src, shiningDst, shiningPaint);
        } else {
            //画灰赞
            canvas.drawBitmap(unSelectedBtimap, src, selectDst, imagePaint);
        }
    }

    public void setShiningAlpha(float shiningAlpha) {
        this.shiningAlpha = shiningAlpha;
        invalidate();
    }

    public void setAdd(boolean add) {
        isAdd = !add;
        shiningAlpha = 1.0f;
        invalidate();
    }

    public void changeLike(boolean isAdd) {
        this.isAdd = !isAdd;
        invalidate();
        anim();
    }

    private void anim() {
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(this, "scaleX", 0.7f, 1f);
        scaleXAnim.setDuration(500);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(this, "scaleY", 0.7f, 1f);
        scaleYAnim.setDuration(500);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(this, "shiningAlpha", 0f, 1f);
        alphaAnim.setDuration(500);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnim, scaleYAnim, alphaAnim);
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.start();
    }
}
