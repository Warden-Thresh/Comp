package com.warden.myapplication.Activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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
import android.widget.Button;

import com.warden.myapplication.R;

public class OrderActivity extends AppCompatActivity {
    private NotificationManager manager;
    NotificationCompat.Builder notifyBuilder;
    Button button;
    Context context;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        context = OrderActivity.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        button = (Button) findViewById(R.id.order_status);
        button.setClickable(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context,ElectricTicketActivity.class);
                context.startActivity(intent);

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(3000);
                    Log.d("DDD","");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setText("订单已生效");
                        button.setClickable(true);
                        Intent intent = new Intent(OrderActivity.this, OrderActivity.class);
                        PendingIntent pi = PendingIntent.getActivity(OrderActivity.this, 0, intent, 0);
                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        Notification notification = new NotificationCompat.Builder(OrderActivity.this)
                                .setContentTitle("e公交")
                                .setContentText("你的预定已生效，请按时乘车,点击查看详情")
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                .setContentIntent(pi)
                                //        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                                //        .setVibrate(new long[]{0, 1000, 1000, 1000})
                                //        .setLights(Color.GREEN, 1000, 1000)
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                //        .setStyle(new NotificationCompat.BigTextStyle().bigText("Learn how to build notifications, send and sync data, and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
                                //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.big_image)))
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
