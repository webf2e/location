package pro.lovexj.location.util;

import android.content.Context;
import android.content.Intent;

import pro.lovexj.location.service.KillSelfService;

public class RestartAPP {

    /**
     * 重启整个APP
     * @param context
     * @param Delayed 延迟多少毫秒
     */
    public static void restartAPP(Context context, long Delayed){
        /**开启一个新的服务，用来重启本APP*/
        if(Constant.isAutoRestartApp && Constant.isRestartApp){
            Intent intent1=new Intent(context, KillSelfService.class);
            intent1.putExtra("PackageName",context.getPackageName());
            intent1.putExtra("Delayed",Delayed);
            context.startService(intent1);

            /**杀死整个进程**/
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
    /***重启整个APP*/
    public static void restartAPP(Context context){
        restartAPP(context,2000);
    }
}