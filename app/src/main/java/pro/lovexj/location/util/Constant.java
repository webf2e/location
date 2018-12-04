package pro.lovexj.location.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import pro.lovexj.location.bean.Location;

/**
 * Created by liuwenbin on 18-12-3.
 */

public class Constant {
    public static LinkedBlockingQueue<Location> blockLonLatList = new LinkedBlockingQueue<>();
    public static ConcurrentLinkedQueue<Location> concurlonLatList = new ConcurrentLinkedQueue<>();
    public static boolean isStartSendToServerThread = false;
    public static boolean isStartDrawMapThread = false;
}
