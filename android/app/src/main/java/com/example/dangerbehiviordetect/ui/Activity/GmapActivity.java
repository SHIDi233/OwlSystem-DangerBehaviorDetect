package com.example.dangerbehiviordetect.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.NaviSetting;
import com.example.dangerbehiviordetect.R;
import com.google.common.reflect.ImmutableTypeToInstanceMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GmapActivity extends AppCompatActivity {
    private MapView mMapView;
    private AMap aMap;
    private final int REQUEST_PERMISSION_CODE = 1001;
    private Button drive_button;
    private Button walk_button;
    private Button bicycle_button;
    private ArrayList<axises> axisesArrayList = new ArrayList<>();
    private void initPermission() {
        List<String> mPermissionList = new ArrayList<>();

        // Android 版本大于等于 12 时，申请新的蓝牙权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //根据实际需要申请定位权限
            mPermissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            mPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            mPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            mPermissionList.add(Manifest.permission.INTERNET);
            mPermissionList.add(Manifest.permission.ACCESS_NETWORK_STATE);
            mPermissionList.add(Manifest.permission.ACCESS_WIFI_STATE);

        } else {
            mPermissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            mPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
//        bicycle_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Gmap.this, SearchActivity.class);
//                startActivity(intent);
//            }
//        });
        ActivityCompat.requestPermissions(this, mPermissionList.toArray(new String[0]), REQUEST_PERMISSION_CODE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmap);

        mMapView = findViewById(R.id.map);

        if (aMap == null){
            aMap = mMapView.getMap();
        }
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.interval(2000);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
//                                    ArrayList<Marker> marker = aMap.addMarkers(arrayList, true);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://116.204.11.171:8080/getAxises")
                .header("token", getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("token","1"))
                .get()
                .build();
        client.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseBody = response.body().string();
                        JsonElement element = JsonParser.parseString(responseBody);
                        int code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                        String temp = element.getAsJsonObject().get("code").toString();
//                        data.putString("code",temp);
                        if(code!=0){
//                            String url = element.getAsJsonObject().get("url").toString();
//                            data.putString("msg",element.getAsJsonObject().get("msg").toString());
//                            data.putString("url",url);
                            String objects = element.getAsJsonObject().get("data").toString();
                            Gson gson = new Gson();
                            axisesArrayList = gson.fromJson(objects, new TypeToken<List<axises>>(){}.getType());
                            ArrayList<MarkerOptions> arrayList = new ArrayList<>();
                            for (axises axise : axisesArrayList){
                                String[] parts = axise.axis.split(",");
                                float axise_X = Float.parseFloat(parts[0]);
                                float axise_Y = Float.parseFloat(parts[1]);
                                LatLng latLng =new LatLng(axise_X, axise_Y);
                                MarkerOptions markerOptions = new MarkerOptions().position(latLng).
                                        title("cID:" + axise.cID).snippet("摄像头").setFlat(true);
                                arrayList.add(markerOptions);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayList<Marker> marker = aMap.addMarkers(arrayList, true);
                                    mMapView.onCreate(savedInstanceState);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }

                    }
                }
        );

        //授权 否则无法显示地图
        AMapLocationClient.updatePrivacyShow(this,true,true);
        AMapLocationClient.updatePrivacyAgree(this,true);
        AMapLocationClient.updatePrivacyShow(this,true,true);
        AMapLocationClient.updatePrivacyAgree(this,true);
        NaviSetting.updatePrivacyShow(this, true, true);
        NaviSetting.updatePrivacyAgree(this, true);


        //授予定位权限
        initPermission();





//        mMapView = findViewById(R.id.map);
//        mMapView.onCreate(savedInstanceState);
//        if (aMap == null){
//            aMap = mMapView.getMap();
//        }
//        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
//        myLocationStyle.interval(2000);
//        aMap.setMyLocationStyle(myLocationStyle);
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);
//        aMap.setMyLocationEnabled(true);
//        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));






    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
//        mMapView.onDestroy();
//        AmapNaviPage.getInstance().exitRouteActivity();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}

class axises{
    String cID;
    String axis;
}