package com.liubs.hotseconds.extension.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Liubsyy
 * @Date: 2023/6/15 12:43 下午
 * Description:
 */
public class IPUtil {

    private static String ipAddress = getLocalIp();

    private static String getLocalIp(){
        InetAddress address = null;
        String ip = null;
        try {
            address = InetAddress.getLocalHost();
            ip = address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }


    public static String getIpAddress() {
        return ipAddress;
    }
}
