package com.example.myapplication3;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去除默认的标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        changeFirstPageFragment();

        //找到下面的radiobutton
        RadioButton rb_home = (RadioButton)findViewById(R.id.rb_home) ;
        RadioButton rb_notice = (RadioButton)findViewById(R.id.rb_notice);
        RadioButton rb_mine = (RadioButton)findViewById(R.id.rb_mine);

        //为按钮设置点击事件
        rb_home.setOnClickListener(this);
        rb_notice.setOnClickListener(this);
        rb_mine.setOnClickListener(this);


        //接口调用
        getDataFormServer();
    }

    //进入程序显示首页fragment
    public void changeFirstPageFragment()
    {
        //拿到fragment的管理者
        FragmentManager fragmentManager = getSupportFragmentManager();
        //用管理者来开启一个事务
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();

        //换成首页的fragment
        beginTransaction.replace(R.id.ll_layout, new FirstPageFragment());

        //一定要提交事务
        beginTransaction.commit();

    }


    @Override
    public void onClick(View v){

        //拿到Fragment的管理者
        FragmentManager fragmentManager = getSupportFragmentManager();

        //用管理者来开启一个事务
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();

        //判断点击到那个按钮
        switch (v.getId())
        {
            //点击到首页按钮
            case R.id.rb_home:
                //换成当前页面要用的fragment
                beginTransaction.replace(R.id.ll_layout, new FirstPageFragment());
                break;

            //点击到创业项目按钮
            case R.id.rb_notice:
                //换成当前页面要用的fragment
                beginTransaction.replace(R.id.ll_layout, new NoticeFragment());
                break;

            //点击到报名流程按钮
            case R.id.rb_mine:
                //换成当前页面要用的fragment
                beginTransaction.replace(R.id.ll_layout, new MineFragment());
                break;
        }

        //一定要提交事务
        beginTransaction.commit();
    }


    private void getDataFormServer(){

        RequestParams params = new RequestParams("http://211.67.177.56:8080/hhdj/news/newsList.do");

        // 设置请求所需要的参数(请替换成实际的参数与值)
        params.addBodyParameter("page", "1");
        params.addBodyParameter("rows", "10");
        params.addBodyParameter("type", "7");

        //开始请求
        x.http().post(params, new Callback.CommonCallback<String>() {

            //请求成功的回调，这里面用到的Gson来解析json数据，在使用之前要先导入Gson库，导入方法和导入其它库类似，库名称为：com.google.code.gson:gson:2.8.5。
            @Override
            public void onSuccess(String result) {
                System.out.println("接口调用成功");
                try {
                    String beanJson = new JSONObject(result).getJSONArray("rows").toString();

                    //初始化Gson对象
                    Gson gson = new Gson();

                    //beanJson为请求后得到的JSON数据,JsonBean[].class为一个包含多个bean的数组.
                    JsonBean[] jsonBeans = gson.fromJson(beanJson, JsonBean[].class);
                    JsonBean object = jsonBeans[0];
                    String title = object.title;
                    System.out.print("==========" + title + "==========");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            //请求失败的回调，可以在这里面给用户提示网络错误
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("接口调用错误");

            }

            //请求取消的回调，这个基本不用
            @Override
            public void onCancelled(CancelledException cex) {
                System.out.println("接口调用取消");

            }

            //请求结束的回调
            @Override
            public void onFinished() {
                System.out.println("接口调用结束");
            }
        });
    }

}
