package com.ljh.gtd3.util;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.ljh.gtd3.broadcastReceiver.NetWorkStateReceiver;

import org.litepal.LitePalApplication;

/**
 * Created by Administrator on 2018/3/13.
 */

public class MyApplication  extends LitePalApplication {
    private static final String TAG = MyApplication.class.getSimpleName();
    private NetWorkStateReceiver mNetWorkStateReceiver;
    /**
     * 应用实例
     **/
    private static MyApplication instance;

    @Override
    public void onCreate() {
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5aaf34f8");
        super.onCreate();
        instance = this;
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        mNetWorkStateReceiver = new NetWorkStateReceiver();
        registerReceiver(mNetWorkStateReceiver,filter);
    }

    /**
     * 获得实例
     *
     * @return
     */
    public static MyApplication getInstance() {
        return instance;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate: 程序结束");
        unregisterReceiver(mNetWorkStateReceiver);
    }
}
