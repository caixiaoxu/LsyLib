package com.lsy.viewlib.weight.seekselect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * 视频封面，封面滑动选择控件
 */
public class VideoCoverSeekLayout<T> extends FrameLayout {

    private Paint paint;

    //线的颜色
    private int lineColor = Color.parseColor("#F50D87");
    //线宽
    private int lineWidth = 8;

    private RecyclerView recyclerView;
    private SeekCallBack callBack;

    //重置状态
    private boolean isRestore = false;
    private LinearLayoutManager linearLayoutManager;

    public VideoCoverSeekLayout(Context context) {
        super(context);
        init();
    }

    public VideoCoverSeekLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoCoverSeekLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initPaint();
        initView();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(lineColor);
        paint.setStrokeWidth(lineWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        recyclerView = new RecyclerView(getContext());
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(lp);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        this.recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //滚动停止时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //如果是重置，不做处理
                    if (isRestore) {
                        isRestore = false;
                        return;
                    }
                    //滚动的距离部长度
                    int scrollOffsetX = recyclerView.computeHorizontalScrollOffset();
                    //总长度-当前显示的长度，因为设置过左右边距，加起来刚好是显示的长度
                    int scrollRangeX = recyclerView.computeHorizontalScrollRange() - recyclerView.computeHorizontalScrollExtent();
                    //计算滚动到总长度的哪个位置
                    float scrollPosition = scrollOffsetX * 1f / scrollRangeX;
                    //回调
                    if (null != callBack) {
                        callBack.seek(scrollPosition);
                    }
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (null != callBack) {
                        callBack.slide();
                    }
                }
            }
        });
        addView(recyclerView);
    }

    /**
     * 设置适配器
     *
     * @param adapter
     * @param callBack
     */
    public void setAdapter(BaseCoverAdapter<T> adapter, SeekCallBack callBack) {
        this.callBack = callBack;
        recyclerView.setAdapter(adapter);
    }

    /**
     * 返回顶部
     */
    public void backStart() {
        if (null != recyclerView) {
            isRestore = true;
            recyclerView.smoothScrollToPosition(0);
        }
    }

    /**
     * 返回顶部
     */
    public void goTail() {
        if (null != recyclerView) {
            isRestore = true;
            linearLayoutManager.scrollToPositionWithOffset(recyclerView.getAdapter().getItemCount() - 1, Integer.MIN_VALUE);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        //画线
        int width = getWidth();
        int height = getHeight();
        canvas.drawLine(width / 2, 0, width / 2, height, paint);
    }

    public static abstract class SeekCallBack {
        public abstract void seek(float progress);

        public abstract void slide();
    }

    public static abstract class BaseCoverAdapter<T> extends RecyclerView.Adapter<BaseCoverViewHolder> {
        private int layoutId;
        private List<T> datas;
        private Context context;
        private int parentWidth;

        public BaseCoverAdapter(Context context, int layoutId, List<T> datas, int parentWidth) {
            this.context = context;
            this.layoutId = layoutId;
            this.datas = datas;
            this.parentWidth = parentWidth;
        }

        public void setNewData(List<T> datas) {
            this.datas = datas;
            notifyDataSetChanged();
        }

        @Override
        public BaseCoverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(layoutId, null);
            return new BaseCoverViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BaseCoverViewHolder holder, int position) {
            if (null != datas) {
                //第一个和最后一个，设置一半的边距
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                if (null == lp)
                    lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                if (0 == position) {
                    lp.leftMargin = parentWidth / 2;
                    lp.rightMargin = 0;
                } else if (position == datas.size() - 1) {
                    lp.leftMargin = 0;
                    lp.rightMargin = parentWidth / 2;
                } else {
                    lp.leftMargin = 0;
                    lp.rightMargin = 0;
                }
                holder.itemView.setLayoutParams(lp);

                //界面设置
                cover(holder, position, datas.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return null == datas ? 0 : datas.size();
        }

        public abstract void cover(BaseCoverViewHolder holder, int position, T item);
    }

    public static class BaseCoverViewHolder extends RecyclerView.ViewHolder {
        public BaseCoverViewHolder(View itemView) {
            super(itemView);
        }

        public void setBitmapById(int imageViewId, Bitmap bitmap) {
            ImageView imageView = itemView.findViewById(imageViewId);
            imageView.setImageBitmap(bitmap);
        }

    }

}
