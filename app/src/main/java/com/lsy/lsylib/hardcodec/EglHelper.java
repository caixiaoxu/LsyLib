package com.lsy.lsylib.hardcodec;

import android.opengl.EGL14;
import android.opengl.GLES20;
import android.view.Surface;

import javax.microedition.khronos.egl.EGL;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class EglHelper {
    private EGL10 mEgl;
    private EGLDisplay mEglDisplay;
    private EGLContext mEglContext;
    private EGLSurface mEglSurface;

    public void init(Surface surface, EGLContext eglContext) {
        //得到EGL实例
        mEgl = (EGL10) EGLContext.getEGL();
        //得到默认的显示设备（就是窗口）
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }
        //初始化默认显示设备
        int[] version = new int[2];
        if (!mEgl.eglInitialize(mEglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed");
        }
        //设置显示设备的属性
        int[] attrbutes = new int[]{
                //红
                EGL10.EGL_RED_SIZE, 8,
                //绿
                EGL10.EGL_GREEN_SIZE, 8,
                //蓝
                EGL10.EGL_BLUE_SIZE, 8,
                //透明度
                EGL10.EGL_ALPHA_SIZE, 8,
                //深度
                EGL10.EGL_DEPTH_SIZE, 8,
                //场景
                EGL10.EGL_STENCIL_SIZE, 8,
                //必写
                EGL10.EGL_RENDERABLE_TYPE, 4,
                //结束
                EGL10.EGL_NONE
        };
        int[] num_configs = new int[1];
        if (!mEgl.eglChooseConfig(mEglDisplay, attrbutes, null, 1, num_configs)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        int num_config = num_configs[0];
        if (num_config <= 0) {
            throw new IllegalArgumentException(
                    "No configs match configSpec");
        }
        //从系统中获取对应的属性的配置
        EGLConfig[] eglConfigs = new EGLConfig[num_config];
        if (!mEgl.eglChooseConfig(mEglDisplay, attrbutes, eglConfigs, num_config, num_configs)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }
        //创建EGLContext
        int[] attrib_list = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
        };
        if (null != eglContext) {
            mEglContext = mEgl.eglCreateContext(mEglDisplay, eglConfigs[0], eglContext, attrib_list);
        } else
            mEglContext = mEgl.eglCreateContext(mEglDisplay, eglConfigs[0], EGL10.EGL_NO_CONTEXT, attrib_list);
        //创建渲染的Surface
        mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, eglConfigs[0], surface, null);
        //绑定EGLContext和Surface到显示设备中
        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            throw new RuntimeException("eglMakeCurrent fail");
        }

    }

    //刷新数据，显示渲染场景
    public boolean swapBuffers() {
        if (null != mEgl) {
            return mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);
        } else {
            throw new RuntimeException("egl is null");
        }
    }

    public EGLContext getEglContext() {
        return mEglContext;
    }

    //销毁
    public void destroyEgl() {
        if (null != mEgl) {
            //销毁Surface
            mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
            mEglSurface = null;
            //销毁EGLContext
            mEgl.eglDestroyContext(mEglDisplay, mEglContext);
            mEglContext = null;
            //销毁显示设备
            mEgl.eglTerminate(mEglDisplay);
            mEglDisplay = null;

            mEgl = null;
        }
    }
}
