package com.ljh.gtd3.addList;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;
import com.ljh.gtd3.data.entity.List;

/**
 * Created by Administrator on 2018/3/17.
 */

public interface AddListContract {
    interface View extends BaseView<Presenter>{
        void showPriority(); //选中优先级时，弹出对话框，并设计优先级图标背景颜色
        void showToast(String message);
        void showList(List list); //显示材料信息
    }

    interface Presenter extends BasePresenter{
        void addList(List list); //添加List,处理listGroup的情况
        void updateList(List list);  //更新list
    }
}
