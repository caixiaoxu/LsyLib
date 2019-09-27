package com.lsy.myview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lsy.viewlib.weight.likeview.LikeView;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_zan)
    public void zan(View view) {
        ActivityCompat.startActivity(this, new Intent(this, ZanActivity.class), null);
    }

    @OnClick(R.id.btn_seek)
    public void seekSelect(View view) {
        ActivityCompat.startActivity(this, new Intent(this, SeekSelectActivity.class), null);
    }
}
