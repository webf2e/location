package pro.lovexj.location.thread;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.lovexj.location.bean.Location;
import pro.lovexj.location.util.Constant;
import pro.lovexj.location.util.HttpUtils;

/**
 * Created by liuwenbin on 18-12-4.
 */

public class LocationServerThread implements Runnable{

    private String url = "http://lovexj.pro/uploatLocationData";

    @Override
    public void run() {
        long lastUploadTime = 0;
        double lastCheckLon = 0;
        double lastCheckLat = 0;
        double currentCheckLon = 0;
        double currentCheckLat = 0;
        long interval = 5000;

        while (true){
            Constant.isStartSendToServerThread = true;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Location location = Constant.serverLonLatList.take();
                //判断发送的时间间隔
                currentCheckLon = location.getLon();
                currentCheckLat = location.getLat();
                if(currentCheckLon != lastCheckLon || currentCheckLat != lastCheckLat){
                    interval = 5000;
                }else{
                    interval = 30000;
                }
                lastCheckLon = currentCheckLon;
                lastCheckLat = currentCheckLat;
                //判断是否在时间间隔内
                if(location.getTimestramp() - lastUploadTime < interval){
                    continue;
                }
                lastUploadTime = location.getTimestramp();
                if(Math.abs(location.getLon() - 4.9E-324) < 0.0001 ||
                        Math.abs(location.getLat() - 4.9E-324) < 0.0001){
                    System.out.println("不合法经纬度坐标");
                    continue;
                }
                Map<String, String> params = new HashMap<>();
                params.put("locData", com.alibaba.fastjson.JSONObject.toJSONString(location));
                String result = HttpUtils.post(url,params,"utf-8");
                Constant.serverDataList.add(result+" "+dateFormat.format(System.currentTimeMillis()));
            }catch (Exception e){
                e.printStackTrace();
            }finally {

            }
        }
    }
}