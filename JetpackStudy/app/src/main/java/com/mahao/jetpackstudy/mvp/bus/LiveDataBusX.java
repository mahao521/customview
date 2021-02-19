package com.mahao.jetpackstudy.mvp.bus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class LiveDataBusX {


    private Map<String, MutableLiveData<Object>> bus;

    private static LiveDataBusX liveDataBus = new LiveDataBusX();

    private LiveDataBusX(){
        bus = new HashMap<>();
    }

    public static LiveDataBusX getInstance(){
        return liveDataBus;
    }

    public synchronized <T>  MutableLiveData<T> with(String key,Class<T> type){
        if(!bus.containsKey(key)){
            bus.put(key,new MutableLiveData<Object>());
        }
        return (MutableLiveData<T>)bus.get(key);
    }

    public static class BusMutableLiveData<T> extends MutableLiveData<T>{

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            super.observe(owner, observer);
            hookObserver(observer);
        }

        //改变hook的onchange流程
        private void hookObserver(Observer<? super  T> observer) {

            try{
                //得到mLastVersion
                //获取到LiveData的类中mObsers对象
                Class<LiveData> liveDataClass = LiveData.class;
               Field mObserVersFiled =  liveDataClass.getDeclaredField("mObservers");
               if(!mObserVersFiled.isAccessible()){
                   mObserVersFiled.setAccessible(true);
               }
               //获取成员变量对象。
                Object mobject = mObserVersFiled.get(this);
                Class<?> aClass = mobject.getClass();
                //获取get方法
                Method get = aClass.getDeclaredMethod("get",Object.class);
                get.setAccessible(true);
                //执行get方法
                Object invokeEntry = get.invoke(mobject,observer);
                //定义一个空的对象。
                Object obserWrapper = null;
                if(invokeEntry != null && invokeEntry instanceof Map.Entry){
                    obserWrapper = ((Map.Entry) invokeEntry).getValue();
                }
                if(obserWrapper == null){
                    throw  new NullPointerException("null pointertrace");
                }
                Class<?> superclass = obserWrapper.getClass().getSuperclass();
                Field mLastVersion = superclass.getDeclaredField("mLastVersion");
                mLastVersion.setAccessible(true);
                //得到mVersion
                Field mVersion = liveDataClass.getDeclaredField("mVersion");
                mVersion.setAccessible(true);
                //吧mversion的数据填入到mLastVersion中
                Object o = mVersion.get(this);
                mLastVersion.set(obserWrapper,o);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
