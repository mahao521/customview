package com.mahao.banner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class SlideLayoutActivity extends AppCompatActivity {

    private static final String TAG = "SlideLayoutActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_layout);
        SlidePanlayout slidingPaneLayout= findViewById(R.id.slideview);
        DisplayMetrics metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        View child = slidingPaneLayout.getChildAt(0);
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        child.measure(View.MeasureSpec.makeMeasureSpec(layoutParams.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY));
        Log.d(TAG, "onCreate: " + child.getMeasuredWidth());
        int paraDistance = (int) (child.getMeasuredWidth());
        slidingPaneLayout.setParallaxDistance(paraDistance - slidingPaneLayout.getPaddingLeft());

    }
}