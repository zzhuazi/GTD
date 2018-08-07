package com.ljh.gtd3.data.UsersSource.remote;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ljh.gtd3.data.Result;
import com.ljh.gtd3.data.entity.Notification;
import com.ljh.gtd3.data.entity.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/12.
 */

public class UserUtility {
    private static final String TAG = UserUtility.class.getSimpleName();

    /**
     * 处理并返回带有user信息的json数据
     * @param response
     * @return
     */
    public static Result<User> handleUserResponse(String response){
        if(!TextUtils.isEmpty(response)) {
            try{
                Log.i(TAG, "handleUserResponse: " + response);
                JSONObject jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                Result<User> result = new Result<>();
                if(code == 200) { //获取成功
                    String data = jsonObject.get("data").toString();
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                            .create();
                    User user = gson.fromJson(data, User.class);
                    result.code =200;
                    result.data = user;
                    result.msg = jsonObject.get("msg").toString();
                    return result;
                }else if(code == 100) {
                    result.code = 100;
                    result.msg = jsonObject.get("msg").toString();
                    return result;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 处理并返回带有注册验证码信息的json数据
     * @param response
     * @return
     */
    public static Result<User> handleCodeResponse(String response){
        if(!TextUtils.isEmpty(response)) {
            try{
                Log.i(TAG, "handleUserResponse: " + response);
                JSONObject jsonObject = new JSONObject(response);
                Result<User> result = new Result<>();
                    result.code =jsonObject.getInt("code");;
                    result.msg = jsonObject.get("msg").toString();
                    return result;
            } catch (JSONException e) {
                e.printStackTrace();
                Result<User> result = new Result<>();
                result.code =100;
                result.msg = "获取验证码失败";
                return result;
            }
        }
        return null;
    }
    /**
     * 处理注册返回带有通知信息的json数据
     * @param response
     * @return
     */
    public static Result<Notification> handleRegisterResponse(String response){
        if(!TextUtils.isEmpty(response)) {
            try{
                Log.i(TAG, "handleRegisterResponse: " + response);
                JSONObject jsonObject = new JSONObject(response);
                Result<Notification> result = new Result<>();
                int code = jsonObject.getInt("code");
                if(code == 200) {
                    String data = jsonObject.get("data").toString();
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                            .create();
                    Notification notification = gson.fromJson(data, Notification.class);
                    result.code = 200;
                    result.data = notification;
                    result.msg = jsonObject.get("msg").toString();
                    return  result;
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
                Result<Notification> result = new Result<>();
                result.code = 100;
                result.msg = "服务器开小差！";
                return result;
            }
        }
        return null;
    }


    public static int handleUpdateResponse(String response){
        if(!TextUtils.isEmpty(response)) {
            try{
                JSONObject jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                if(code == 200) {
                    return 1;
                }else{
                    return 0;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
