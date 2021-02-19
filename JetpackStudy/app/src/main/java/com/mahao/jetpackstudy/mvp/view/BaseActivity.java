package com.mahao.jetpackstudy.mvp.view;

import android.os.Bundle;

import com.mahao.jetpackstudy.mvp.presenter.BasePresenter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity <T extends BasePresenter,V extends IBaseView> extends AppCompatActivity {

    protected T presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = createPresenter();
        presenter.attView((V)this);
        registerSdk();
        init();
    }

    protected abstract T createPresenter() ;
    protected abstract void init();
    protected abstract void unRegister();
    protected abstract void register();


    private void registerSdk() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.deAttachView();
        unRegister();
    }
}
