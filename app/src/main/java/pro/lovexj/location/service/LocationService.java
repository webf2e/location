package pro.lovexj.location.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import pro.lovexj.location.MainActivity;
import pro.lovexj.location.R;
import pro.lovexj.location.listener.LocationListener;

/**
 * Created by liuwenbin on 18-12-7.
 */
public class LocationService {
    public static LocationClient locationClient = null;

    public static synchronized void init(){
        if(null == locationClient){
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
            locationClient = new LocationClient(MainActivity.getContext());
            locationClient.setLocOption(option);
            locationClient.registerLocationListener(new LocationListener());

            //通知
            Notification.Builder builder = new Notification.Builder (MainActivity.getContext());
            //获取一个Notification构造器
            builder.setContentTitle("正在进行后台定位") // 设置下拉列表里的标题
                    .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                    .setContentText("后台定位通知") // 设置上下文内容
                    .setAutoCancel(false)
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
            Notification notification = null;
            notification = builder.build();
            notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
            locationClient.enableLocInForeground(1001, notification);// 调起前台定位
        }
    }

    public static void start(){
        if(null == locationClient){
            init();
        }
        locationClient.start();
    }

    public static void stop(){
        if(null == locationClient){
            init();
        }
        if(locationClient.isStarted()){
            locationClient.stop();
        }
    }

    public static void restart(){
        if(LocationService.isStarted()){
            System.out.println("先关闭定位");
            LocationService.stop();
        }
        System.out.println("开启定位");
        LocationService.start();
    }

    public static boolean isStarted(){
        return locationClient.isStarted();
    }
}
