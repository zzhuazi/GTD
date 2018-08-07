package com.ljh.gtd3.data.AffairSource.remote;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ljh.gtd3.data.Result;
import com.ljh.gtd3.data.entity.Affair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2018/3/24.
 */

public class AffairUtility {
    public static final String TAG = AffairUtility.class.getSimpleName();

    /**
     * 处理请求所有affairs所返回的数据
     * @param response
     * @return
     */
    public static Result<List<Affair>> handleAffairsResponse(String response){
        if(!TextUtils.isEmpty(response)) {
            try{
                Log.i(TAG, "handleUserResponse: " + response);
                JSONObject jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                Result<List<Affair>> result = new Result<>();
                if(code == 200) { //获取成功
                    String data = jsonObject.get("data").toString();
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                            .create();
                    List<Affair> affairs= gson.fromJson(data, new TypeToken<List<Affair>>() {
                    }.getType());
                    result.code =200;
                    result.data = affairs;
                    result.msg = jsonObject.get("msg").toString();
                    return result;
                }else if(code == 100) {
                    result.code = 100;
                    result.msg = jsonObject.get("msg").toString();
                    return result;
                }else {
                    result.code = 100;
                    result.msg = "服务器开小差！";
                    return result;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 处理返回信息的数据
     * @param response
     * @return
     */
    public static Result<Affair> handleResultResponse(String response){
        if(!TextUtils.isEmpty(response)) {
            try{
                Log.i(TAG, "handleUserResponse: " + response);
                JSONObject jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                Result<Affair> result = new Result<>();
                if(code == 200 || code == 100) { //获取成功
                    result.code = code;
                    result.msg = jsonObject.get("msg").toString();
                    return result;
                }else {
                    result.code = 100;
                    result.msg = "服务器开小差！";
                    return result;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
