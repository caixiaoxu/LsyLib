package com.lsy.myview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.lsy.viewlib.weight.ScrollSingleCharTextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScrollSingleCharTextView ztv = findViewById(R.id.zanTextview);
    }
}
