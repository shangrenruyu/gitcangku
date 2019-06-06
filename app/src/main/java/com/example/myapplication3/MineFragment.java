package com.example.myapplication3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MineFragment extends Fragment {
    public View current_view;
    public LayoutInflater current_inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //在fragment中就不用获取打气桶了，直接用fragment中提供的就可以。
        current_inflater = inflater;

        //找到布局文件
        current_view = inflater.inflate(R.layout.fragment_mine, null);

        CommonClass.hideTopViewAndSetText(current_view,"我的党建");



        // Inflate the layout for this fragment
        return current_view;
    }
}
