package com.lsy.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.lsy.viewlib.weight.seekselect.VideoCoverSeekLayout;

import java.util.List;

public class SeekSeletAdapter extends VideoCoverSeekLayout.BaseCoverAdapter<Bitmap> {

    /**
     * @param context
     * @param layoutResId
     * @param data
     * @param measuredWidth 父控件的宽度
     */
    public SeekSeletAdapter(Context context, int layoutResId, @Nullable List<Bitmap> data, int measuredWidth) {
        super(context, layoutResId, data, measuredWidth);
    }

    @Override
    public void cover(VideoCoverSeekLayout.BaseCoverViewHolder holder, int position, Bitmap item) {
        ImageView imageView = holder.itemView.findViewById(R.id.iv_item_cover_thumb);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.width = item.getWidth();
        layoutParams.height = item.getHeight();
        imageView.setLayoutParams(layoutParams);
        imageView.setImageBitmap(item);
    }
}

