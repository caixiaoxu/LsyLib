package com.lsy.lsylib.hardcodec;

import android.content.Context;
import android.util.AttributeSet;

public class EGLSurfaceView extends WLEGlSurfaceView {
    public EGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public EGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EGLSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setRender(new EGLRender());
        setmRenderMode(RENDERMODE_WHEN_DIRTY);
    }
}
