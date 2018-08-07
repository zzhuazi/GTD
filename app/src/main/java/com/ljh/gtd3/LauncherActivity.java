package com.ljh.gtd3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ljh.gtd3.allStuff.AllStuffActivity;
import com.ljh.gtd3.login.LoginActivity;
import com.ljh.gtd3.service.AddOperationWordService;
import com.ljh.gtd3.service.NotifyService;
import com.ljh.gtd3.util.HttpUtil;
import com.ljh.gtd3.util.MyApplication;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = LauncherActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        boolean firstOpen = sharedPreferences.getBoolean("FIRSTOPEN", true);
        if (firstOpen) {
            sharedPreferences.edit().putBoolean("FIRSTOPEN", false).apply();
            Intent intent = new Intent(this, AddOperationWordService.class);
            startService(intent);
        }
        IsNetWorkTask isNetWorkTask = new IsNetWorkTask();
        isNetWorkTask.execute();
        String userId = sharedPreferences.getString("USERID", null);
        if (userId != null) {
            startAllStuffActivity(userId);
        } else {
            startLoginActivity();
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void startAllStuffActivity(String userId) {
        Log.d(TAG, "startAllStuffActivity: " + userId);
        //开启通知
        Intent startServiceIntent = new Intent(this, NotifyService.class);
        startServiceIntent.putExtra("USERID", userId);
        startService(startServiceIntent);
        Intent intent = new Intent(LauncherActivity.this, AllStuffActivity.class);
        startActivity(intent);
        this.finish();
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
