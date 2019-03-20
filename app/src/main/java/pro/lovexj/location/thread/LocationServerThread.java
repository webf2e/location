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

    private String url = "http://lovexj.pro/uploadLocationData";

    @Override
    public void run() {
        long lastUploadTime = 0;
        double lastCheckLon = 0;
        double lastCheckLat = 0;
        double currentCheckLon = 0;
        double currentCheckLat = 0;
        long interval = 5000;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (true){
            Constant.isStartSendToServerThread = true;
            try {
                Location location = Constant.serverLonLatList.take();
                //判断发送的时间间隔
                currentCheckLon = location.getL();
                currentCheckLat = location.getB();
                if(currentCheckLon != lastCheckLon || currentCheckLat != lastCheckLat){
                    interval = 5000;
                }else{
                    interval = 20000;
                }
                lastCheckLon = currentCheckLon;
                lastCheckLat = currentCheckLat;
                //判断是否在时间间隔内
                long timestramp = System.currentTimeMillis();
                if(timestramp - lastUploadTime < interval){
                    continue;
                }
                lastUploadTime = timestramp;
                if(Math.abs(location.getL() - 4.9E-324) < 0.0001 ||
                        Math.abs(location.getB() - 4.9E-324) < 0.0001){
                    System.out.println("不合法经纬度坐标");
                    continue;
                }
                Map<String, String> params = new HashMap<>();
                params.put("locData", location.toString());
                String result = HttpUtils.post(url,params,"utf-8");
                operateResult(result);
                Constant.serverDataList.add(result);
            }catch (Exception e){
                e.printStackTrace();
            }finally {

            }
        }
    }

    private void operateResult(String result){
        try{
            String[] results = result.split(" ");
            if(results[3].equals("0")){
                Constant.isAutoRestartApp = false;
            }else{
                Constant.isAutoRestartApp = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
