package com.mahao.jetpackstudy.mvp.presenter;

import com.mahao.jetpackstudy.mvp.view.IBaseView;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

public class BasePresenter<T extends IBaseView> implements LifecycleObserver, LifecycleOwner {
    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    WeakReference<T> iGrilView;

    public void attView(T view){
        iGrilView = new WeakReference<>(view);
    }


    public void deAttachView(){
        if(iGrilView != null){
            iGrilView.clear();
            iGrilView = null;
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestory(LifecycleOwner owner){}


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate(LifecycleOwner owner){}

}
