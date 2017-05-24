package com.warden.myapplication.Activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.MapView;
import com.warden.myapplication.R;

import butterknife.BindView;

public class RoutePlanActivity extends AppCompatActivity {
    private static final String TAG = RoutePlanActivity.class.getName();

    EditText mEditStart;

    EditText mEditEnd;

    ImageView mImgBack;

    ImageView mImgReturn;

    TabLayout mTabLayout;

    MapView mMapView;

    RelativeLayout mBottomInfo;

    RecyclerView mRecycler;

    TextView mFirstLine;

    TextView mSeconLine;

    LinearLayout mDetail;

    ProgressBar mProgressBar;

    TextView mTvLogo;

    RelativeLayout mRootLayout;
    public double aimLat;
    public double aimLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan);
        mImgBack = (ImageView)findViewById(R.id.img_back);
        setOnclickListener();
        Intent intent = getIntent();
        aimLat = intent.getDoubleExtra("aimLat",0.0);
        aimLon =intent.getDoubleExtra("aimLon",0.0);
        Toast.makeText(this,"aimLat："+aimLat,Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"aimLon:"+aimLon,Toast.LENGTH_SHORT).show();
    }
    private void setOnclickListener(){
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
