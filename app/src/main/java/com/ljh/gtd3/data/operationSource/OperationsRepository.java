package com.ljh.gtd3.data.operationSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.Operation;
import com.ljh.gtd3.util.AppExecutors;

import org.litepal.LitePal;

import java.util.List;

public class OperationsRepository implements OperationDataSource{
    private static OperationsRepository INSTANCE = null;
    private AppExecutors mAppExecutors;

    private OperationsRepository(AppExecutors mAppExecutors) {
        this.mAppExecutors = mAppExecutors;
    }

    public static OperationsRepository getInstance(AppExecutors appExecutors){
        if(INSTANCE == null) {
            INSTANCE = new OperationsRepository(appExecutors);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getOperations(@NonNull final GetOperationsCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Operation> operations = LitePal.findAll(Operation.class);
                if(operations.isEmpty()) {
                    callBack.onOperationsFail("没有操作命令词");
                }else {
                    callBack.onOperationsLoad(operations, "success");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void addOperations(final List<Operation> operations) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LitePal.saveAll(operations);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
