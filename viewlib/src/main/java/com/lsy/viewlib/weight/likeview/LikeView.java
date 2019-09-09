package com.lsy.viewlib.weight.likeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.lsy.viewlib.R;
import com.lsy.viewlib.utils.DensityUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class LikeView extends LinearLayout {
    private final int IMAGEPADDING = 4;

    private boolean isAdd = false;

    private int num;
    private int textSize;
    private int textColor;

    private int imagePadding;

    private List<LikeCharTextView> charTvs = new ArrayList<>();
    private LikeImageView likeImageView;

    public LikeView(Context context) {
        super(context);
        init();
    }

    public LikeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initAttr(context, attrs);

        init();
    }

    public LikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttr(context, attrs);

        init();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LikeView);
        textColor = typedArray.getColor(R.styleable.LikeView_textColor,
                LikeCharTextView.DEFAULT_TEXTCOLOR);
        textSize = typedArray.getDimensionPixelSize(R.styleable.LikeView_textSize,
                LikeCharTextView.DEFAULT_TEXTSIZE);
        num = typedArray.getInt(R.styleable.LikeView_number, 0);
        imagePadding = typedArray.getDimensionPixelSize(R.styleable.LikeView_imagePadding, IMAGEPADDING);

        typedArray.recycle();
    }

    /**
     * 初始化
     */
    private void init() {

        initView();
    }

    protected void initView() {
        removeAllViews();

        likeImageView = new LikeImageView(getContext());
        likeImageView.setAdd(isAdd);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.rightMargin = imagePadding;
        likeImageView.setLayoutParams(layoutParams);
        addView(likeImageView);

        charTvs.clear();
        String str_num = String.valueOf(num);
        for (int i = 0; i < str_num.length(); i++) {
            LikeCharTextView textView = new LikeCharTextView(getContext());
            int show_num = Integer.valueOf(str_num.substring(i, i + 1));
            Log.e("zanview", "show_num:" + show_num);
            textView.setTextSize(textSize);
            textView.setTextColor(textColor);
            textView.setNum(show_num);
            addView(textView);
            charTvs.add(textView);
        }
    }

    public void setNum(int num) {
        this.num = num;
        init();
        invalidate();
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //计算出所有的childView的宽高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    /**
     * 测量宽度
     *
     * @param widthMeasureSpec
     * @return
     */
    private int measureWidth(int widthMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                widthSize = 0;
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    //获取子view的宽
                    int cWidth = childView.getMeasuredWidth();
                    MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
                    widthSize += cWidth + params.leftMargin + params.rightMargin;
                }
                break;
            case MeasureSpec.EXACTLY:
                break;
        }
        return widthSize;
    }

    /**
     * 测量高度
     *
     * @param widthMeasureSpec
     * @return
     */
    private int measureHeight(int widthMeasureSpec) {
        int heightMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                heightSize = 0;
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    //获取子view的宽
                    int cWidth = childView.getMeasuredHeight();
                    MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
                    int height = cWidth + params.leftMargin + params.rightMargin;
                    heightSize = Math.max(heightSize, height);
                }
                break;
            case MeasureSpec.EXACTLY:
                break;
        }
        return heightSize;
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
        Logger.e("点击事件" + isAdd);
        String str_num = String.valueOf(num);
        Logger.e("点击事件,str_num:" + str_num);
        boolean nextAnim = false;
        if (isAdd) {
            likeImageView.changeLike(true);
            for (int i = (str_num.length() - 1); i >= 0; i--) {
                int chr_num = Integer.valueOf(str_num.substring(i, i + 1));
                Logger.e("点击事件,chr_num:%d,charTvs.size:%d,i:%d", chr_num, charTvs.size(), i);
                Logger.e("是否执行动画:" + (charTvs.size() > i));
                if (charTvs.size() > i) {
                    if (i == (str_num.length() - 1) || nextAnim) {
                        Logger.e("点击事件,执行个位动画||%b执行执行上%d位动画", nextAnim, i);
                        charTvs.get(i).change(true);

                        chr_num--;
                        Logger.e("chr_num:%d，是否执行上一位动画:", chr_num, (chr_num < 0));
                        if (chr_num < 0) {
                            nextAnim = true;
                        } else {
                            nextAnim = false;
                        }

                        Logger.e("nextAnim:" + nextAnim);
                    }
                }
            }
            num--;
            isAdd = !isAdd;
        } else {
            likeImageView.changeLike(false);
            for (int i = (str_num.length() - 1); i >= 0; i--) {
                int chr_num = Integer.valueOf(str_num.substring(i, i + 1));
                Logger.e("点击事件,chr_num:%d,charTvs.size:%d,i:%d", chr_num, charTvs.size(), i);
                Logger.e("是否执行动画:" + (charTvs.size() > i));
                if (charTvs.size() > i) {
                    if (i == (str_num.length() - 1) || nextAnim) {
                        Logger.e("点击事件,执行个位动画||%b执行执行上%d位动画", nextAnim, i);
                        charTvs.get(i).change(false);

                        chr_num++;
                        Logger.e("chr_num:%d，是否执行上一位动画:", chr_num, (chr_num > 9));
                        if (chr_num > 9) {
                            nextAnim = true;
                        } else {
                            nextAnim = false;
                        }
                        Logger.e("nextAnim:" + nextAnim);
                    }
                }
            }
            num++;
            isAdd = !isAdd;
        }
    }
}
