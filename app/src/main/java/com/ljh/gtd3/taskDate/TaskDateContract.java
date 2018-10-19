package com.ljh.gtd3.taskDate;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;

/**
 * Created by Administrator on 2018/3/25.
 */

public interface TaskDateContract {

    interface View extends BaseView<Presenter>{
        void getDate(String date, String time);  //获取日期和时间
        void setSelectedResult();
        void setNoSelectedResult();
    }

    interface Presenter extends BasePresenter{
        void loadDate(String date);  //获取日期
        void isSelected(boolean isSelected); //返回对应的activity
    }
}
