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

import java.util.ArrayList;
import java.util.List;

import pro.lovexj.location.bean.Location;
import pro.lovexj.location.service.IntentService;
import pro.lovexj.location.service.PushService;
import pro.lovexj.location.service.LocationService;
import pro.lovexj.location.thread.LocationServerThread;
import pro.lovexj.location.thread.RestartLocationThread;
import pro.lovexj.location.util.Constant;
import pro.lovexj.location.util.OsUtils;

public class MainActivity extends AppCompatActivity {

    private  static Context context;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private TextView codeText;
    private TextView timeText;
    private TextView lonlatText;
    private TextView addrText;
    private TextView serverDataText;
    private Handler handler;

    private String code;
    private String time;
    private String lonlat;
    private String addr;
    private String serverData;

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

                    codeText.setBackgroundColor(Color.parseColor("#ffffff"));
                    timeText.setBackgroundColor(Color.parseColor("#ffffff"));
                    addrText.setBackgroundColor(Color.parseColor("#ffffff"));
                    lonlatText.setBackgroundColor(Color.parseColor("#ffffff"));

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
                            code = "定位状态码：" + location.getErrorCode();
                            time = "定位时间：" + location.getTime()+"；时间戳：" + location.getTimestramp();
                            lonlat = "经度：" + location.getLon()+"；纬度：" + location.getLat() + "；半径：" + location.getRadius();
                            addr = "地址：" + location.getAddr() + "（" + location.getLocationDescribe() + "）";
                            handler.post(changeUI);
                            //绘制线
                            //添加到线中
                            points.add(point);
                            if(points.size() > 5000){
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

        if(!Constant.isRestartLocationThread){
            //发送服务器的线程
            new Thread(new RestartLocationThread()).start();
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
