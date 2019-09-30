package com.lsy.lsylib.hardcodec;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;

import com.lsy.lsylib.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DXVAActivity extends AppCompatActivity {

    @BindView(R.id.surface)
    SurfaceView surface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dxva);
        ButterKnife.bind(this);
    }
}
