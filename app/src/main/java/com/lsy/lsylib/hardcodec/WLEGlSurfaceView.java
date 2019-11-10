package com.lsy.lsylib.hardcodec;

import android.content.Context;
import android.opengl.GLES20;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

//1、继成SurfaceView，并实现其CallBack回调
public class WLEGlSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    public final static int RENDERMODE_WHEN_DIRTY = 0;
    public final static int RENDERMODE_CONTINUOUSLY = 1;

    private Surface surface;
    private EGLContext eglContext;
    private WLEGLThread wleglThread;
    private WLEGLRender wleglRender;

    private int mRenderMode = RENDERMODE_CONTINUOUSLY;

    public WLEGlSurfaceView(Context context) {
        super(context);
        init();
    }

    public WLEGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WLEGlSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        getHolder().addCallback(this);
    }

    public void setSurfaceAndEGLContext(Surface surface, EGLContext eglContext) {
        this.surface = surface;
        this.eglContext = eglContext;
    }

    public void setRender(WLEGLRender wleglRender) {
        this.wleglRender = wleglRender;
    }

    public void setmRenderMode(int mRenderMode) {
        if (null == wleglRender) {
            throw new RuntimeException("must set render before");
        }

        this.mRenderMode = mRenderMode;
    }

    public EGLContext getEglContext() {
        if (null != wleglThread) {
            return wleglThread.getEGLContext();
        }
        return null;
    }

    public void requestRender() {
        if (null != wleglThread) {
            wleglThread.requestRender();
        }
    }

    //4、提供和系统GLSurfaceView相同的调用方法
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (null == surface) {
            surface = holder.getSurface();
        }

        wleglThread = new WLEGLThread(new WeakReference<>(this));
        wleglThread.isCreate = true;
        wleglThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        wleglThread.width = width;
        wleglThread.height = height;
        wleglThread.isChange = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        wleglThread.onDestory();
    }

    //2、自定义GLThread线程类，主要用于OpenGL的绘制操作
    static class WLEGLThread extends Thread {
        //3、添加设置Surface和EglContext的方法
        private WeakReference<WLEGlSurfaceView> wleGlSurfaceViewWeakReference;
        private EglHelper eglHelper;
        private boolean isExit = false;
        private boolean isCreate = false;
        private boolean isChange = false;
        private boolean isStart = false;

        private int width;
        private int height;

        private Object object;

        public WLEGLThread(WeakReference<WLEGlSurfaceView> wleGlSurfaceViewWeakReference) {
            this.wleGlSurfaceViewWeakReference = wleGlSurfaceViewWeakReference;
        }

        @Override
        public void run() {
            super.run();
            isExit = false;
            isStart = false;
            object = new Object();
            WLEGlSurfaceView wleGlSurfaceView = wleGlSurfaceViewWeakReference.get();
            if (null != wleGlSurfaceView) {
                eglHelper = new EglHelper();
                eglHelper.init(wleGlSurfaceView.surface, wleGlSurfaceView.eglContext);

                while (true) {
                    if (isExit) {
                        //释放资源
                        release();
                        break;
                    }

                    if (isStart) {
                        if (wleGlSurfaceView.mRenderMode == RENDERMODE_WHEN_DIRTY) {
                            synchronized (object) {
                                try {
                                    object.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (wleGlSurfaceView.mRenderMode == RENDERMODE_CONTINUOUSLY) {
                            try {
                                Thread.sleep(1000 / 60);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            throw new RuntimeException("mRenderModel is wrong value");
                        }
                    }

                    onCreate();
                    onChanger(width, height);
                    onDraw();

                    isStart = true;
                }
            }
        }

        private void onCreate() {
            WLEGlSurfaceView wleGlSurfaceView = wleGlSurfaceViewWeakReference.get();
            if (isCreate && null != wleGlSurfaceView && null != wleGlSurfaceView.wleglRender) {
                isCreate = false;
                wleGlSurfaceView.wleglRender.onSurfaceCreated();
            }
        }

        private void onChanger(int width, int height) {
            WLEGlSurfaceView wleGlSurfaceView = wleGlSurfaceViewWeakReference.get();
            if (isChange && null != wleGlSurfaceView && null != wleGlSurfaceView.wleglRender) {
                isChange = false;
                wleGlSurfaceView.wleglRender.onSurfaceChanged(width, height);
            }
        }

        private void onDraw() {
            WLEGlSurfaceView wleGlSurfaceView = wleGlSurfaceViewWeakReference.get();
            if (null != wleGlSurfaceView && null != wleGlSurfaceView.wleglRender && null != eglHelper) {
                if (!isStart) {
//                    wleGlSurfaceView.wleglRender.onDrawFrame();
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                    GLES20.glClearColor(0.6f, 0.8f, 0.9f, 1.0f);
                }
                wleGlSurfaceView.wleglRender.onDrawFrame();
                eglHelper.swapBuffers();
            }
        }

        public void requestRender() {
            if (null != object) {
                synchronized (object) {
                    object.notifyAll();
                }
            }
        }

        public void onDestory() {
            isExit = true;
            requestRender();
        }

        public void release() {
            if (null != eglHelper) {
                eglHelper.destroyEgl();
                eglHelper = null;
                object = null;
                wleGlSurfaceViewWeakReference = null;
            }
        }

        public EGLContext getEGLContext() {
            if (null != eglHelper) {
                return eglHelper.getEglContext();
            }
            return null;
        }
    }

    public interface WLEGLRender {
        void onSurfaceCreated();

        void onSurfaceChanged(int width, int height);

        void onDrawFrame();
    }
}
