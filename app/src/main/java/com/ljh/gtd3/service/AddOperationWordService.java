package com.ljh.gtd3.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.ljh.gtd3.data.entity.Operation;
import com.ljh.gtd3.data.operationSource.OperationsRepository;
import com.ljh.gtd3.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddOperationWordService extends Service {
    public AddOperationWordService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String[] operationName = {"打开","关闭","设置","完成", "增加","新增", "添加","删除","修改"};
        AppExecutors appExecutors = new AppExecutors();
        OperationsRepository operationsRepository = OperationsRepository.getInstance(appExecutors);
        List<Operation> operations = new ArrayList<>();
        for (String name : operationName){
            Operation operation = new Operation();
            operation.setName(name);
            operations.add(operation);
        }
        operationsRepository.addOperations(operations);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
