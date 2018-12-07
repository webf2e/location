package pro.lovexj.location.thread;

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

public class LocationThread implements Runnable{

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
                Map<String, String> params = new HashMap<>();
                params.put("locData", com.alibaba.fastjson.JSONObject.toJSONString(location));
                String result = HttpUtils.post(url,params,"utf-8");
                System.out.println(result);
            }catch (Exception e){
                e.printStackTrace();
            }finally {

            }
        }
    }
}
