package com.mahao.jetpackstudy.mvp.bus;

import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class Rxbus {

    //订阅者集合
    private Set<Object> subscribes;

    /**
     *  注册
     * @param subscriber
     */
    public synchronized void register(Object subscriber){
        subscribes.add(subscriber);
    }

    /**
     *  取消注册
     * @param subscriber
     */
    public synchronized void unRegister(Object subscriber){
        subscribes.remove(subscriber);
    }


    private static volatile  Rxbus instance;

    private Rxbus(){
        subscribes = new CopyOnWriteArraySet<>();
    }

    public static Rxbus getInstance(){
        if(instance == null){
            synchronized (Rxbus.class){
                if(instance == null){
                    instance = new Rxbus();
                }
            }
        }
        return instance;
    }


    public void chainProcess(Function function){
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .map(function)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        //上面function的处理结果就会到data上
                        if(o == null){
                            return;
                        }
                        //吧数据发送表示层
                        sendData(o);
                    }
                });
    }

    private void sendData(Object data) {
        for (Object  subscribe : subscribes){
            //扫描注解，将数据发送到注册的对象标记的位置
            callMethodByAnnotation(subscribe,data);
        }
    }

    private void callMethodByAnnotation(Object subscribe, Object data) {
        //得到presenter中写的所有的方法
        Method[] methods = Target.class.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            try {
                //如果哪个方法上用到我们注解，就把数据输入
                if(methods[i].getAnnotation(RegisterBus.class) != null){
                    Class<?> parameterType = methods[i].getParameterTypes()[0];
                    if(data.getClass().getName().equals(parameterType.getName())){
                        methods[i].invoke(subscribe,new Object[]{data});
                    }
                }

            }catch (Exception e){

            }
        }
    }
}











