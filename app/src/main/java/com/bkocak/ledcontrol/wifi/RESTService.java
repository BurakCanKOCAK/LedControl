package com.bkocak.ledcontrol.wifi;

import android.util.Log;

import com.bkocak.ledcontrol.config.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

public class RESTService {

    private static String userID = "";
    private static String endpoint = "";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static String changeFlatStatus(String flatID, String status) throws ExecutionException, InterruptedException {
        try {
            endpoint = Config.getServerAddress() + "/api/flat/" + flatID + "/" + status;
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }
    }

    public static String changeCommercialStatus(String commercialID, String status) throws ExecutionException, InterruptedException {
        try {
            endpoint = Config.getServerAddress() + "/api/commercial/" + commercialID + "/" + status;
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }

    }

    public static String flatBuildingStatus(String buildingId, String status) throws ExecutionException, InterruptedException {
        try {
            endpoint = Config.getServerAddress() + "/api/building/flat/" + buildingId + "/" + status;
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }

    }

    public static String commercialBuildingStatus(String buildingId, String status) throws ExecutionException, InterruptedException {
        try {
            endpoint = Config.getServerAddress() + "/api/building/commercial/" + buildingId + "/" + status;
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }

    }

    public static String showOnSale() throws ExecutionException, InterruptedException {
        try {
            endpoint = Config.getServerAddress() + "/api/show/onsale";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }

    }

    public static String flatsOff() throws ExecutionException, InterruptedException {
        try {
            endpoint = Config.getServerAddress() + "/api/flat/off";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }

    }

    public static String flatsOn() throws ExecutionException, InterruptedException {
        try {
            endpoint = Config.getServerAddress() + "/api/flat/on";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }
    }

    public static String allOn() throws ExecutionException, InterruptedException {
        try {
            endpoint = Config.getServerAddress() + "/api/allon";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }
    }

    public static String allOff() throws ExecutionException, InterruptedException {
        try {
            endpoint = Config.getServerAddress() + "/api/alloff";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }
    }

    public static String effect() throws ExecutionException, InterruptedException {
        try {
            endpoint = Config.getServerAddress() + "/api/show/effect";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }
    }

    public static String effectAsync() throws ExecutionException, InterruptedException {
        try {
            //endpoint="http://192.168.1.34:3000/effect";
            endpoint = Config.getServerAddress() + "/api/show/effect";
            Log.e("Log : ", endpoint);
            SyncHttpClient client = new SyncHttpClient();
            if (!Config.dataSendingInProgressAsyncEffect) {
                Config.dataSendingInProgressAsyncEffect = true;
                client.get(endpoint, new ResponseHandlerInterface() {
                    @Override
                    public void sendResponseMessage(HttpResponse response) throws IOException {
                        Config.dataSendingInProgressAsyncEffect = false;
                    }

                    @Override
                    public void sendStartMessage() {

                    }

                    @Override
                    public void sendFinishMessage() {

                    }

                    @Override
                    public void sendProgressMessage(long bytesWritten, long bytesTotal) {

                    }

                    @Override
                    public void sendCancelMessage() {

                    }

                    @Override
                    public void sendSuccessMessage(int statusCode, Header[] headers, byte[] responseBody) {
                        Config.dataSendingInProgressAsyncEffect = false;
                    }

                    @Override
                    public void sendFailureMessage(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Config.dataSendingInProgressAsyncEffect = false;
                    }

                    @Override
                    public void sendRetryMessage(int retryNo) {

                    }

                    @Override
                    public URI getRequestURI() {
                        return null;
                    }

                    @Override
                    public void setRequestURI(URI requestURI) {

                    }

                    @Override
                    public Header[] getRequestHeaders() {
                        return new Header[0];
                    }

                    @Override
                    public void setRequestHeaders(Header[] requestHeaders) {

                    }

                    @Override
                    public boolean getUseSynchronousMode() {
                        return false;
                    }

                    @Override
                    public void setUseSynchronousMode(boolean useSynchronousMode) {

                    }

                    @Override
                    public boolean getUsePoolThread() {
                        return false;
                    }

                    @Override
                    public void setUsePoolThread(boolean usePoolThread) {

                    }

                    @Override
                    public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {

                    }

                    @Override
                    public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
                        Config.dataSendingInProgressAsyncEffect = false;
                    }

                    @Override
                    public Object getTag() {
                        return null;
                    }

                    @Override
                    public void setTag(Object TAG) {

                    }
                });
            }
            return "ok";
        } catch (Exception e) {
            return "error";
        }
    }

    public static String commercialsOn() throws ExecutionException, InterruptedException {
        try {
            endpoint = Config.getServerAddress() + "/api/commercial/on";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }
    }

    public static String commercialsOff() throws ExecutionException, InterruptedException {
        try {
            endpoint = Config.getServerAddress() + "/api/commercial/off";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }

    }

    public static String setSingleCommercialOn(int commercialID) throws ExecutionException, InterruptedException {
        try {
            endpoint = Config.getServerAddress() + "/api/commercial/" + String.valueOf(commercialID) + "/on";
            Log.e("Log : ", endpoint);
            sendGETCommand(endpoint);
            return "ok";
        } catch (Exception e) {
            return "error";
        }

    }

    public static String setSingleCommercialOff(int commercialID) throws ExecutionException, InterruptedException {
        try {
            endpoint = Config.getServerAddress() + "/api/commercial/" + String.valueOf(commercialID) + "/off";
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
        if (!Config.dataSendingInProgress) {
            Log.e("Data Status :", "SENT");
            Config.dataSendingInProgress = true;
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, null, new TextHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String res) {
                            // called when response HTTP status is "200 OK"
                            Config.dataSendingInProgress = false;
                            Log.e("Response : ", "( " + Integer.toString(statusCode) + " ) " + res);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            Config.dataSendingInProgress = false;
                            Log.e("Response : ", "( " + Integer.toString(statusCode) + " ) " + res);
                        }
                    }
            );
        } else {
            Log.e("Data Status :", "NOT SEND");
        }
    }
}
