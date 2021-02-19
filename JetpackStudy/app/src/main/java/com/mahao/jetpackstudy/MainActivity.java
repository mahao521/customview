package com.mahao.jetpackstudy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mahao.jetpackstudy.livedata.ChooseActivity;
import com.mahao.jetpackstudy.livedata.TestFragmentActivity2;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void click1(View view) {

        Intent intent = new Intent(this, ChooseActivity.class);
        startActivity(intent);
    }

    public void click2(View view) {
        Intent intent = new Intent(this, TestFragmentActivity2.class);
        startActivity(intent);
    }



}