package com.warden.myapplication.Activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.warden.myapplication.R;

public class ElectricTicketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electric_ticket);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(5000);
                    Log.d("DDD","");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ElectricTicketActivity.this, OrderActivity.class);
                        PendingIntent pi = PendingIntent.getActivity(ElectricTicketActivity.this, 0, intent, 0);
                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        Notification notification = new NotificationCompat.Builder(ElectricTicketActivity.this)
                                .setContentTitle("你预定的公交还有五分钟到达")
                                .setContentText("按照当前你的位置，您需要立即前往,点击查看详情")
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                .setContentIntent(pi)
                                //        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                                //        .setVibrate(new long[]{0, 1000, 1000, 1000})
                                //        .setLights(Color.GREEN, 1000, 1000)
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                //        .setStyle(new NotificationCompat.BigTextStyle().bigText("Learn how to build notifications, send and sync data, and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
                                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.noticmap)))
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .build();
                        manager.notify(1, notification);

                    }
                });
            }
        }).start();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
