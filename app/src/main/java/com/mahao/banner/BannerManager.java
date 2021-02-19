package com.mahao.banner;


import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

public class BannerManager implements PagerInterface {

    private static final String TAG = "BannerManager";
    public static final int MSG = 999;
    public static long TIME_INTERVAL = 2 * 1000L;
    private TimeHandler mHandler = new TimeHandler();
    private int a = 1;

    @Override
    public void start() {
        Log.d(TAG, "start: ");
        stop();
        Message message = Message.obtain();
        message.what = MSG;
        mHandler.sendMessageDelayed(message, TIME_INTERVAL);
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop: ");
        mHandler.removeCallbacksAndMessages(null);
    }


    public class TimeHandler extends Handler {

        @Override
        public void dispatchMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG:
                    Message message = Message.obtain();
                    message.what = MSG;
                    mHandler.sendMessageDelayed(message, TIME_INTERVAL);
                    //Log.d(TAG, "dispatchMessage: " + a++);
                    break;
            }
        }
    }


    public void binLifeCycle(LifecycleOwner owner) {
        owner.getLifecycle().addObserver(new ManaLifeObserver());
    }


    class ManaLifeObserver implements LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        public void onStart() {
            Log.d(TAG, "onStart:  -- ");
            start();
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        public void onStop() {
            Log.d(TAG, "onStop:  -- ");
            stop();
        }
    }

}
