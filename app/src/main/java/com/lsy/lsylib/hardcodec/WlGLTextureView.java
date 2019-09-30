package com.lsy.lsylib.hardcodec;

import android.content.Context;
import android.util.AttributeSet;

public class WlGLTextureView extends WLEGlSurfaceView {
    public WlGLTextureView(Context context) {
        super(context);
        init(context);
    }

    public WlGLTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WlGLTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setRender(new WLGLRender(context));
    }
}
