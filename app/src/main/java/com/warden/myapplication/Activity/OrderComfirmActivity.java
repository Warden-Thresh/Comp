package com.warden.myapplication.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.baidu.mapapi.map.MyLocationData;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.warden.myapplication.R;
import com.warden.myapplication.adapter.SimpleAdapter;
import com.warden.myapplication.util.Data;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class OrderComfirmActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{
    //目的地信息
    public double aimLat;
    public double aimLon;
    public String aimName;
    //dialog
    DialogPlus dialog;
    Context context;
    Intent intent;
    //当前位置
    private MyLocationData locData;
    private double currentLat;
    private double currentLon;
    private String endLocationName;
    private String startLocationName;
    private String date;
    private String previewImageUrl;
    private ProgressBar progressBar;
    ProgressDialog progressDialog;
    private Calendar[] selectableCalenders=new Calendar[7];
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comfirm_order);
        initView();
        initDialog();
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
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                OrderComfirmActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setTitle("选择出发日期");
        dpd.setDisabledDays(getSelectableDays());
        dpd.show(getFragmentManager(), "Datepickerdialog");
        dpd.setSelectableDays(getSelectableDays());
    }
    private void initView(){
        Button buttonCancel = (Button) findViewById(R.id.cancel_button) ;
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button buttonComfirm = (Button)findViewById(R.id.comfirm_button);
        buttonComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                /*Context context = OrderComfirmActivity.this;
                Intent intent = new Intent(getApplicationContext(),OrderActivity.class);
                intent.putExtra("aimLat",aimLat);
                intent.putExtra("aimLon",aimLon);
                intent.putExtra("aimName",aimName);
                context.startActivity(intent);
                */
            }
        });

    }
    private void initDialog(){
        SimpleAdapter adapter = new SimpleAdapter(OrderComfirmActivity.this,false);
        dialog = DialogPlus.newDialog(OrderComfirmActivity.this)
                .setAdapter(adapter)
                .setExpanded(false)
                .setContentHolder(new ViewHolder(R.layout.alipay_content))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.header_container:
                                break;
                            case R.id.paycomfirm:
                                progressDialog = new ProgressDialog(OrderComfirmActivity.this);
                                progressDialog.setTitle("请稍后");
                                progressDialog.setMessage("正在支付....");
                                progressDialog.setCancelable(true);
                                progressDialog.show();
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
                                                progressDialog.dismiss();
                                                Context context = OrderComfirmActivity.this;
                                                Intent intent = new Intent(getApplicationContext(),OrderActivity.class);
                                                intent.putExtra("aimLat",aimLat);
                                                intent.putExtra("aimLon",aimLon);
                                                intent.putExtra("aimName",aimName);
                                                context.startActivity(intent);
                                            }
                                        });
                                    }
                                }).start();
                                break;
                            case R.id.footer_close_button:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                    }
                })
                .create();
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
