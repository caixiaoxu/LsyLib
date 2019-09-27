package com.lsy.myview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.lsy.myview.weight.ViewActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_custom_view)
    public void custonView(View view) {
        ActivityCompat.startActivity(this, new Intent(this, ViewActivity.class), null);
    }
    @OnClick(R.id.btn_ffmpeg)
    public void ffmpeg(View view) {
        ActivityCompat.startActivity(this, new Intent(this, ViewActivity.class), null);
    }
    @OnClick(R.id.btn_mediacodec)
    public void hardCodec(View view) {
        ActivityCompat.startActivity(this, new Intent(this, ViewActivity.class), null);
    }
}
