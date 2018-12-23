package pro.lovexj.location.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuwenbin on 18-12-23.
 */

public class Notify {

    private static String url = "http://lovexj.pro/pushToApp";

    public static void send(String title,String content){
        final Map<String, String> params1 = new HashMap<>();
        params1.put("title",title);
        params1.put("content",content);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils.post(url, params1,"utf-8");
            }
        }).start();
    }
}
