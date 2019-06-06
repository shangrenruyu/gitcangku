package com.example.myapplication3;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CommonClass {
    //隐藏顶部导航栏上的控件
    public static void hideTopViewAndSetText(View view, String str) {

        //找到左边的imgView,并将其隐藏
        ImageView imgView = (ImageView) view.findViewById(R.id.iv_leftimg);
        imgView.setVisibility(View.GONE);

        //找到登录Button，并将其隐藏
        Button btn_login = (Button) view.findViewById(R.id.btn_login);
        btn_login.setVisibility(View.GONE);

        //找到TextView,并为其设置内容
        TextView textView = (TextView) view.findViewById(R.id.tv_title);
        textView.setText(str);

    }
}
