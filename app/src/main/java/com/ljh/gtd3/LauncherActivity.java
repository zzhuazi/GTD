package com.ljh.gtd3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ljh.gtd3.allTask.AllTasksActivity;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Task;
import com.ljh.gtd3.service.AddOperationWordService;
import com.ljh.gtd3.service.NotifyService;
import com.ljh.gtd3.util.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = LauncherActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        boolean firstOpen = sharedPreferences.getBoolean("FIRSTOPEN", true);
        if (firstOpen) {
            createDefaultListAndTask();
            sharedPreferences.edit().putBoolean("FIRSTOPEN", false).apply();
            Intent intent = new Intent(this, AddOperationWordService.class);
            startService(intent);
        }
        startAllStuffActivity();
    }

    private void createDefaultListAndTask() {
        //测试分支是否可靠
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List defaultList = new List();
        defaultList.setId(1);
        defaultList.setName("收集箱");
        defaultList.setTasks(1);
        defaultList.setPriority(0);
        defaultList.setGmtCreate(simpleDateFormat.format(new Date()));
        defaultList.setGmtModified(simpleDateFormat.format(new Date()));
        defaultList.save();
        //创建初始的任务
        Task task = new Task();
        task.setId(1);
        task.setName("欢迎使用GTD软件");
        task.setFinished(false);
        task.setPriority(0);
        task.setStartTime(simpleDateFormat.format(new Date()));
        task.setGmtCreate(simpleDateFormat.format(new Date()));
        task.setGmtModified(simpleDateFormat.format(new Date()));
        task.setList(defaultList);
        task.save();
    }


    private void startAllStuffActivity() {
        //开启通知
        Intent startServiceIntent = new Intent(this, NotifyService.class);
        startService(startServiceIntent);
        //跳转到所有任务列表
        Intent intent = new Intent(LauncherActivity.this, AllTasksActivity.class);
        startActivity(intent);
        this.finish();
    }
}
