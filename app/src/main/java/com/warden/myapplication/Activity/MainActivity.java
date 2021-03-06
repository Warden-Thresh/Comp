package com.warden.myapplication.Activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.warden.myapplication.Fragment.BusFragment;
import com.warden.myapplication.Fragment.BusLocationFragment;
import com.warden.myapplication.helper.BottomNavigationViewHelper;
import com.warden.myapplication.Fragment.FirstFragment;
import com.warden.myapplication.Fragment.FourthFragment;
import com.warden.myapplication.Fragment.SecondFragment;
import com.warden.myapplication.Fragment.ThirdFragment;
import com.warden.myapplication.R;
import com.warden.myapplication.service.AutoUpdateService;

public class MainActivity extends AppCompatActivity implements FirstFragment.OnFragmentInteractionListener ,
        SecondFragment.OnFragmentInteractionListener,
        ThirdFragment.OnFragmentInteractionListener,
        FourthFragment.OnFragmentInteractionListener,BusLocationFragment.OnFragmentInteractionListener,BusFragment.OnFragmentInteractionListener {
    //SDKReceiver mReceiver;
    private FragmentManager fragmentManager;
    private int currentFragmentId = 0;
    private Fragment currentFragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchFragment(0);
                    return true;
                case R.id.navigation_wait:
                    switchFragment(1);
                    return true;
                case R.id.navigation_dashboard:
                    switchFragment(2);
                    return true;
                case R.id.navigation_notifications:
                    switchFragment(3);
                    Toast.makeText(MainActivity.this, "3", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //启动服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
        // 初始化view
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        //mReceiver = new SDKReceiver();
        //registerReceiver(mReceiver, iFilter);
        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState) {


        // 主页面默认添加NewsFragment
        fragmentManager = getSupportFragmentManager();

        /**
         * 防止碎片重叠
         */
        Fragment secondFragment;
        Fragment thirdFragment;
        Fragment fourtFragment;
        if (savedInstanceState != null) {
            currentFragment = fragmentManager.findFragmentByTag("home");
            secondFragment = fragmentManager.findFragmentByTag("second");
            thirdFragment = fragmentManager.findFragmentByTag("third");
            fourtFragment = fragmentManager.findFragmentByTag("fourth");
            fragmentManager.beginTransaction().show(currentFragment)
                    .hide(secondFragment)
                    .hide(thirdFragment)
                    .hide(fourtFragment)
                    .commit();

        } else {
            currentFragment = new FirstFragment();
            secondFragment = new SecondFragment();
            thirdFragment = new ThirdFragment();
            fourtFragment = new FourthFragment();
            fragmentManager.beginTransaction().add(R.id.fragment_content, currentFragment, "home")
                    .add(R.id.fragment_content, secondFragment, "second")
                    .add(R.id.fragment_content, thirdFragment, "third")
                    .add(R.id.fragment_content, fourtFragment, "fourth")
                    .show(currentFragment)
                    .hide(secondFragment)
                    .hide(thirdFragment)
                    .hide(fourtFragment)
                    .commit();
        }
    }

    public void switchFragment(int fragmentId) {
        if (currentFragmentId == fragmentId)
            return;
        currentFragmentId = fragmentId;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment toFragment = null;
        switch (fragmentId) {
            case 0:
                toFragment = fragmentManager.findFragmentByTag("home");
                Toast.makeText(MainActivity.this, "to1", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                toFragment = fragmentManager.findFragmentByTag("second");
                //toolbar.setTitle("新闻资讯");
                break;
            case 2:
                toFragment = fragmentManager.findFragmentByTag("third");
                //toolbar.setTitle("视频");
                break;
            case 3:
                toFragment = fragmentManager.findFragmentByTag("fourth");
                Toast.makeText(MainActivity.this, "to3", Toast.LENGTH_SHORT).show();
                break;
        }
        fragmentTransaction.hide(currentFragment).show(toFragment).commit();
        currentFragment = toFragment;
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
