package com.ljh.gtd3.login;

import android.support.annotation.NonNull;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;

/**
 * Created by Administrator on 2018/3/12.
 */

public interface LoginContract {

    interface View extends BaseView<Presenter>{
        void finish();
        void showRegister();
        void showAllStuff(String userId);
        void showToast(String message);
    }

    interface Presenter extends BasePresenter{
        void login(@NonNull String email, @NonNull String password);
        void showRegister();
    }
}
