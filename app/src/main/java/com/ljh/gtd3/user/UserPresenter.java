package com.ljh.gtd3.user;

import com.ljh.gtd3.data.UsersSource.UsersDataSource;
import com.ljh.gtd3.data.UsersSource.UsersRepository;
import com.ljh.gtd3.data.entity.User;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/4/2.
 */

public class UserPresenter implements UserContract.Presenter{
    private UserContract.View mUserContractView;
    private UsersRepository mUsersRepository;
    private String mUserId;

    public UserPresenter(UserContract.View mUserContractView, UsersRepository mUsersRepository, String mUserId) {
        this.mUserContractView = mUserContractView;
        this.mUsersRepository = mUsersRepository;
        this.mUserId = mUserId;
        mUserContractView.setPresenter(this);
    }

    @Override
    public void loadUser() {
        mUsersRepository.getUser(mUserId, new UsersDataSource.GetUserCallBack() {
            @Override
            public void onUserLoaded(User user) {
                mUserContractView.showUser(user);
            }

            @Override
            public void onDataNotAvailable(String message) {

            }
        });
    }

    @Override
    public void updateUser(User user) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        user.setUserId(mUserId);
        user.setGmtModified(simpleDateFormat.format(new Date()));
        mUsersRepository.updateUser(user);
    }

    @Override
    public void loadUser(String userId) {
        mUsersRepository.getUser(userId, new UsersDataSource.GetUserCallBack() {
            @Override
            public void onUserLoaded(User user) {
                mUserContractView.loadUser(user);
            }

            @Override
            public void onDataNotAvailable(String message) {

            }
        });
    }

    @Override
    public void start() {
        loadUser();
        loadUser(mUserId);
    }
}
