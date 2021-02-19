package com.mahao.banner;

import android.app.Application;

import com.mahao.banner.util.AppContext;

public class StudyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        AppContext.setContext(this.getApplicationContext());
    }
}
