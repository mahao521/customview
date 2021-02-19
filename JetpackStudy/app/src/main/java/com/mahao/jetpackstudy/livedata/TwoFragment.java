package com.mahao.jetpackstudy.livedata;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahao.jetpackstudy.R;


public class TwoFragment extends Fragment {

    private TextView mTvTwoCenter;
    private LiveDataTimerViewModel mModel;
    private static final String TAG = "TwoFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_two, container, false);
         mTvTwoCenter = inflate.findViewById(R.id.tv_two_center);
/*        mModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this.getActivity().getApplication())
                .create(LiveDataTimerViewModel.class);*/
        mModel = new ViewModelProvider(this.getActivity()).get(LiveDataTimerViewModel.class);
        Log.d(TAG, "onCreateView: " + mModel.hashCode());
        mModel.getElapseTime().observe(getActivity(), new Observer<Long>() {
            @Override
            public void onChanged(Long s) {
                Log.d(TAG, "onChanged: " + s);
                mTvTwoCenter.setText(s+"   1");
            }
        });
        return inflate;
    }
}