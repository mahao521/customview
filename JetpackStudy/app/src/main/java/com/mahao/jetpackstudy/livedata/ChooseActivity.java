package com.mahao.jetpackstudy.livedata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mahao.jetpackstudy.R;

public class ChooseActivity extends AppCompatActivity {

    private static final String TAG = "ChooseActivity";
    private LiveDataTimerViewModel mLiveDataTimerViewModel;
    private TextView mTvCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        mTvCenter = findViewById(R.id.tv_center);
        mLiveDataTimerViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())
                .create(LiveDataTimerViewModel.class);
        subscribe();
    }


    private void subscribe() {
        final Observer<Long> elapsedTimeObserver = new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                Log.d(TAG, "onChanged: " + aLong);
                String newText = "aaa";
                mTvCenter.setText(newText + aLong);
            }
        };
        mLiveDataTimerViewModel.getElapseTime().observe(this,elapsedTimeObserver);

    }
}