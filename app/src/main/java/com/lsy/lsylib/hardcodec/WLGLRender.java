package com.lsy.lsylib.hardcodec;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import androidx.core.content.ContextCompat;

import com.example.harddecode.utils.WlShaderUtil;
import com.lsy.lsylib.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL;

public class WLGLRender implements WLEGlSurfaceView.WLEGLRender {

    private final Context context;

    private float[] vertexData = {
            -1f, -0.5f,
            1f, -0.5f,
            -1f, 0.5f,
            1f, 0.5f
    };
    private FloatBuffer vertexBuffer;

    private float[] fragmentData = {
            0f, 0f,
            0f, 1f,
            1f, 0f,
            1f, 1f
    };
    private FloatBuffer fragmentBuffer;

    private int program;
    private int v_position;
    private int f_position;
    private int sampler;
    private int texture;


    public WLGLRender(Context context) {
        this.context = context;

        //顶点坐标
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexData);
        vertexBuffer.position(0);

        //纹理坐标
        fragmentBuffer = ByteBuffer.allocateDirect(fragmentData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(fragmentData);
        fragmentBuffer.position(0);

    }

    @Override
    public void onSurfaceCreated() {
        //资源
        String vertexSource = WlShaderUtil.getRawSource(context, R.raw.vertex_shader);
        String fragmentSource = WlShaderUtil.getRawSource(context, R.raw.fragment_shader);
        //程序
        program = WlShaderUtil.createProgram(vertexSource, fragmentSource);
        if (0 < program) {
            //得到着色器中的属性
            v_position = GLES20.glGetAttribLocation(program, "v_Position");
            f_position = GLES20.glGetAttribLocation(program, "f_Position");
            sampler = GLES20.glGetUniformLocation(program, "sTexture");

            //创建和绑定纹理
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            texture = textures[0];
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glUniform1i(sampler, 0);

            //设置环绕方式（超出纹理坐标范围）：（s==x t==y GL_REPEAT重复）
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            //过滤（纹理像素映射到坐标点）：(缩小、放大：GL_LINEAR线性)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            //绘制图片
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.mv);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            //清空
            bitmap.recycle();
            bitmap = null;
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1f, 0f, 0f, 1f);

        //使用源程序（生效）
        GLES20.glUseProgram(program);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        //使顶点数据数组生效
        GLES20.glEnableVertexAttribArray(v_position);
        //为顶点赋值
        GLES20.glVertexAttribPointer(v_position, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);
        //使纹理数据数组生效
        GLES20.glEnableVertexAttribArray(f_position);
        //为纹理赋值
        GLES20.glVertexAttribPointer(f_position, 2, GLES20.GL_FLOAT, false, 8, fragmentBuffer);

        //绘制图形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }
}
