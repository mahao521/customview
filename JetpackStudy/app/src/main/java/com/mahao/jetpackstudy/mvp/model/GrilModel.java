package com.mahao.jetpackstudy.mvp.model;

import com.mahao.jetpackstudy.R;
import com.mahao.jetpackstudy.mvp.Gril;
import com.mahao.jetpackstudy.mvp.bus.LiveDataBus;
import com.mahao.jetpackstudy.mvp.bus.Rxbus;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import io.reactivex.functions.Function;

public class GrilModel implements IGrilModel, LifecycleOwner {


    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    @Override
    public void loadGrilData() {
        Rxbus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                ArrayList<Gril> data = new ArrayList<>();
                data.add(new Gril(R.drawable.ic_launcher_background,"一颗星","kkkkk"));
                data.add(new Gril(R.drawable.ic_launcher_background,"两颗星","222"));
                LiveDataBus.getInstance().with("list",ArrayList.class).postValue(data);
                return data;
            }
        });
    }


}
