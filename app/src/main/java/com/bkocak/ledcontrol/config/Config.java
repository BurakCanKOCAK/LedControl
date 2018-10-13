package com.bkocak.ledcontrol.config;

/**
 * Created by Burak Can KOCAK on 12-May-18.
 */

public class Config {
    public static boolean dataSendingInProgress= false;
    public static boolean dataSendingInProgressAsyncEffect= false;

    private static final String serverAddress = "http://192.168.0.1";
    private static String testServerAddress="0.0.0.0";
    private static final String serverPort="8484";

    private static final String apiFlatControlEndpoint="/api/flat/";
    private static final String apiCommercialControlEndpoint="/api/flat/";
    private static final String apiBuildingControlEndpoint="/api/flat/";
    private static final String apiShowOnSaleEndpoint="/api/show/onsale";
    private static final String apiEffectEndpoint="/api/show/effect";
    private static final String apiAllOnEndpoint="/api/allon";
    private static final String apiAllOffEndpoint="/api/alloff";


    public static String getServerIP(){
        return serverAddress;
    }

    public static String getServerAddress() {
        return serverAddress+":"+serverPort;
    }

    public static String getTestServerAddress() {
        return testServerAddress;
    }

    public static String getServerPort() {
        return serverPort;
    }

    public static void setTestServerAddress(String testServerAddress) {
        Config.testServerAddress = testServerAddress;
    }
}
