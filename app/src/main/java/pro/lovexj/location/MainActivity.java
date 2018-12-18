package pro.lovexj.location;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.igexin.sdk.PushManager;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pro.lovexj.location.bean.Location;
import pro.lovexj.location.service.IntentService;
import pro.lovexj.location.service.PushService;
import pro.lovexj.location.service.LocationService;
import pro.lovexj.location.thread.LocationServerThread;
import pro.lovexj.location.util.Constant;
import pro.lovexj.location.util.OsUtils;
import pro.lovexj.location.util.RestartAPP;

public class MainActivity extends AppCompatActivity {

    private  static Context context;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private TextView codeText;
    private TextView timeText;
    private TextView lonlatText;
    private TextView addrText;
    private TextView serverDataText;
    private TextView cidText;
    private TextView appStartTimeText;
    private Handler handler;

    private String code;
    private String time;
    private String lonlat;
    private String addr;
    private String serverData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constant.appStartTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
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
        PushManager.getInstance().initialize(this.getApplicationContext(), PushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), IntentService.class);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showScaleControl(true);
        mMapView.showZoomControls(true);

        LocationService.start();
        handler = new Handler();

        if(!Constant.isStartDrawMapThread){
            //界面显示位置
            final Runnable changeUI = new Runnable() {
                @Override
                public void run() {
                    codeText.setText(code);
                    timeText.setText(time);
                    lonlatText.setText(lonlat);
                    addrText.setText(addr);
                    serverDataText.setText(serverData);
                    cidText.setText("客户端ID："+Constant.cid);
                    appStartTimeText.setText("APP启动时间："+Constant.appStartTime);
                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                mBaiduMap = mMapView.getMap();
                codeText = (TextView)findViewById(R.id.code);
                timeText = (TextView)findViewById(R.id.time);
                lonlatText = (TextView)findViewById(R.id.lonlat);
                addrText = (TextView)findViewById(R.id.addr);
                cidText = (TextView)findViewById(R.id.cid);
                appStartTimeText = (TextView)findViewById(R.id.appStartTime);

                codeText.setBackgroundColor(Color.parseColor("#ffffff"));
                timeText.setBackgroundColor(Color.parseColor("#ffffff"));
                addrText.setBackgroundColor(Color.parseColor("#ffffff"));
                lonlatText.setBackgroundColor(Color.parseColor("#ffffff"));
                cidText.setBackgroundColor(Color.parseColor("#ffffff"));
                appStartTimeText.setBackgroundColor(Color.parseColor("#ffffff"));

                List<LatLng> points = new ArrayList<>();
                int radiusOKCount = 0;
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
                        code = "定位状态码：" + location.getErrorCode();
                        time = "定位时间：" + location.getTime()+"；时间戳：" + location.getTimestramp();
                        lonlat = "经度：" + location.getLon()+"；纬度：" + location.getLat() + "；半径：" + location.getRadius();
                        addr = "地址：" + location.getAddr() + "（" + location.getLocationDescribe() + "）";
                        handler.post(changeUI);
                        //绘制线
                        //添加到线中
                        points.add(point);
                        if(points.size() > 2000){
                            points.remove(0);
                        }
                        if(points.size() >= 2){
                            OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
                            mBaiduMap.addOverlay(ooPolyline);
                        }
                        //80
                        if(location.getRadius() > 80){
                            radiusOKCount ++;
                        }else{
                            radiusOKCount = 0;
                        }
                        //60
                        System.out.println("radiusOKCount:"+radiusOKCount);
                        if(radiusOKCount > 60){
                            RestartAPP.restartAPP(getContext());
                            radiusOKCount = 0;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                serverDataText = (TextView)findViewById(R.id.serverData);
                serverDataText.setBackgroundColor(Color.parseColor("#ffffff"));
                while (true){
                    try{
                        serverData = Constant.serverDataList.take();
                        handler.post(changeUI);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                }
            }).start();
        }

        if(!Constant.isStartSendToServerThread){
            //发送服务器的线程
            new Thread(new LocationServerThread()).start();
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
        PushManager.getInstance().initialize(this.getApplicationContext(), PushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), IntentService.class);
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
