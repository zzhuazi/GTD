package com.ljh.gtd3.register;

import android.support.annotation.NonNull;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;

/**
 * Created by Administrator on 2018/3/12.
 */

public interface RegisterContract {

    interface View extends BaseView<Presenter>{
        void showLogin(); //注册成功后返回登录页面
        void showToast(String message);
    }

    interface Presenter extends BasePresenter{
        void sendCode(@NonNull String email);//发送验证码
        void register(@NonNull String eamil, @NonNull String password, @NonNull String code); //注册
    }
}
