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

import org.json.JSONObject;

import java.text.SimpleDateFormat;

import pro.lovexj.location.MainActivity;
import pro.lovexj.location.R;
import pro.lovexj.location.bean.Location;
import pro.lovexj.location.util.Constant;

public class LocationListener extends BDAbstractLocationListener {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onReceiveLocation(BDLocation location){
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取经纬度相关（常用）的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
        long timestramp = System.currentTimeMillis();
        String time = dateFormat.format(timestramp);
        double latitude = location.getLatitude();    //获取纬度信息
        double longitude = location.getLongitude();    //获取经度信息
        float radius = location.getRadius();    //获取定位精度，默认值为0.0f
        String addr = location.getAddrStr();    //获取详细地址信息
        String country = location.getCountry();    //获取国家
        String province = location.getProvince();    //获取省份
        String city = location.getCity();    //获取城市
        String district = location.getDistrict();    //获取区县
        String street = location.getStreet();    //获取街道信息
        int errorCode = location.getLocType();
        String locationDescribe = location.getLocationDescribe();    //获取位置描述信息
        Location l = new Location();
        l.setAddr(addr);
        l.setCity(city);
        l.setCountry(country);
        l.setDistrict(district);
        l.setErrorCode(errorCode);
        l.setLat(latitude);
        l.setLocationDescribe(locationDescribe);
        l.setLon(longitude);
        l.setProvince(province);
        l.setRadius(radius);
        l.setStreet(street);
        l.setTime(time);
        l.setTimestramp(timestramp);
        if(Constant.isStartDrawMapThread){
            Constant.blockLonLatList.add(l);
        }

        if(Constant.isStartSendToServerThread){
            Constant.concurlonLatList.offer(l);
        }
    }
}