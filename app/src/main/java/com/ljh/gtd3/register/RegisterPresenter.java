package com.ljh.gtd3.register;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.UsersSource.UsersDataSource;
import com.ljh.gtd3.data.UsersSource.UsersRepository;
import com.ljh.gtd3.data.entity.User;
import com.ljh.gtd3.util.MD5Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Administrator on 2018/3/12.
 */

public class RegisterPresenter implements RegisterContract.Presenter{

    private final UsersRepository mUsersRepository;

    private final RegisterContract.View mRegisterView;

    public RegisterPresenter(UsersRepository usersRepository, RegisterContract.View registerView) {
        this.mUsersRepository = usersRepository;
        this.mRegisterView = registerView;

        mRegisterView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void sendCode(@NonNull String email) {
        try{
            if(email.isEmpty()) {
                mRegisterView.showToast("请输入邮箱");
            }else {
                mUsersRepository.sendCode(email, new UsersDataSource.SendRequestCallBack() {
                    @Override
                    public void onRequestSuccess(String message) {
                        mRegisterView.showToast(message);
                    }

                    @Override
                    public void onRequestFail(String message) {
                        mRegisterView.showToast(message);
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
            mRegisterView.showToast("未知错误");
        }
    }

    @Override
    public void register(@NonNull String email, @NonNull String password, @NonNull String code) {
        try{
            if(email.isEmpty() || password.isEmpty() || code.isEmpty()) {
                mRegisterView.showToast("请输入邮箱、密码或验证码");
            }else if(!email.isEmpty() && !password.isEmpty() && !code.isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                User user = new User();
                user.setUserId(UUID.randomUUID().toString());
                user.setEmail(email);
                user.setName(email);
                user.setAvatar("person.png");
                user.setPassword(MD5Util.encrypt(password));
                user.setGmtCreate(simpleDateFormat.format(new Date()));
                user.setGmtModified(simpleDateFormat.format(new Date()));
                mUsersRepository.register(user, code, new UsersDataSource.SendRequestCallBack() {
                    @Override
                    public void onRequestSuccess(String message) {
                        //注册成功，跳转到登录页面
                        mRegisterView.showToast(message);
                        mRegisterView.showLogin();
                    }

                    @Override
                    public void onRequestFail(String message) {
                        //注册失败，显示注册失败信息
                        mRegisterView.showToast(message);
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
            mRegisterView.showToast("未知错误");
        }
    }
}
