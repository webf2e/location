package pro.lovexj.location.listener;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

import java.text.SimpleDateFormat;

import pro.lovexj.location.bean.Location;
import pro.lovexj.location.util.Constant;

public class LocationListener extends BDAbstractLocationListener {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onReceiveLocation(BDLocation location){
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取经纬度相关（常用）的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
        double latitude = location.getLatitude();    //获取纬度信息
        double longitude = location.getLongitude();    //获取经度信息
        float radius = location.getRadius();    //获取定位精度，默认值为0.0f
        int errorCode = location.getLocType();
        double height = location.getAltitude();
        String locationDescribe = location.getLocationDescribe();    //获取位置描述信息
        Location l = new Location();
        l.setE(errorCode);
        l.setB(latitude);
        l.setL(longitude);
        l.setR(radius);
        l.setH(height);
        if(Constant.isStartDrawMapThread){
            Constant.mapLonLatList.add(l);
        }

        if(Constant.isStartSendToServerThread){
            Constant.serverLonLatList.add(l);
        }
    }
}