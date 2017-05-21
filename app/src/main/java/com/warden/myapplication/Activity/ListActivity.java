package com.warden.myapplication.Activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.warden.myapplication.R;
import com.warden.myapplication.adapter.MessageAdapter;
import com.warden.myapplication.model.Fruit;
import com.warden.myapplication.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ListActivity extends AppCompatActivity {
    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefresh;
    private List<Message> messageList = new ArrayList<>();
    private MessageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_message);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置title，接用toolbar会出错
        getSupportActionBar().setTitle("我的消息");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        FloatingActionButton fab =(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view,"Data delete", Snackbar.LENGTH_SHORT)
                        .setAction("Undo",new View.OnClickListener(){

                            @Override
                            public void onClick(View v) {
                                Toast.makeText(ListActivity.this, "Data restored",Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        Message message = new Message();
        message.setTitle("亮点功能");
        message.setImageUrl("@drawable/more");
        message.setContext("在对广大乘客进行出行调查的基础上，北京公交集团根据调查结果设计商务班车线路，并在定制公交平台上招募乘客、预订座位、在线支付，根据约定的时间、地点、方向开行。\n" +
                "商务班车可以走公交专用道，具备优先通行的优势；采用一人一座、一站直达、优质优价的服务方式；使用配备空调和车载Wi-Fi的公交车，为广大乘客提供安全、快捷、舒适、环保的公交出行服务。\n" +
                "欢迎点击定制公交平台加入到公交出行的行列，减少私家车使用，为缓解交通拥堵和倡导节能减排做出贡献。");
        message.setPreview("提供周边最新公交查询信息,包括公交查询.....");
        message.setDate("今天");
        message.setTime("12:50");
        messageList.add(message);
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });
    }

    private void refreshFruits() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.setTitle("亮点功能");
                        message.setImageUrl("@drawable/more");
                        message.setContext("在对广大乘客进行出行调查的基础上，北京公交集团根据调查结果设计商务班车线路，并在定制公交平台上招募乘客、预订座位、在线支付，根据约定的时间、地点、方向开行。\n" +
                                "商务班车可以走公交专用道，具备优先通行的优势；采用一人一座、一站直达、优质优价的服务方式；使用配备空调和车载Wi-Fi的公交车，为广大乘客提供安全、快捷、舒适、环保的公交出行服务。\n" +
                                "欢迎点击定制公交平台加入到公交出行的行列，减少私家车使用，为缓解交通拥堵和倡导节能减排做出贡献。");
                        message.setPreview("提供周边最新公交查询信息,包括公交查询.....");
                        message.setDate("今天");
                        message.setTime("12:50");
                        messageList.add(message);
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
