package com.mahao.jetpackstudy.livedata;

import java.nio.channels.MulticastChannel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NameViewModel extends ViewModel {


    public NameViewModel(){

    }

    private MutableLiveData<String> mName;

    public MutableLiveData<String> getCurrentName(){
        if(mName == null){
            mName = new MutableLiveData<String>();
        }
        return  mName;

    }


}
