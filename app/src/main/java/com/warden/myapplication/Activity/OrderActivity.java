package com.warden.myapplication.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.baidu.mapapi.map.MyLocationData;
import com.warden.myapplication.R;
import com.warden.myapplication.util.Data;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class OrderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{
    //目的地信息
    public double aimLat;
    public double aimLon;
    public String aimName;
    //当前位置
    private MyLocationData locData;
    private double currentLat;
    private double currentLon;
    private String endLocationName;
    private String startLocationName;
    private String date;
    private String previewImageUrl;
    private Calendar[] selectableCalenders=new Calendar[7];
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Intent intent = getIntent();
        aimLat = intent.getDoubleExtra("aimLat",0.0);
        aimLon =intent.getDoubleExtra("aimLon",0.0);
        aimName = intent.getStringExtra("aimName");
        final Data data = (Data) getApplication();
        currentLat = data.getCurrentLat();
        currentLon = data.getCurrentLong();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                OrderActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setTitle("选择出发日期");
        dpd.setDisabledDays(getSelectableDays());
        dpd.show(getFragmentManager(), "Datepickerdialog");
        dpd.setSelectableDays(getSelectableDays());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
         date = year+"-"+monthOfYear+"-"+dayOfMonth;
        Log.d("date",date);

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

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
    private Calendar[] getSelectableDays(){
       Calendar now= Calendar.getInstance();
        for (int i = 0;i<selectableCalenders.length;i++){
            now.add(Calendar.DAY_OF_MONTH,1);
            selectableCalenders[i]=now;
            System.out.print(now);
            Log.d("now",now.getTime().toString());
        }
        return selectableCalenders;
    }
}
