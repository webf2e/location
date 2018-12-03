package pro.lovexj.location.listener;

import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import pro.lovexj.location.MainActivity;
import pro.lovexj.location.R;
import pro.lovexj.location.util.Constant;

public class MyLocationListener extends BDAbstractLocationListener {

    public LocationClient mLocationClient = null;
    @Override
    public void onReceiveLocation(BDLocation location){
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取经纬度相关（常用）的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

        double latitude = location.getLatitude();    //获取纬度信息
        double longitude = location.getLongitude();    //获取经度信息
        //float radius = location.getRadius();    //获取定位精度，默认值为0.0f

        //String coorType = location.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

        //int errorCode = location.getLocType();
        //String text = "lon:"+longitude+"; lat:"+latitude+";radius:"+radius+";errorCode："+errorCode;
        //Toast.makeText(MainActivity.getContext(), text, Toast.LENGTH_SHORT).show();
        Constant.lonLat.add(longitude+","+latitude);

    }
}