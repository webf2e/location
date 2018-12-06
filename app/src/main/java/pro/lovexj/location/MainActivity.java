package pro.lovexj.location;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import pro.lovexj.location.bean.Location;
import pro.lovexj.location.listener.LocationListener;
import pro.lovexj.location.thread.LocationThread;
import pro.lovexj.location.util.Constant;
import pro.lovexj.location.util.OsUtils;

public class MainActivity extends AppCompatActivity {

    private  static Context context;
    public LocationClient mLocationClient = null;
    private LocationListener myListener = new LocationListener();
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private TextView info;
    private String content;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String processName = OsUtils.getProcessName(this, android.os.Process.myPid());
        System.out.println("processName:"+processName);
        if(null == processName || !processName.endsWith("pro.lovexj.location")) {
            System.out.println("跳出onCreate方法");
            return;
        }
        System.out.println("进入onCreate方法");
        context=getApplicationContext();
        SDKInitializer.initialize(context);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showScaleControl(true);
        mMapView.showZoomControls(true);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        option.setIsNeedAltitude(true);
        option.setIsNeedLocationDescribe(true);
        option.setPriority(LocationClientOption.GpsFirst);
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
        //通知
        Notification.Builder builder = new Notification.Builder (MainActivity.this.getApplicationContext());
        //获取一个Notification构造器
        Intent nfIntent = new Intent(MainActivity.this.getApplicationContext(), MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(MainActivity.this, 0, nfIntent, 0)) // 设置PendingIntent
                .setContentTitle("正在进行后台定位") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("后台定位通知") // 设置上下文内容
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        Notification notification = null;
        notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        mLocationClient.enableLocInForeground(1001, notification);// 调起前台定位
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
        handler = new Handler();

        if(!Constant.isStartDrawMapThread){
            //界面显示位置
            final Runnable changeUI = new Runnable() {
                @Override
                public void run() {
                    info.setText(content);
                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mBaiduMap = mMapView.getMap();
                    info=(TextView)findViewById(R.id.info);
                    info.setBackgroundColor(Color.parseColor("#ffffff"));
                    List<LatLng> points = new ArrayList<>();
                    while (true){
                        try{
                            Constant.isStartDrawMapThread = true;
                            Location location = Constant.mapLonLatList.take();
                            //清除地图上的点
                            mBaiduMap.clear();
                            //绘制点
                            LatLng point = new LatLng(location.getLat(),location.getLon());
                            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark1);
                            OverlayOptions opt = new MarkerOptions().position(point).icon(bitmap);
                            mBaiduMap.addOverlay(opt);
                            content = JSONObject.toJSONString(location);
                            handler.post(changeUI);
                            //绘制线
                            //添加到线中
                            points.add(point);
                            if(points.size() > 1000){
                                points.remove(0);
                            }
                            if(points.size() >= 2){
                                OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
                                mBaiduMap.addOverlay(ooPolyline);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        if(!Constant.isStartSendToServerThread){
            //发送服务器的线程
            new Thread(new LocationThread()).start();
        }
    }

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause");
        mMapView.onPause();
    }
}
