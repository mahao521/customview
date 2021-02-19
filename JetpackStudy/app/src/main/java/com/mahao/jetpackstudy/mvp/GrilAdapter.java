package com.mahao.jetpackstudy.mvp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahao.jetpackstudy.R;

import java.nio.file.attribute.PosixFileAttributes;
import java.util.List;

public class GrilAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private List<Gril> mGrils;

    public GrilAdapter(List<Gril> grils) {
        mGrils = grils;
    }

    @Override
    public int getCount() {
        return mGrils.size();
    }

    @Override
    public Object getItem(int i) {
        return mGrils.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View content = LayoutInflater.from(view.getContext()).inflate(R.layout.item,viewGroup,false);
        Gril gril = mGrils.get(i);
        ImageView ivHeader = content.findViewById(R.id.iv_header);
        ivHeader.setImageResource(gril.icon);
        TextView tvLevel = content.findViewById(R.id.tv_level);
        tvLevel.setText(gril.like);
        return content;
    }
}
