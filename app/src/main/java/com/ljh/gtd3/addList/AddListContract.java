package com.ljh.gtd3.addList;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.ListGroup;

/**
 * Created by Administrator on 2018/3/17.
 */

public interface AddListContract {
    interface View extends BaseView<Presenter>{
        void showPriority(); //选中优先级时，弹出对话框，并设计优先级图标背景颜色
        void showSelectListGroups(java.util.List<ListGroup> listGroups); //选中清单组时，弹出对话框，选择清单组
        void showToast(String message);
        void showListGroups(); //显示清单组页面
        void showList(List list, ListGroup listGroup); //显示材料信息

    }

    interface Presenter extends BasePresenter{
        void showSelectListGroups(); //选中文件夹时，弹出对话框,加载listGroups信息 并返回到fragment中
        void addList(List list); //添加List,处理listGroup的情况
        void showListGroup(); //显示清单组的页面
        void addListGroup(ListGroup listGroup); //添加listgroup
        void updateList(List list);  //更新list
    }
}
