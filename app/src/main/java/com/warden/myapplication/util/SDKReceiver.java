package com.warden.myapplication.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Warden on 2017/5/17.
 */

public class SDKReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR))
        {
            Log.d("BDMAP:","key fail");
        }
        if(action.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
            Log.d("BDMAP:",action);
        }
    }
}
