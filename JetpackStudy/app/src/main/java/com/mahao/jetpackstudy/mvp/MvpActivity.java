package com.mahao.jetpackstudy.mvp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.mahao.jetpackstudy.R;
import com.mahao.jetpackstudy.mvp.bus.LiveDataBus;
import com.mahao.jetpackstudy.mvp.bus.Rxbus;
import com.mahao.jetpackstudy.mvp.presenter.GrilPresenter;
import com.mahao.jetpackstudy.mvp.view.BaseActivity;
import com.mahao.jetpackstudy.mvp.view.IGridView;

import java.util.ArrayList;
import java.util.List;

public class MvpActivity extends BaseActivity<GrilPresenter<IGridView>,IGridView> implements IGridView{

    private static final String TAG = "MvpActivity";
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp);
        mListView = findViewById(R.id.listView);
        LiveDataBus.getInstance().with("list", ArrayList.class).observe(
                this,
                new Observer<ArrayList>() {
                    @Override
                    public void onChanged(ArrayList arrayList) {
                        if(arrayList != null){
                            Log.d(TAG, "onChanged: " + arrayList.size());
                        }
                    }
                }
        );
    }

    @Override
    protected GrilPresenter<IGridView> createPresenter() {
        return new GrilPresenter<>();
    }

    @Override
    protected void init() {
        getLifecycle().addObserver(presenter);

    }

    @Override
    protected void unRegister() {
        Rxbus.getInstance().unRegister(presenter);

    }

    @Override
    protected void register() {
        Rxbus.getInstance().register(presenter);
    }

    @Override
    public void showGrilView(List<Gril> grils) {
        mListView.setAdapter(new GrilAdapter(grils));
    }

    @Override
    public void showErrorMessage(String msg) {

    }
}