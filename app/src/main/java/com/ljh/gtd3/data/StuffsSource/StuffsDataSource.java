package com.ljh.gtd3.data.StuffsSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.Stuff;

import java.text.ParseException;
import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public interface StuffsDataSource {
    //获取stuffs回调
    interface GetStuffsCallBack{
        void onStuffsLoaded(List<Stuff> stuffs, String message);
        void onStuffsFail(String message);
    }

    //获取单个stuff回调
    interface GetStuffCallBack{
        void onStuffLoaded(Stuff stuff, String message);
        void onStuffFail(String message);
    }

    //获取网络请求结果回调
    interface SendRequestCallBack{
        void onRequestSuccess(String message);
        void onRequestFail(String message);
    }

    void getStuff( @NonNull String stuffId, @NonNull GetStuffCallBack callBack);

    void getAllStuffs(@NonNull String userId, @NonNull GetStuffsCallBack callBack);

    void getStuffsByListId(@NonNull String userId, @NonNull String listId, @NonNull GetStuffsCallBack callBack);

    void getStuffsByStartDate(@NonNull String userId, @NonNull String startDate, @NonNull GetStuffsCallBack callBack);

    void deleteStuff(@NonNull String stuffId, @NonNull SendRequestCallBack callBack);

    void deleteStuffs(@NonNull String userId, @NonNull SendRequestCallBack callback);

    void deleteStuffsByListId(@NonNull String listId, @NonNull SendRequestCallBack callback);

    void updateStuff(@NonNull Stuff stuff);

    void addStuff(@NonNull Stuff stuff);
}
