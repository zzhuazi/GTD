package com.ljh.gtd3.user;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;
import com.ljh.gtd3.data.entity.User;

/**
 * Created by Administrator on 2018/4/2.
 */

public interface UserContract {
    interface View extends BaseView<Presenter> {
        void showUser(User user);
        void updateUserName();
        void updatePhone();
        void updateSex();
        void updateIntroduce();
        void logout();
        void loadUser(User user);
    }

    interface Presenter extends BasePresenter{
        void loadUser();
        void updateUser(User user);
        void loadUser(String userId);
    }
}
