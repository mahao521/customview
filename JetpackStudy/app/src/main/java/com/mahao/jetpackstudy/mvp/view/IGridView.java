package com.mahao.jetpackstudy.mvp.view;

import com.mahao.jetpackstudy.mvp.Gril;

import java.util.List;

public interface IGridView extends IBaseView {

    //显示图片（回调函数）
    void showGrilView(List<Gril> grils);

}
