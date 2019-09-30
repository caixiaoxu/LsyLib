package com.lsy.lsylib.hardcodec;

import android.opengl.GLES20;

import com.orhanobut.logger.Logger;

public class EGLRender implements WLEGlSurfaceView.WLEGLRender {

    public EGLRender() {
    }

    @Override
    public void onSurfaceCreated() {
        Logger.e("EGLRender.onSurfaceCreated");

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Logger.e("EGLRender.onSurfaceChanged");
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame() {
        Logger.e("EGLRender.onDrawFrame");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.6f, 0.8f, 0.9f, 1.0f);
    }
}
