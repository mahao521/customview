package com.mahao.jetpackstudy.livedata;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mahao.jetpackstudy.R;

public class OneFragment extends Fragment {

    private EditText edContent;
    private Button btnSend;
    private LiveDataTimerViewModel mModel;
    private static final String TAG = "OneFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_one, container, false);
        btnSend = inflate.findViewById(R.id.btn_send);
        edContent = inflate.findViewById(R.id.edit_query);

        mModel = new ViewModelProvider(this.getActivity()).get(LiveDataTimerViewModel.class);
        Log.d(TAG, "onCreateView: " + mModel.hashCode());
/*        mModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this.getActivity().getApplication())
                .create(LiveDataTimerViewModel.class);*/
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + edContent.getText().toString());
                mModel.getElapseTime().setValue(100l);
            }
        });
        return inflate;
    }
}