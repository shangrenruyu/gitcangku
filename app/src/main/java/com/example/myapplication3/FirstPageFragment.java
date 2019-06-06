package com.example.myapplication3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


public class FirstPageFragment extends Fragment {

    private List<String> images;
    private List<String> titles;
    private Banner banner;
    private GridView gridView;


    public View current_view;
    public LayoutInflater current_inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //在fragment中就不用获取打气桶了，直接用fragment中提供的就可以。
        current_inflater = inflater;

        //找到布局文件
        current_view = inflater.inflate(R.layout.fragment_first_page, null);

        //找到TextView，并将其隐藏
        TextView textView = (TextView)current_view.findViewById(R.id.tv_title);
        textView.setVisibility(View.GONE);

        //初始化数组
        images = new ArrayList<>();
        titles = new ArrayList<>();

        //加载控件
        initBanner();

        //接口调用
        getDataFormServer();

        initGridView();

        // Inflate the layout for this fragment
        return current_view;
    }

    public void initBanner(){
       banner = (Banner) current_view.findViewById(R.id.banner);
       //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        banner.setImageLoader(new PicassoImageLoader());

        //设置banner动画效果
        banner.setBannerAnimation(Transformer.Default);

        //为图片设置点击事件
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                System.out.print("点击到了某行" + position);
            }
        });

        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(1500);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);

    }

    //如果你需要更好的体验，可以这么操作
    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }

    private void getDataFormServer(){

        RequestParams params = new RequestParams("http://211.67.177.56:8080/hhdj/carousel/carouselList.do?");

        // 设置请求所需要的参数(请替换成实际的参数与值)
        params.addBodyParameter("type", "0");

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
                    for (int i = 0;i < jsonBeans.length; ++i){
                        JsonBean obj = jsonBeans[i];
                        images.add(obj.imgUrl);
                        titles.add(obj.title);

                    }

                    //设置图片集合
                    banner.setImages(images);
                    //设置标题集合（当banner有显示title时）
                    banner.setBannerTitles(titles);
                    //banner设置方法全部调用完毕时最后调用
                    banner.start();


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

    private void initGridView(){
        //获取到GridView
        gridView = (GridView)current_view.findViewById(R.id.gv_all);
        //给gridview设置数据适配器
        gridView.setAdapter(new MainGridViewAdapter());
        //点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    System.out.print("+++++++++++++++++\n");
                } else if(position == 3){
                    System.out.print("+++++++++++++++++\n");
                } else if(position == 4){
                    System.out.print("+++++++++++++++++\n");
                }
            }
        });
    }

    // 完成gridview 数据到界面的适配
    public class MainGridViewAdapter extends BaseAdapter {
        private String[] names = {"信工新闻眼", "掌上组织生活", "党员云互动", "党建一点通", "党员亮身份", "党史上的今天"};
        private int[] icons = {R.drawable.icon_01, R.drawable.icon_02, R.drawable.icon_03, R.drawable.icon_04, R.drawable.icon_05, R.drawable.icon_06};

        // 返回gridview里面有多少个条目
        public int getCount() {
            return names.length;
        }

        //返回某个position对应的条目
        public Object getItem(int position) {
            return position;
        }

        //返回某个position对应的id
        public long getItemId(int position) {
            return position;
        }

        //返回某个位置对应的视图
        public View getView(int position, View convertView, ViewGroup parent) {
            //把一个布局文件转换成视图
            View view = View.inflate(getActivity().getApplicationContext(), R.layout.gridview_btntext, null);
            ImageView iv = (ImageView) view.findViewById(R.id.gv_iv);
            TextView tv = (TextView) view.findViewById(R.id.gv_tv);

            //设置每一个item的名字和图标
            iv.setImageResource(icons[position]);
            tv.setText(names[position]);
            return view;
        }
    }




}
