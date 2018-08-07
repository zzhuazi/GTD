package com.ljh.gtd3.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ljh.gtd3.TestActivity;
import com.ljh.gtd3.util.VoiceController;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class VoiceService extends Service {
    private static final String TAG = VoiceService.class.getSimpleName();

    public VoiceService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String userId = intent.getStringExtra("USERID");
        String result = intent.getStringExtra("RESULT");
        VoiceController controller = new VoiceController();
        controller.start(getBaseContext(), userId, result);
//        Intent intent1 = new Intent(getBaseContext(), TestActivity.class);
//        intent1.setFlags(FLAG_ACTIVITY_NEW_TASK);
//        intent1.putExtra("RESULT",result);
//        intent1.putExtra("CONTENT", start);
//        startActivity(intent1);
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onStartCommand: 服务停止");
    }
}
