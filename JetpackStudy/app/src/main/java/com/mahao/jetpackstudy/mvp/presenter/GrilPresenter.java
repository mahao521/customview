package com.mahao.jetpackstudy.mvp.presenter;

import android.util.Log;

import com.mahao.jetpackstudy.mvp.Gril;
import com.mahao.jetpackstudy.mvp.bus.RegisterBus;
import com.mahao.jetpackstudy.mvp.model.GrilModel;
import com.mahao.jetpackstudy.mvp.model.IGrilModel;
import com.mahao.jetpackstudy.mvp.view.IGridView;

import java.util.ArrayList;

import androidx.lifecycle.LifecycleOwner;

public class GrilPresenter<T extends IGridView> extends BasePresenter<T>{

    private static final String TAG = "GrilPresenter";
    IGrilModel mIGrilModel = new GrilModel();

    public GrilPresenter(){
        this.mIGrilModel.loadGrilData();
    }


    @RegisterBus("")
    public void getShowGrilData(ArrayList<Gril> girls){
        iGrilView.get().showGrilView(girls);
    }

    @Override
    void onDestory(LifecycleOwner owner) {
        super.onDestory(owner);
        Log.d(TAG, "onDestory: ");
    }

    @Override
    void onCreate(LifecycleOwner owner) {
        super.onCreate(owner);
        Log.d(TAG, "onCreate: ");
    }
}
