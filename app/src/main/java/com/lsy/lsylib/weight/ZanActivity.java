package com.lsy.lsylib.weight;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.lsy.lsylib.R;
import com.lsy.viewlib.weight.likeview.LikeView;

public class ZanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zan);

        LikeView ztv = findViewById(R.id.zanTextview);
    }
}
