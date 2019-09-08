package com.lsy.viewlib.weight.likeview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.lsy.viewlib.R;

public class LikeImageView extends View {

    private Paint       imagePaint;

    private final float SHININGMOVEXRATIO = 1.0f / 30;
    private final float SHININGMOVEYRATIO = 1.0f / 4;

    private int         shiningMoveX;
    private int         shiningMoveY;

    private int         measureWidth;
    private int         measureHeight;
    private Bitmap      selectedBtimap;
    private Bitmap      selectedShiningBtimap;
    private Bitmap      unSelectedBtimap;

    private boolean     isAdd             = true;

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
                shiningMoveX = (int) (widthSize * SHININGMOVEXRATIO);
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
                shiningMoveY = (int) (heightSize * SHININGMOVEYRATIO);
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
        Rect selectDst = new Rect(0, shiningMoveY, selectedBtimap.getWidth(),
                shiningMoveY + selectedBtimap.getHeight());

        if (isAdd) {
            //画红赞
            canvas.drawBitmap(selectedBtimap, src, selectDst, imagePaint);
            //画阴影
            Rect shiningDst = new Rect(width - selectedShiningBtimap.getWidth() - shiningMoveX, 0,
                    width - shiningMoveX, selectedShiningBtimap.getHeight());
            canvas.drawBitmap(selectedShiningBtimap, src, shiningDst, imagePaint);
        } else {
            //画灰赞
            canvas.drawBitmap(unSelectedBtimap, src, selectDst, imagePaint);
        }
    }
}
