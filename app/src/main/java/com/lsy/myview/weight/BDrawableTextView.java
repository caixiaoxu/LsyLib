package com.lsy.myview.weight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lsy.myview.R;
import com.lsy.viewlib.utils.DensityUtil;

/**
 * 
 */
public class BDrawableTextView extends View {

    public static final float DEFAULT_TEXTSIZE  = 40;
    public static final int   DEFAULT_TEXTCOLOR = Color.BLUE;

    private Paint             paint;
    private TextPaint         textPaint;

    private String            text              = "你好，世界";

    private float             LTXRatio          = 0.191f;
    private float             LTYRatio          = 0.3f;
    private float             RBXRatio          = 0.832f;
    private float             RBYRatio          = 0.709f;

    //    private float             LTXRatio          = 0.368f;
    //    private float             LTYRatio          = 0.39f;
    //    private float             RBXRatio          = 0.631f;
    //    private float             RBYRatio          = 0.584f;

    public BDrawableTextView(Context context) {
        super(context);
        init();
    }

    public BDrawableTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BDrawableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        initPaint();

    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(DEFAULT_TEXTSIZE);
        textPaint.setColor(DEFAULT_TEXTCOLOR);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //如果宽高为wrap_content，设置默认大小200
        int widthSpecModel = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (widthSpecModel) {
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                widthSpecSize = DensityUtil.dp2PxInt(getContext(), 200f);
                break;
            case MeasureSpec.EXACTLY:
                break;
        }

        int heightSpecModel = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (heightSpecModel) {
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                heightSpecSize = DensityUtil.dp2PxInt(getContext(), 200);
                break;
            case MeasureSpec.EXACTLY:
                break;
        }

        setMeasuredDimension(widthSpecSize, heightSpecSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLUE);

        int width = getWidth();
        int height = getHeight();

        Bitmap bgBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.cs);
        int bitmapWidth = bgBitmap.getWidth();
        int bitmapHeight = bgBitmap.getHeight();

        float scaleX = bitmapWidth * 1.0f / width;
        float scaleY = bitmapHeight * 1.0f / height;

        float scale = Math.max(scaleX, scaleY);

        int showWidth = (int) Math.min(bitmapWidth / scale, width);
        int showHeight = (int) Math.min(bitmapHeight / scale, height);

        int LTX = (int) (bitmapWidth * LTXRatio / scale);
        int LTY = (int) (bitmapHeight * LTYRatio / scale);

        int RBX = (int) (bitmapWidth * RBXRatio / scale);
        int RBY = (int) (bitmapHeight * RBYRatio / scale);

        int ml = (width - showWidth) / 2;
        int mt = (height - showHeight) / 2;

        canvas.save();
        canvas.translate(ml,mt);

        Rect rect = new Rect(0, 0, showWidth, showHeight);
        canvas.drawBitmap(bgBitmap, null, rect, paint);

        paint.setColor(Color.RED);
        canvas.drawRect(LTX , LTY, RBX, RBY, paint);
        canvas.restore();



        //        int contentScaleWidht = RBX - LTX;
        //
        //        StaticLayout staticLayout = new StaticLayout(text, textPaint, contentScaleWidht,
        //                Layout.Alignment.ALIGN_NORMAL, 1, 0, true);
        //
        //        canvas.save();
        //        canvas.translate(LTX, LTY + DEFAULT_TEXTSIZE);
        //        //        canvas.translate(56, 66);
        //        staticLayout.draw(canvas);
        //        canvas.restore();

    }
}
