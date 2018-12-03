package pro.lovexj.location;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import pro.lovexj.location.listener.MyLocationListener;
import pro.lovexj.location.util.Constant;

public class MainActivity extends AppCompatActivity {

    private  static Context context;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getApplicationContext();
        SDKInitializer.initialize(context);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.bmapView);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setScanSpan(10000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.setLocOption(option);
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mMapView = (MapView) findViewById(R.id.bmapView);
                mBaiduMap = mMapView.getMap();
                List<LatLng> points = new ArrayList<>();
                while (true){
                    try{
                        String[] lonlat = Constant.lonLat.take().split(",");
                        mBaiduMap.clear();
                        System.out.println(lonlat[0]+":"+lonlat[1]);
                        LatLng point = new LatLng(Double.parseDouble(lonlat[1]),
                                Double.parseDouble(lonlat[0]));
                        //添加到线中
                        points.add(point);
                        if(points.size() > 1000){
                            points.remove(0);
                        }


                        BitmapDescriptor bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.icon_mark1);
                        OverlayOptions opt = new MarkerOptions()
                                .position(point)
                                .icon(bitmap);
                        mBaiduMap.addOverlay(opt);

                        //绘制线
                        OverlayOptions ooPolyline = new PolylineOptions().width(10)
                                .color(0xAAFF0000).points(points);
                        mBaiduMap.addOverlay(ooPolyline);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
