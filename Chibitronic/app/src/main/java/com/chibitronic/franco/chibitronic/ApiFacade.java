package com.chibitronic.franco.chibitronic;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by franco on 16/11/2015.
 */
public class ApiFacade {

    protected Context ctx;
    private static AsyncHttpClient client;
    private static final String BASE_URL = "https://api.spark.io:443/v1/devices/2c001a001647343337363432/";
    private static final String TOKEN = "2d71c894e2336baf77f854a8748b4ba761985330";

    public static ApiFacade getInstance(Context context) {
        ApiFacade api = new ApiFacade();
        api.ctx = context;
        client = new AsyncHttpClient();
        client.addHeader("Content-Type","application/x-www-form-urlencoded");
        client.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; >Windows NT 5.1; en-US; rv:1.9.2.3) Gecko/20100401");

        return api;
    }

    public void callFunction(String url, Integer params) throws JSONException {

//        JSONObject jsonParams = new JSONObject();


//            jsonParams.put("access_token",TOKEN);
//            jsonParams.put("args", params);

        try{
            JsonHttpResponseHandler handler = new JsonHttpResponseHandler();
            RequestParams requestParams = new RequestParams();

            requestParams.put("access_token",TOKEN);
            requestParams.put("args", params);

            HttpEntity entity = requestParams.getEntity(handler);
            client.post(ctx, getAbsoluteUrl(url), entity, "application/x-www-form-urlencoded",handler);
        } catch (IOException e) {
            e.printStackTrace();
        }


//            StringEntity entity = new StringEntity(jsonParams.toString());
//            entity.setContentType("application/json");

        // client.addHeader("Content-Type","application/x-www-form-urlencoded");
        //client.post(null,getAbsoluteUrl(url),requestParams,handler);
//            client.post(null,getAbsoluteUrl(url),entity,"application/x-www-form-urlencoded",handler);


    }

    /** Gets Absolute URL with function */
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
