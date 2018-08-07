package com.ljh.gtd3.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ljh.gtd3.util.HttpUtil;
import com.ljh.gtd3.util.MyApplication;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NetWorkStateReceiver extends BroadcastReceiver {

    private static final String TAG = NetWorkStateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("网络状态发生变化");
        IsNetWorkTask isNetWorkTask = new IsNetWorkTask();
        isNetWorkTask.execute();
//        //检测API是不是小于21，因为到了API21之后getNetworkInfo(int networkType)方法被弃用
//        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
//
//            //获得ConnectivityManager对象
//            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//            //获取ConnectivityManager对象对应的NetworkInfo对象
//            //获取WIFI连接的信息
//            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//            //获取移动数据连接的信息
//            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//            if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
//                Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
//            } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
//                Toast.makeText(context, "WIFI已连接,移动数据已断开", Toast.LENGTH_SHORT).show();
//            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
//                Toast.makeText(context, "WIFI已断开,移动数据已连接", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(context, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show();
//            }
//        }else {
//            //这里的就不写了，前面有写，大同小异
//            System.out.println("API level 大于21");
//            //获得ConnectivityManager对象
//            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//            //获取所有网络连接的信息
//            Network[] networks = connMgr.getAllNetworks();
//            //用于存放网络连接信息
//            StringBuilder sb = new StringBuilder();
//            //通过循环将网络信息逐个取出来
//            for (int i=0; i < networks.length; i++){
//                //获取ConnectivityManager对象对应的NetworkInfo对象
//                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
//                sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
//            }
//            Toast.makeText(context, sb.toString(),Toast.LENGTH_SHORT).show();
//        }
    }

    class IsNetWorkTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
            String address = "http://192.168.253.1:8080/netWork";
            HttpUtil.sendOkHttpGetRequest(address, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: network fail");
                    sharedPreferences.edit().putBoolean("NETWORK", false).apply();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d(TAG, "onResponse: network success");
                    sharedPreferences.edit().putBoolean("NETWORK", true).apply();
                }
            });
            return null;
        }
    }
}
