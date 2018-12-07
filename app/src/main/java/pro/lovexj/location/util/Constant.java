package pro.lovexj.location.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import pro.lovexj.location.bean.Location;

/**
 * Created by liuwenbin on 18-12-3.
 */

public class Constant {
    public static LinkedBlockingQueue<Location> mapLonLatList = new LinkedBlockingQueue<>();
    public static LinkedBlockingQueue<Location> serverLonLatList = new LinkedBlockingQueue<>();
    public static LinkedBlockingQueue<String> serverDataList = new LinkedBlockingQueue<>();
    public static boolean isStartSendToServerThread = false;
    public static boolean isRestartLocationThread = false;
    public static boolean isStartDrawMapThread = false;
}
