package com.lsy.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lsy.viewlib.weight.seekselect.VideoCoverSeekLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SeekSelectActivity extends AppCompatActivity {

    @BindView(R.id.sl_select)
    VideoCoverSeekLayout slSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek_select);
        ButterKnife.bind(this);

        List<Bitmap> thumbs = new ArrayList<>();
        thumbs.add(getBitmap(R.mipmap.icon_thumb));
        thumbs.add(getBitmap(R.mipmap.icon_thumb));
        thumbs.add(getBitmap(R.mipmap.icon_thumb));
        thumbs.add(getBitmap(R.mipmap.icon_thumb));
        thumbs.add(getBitmap(R.mipmap.icon_thumb));
        slSelect.post(new Runnable() {
            @Override
            public void run() {
                SeekSeletAdapter adapter = new SeekSeletAdapter(SeekSelectActivity.this, R.layout.item_seek_select, thumbs, slSelect.getMeasuredWidth());
                slSelect.setAdapter(adapter, new VideoCoverSeekLayout.SeekCallBack() {
                    @Override
                    public void seek(float progress) {
                    }

                    @Override
                    public void slide() {
                    }
                });
            }
        });
    }

    /**
     * 此代码在4.4上运行正常，但在5.0以上的系统会出现空指针，原因在于此本来方法不能将vector转化为bitmap，而apk编译时为了向下兼容，会根据vector生产相应的png，而4.4的系统运行此代码时其实用的是png资源。
     * @param vectorDrawableId
     * @return
     */
    private Bitmap getBitmap(int vectorDrawableId) {
        Bitmap bitmap=null;
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
            Drawable vectorDrawable = getDrawable(vectorDrawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        }else {
            bitmap = BitmapFactory.decodeResource(getResources(), vectorDrawableId);
        }
        return bitmap;
    }
}
