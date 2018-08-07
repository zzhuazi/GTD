package com.ljh.gtd3.stuffDate;

import android.util.Log;

import com.ljh.gtd3.stuffDetail.StuffDetailContract;

/**
 * Created by Administrator on 2018/3/25.
 */

public class StuffDatePresenter implements StuffDateContract.Presenter{
    private static final String TAG = StuffDatePresenter.class.getSimpleName();
    private StuffDateContract.View mStuffDateView;

    public StuffDatePresenter(StuffDateContract.View stuffDateView) {
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
