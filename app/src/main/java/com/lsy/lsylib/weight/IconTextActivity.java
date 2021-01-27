package com.lsy.lsylib.weight;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lsy.lsylib.R;
import com.lsy.viewlib.weight.IconTextView;
import com.lsy.viewlib.weight.likeview.LikeView;

public class IconTextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_text);

        IconTextView iconTextView = findViewById(R.id.iconTextview);
        iconTextView.setText("这是固定标题这是固定标题是固定标题是", "这是省略这是省略这是省略这是省略", R.mipmap.sex_female);

    }
}
