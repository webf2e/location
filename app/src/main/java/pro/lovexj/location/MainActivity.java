package pro.lovexj.location;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import pro.lovexj.location.listener.MyLocationListener;

public class MainActivity extends AppCompatActivity {

    private  static Context context;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getApplicationContext();
        setContentView(R.layout.activity_main);

        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(1000);
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
    }

    public static Context getContext() {
        return context;
    }

}
