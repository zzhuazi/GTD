package com.ljh.gtd3.allList;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;
import com.ljh.gtd3.data.entity.List;

/**
 * @author Administrator
 * @date 2018/10/20
 */
public interface AllListContract {
    interface View extends BaseView<Presenter>{
        void setLoadingIndicator(boolean active);  //是否加载指示
        void setLoadingListsError();                //加载错误
        void showNoLists();                         //显示没有List
        boolean isActive();                         //是否加载Fragment
        void showToast(String message);             //显示Toast

        void showAllLists(java.util.List<List> lists);//显示所有List

        void showList();            //跳转到该清单下的任务列表
    }

    interface Presenter extends BasePresenter{
        void loadLists(); //加载所有清单

        void showListDetail();  //长按跳转到ListDetail
        void deleteList();      //删除list
    }
}
