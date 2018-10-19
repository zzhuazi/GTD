package com.ljh.gtd3.taskDate;

import android.util.Log;

/**
 * Created by Administrator on 2018/3/25.
 */

public class TaskDatePresenter implements TaskDateContract.Presenter{
    private static final String TAG = TaskDatePresenter.class.getSimpleName();
    private TaskDateContract.View mStuffDateView;

    public TaskDatePresenter(TaskDateContract.View stuffDateView) {
        this.mStuffDateView = stuffDateView;
        mStuffDateView.setPresenter(this);
    }

    @Override
    public void start() {
        
    }

    @Override
    public void loadDate(String date) {
        Log.d(TAG, "loadDate: " + date);
        if(date == null || date.equals("null")) {
            String datetime = null;
            String time = null;
            mStuffDateView.getDate(datetime, time);
        }else {
            String datetime = date.substring(0,10);
            String time = date.substring(11);
            mStuffDateView.getDate(datetime, time);
        }
    }

    @Override
    public void isSelected(boolean isSelected) {
        if(isSelected) {
            mStuffDateView.setSelectedResult();
        }else {
            mStuffDateView.setNoSelectedResult();
        }
    }


}
