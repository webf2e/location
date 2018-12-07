package pro.lovexj.location.thread;

import javax.xml.transform.Source;

import pro.lovexj.location.service.LocationService;
import pro.lovexj.location.util.Constant;

/**
 * Created by liuwenbin on 18-12-7.
 */

public class RestartLocationThread implements Runnable{

    @Override
    public void run() {
        while (true){
            try {
                Constant.isRestartLocationThread = true;
                if(LocationService.isStarted()){
                    System.out.println("先关闭定位");
                    LocationService.stop();
                }
                System.out.println("开启定位");
                LocationService.start();
                Thread.sleep(1000 * 90);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
