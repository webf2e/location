package pro.lovexj.location.thread;

import com.baidu.mapapi.http.HttpClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        while (true){
            try {
                if(Constant.concurlonLatList.size() > 10){
                    List<Location> locList = new ArrayList<>();
                    for(int i = 0;i < 10;i ++){
                        locList.add(Constant.concurlonLatList.poll());
                    }
                    Map<String, String> params = new HashMap<>();
                    params.put("locData", com.alibaba.fastjson.JSONObject.toJSONString(locList));
                    String result = HttpUtils.post(url,params,"utf-8");
                    System.out.println(result);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try{
                    Thread.sleep(1000);
                }catch (Exception e){}
            }
        }
    }
}
