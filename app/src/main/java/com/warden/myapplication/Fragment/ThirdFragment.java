package com.warden.myapplication.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.warden.myapplication.util.Data;
import com.warden.myapplication.R;
import com.warden.myapplication.gson.Weather;
import com.warden.myapplication.util.HttpUtil;
import com.warden.myapplication.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ThirdFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment {
    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private Button refreshBotton;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;

    private String mWeatherId;
    private double currentLat;
    private double currentLon;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ThirdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
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
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //设置title，接用toolbar会出错
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("便民");
        initView(view);
        return view;
    }

    private void initView(View view) {
        //bingPicImg = (ImageView) view.findViewById(R.id.bing_pic_img);
        //weatherLayout = (ScrollView)  view.findViewById(R.id.weather_layout);
        titleCity = (TextView) view.findViewById(R.id.title_city);
        titleUpdateTime = (TextView) view.findViewById(R.id.title_update_time);
        degreeText = (TextView) view.findViewById(R.id.degree_text);
        weatherInfoText = (TextView) view.findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) view.findViewById(R.id.forecast_layout);
        aqiText = (TextView) view.findViewById(R.id.aqi_text);
        pm25Text = (TextView) view.findViewById(R.id.pm25_text);
        comfortText = (TextView) view.findViewById(R.id.comfort_text);
        carWashText = (TextView) view.findViewById(R.id.car_wash_text);
        sportText = (TextView) view.findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
       // refreshBotton = (Button) view.findViewById(R.id.refresh_button);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String weatherString = prefs.getString("weather", null);
        String longitude = prefs.getString("currentLongitude", null);
        String latitude = prefs.getString("currentLatitude", null);
        String weatherId = longitude + "," + latitude;
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.getHeWeather5().get(0).getBasic().getId();
            showWeatherInfo(weather);
        }
        // 无缓存时去服务器查询位置
        if (latitude == null||longitude == null){
            FirstFragment fragment = (FirstFragment) getActivity().getSupportFragmentManager().findFragmentByTag("home");
            fragment.querryWeather=true;
            fragment.requestLocation();
            Log.d("Weather","requestLocation");
        }else {
            Log.d("Weather","requestWeather");
            requestWeather();
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather();
            }
        });
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather() {
        final Data data = (Data) getActivity().getApplication();
        currentLat = data.getCurrentLat();
        currentLon = data.getCurrentLong();
        String weatherId = currentLon + ","+currentLat;
        String weatherUrl = "https://api.heweather.com/v5/weather?city=" + weatherId + "&key=0d4a4167be61427cab63223e1c184d21";
        Log.d("URL:",weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                final String status;
                if (weather!=null){
                    status = weather.getHeWeather5().get(0).getStatus();
                }else {
                    status = "请求异常";
                }
                if (weather != null && "ok".equals(status)) {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Data.getContext()).edit();
                    editor.putString("weather", responseText);
                    editor.apply();
                    mWeatherId = weather.getHeWeather5().get(0).getBasic().getId();
                }
               getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Data.getContext()).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            mWeatherId = weather.getHeWeather5().get(0).getBasic().getId();
                            showWeatherInfo(weather);
                            Toast.makeText(Data.getContext(), "天气信息已刷新", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Data.getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Data.getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        //loadBingPic();
    }

    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.getHeWeather5().get(0).getBasic().getCity();
        String updateTime = weather.getHeWeather5().get(0).getBasic().getUpdate().getLoc().split(" ")[1];
        String degree = weather.getHeWeather5().get(0).getNow().getTmp()+ "℃";
        String weatherInfo = weather.getHeWeather5().get(0).getNow().getCond().getTxt();
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Weather.HeWeather5Bean.DailyForecastBean forecast : weather.getHeWeather5().get(0).getDaily_forecast()) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.getDate());
            infoText.setText(forecast.getCond().getCode_d());
            maxText.setText(forecast.getTmp().getMax());
            minText.setText(forecast.getTmp().getMin());
            forecastLayout.addView(view);
        }
        Weather.HeWeather5Bean.AqiBean aqi = weather.getHeWeather5().get(0).getAqi();
        if (aqi != null) {
            aqiText.setText(aqi.getCity().getAqi());
            pm25Text.setText(aqi.getCity().getPm25());
        }
        Weather.HeWeather5Bean.SuggestionBean suggestion =weather.getHeWeather5().get(0).getSuggestion();
        if (suggestion != null){
            String comfort = "舒适度：" +suggestion.getComf().getTxt();
            String carWash = "洗车指数：" + suggestion.getCw().getTxt();
            String sport = "运行建议：" + suggestion.getSport().getTxt();
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
        }

    }

}
