package com.lsy.lsylib.weight;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.lsy.lsylib.R;
import com.lsy.viewlib.weight.SubwayBoardView;

public class SubwayBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subway_board);

        SubwayBoardView subwayBoard = findViewById(R.id.subwayBoard);
        subwayBoard.animCenterCircleRing();
    }
}
