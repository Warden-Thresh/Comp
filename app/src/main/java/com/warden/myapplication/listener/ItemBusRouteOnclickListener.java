package com.warden.myapplication.listener;

import android.view.View;

import com.baidu.mapapi.search.route.TransitRouteResult;

/**
 * Created by Warden on 2017/5/24.
 */

public interface ItemBusRouteOnclickListener {
    void onClick(View view, TransitRouteResult result,int position);
}
