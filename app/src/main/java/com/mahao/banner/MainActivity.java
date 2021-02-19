package com.mahao.banner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BannerManager bannerManager = new BannerManager();
        bannerManager.start();
        bannerManager.binLifeCycle(this);
        Button btnMessage = findViewById(R.id.btn_send_message);
        findViewById(R.id.btn_jump).setOnClickListener(this);
        findViewById(R.id.btn_slidelayout).setOnClickListener(this);
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 20; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                     //       bannerManager.start();
                        }
                    }).start();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_jump:
                jumpTarget(SecondActivity.class);
                break;
            case R.id.btn_slidelayout:
                jumpTarget(SlideLayoutActivity.class);
                break;
        }
    }


    public void jumpTarget(Class classx){
        Intent intent = new Intent (this,classx);
        startActivity(intent);
    }
}