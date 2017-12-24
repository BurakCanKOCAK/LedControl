package com.bkocak.ledcontrol.wifi;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by BurakCan on 27/11/2017.
 */

public class RESTService {
    private static String deviceIP="";
    private static String userID="";


    public static String sendGETCommand(String url) throws ExecutionException, InterruptedException {
        //String to place our result in
        String result;
        //Instantiate new instance of our class
        HttpGetRequest getRequest = new HttpGetRequest();
        //Perform the doInBackground method, passing in our url
        result = getRequest.execute(url).get();
        return result;
    }
}
