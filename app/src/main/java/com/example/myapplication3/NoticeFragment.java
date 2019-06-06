package com.example.myapplication3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
//import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicDefaultFooter;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;


public class NoticeFragment extends Fragment {
    public View current_view;
    public LayoutInflater current_inflater;

    //上下拉控件
    private PtrFrameLayout ptr_frame;
    //数组存放数据
    public List<JsonObject> lists;
    //给ListView提供数据
    public MyAdapter adapter;
    public ListView lv;
    private int page;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //在fragment中就不用获取打气桶了，直接用fragment中提供的就可以。
        current_inflater = inflater;

        //找到布局文件
        current_view = inflater.inflate(R.layout.fragment_notice, null);

        CommonClass.hideTopViewAndSetText(current_view,"通知早知道");

        //获取PtrFrameLayout控件
        ptr_frame = (PtrFrameLayout)current_view.findViewById(R.id.ptr_frame);

        //PtrFameLayout属性设置及刷新加载的实现
        setPtrFrameAttribute();

        //找到listView
        lv = (ListView)current_view.findViewById(R.id.lv_notice);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                JsonObject obj = lists.get(position);
//                Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);
//
//                //页面跳转时传递参数
//                intent.putExtra("newsid", obj.newsId);
//
//                startActivity(intent);
           }
       });

       //初始化容器对象
        lists = new ArrayList<JsonObject>();
       //初始化第几页的值
        page = 1;

        //android中的表格时adapter来管理的，需要自己定义adapter来管理数据
        adapter = new NoticeFragment.MyAdapter();

        //获取服务器上的数据
        getDataFromServer(Integer.toString(page),"6");

        // Inflate the layout for this fragment
        return current_view;
    }

    private class MyAdapter extends BaseAdapter{

        //用来控制表格中的单元格的数量的方法
        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position){
            return null;
        }

        @Override
        public long getItemId(int position){
            return 0L;
        }

        //用来
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = View.inflate(getActivity().getApplicationContext(), R.layout.item_notice,null);
            }else{
                view = convertView;
            }

            //找到单元格上的各个控件
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
            ImageView imgView = (ImageView)view.findViewById(R.id.iv_icon);

            //为单元格上的每个控件赋值
            JsonObject obj = lists.get(position);
            tv_title.setText(obj.title);
            tv_time.setText(getFetureDate(Long.parseLong(obj.createTime)));

            //xUtil3加载网络图片的方式，第一个参数是ImageView对象，第二个参数是图片的在线路径
            x.image().bind(imgView,obj.pic);

            return view;
        }
    }

    public static String getFetureDate(long expire){
        //PHP和Java时间戳存在三位位差，用000补齐
        if (String.valueOf(expire).length() == 10){
            expire = expire *1000;
        }
        Date date = new Date(expire);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String result = format.format(date);
        if (result.startsWith("0")){
            result = result.substring(1);
        }
        return result;
    }

    /***
     * 必要属性设置及刷新加载的实现
     */
    private void setPtrFrameAttribute() {

        // Matrial风格头部的实现
        MaterialHeader header = new MaterialHeader(getContext());
        header.setPadding(0, PtrLocalDisplay.dp2px(15),0,0);
        ptr_frame.setHeaderView(header);
        ptr_frame.addPtrUIHandler(header);

        // 经典的底部布局实现
        PtrClassicDefaultFooter footer = new PtrClassicDefaultFooter(getContext());
        footer.setPadding(0, PtrLocalDisplay.dp2px(15),0,0);
        ptr_frame.setFooterView(footer);
        ptr_frame.addPtrUIHandler(footer);


        /**
         * 不用判断什么时候刷新和加载，方法里有自己的判断，适合大多数的情况
         */
        ptr_frame.setPtrHandler(new PtrDefaultHandler2()
        {
            // 上拉加载更多开始会执行该方法
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                page++;

                // 这里做一些加载的操作,增加新的数据
                getDataFromServer(Integer.toString(page), "6");

            }


            // 下拉刷新开始会执行该方法
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                lists.clear();
                page = 1;

                //去服务器端获取最新的数据
                getDataFromServer(Integer.toString(page), "6");
            }

        });
    }

    private void getDataFromServer(final String page, String num){
        //初始化请求对象
        RequestParams params = new RequestParams("http://211.67.177.56:8080/hhdj/news/newsList.do");

        // 设置请求所需要的参数(请替换成实际的参数与值)
        params.addBodyParameter("page", page);
        params.addBodyParameter("rows", num);
        params.addBodyParameter("type", "1");

        //开始请求
        x.http().post(params, new Callback.CommonCallback<String>() {

            //请求成功的回调，这里面用到的Gson来解析json数据，在使用之前要先导入Gson库，导入方法和导入其它库类似，库名称为：com.google.code.gson:gson:2.8.4。
            @Override
            public void onSuccess(String result) {
                try {
                    String beanJson = new JSONObject(result).getJSONArray("rows").toString();

                    //初始化Gson对象
                    Gson gson = new Gson();

                    //beanJson为请求后得到的JSON数据,JsonBean[].class为一个包含多个bean的数组.
                    JsonObject[] jsonBeans = gson.fromJson(beanJson, JsonObject[].class);
                    for (int i = 0; i < jsonBeans.length; ++i)
                    {
                        lists.add(jsonBeans[i]);
                    }

                    //如果是下拉刷新
                    if (page.equals("1"))
                    {
                        //得到数据后刷新listView
                        lv.setAdapter(adapter);

                    } else if (jsonBeans.length == 0){//如果数据请求完毕

                        Toast.makeText(getContext(), "哥，这回真没有了", Toast.LENGTH_SHORT).show();

                    } else {//如果是上拉加载更多
                        //调用此方法listView只更新最新的数据
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            //请求失败的回调，可以在这里面给用户提示网络错误
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("网络错误");

                Toast.makeText(getContext(), "请检查您的网络是否有问题！", Toast.LENGTH_SHORT).show();
            }

            //请求取消的回调，这个基本不用
            @Override
            public void onCancelled(CancelledException cex) {
                System.out.println("其它错误");

            }

            //请求结束的回调
            @Override
            public void onFinished() {
                System.out.println("接口调用结束");

                // 用于关闭刷新视图
                ptr_frame.refreshComplete();
            }
        });
    }
}
