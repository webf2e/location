package pro.lovexj.location.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import pro.lovexj.location.util.Constant;
import pro.lovexj.location.util.HttpUtils;
import pro.lovexj.location.util.Notify;

/**
 * 监听获取手机系统剩余电量
 * Created by Lx on 2016/9/17.
 */
public class BatteryReceiver extends BroadcastReceiver {
    private String url = "http://lovexj.pro/setting/setSetting";
    private TextView isRestartText;
    private TextView batteryText;
    private boolean isUpNotify = false;
    private boolean isLowNotify = false;

    public BatteryReceiver(TextView batteryText,TextView isRestartText){
        this.batteryText = batteryText;
        this.isRestartText = isRestartText;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int current = intent.getExtras().getInt("level");// 获得当前电量
        int total = intent.getExtras().getInt("scale");// 获得总电量
        int percent = current * 100 / total;
        batteryText.setText("电量："+percent + "%");
        //小于20不再重启app
        if(percent <= 20){
            Constant.isRestartApp = false;
            isRestartText.setText("不重启");
        }else{
            Constant.isRestartApp = true;
            isRestartText.setText("会重启");
        }
        //小于10，不再短信和推送末次位置是否更新
        if(percent <= 10){
            if(!isLowNotify){
                if(isUpNotify){
                    //不是首次，需要提醒
                    try {
                        sendToServer("0");
                        Notify.send("电池电量低于10%","短信和推送末次位置是否更新不再提醒");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                isLowNotify = true;
                isUpNotify = false;
            }
        }else{
            if(!isUpNotify){
                if(isLowNotify){
                    //不是首次，需要提醒
                    try {
                        sendToServer("1");
                        Notify.send("电池电量恢复到10%以上","短信和推送末次位置是否更新恢复提醒");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                isLowNotify = false;
                isUpNotify = true;
            }
        }
    }

    private void sendToServer(String type){
        final Map<String, String> params1 = new HashMap<>();
        final Map<String, String> params2 = new HashMap<>();
        params1.put("name","isNeedLocationNotUpdateForSmsNotify");
        params2.put("name","isNeedLocationNotUpdateForAppNotify");
        params1.put("value",type);
        params2.put("value",type);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils.post(url, params1,"utf-8");
                HttpUtils.post(url, params2,"utf-8");
            }
        }).start();
    }
}