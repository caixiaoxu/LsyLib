package com.example.harddecode.utils;

import android.content.Context;
import android.opengl.GLES20;

import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WlShaderUtil {


    /**
     * 获取资源文件字符串
     *
     * @param context 上下文
     * @param rawId   资源ID
     * @return 资源文件字符串
     */
    public static String getRawSource(Context context, int rawId) {
        InputStream is = context.getResources().openRawResource(rawId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 创建、加载、编译shader
     *
     * @param sharedType
     * @param source
     * @return
     */
    public static int loadSource(int sharedType, String source) {
        //创建Shader(着色器：顶点或片元)
        int shader = GLES20.glCreateShader(sharedType);
        if (0 < shader) {
            //加载shader源码
            GLES20.glShaderSource(shader, source);
            //编译
            GLES20.glCompileShader(shader);

            //检查是否编译成功
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (GLES20.GL_TRUE != compiled[0]) {
                Logger.e("shader compiled fail");
                GLES20.glDeleteShader(shader);
                shader = -1;
            }
        }
        return shader;
    }

    /**
     * 创建渲染程序
     *
     * @param vertexSource
     * @param fragmentSource
     * @return
     */
    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadSource(GLES20.GL_VERTEX_SHADER, vertexSource);
        int fragmentShader = loadSource(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (0 != vertexShader && 0 != fragmentShader) {
            //创建渲染程序
            int program = GLES20.glCreateProgram();
            //将着色器程序添加到渲染程序中
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            //链接源程序
            GLES20.glLinkProgram(program);
            //检查程序链接是否成功
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (GLES20.GL_TRUE != linkStatus[0]) {
                Logger.e("program link fail");
                GLES20.glDeleteProgram(program);
                program = -1;
            }
            return program;
        }
        return -1;
    }


}
