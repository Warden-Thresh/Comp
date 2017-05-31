package com.warden.myapplication.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.BusLineOverlay;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;


import com.baidu.platform.comapi.map.B;
import com.baidu.platform.comapi.map.F;
import com.warden.myapplication.Activity.MainActivity;
import com.warden.myapplication.R;
import com.warden.myapplication.util.Data;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BusLocationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BusLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusLocationFragment extends Fragment implements
        OnGetPoiSearchResultListener, OnGetBusLineSearchResultListener,
        BaiduMap.OnMapClickListener,BaiduMap.OnMapLoadedCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MapView mMapView;
    private double currentLat;
    private double currentLon;
    private MyLocationData locData;
    private Button mBtnPre = null; // 上一个节点
    private Button mBtnNext = null; // 下一个节点
    private Button mBtnSearch = null;
    private LinearLayout searchLayout;
    private EditText editCity;
    private EditText editSearchKey;
    private int nodeIndex = -2; // 节点索引,供浏览节点时使用
    private BusLineResult route = null; // 保存驾车/步行路线数据的变量，供浏览节点时使用
    private List<String> busLineIDList = null;
    private int busLineIndex = 0;
    // 搜索相关
    private PoiSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private BusLineSearch mBusLineSearch = null;
    private BaiduMap mBaiduMap = null;
    BusLineOverlay overlay; // 公交路线绘制对象
    // 通过设置间隔时间和距离可以控制速度和图标移动的距离
    private static final int TIME_INTERVAL = 80;
    private static final double DISTANCE = 0.00002;
    private Marker mMoveMarker;
    private Handler mHandler;
    private List<LatLng> busLinePoints;
    public BusLocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BusLocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusLocationFragment newInstance(String param1) {
        BusLocationFragment fragment = new BusLocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bus_location, container, false);
        //mBtnPre = (Button) view.findViewById(R.id.pre);
        //mBtnNext = (Button) view.findViewById(R.id.next);
        final Data data=(Data) getActivity().getApplication();
        currentLat = data.getCurrentLat();
        Log.d("current",""+currentLat);
        currentLon = data.getCurrentLong();
        mBtnSearch = (Button)view.findViewById(R.id.search);
        editCity = (EditText) view.findViewById(R.id.city);
        editSearchKey = (EditText) view.findViewById(R.id.searchkey);
        searchLayout = (LinearLayout) view.findViewById(R.id.search_layout);
        mMapView = (MapView) view.findViewById(R.id.bmapView_fragment);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapClickListener(this);
        navigateTo(currentLat,currentLon);
        mSearch = PoiSearch.newInstance();
        mSearch.setOnGetPoiSearchResultListener(this);
        mBusLineSearch = BusLineSearch.newInstance();
        mBusLineSearch.setOnGetBusLineSearchResultListener(this);
        busLineIDList = new ArrayList<String>();
        if (mParam1.equals("我的订单")){
            searchLayout.setVisibility(View.GONE);
            busLineIDList.clear();
            busLineIndex = 0;
            // 发起poi检索，从得到所有poi中找到公交线路类型的poi，再使用该poi的uid进行公交详情搜索
            mSearch.searchInCity((new PoiCitySearchOption()).city(
                    "昆明")
                    .keyword("z56"));
        }
        overlay = new BusLineOverlay(mBaiduMap);
         mBaiduMap.setOnMarkerClickListener(overlay);
        mHandler = new Handler(Looper.getMainLooper());
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                UiSettings uiSettings = mBaiduMap.getUiSettings();
                // uiSettings.setCompassEnabled(false); // 是否显示指南针
                // 设置指南针的位置，在 onMapLoadFinish 后生效
                uiSettings.setCompassEnabled(true);
                uiSettings.setOverlookingGesturesEnabled(true); //设置是否允许俯视手势
                uiSettings.setRotateGesturesEnabled(true); //设置是否允许旋转手势
                // uiSettings.setScrollGesturesEnabled(false); //设置是否允许拖拽手势
                // uiSettings.setZoomGesturesEnabled(false); //设置是否允许缩放手势

            }
        });
        viewListener();
        return view;
    }
    private void navigateTo(double lat,double lon){

        LatLng ll = new LatLng(lat,
                lon);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()),1000);
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder
                .latitude(lat)
                .longitude(lon);
        locData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locData);
    }
   private void viewListener(){
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                busLineIDList.clear();
                busLineIndex = 0;
                // 发起poi检索，从得到所有poi中找到公交线路类型的poi，再使用该poi的uid进行公交详情搜索
                mSearch.searchInCity((new PoiCitySearchOption()).city(
                        editCity.getText().toString())
                        .keyword(editSearchKey.getText().toString()));
            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapLoaded() {
        navigateTo(currentLat,currentLon);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    /**
     * 发起检索
     *
     * @param v
     */
    public void searchButtonProcess(View v) {
        busLineIDList.clear();
        busLineIndex = 0;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        //EditText editCity = (EditText) v.findViewById(R.id.city);
        //EditText editSearchKey = (EditText) v.findViewById(R.id.searchkey);
        // 发起poi检索，从得到所有poi中找到公交线路类型的poi，再使用该poi的uid进行公交详情搜索
        mSearch.searchInCity((new PoiCitySearchOption()).city(
                editCity.getText().toString())
                .keyword(editSearchKey.getText().toString()));
    }

    public void searchNextBusline(View v) {
        if (busLineIndex >= busLineIDList.size()) {
            busLineIndex = 0;
        }
        if (busLineIndex >= 0 && busLineIndex < busLineIDList.size()
                && busLineIDList.size() > 0) {
            mBusLineSearch.searchBusLine((new BusLineSearchOption()
                    .city(editCity.getText()
                            .toString()).uid(busLineIDList.get(busLineIndex))));

            busLineIndex++;
        }

    }

    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {

        if (nodeIndex < -1 || route == null
                || nodeIndex >= route.getStations().size()) {
            return;
        }
        TextView popupText = new TextView(getContext());
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xff000000);
        // 上一个节点
        if (mBtnPre.equals(v) && nodeIndex > 0) {
            // 索引减
            nodeIndex--;
        }
        // 下一个节点
        if (mBtnNext.equals(v) && nodeIndex < (route.getStations().size() - 1)) {
            // 索引加
            nodeIndex++;
        }
        if (nodeIndex >= 0) {
            // 移动到指定索引的坐标
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(route
                    .getStations().get(nodeIndex).getLocation()));
            // 弹出泡泡
            popupText.setText(route.getStations().get(nodeIndex).getTitle());
            mBaiduMap.showInfoWindow(new InfoWindow(popupText, route.getStations()
                    .get(nodeIndex).getLocation(), 10));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d("BusLineFragment","onResume");
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mSearch.destroy();
        mBusLineSearch.destroy();
        super.onDestroy();
    }

    @Override
    public void onGetBusLineResult(BusLineResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getContext(), "抱歉，未找到结果",
                    Toast.LENGTH_LONG).show();
            return;
        }
        //mBaiduMap.clear();
        route = result;
        nodeIndex = -1;
        overlay.removeFromMap();
        overlay.setData(result);
        overlay.addToMap();
        overlay.zoomToSpan();
        busLinePoints = overlay.getBusLinePoints();
        Toast.makeText(getContext(), result.getBusLineName(),
                Toast.LENGTH_SHORT).show();
        OverlayOptions markerOptions;
        markerOptions = new MarkerOptions().flat(true).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory
                .fromResource(R.drawable.marker)).position(busLinePoints.get(0)).rotate((float) getAngle(0));
        mMoveMarker =(Marker) mBaiduMap.addOverlay(markerOptions);
        if (mParam1.equals("我的订单")){
           moveLooper();
        }
    }

    @Override
    public void onGetPoiResult(PoiResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getContext(), "抱歉，未找到结果",
                    Toast.LENGTH_LONG).show();
            return;
        }
        // 遍历所有poi，找到类型为公交线路的poi
        busLineIDList.clear();
        for (PoiInfo poi : result.getAllPoi()) {
            if (poi.type == PoiInfo.POITYPE.BUS_LINE
                    || poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
                busLineIDList.add(poi.uid);
            }
        }
        searchNextBusline(null);
        route = null;
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    @Override
    public void onMapClick(LatLng point) {
        Toast.makeText(getActivity(),"click", Toast.LENGTH_SHORT).show();
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }

    /**
     * 根据点获取图标转的角度
     */
    private double getAngle(int startIndex) {
        if ((startIndex + 1) >= busLinePoints.size()) {
            throw new RuntimeException("index out of bonds");
        }
        LatLng startPoint = busLinePoints.get(startIndex);
        LatLng endPoint = busLinePoints.get(startIndex + 1);
        return getAngle(startPoint, endPoint);
    }

    /**
     * 根据两点算取图标转的角度
     */
    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }

    /**
     * 根据点和斜率算取截距
     */
    private double getInterception(double slope, LatLng point) {

        double interception = point.latitude - slope * point.longitude;
        return interception;
    }

    /**
     * 算取斜率
     */
    private double getSlope(int startIndex) {
        if ((startIndex + 1) >= busLinePoints.size()) {
            throw new RuntimeException("index out of bonds");
        }
        LatLng startPoint = busLinePoints.get(startIndex);
        LatLng endPoint = busLinePoints.get(startIndex + 1);
        return getSlope(startPoint, endPoint);
    }

    /**
     * 算斜率
     */
    private double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
        return slope;

    }


    /**
     * 计算x方向每次移动的距离
     */
    private double getXMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
    }

    /**
     * 循环进行移动逻辑
     */
    public void moveLooper() {
        new Thread() {

            public void run() {

                while (true) {
                    int start = busLinePoints.size()*5/10;

                    for (int i =start; i < busLinePoints.size() - 1; i++) {
                        Log.d("Point"+busLinePoints.size(),i+":"+busLinePoints.get(i).toString());
                        final LatLng startPoint = busLinePoints.get(i);
                        final LatLng endPoint = busLinePoints.get(i+1);
                        mMoveMarker.setPosition(startPoint);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // refresh marker's rotate
                                if (mMapView == null) {
                                    Log.d("Mapview==null","");
                                    return;
                                }
                                mMoveMarker.setRotate((float) getAngle(startPoint,endPoint));
                                Log.d("MoveMarker","角度"+getAngle(startPoint,endPoint));
                            }
                        });
                        double slope = getSlope(startPoint, endPoint);
                        // 是不是正向的标示
                        boolean isReverse = (startPoint.latitude > endPoint.latitude);

                        double intercept = getInterception(slope, startPoint);

                        double xMoveDistance = isReverse ? getXMoveDistance(slope) :
                                -1 * getXMoveDistance(slope);
                        Log.d("MoveMarker","距离"+xMoveDistance);
                        if (xMoveDistance ==-0){
                            xMoveDistance=-0.1;
                        }
                        for (double j = startPoint.latitude; !((j > endPoint.latitude) ^ isReverse);
                             j = j - xMoveDistance) {

                            LatLng latLng = null;
                            if (slope == Double.MAX_VALUE) {
                                latLng = new LatLng(j, startPoint.longitude);
                            } else {
                                latLng = new LatLng(j, (j - intercept) / slope);
                            }

                            final LatLng finalLatLng = latLng;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mMapView == null) {
                                        Log.d("Mapview==null","");
                                        return;
                                    }
                                    mMoveMarker.setPosition(finalLatLng);
                                }
                            });
                            try {
                                Thread.sleep(TIME_INTERVAL);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            }

        }.start();
    }


}
