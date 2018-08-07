package com.ljh.gtd3.data.operationSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.Operation;

import java.util.List;

public interface OperationDataSource {
    interface GetOperationsCallBack{
        void onOperationsLoad(List<Operation> operations, String message);
        void onOperationsFail(String message);
    }

    void getOperations(@NonNull GetOperationsCallBack callBack);

    void addOperations(List<Operation> operations);
}
