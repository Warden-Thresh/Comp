package com.warden.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.Point;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FirstFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment extends Fragment {
    public DrawerLayout drawerLayout;
    MyLocationListener myListener = new MyLocationListener();
    LocationClient mLocationClient;
    private OnFragmentInteractionListener mListener;
    private TextView positionText;
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public FirstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment newInstance(String param1, String param2) {


        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SDKInitializer.initialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        mapView = (MapView) view.findViewById(R.id.bmapView);
        positionText =(TextView)view.findViewById(R.id.position_text_view) ;
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        baiduMap.setOnMapClickListener(listener);
        baiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                UiSettings uiSettings = baiduMap.getUiSettings();
                // uiSettings.setCompassEnabled(false); // 是否显示指南针
                // 设置指南针的位置，在 onMapLoadFinish 后生效
                uiSettings.setCompassEnabled(true);
                uiSettings.setOverlookingGesturesEnabled(true); //设置是否允许俯视手势
                // uiSettings.setRotateGesturesEnabled(false); //设置是否允许旋转手势
                // uiSettings.setScrollGesturesEnabled(false); //设置是否允许拖拽手势
                // uiSettings.setZoomGesturesEnabled(false); //设置是否允许缩放手势

            }
        });

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            requestPermissions( permissions, 1);
        } else {
            requestLocation();
        }
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocationClient.requestLocation();
                isFirstLocate=true;
               /* Snackbar.make(view, "Data delete", Snackbar.LENGTH_SHORT)
                        .setAction("Undo", new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity(), "Data restored", Toast.LENGTH_SHORT).show();
                            }
                        }).show();*/
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private void requestLocation() {
        Toast.makeText(getActivity(),"dwe", Toast.LENGTH_SHORT).show();
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        Toast.makeText(getActivity(),"initlocation", Toast.LENGTH_SHORT).show();
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(getActivity(),"shouqun huitiao", Toast.LENGTH_SHORT).show();
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    for (int result : grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED ){
                            Toast.makeText(getActivity(),"必须统一所有权限才能使用本程序", Toast.LENGTH_SHORT).show();

                            return;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(getActivity(), "发生未知错误", Toast.LENGTH_SHORT).show();

                }
                break;
            default:
        }
    }
    private void navigateTo(BDLocation location){
        if (isFirstLocate){
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            Log.d("Location:",":Latitude:"+location.getLatitude());
            Log.d("Location:",":Longitude:"+location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll,18f);
            baiduMap.animateMapStatus(update);


        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder
                .accuracy(location.getRadius())
                .direction(180).latitude(location.getLatitude())
                .longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Log.d("Location","suss");
            Log.d("Location",bdLocation.getCity()+"Altitude"+bdLocation.getAltitude()+"Longitude"+bdLocation.getLongitude());
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation
                    || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                navigateTo(bdLocation);
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

    }

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
    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }
    BaiduMap.OnMapClickListener listener = new BaiduMap.OnMapClickListener() {
        /**
         * 地图单击事件回调函数
         * @param point 点击的地理坐标
         */
        public void onMapClick(LatLng point){
            Toast.makeText(getActivity(), "Map", Toast.LENGTH_SHORT).show();
        }
        /**
         * 地图内 Poi 单击事件回调函数
         * @param poi 点击的 poi 信息
         */
        public boolean onMapPoiClick(MapPoi poi){
            Toast.makeText(getActivity(), "HotSpot", Toast.LENGTH_SHORT).show();

            return true;
        }
    };
}
