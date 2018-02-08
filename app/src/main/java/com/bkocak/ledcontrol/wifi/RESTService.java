package com.bkocak.ledcontrol.wifi;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by BurakCan on 27/11/2017.
 */

public class RESTService {
    private static String deviceIP = "http://192.168.0.1:3000";
    private static String userID = "";
    private static String endpoint = "";

    public static String changeFlatStatus(int flatID, String status) throws ExecutionException, InterruptedException {
        try {
            endpoint = deviceIP + "/api/flat/" + String.valueOf(flatID) + "/" + status;
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }
    }

    public static String changeCommercialStatus(int commercialID, String status) throws ExecutionException, InterruptedException {
        try {
            endpoint = deviceIP + "/api/commercial/" + String.valueOf(commercialID) + "/" + status;
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }

    }

    public static String showOnSale() throws ExecutionException, InterruptedException {
        try {
            endpoint = deviceIP + "/api/show/onsale";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }

    }

    public static String flatsOff() throws ExecutionException, InterruptedException {
        try {
            endpoint = deviceIP + "/api/flat/off";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }

    }

    public static String flatsOn() throws ExecutionException, InterruptedException {
        try {
            endpoint = deviceIP + "/api/flat/on";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }
    }

    public static String allOn() throws ExecutionException, InterruptedException {
        try {
            endpoint = deviceIP + "/api/allon";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }
    }

    public static String allOff() throws ExecutionException, InterruptedException {
        try {
            endpoint = deviceIP + "/api/alloff";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }
    }

    public static String effect() throws ExecutionException, InterruptedException {
        try {
            endpoint = deviceIP + "/api/show/effect";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }
    }

    public static String commercialsOn() throws ExecutionException, InterruptedException {
        try {
            endpoint = deviceIP + "/api/commercial/on";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }
    }

    public static String commercialsOff() throws ExecutionException, InterruptedException {
        try {
            endpoint = deviceIP + "/api/commercial/off";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }

    }

    public static String setSingleCommercialOn(int commercialID) throws ExecutionException, InterruptedException {
        try {
            endpoint = deviceIP + "/api/commercial/" + String.valueOf(commercialID) + "/on";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }

    }

    public static String setSingleCommercialOff(int commercialID) throws ExecutionException, InterruptedException {
        try {
            endpoint = deviceIP + "/api/commercial/" + String.valueOf(commercialID) + "/off";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }

    }


    public static void sendGETCommand(String url) throws ExecutionException, InterruptedException {
        /*//String to place our result in
        String result;
        //Instantiate new instance of our class
        HttpGetRequest getRequest = new HttpGetRequest();
        //Perform the doInBackground method, passing in our url
        result = getRequest.execute(url).get();
        return result;*/
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }
                }
        );
    }

}
