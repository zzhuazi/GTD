package com.ljh.gtd3.util;

import android.util.Log;

import java.net.CookieManager;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/11/27.
 * http工具类
 */

public class HttpUtil {
    private static final String TAG = HttpUtil.class.getSimpleName();

    public static void sendOkHttpGetRequest(String address, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }


    public static void sendOkHttpPostRequest(String address, HashMap<String, Object> paramsMap, Callback callback) {
        CookieManager cookieManager = new CookieManager();
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(13, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(13, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(15, TimeUnit.SECONDS)//设置连接超时时间
                .cookieJar(new CookiesManager())
                .build();

        //创建一个FormBody.Builder
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : paramsMap.keySet()) {
            //追加表单信息
            Log.i(TAG, "sendOkHttpPostRequest: key: " + key.toString());
            builder.add(key, paramsMap.get(key).toString());
        }
        //生成表单实体对象
        RequestBody formBody = builder.build();
        Log.i(TAG, "sendOkHttpPostRequest: formBody " + formBody.toString());
        Request requestPost = new Request.Builder()
                .url(address)
                .post(formBody)
                .build();
        Log.i(TAG, "sendOkHttpPostRequest: requestPost" + requestPost.toString());
        client.newCall(requestPost).enqueue(callback);
    }

    public static void sendOkHttpEmailPostRequest(String address, HashMap<String, Object> paramsMap, Callback callback) {
        CookieManager cookieManager = new CookieManager();
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(58, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(58, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间
                .cookieJar(new CookiesManager())
                .build();

        //创建一个FormBody.Builder
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : paramsMap.keySet()) {
            //追加表单信息
            Log.i(TAG, "sendOkHttpPostRequest: key: " + key.toString());
            builder.add(key, paramsMap.get(key).toString());
        }
        //生成表单实体对象
        RequestBody formBody = builder.build();
        Log.i(TAG, "sendOkHttpPostRequest: formBody " + formBody.toString());
        Request requestPost = new Request.Builder()
                .url(address)
                .post(formBody)
                .build();
        Log.i(TAG, "sendOkHttpPostRequest: requestPost" + requestPost.toString());
        client.newCall(requestPost).enqueue(callback);
    }
    /**
     * 自动管理Cookies
     */
    private static class CookiesManager implements CookieJar {
        private final PersistentCookieStore cookieStore = new PersistentCookieStore(MyApplication.getInstance().getApplicationContext());

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (cookies != null && cookies.size() > 0) {
                for (Cookie item : cookies) {
                    cookieStore.add(url, item);
                    Log.d(TAG, "saveFromResponse: cookie" + item);
                }
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url);
            return cookies;
        }
    }

}
