package com.warden.myapplication.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.warden.myapplication.Activity.RoutePlanActivity;
import com.warden.myapplication.R;
import com.warden.myapplication.adapter.SimpleAdapter;
import com.warden.myapplication.util.Data;

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
public class FirstFragment extends Fragment implements SensorEventListener,OnGetGeoCoderResultListener ,BaiduMap.OnMapLoadedCallback{
    private int mRequestCode;
    public DrawerLayout drawerLayout;
    private FloatingActionButton fab;
    //定位
    private MyLocationConfiguration.LocationMode mCurrentMode;
    MyLocationListener myListener = new MyLocationListener();
    LocationClient mLocationClient;
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private double choosedLat;
    private double choosedLon;
    private String chooseName;
    private BDLocation mLastLocation;
    private float mCurrentAccracy;
    //UI
    RadioGroup.OnCheckedChangeListener radioButtonListener;
    private OnFragmentInteractionListener mListener;
    private TextView positionText;
    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;
    private boolean isFirstLocate = true;
    private MyLocationData locData;
    private BDLocation mBdlocation;
    public boolean querryWeather= false;
    //GEO
    GeoCoder mSearch = null; // 搜索模块

    //dialog
    private DialogPlus dialog;
    private RadioGroup radioGroup;
    private CheckBox headerCheckBox;
    private CheckBox footerCheckBox;
    private CheckBox expandedCheckBox;
    private OnClickListener searchRouteBtnListener;
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
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        initDialog();
        mLastLocation = new BDLocation();
        mMapView = (TextureMapView) view.findViewById(R.id.bmapView);
        //positionText =(TextView)view.findViewById(R.id.position_text_view) ;
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        mBaiduMap.setOnMapClickListener(onMapClickListener);
        mBaiduMap.setOnMapLongClickListener(onMapLongClickListener);
        mBaiduMap.setOnMapLoadedCallback(this);


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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String longitude = prefs.getString("currentLongitude",null);
        String latitude = prefs.getString("currentLatitude",null);
        if (longitude !=null&latitude !=null){
            mLastLocation.setLongitude(Double.parseDouble(longitude));
            mLastLocation.setLatitude(Double.parseDouble(latitude));
            Log.d("LastLocation:",":Latitude:"+ mLastLocation.getLatitude());
            Log.d("LastLocation:",":Longitude:"+ mLastLocation.getLongitude());
            navigateToLast(mLastLocation);

        }
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        setOnClickListener();
        return view;
    }
    private void navigateToLast(BDLocation location){

        LatLng ll = new LatLng(location.getLatitude(),
                location.getLongitude());
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(10.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder
                .accuracy(location.getRadius())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude());
        locData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locData);
    }
    private void initDialog(){
        SimpleAdapter adapter = new SimpleAdapter(getContext(),false);
        dialog = DialogPlus.newDialog(getContext())
                .setAdapter(adapter)
                .setExpanded(true,450)
                .setHeader(R.layout.location_detail_header)
                .setContentHolder(new ListHolder())
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        Toast.makeText(getActivity(),"clickDialog",Toast.LENGTH_SHORT).show();
                        switch (view.getId()) {
                            case R.id.header_container:
                                break;
                            case R.id.cancel_button:
                                break;
                            case R.id.go_to_button:
                                Context context = getContext();
                                Intent intent = new Intent(context, RoutePlanActivity.class);
                                intent.putExtra("aimLat",choosedLat);
                                intent.putExtra("aimLon",choosedLon);
                                intent.putExtra("aimName",chooseName);
                                context.startActivity(intent);
                                break;
                            case R.id.footer_confirm_button:

                                break;
                            case R.id.footer_close_button:

                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        Toast.makeText(getActivity(),"item",Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
    }
    private  void setOnClickListener(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BDLocation bdLocation = new BDLocation();
                bdLocation.setLatitude(mCurrentLat);
                bdLocation.setLongitude(mCurrentLon);
                navigateTo(bdLocation);
                switch (mCurrentMode) {
                    case NORMAL:
                        Toast.makeText(getActivity(),"跟随",Toast.LENGTH_SHORT).show();
                        //requestLocButton.setText("跟随");
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.overlook(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        break;
                    case COMPASS:
                        Toast.makeText(getActivity(),"普通",Toast.LENGTH_SHORT).show();
                        //requestLocButton.setText("普通");
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        MapStatus.Builder builder1 = new MapStatus.Builder();
                        builder1.overlook(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                        break;
                    case FOLLOWING:
                        Toast.makeText(getActivity(),"罗盘",Toast.LENGTH_SHORT).show();
                        //requestLocButton.setText("罗盘");
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public void requestLocation() {
        Toast.makeText(getActivity(),"dwe", Toast.LENGTH_SHORT).show();
        initLocation();
        if (mLocationClient.isStarted()){
            mLocationClient.requestLocation();
        }else {
            mLocationClient.start();
        }
    }

    private void initLocation(){
        //Toast.makeText(getActivity(),"initlocation", Toast.LENGTH_SHORT).show();
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll");
        option.setScanSpan(2000);
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

        LatLng ll = new LatLng(location.getLatitude(),
                location.getLongitude());
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()),2500);
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder
                .accuracy(location.getRadius())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude());
        locData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locData);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        ReverseGeoCodeResult.AddressComponent addressDetail = result.getAddressDetail();
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_marka)));
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(result
                .getLocation(),18f), 800);
        TextView chooseLocationDetial = (TextView) dialog.getHeaderView().findViewById(R.id.choose_location_detail);
        chooseLocationDetial.setText(result.getSematicDescription());

    }

    @Override
    public void onMapLoaded() {
        navigateTo(mBdlocation);
        Log.d("MapLoaded","");

    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            mBdlocation = location;
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            final Data data = (Data) getActivity().getApplication();
            data.setCurrentLat(mCurrentLat);
            data.setCurrentLong(mCurrentLon);
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if(location.getLocType() == BDLocation.TypeNetWorkLocation||location.getLocType()== BDLocation.TypeGpsLocation||location.getLocType()==BDLocation.TypeOffLineLocation){
                if (isFirstLocate) {
                    isFirstLocate = false;
                }
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
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();
        editor.putString("currentLongitude", String.valueOf(mCurrentLon));
        editor.putString("currentLatitude", String.valueOf(mCurrentLat));
        editor.apply();
        // 退出时销毁定位
        mLocationClient.stop();
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        Log.d("FirstFragment","onDestroy");
    }
    @Override
    public void onResume() {
        Log.d("FirstFragment","onResume");
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    public void onPause() {
        Log.d("FirstFragment","onPause");
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        Log.d("FirstFragment","onStop");
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    BaiduMap.OnMapClickListener onMapClickListener = new BaiduMap.OnMapClickListener() {
        /**
         * 地图单击事件回调函数
         * @param point 点击的地理坐标
         */
        public void onMapClick(LatLng point){
            mBaiduMap.clear();
        }
        /**
         * 地图内 Poi 单击事件回调函数
         * @param poi 点击的 poi 信息
         */
        public boolean onMapPoiClick(MapPoi poi){
            choosedLat = poi.getPosition().latitude;
            choosedLon = poi.getPosition().longitude;
            chooseName = poi.getName();
            dialog.show();
            TextView textTitle = (TextView) dialog.getHeaderView().findViewById(R.id.text_title);
            textTitle.setText(poi.getName());
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(poi.getPosition()));
            return true;
        }
    };
    /**
     * 发起搜索
     *
     * @param point
     */
    public void reverseGeoCode(LatLng point) {
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(point));

    }

    BaiduMap.OnMapLongClickListener onMapLongClickListener = new BaiduMap.OnMapLongClickListener() {
        /**
         * 地图长按事件监听回调函数
         * @param point 长按的地理坐标
         */
        public void onMapLongClick(LatLng point){
            reverseGeoCode(point);
        }
    };

}
