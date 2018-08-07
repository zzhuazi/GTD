package com.ljh.gtd3.login;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ljh.gtd3.data.AffairSource.AffairsDataSource;
import com.ljh.gtd3.data.AffairSource.AffairsLocalDataSource;
import com.ljh.gtd3.data.AffairSource.AffairsRepository;
import com.ljh.gtd3.data.AffairSource.remote.AffairsRemoteDataSource;
import com.ljh.gtd3.data.ListGroupsSource.ListGroupsDataSource;
import com.ljh.gtd3.data.ListGroupsSource.ListGroupsLocalDataSource;
import com.ljh.gtd3.data.ListGroupsSource.ListGroupsRepository;
import com.ljh.gtd3.data.ListGroupsSource.remote.ListGroupsRemoteDataSource;
import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsLocalDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.ListsSource.remote.ListsRemoteDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsLocalDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsRepository;
import com.ljh.gtd3.data.StuffsSource.remote.StuffsRemoteDataSource;
import com.ljh.gtd3.data.UsersSource.UsersDataSource;
import com.ljh.gtd3.data.UsersSource.UsersRepository;
import com.ljh.gtd3.data.entity.Affair;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.ListGroup;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.data.entity.User;
import com.ljh.gtd3.util.AppExecutors;
import com.ljh.gtd3.util.MD5Util;
import com.ljh.gtd3.util.MyApplication;

/**
 * Created by Administrator on 2018/3/12.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = LoginPresenter.class.getSimpleName();
    private final UsersRepository mUserRepository;

    private final LoginContract.View mLoginView;

    public LoginPresenter(@NonNull UsersRepository usersRepository, @NonNull LoginContract.View loginView) {
        mUserRepository = usersRepository;
        mLoginView = loginView;

        mLoginView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void login(@NonNull String email, @NonNull String password) {
        if (email.isEmpty() && password.isEmpty()) {
            mLoginView.showToast("请输入邮箱和密码");
        } else if (email.isEmpty()) {
            mLoginView.showToast("请输入邮箱");
        } else if (password.isEmpty()) {
            mLoginView.showToast("请输入密码");
        } else {
            mUserRepository.login(email, MD5Util.encrypt(password), new UsersDataSource.GetUserCallBack() {
                @Override
                public void onUserLoaded(final User user) {
                    Log.d(TAG, "onUserLoaded: 登录成功");
                    //登录成功,加载userID信息
                    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("USERID", user.getUserId()).apply();
                    //登录后获取清单及材料信息
                    final AppExecutors appExecutors = new AppExecutors();
                    final ListGroupsRepository listGroupsRepository = ListGroupsRepository.getInstance(ListGroupsLocalDataSource.getInstance(appExecutors), ListGroupsRemoteDataSource.getInstance(appExecutors));
                    listGroupsRepository.getListGroupsFromRemoteDateSource(user.getUserId(), new ListGroupsDataSource.GetListGroupsCallBack() {
                        @Override
                        public void onListGroupsLoaded(java.util.List<ListGroup> listGroups, String message) {

                        }

                        @Override
                        public void onListGroupsFail(String message) {

                        }
                    });
                    final ListsRepository listsRepository = ListsRepository.getInstance(ListsLocalDataSource.getInstance(appExecutors), ListsRemoteDataSource.getInstance(appExecutors));
                    listsRepository.getListsFromRemoteDataSource(user.getUserId(), new ListsDataSource.GetListsCallBack() {
                        @Override
                        public void onListsLoaded(java.util.List<List> lists, String message) {
                            editor.putString("LISTID", lists.get(0).getListId());
                            for (com.ljh.gtd3.data.entity.List list : lists) {
                                if (list.getName().equals("收集箱")) {
                                    sharedPreferences.edit().putString("DEFAULTLISTID", list.getListId()).apply();
                                }
                            }
                        }

                        @Override
                        public void onListsFail(String message) {

                        }
                    });
                    final StuffsRepository stuffsRepository = StuffsRepository.getInstance(StuffsLocalDataSource.getInstance(appExecutors), StuffsRemoteDataSource.getInstance(appExecutors));
                    final AffairsRepository affairsRepository = AffairsRepository.getInstance(AffairsLocalDataSource.getInstance(appExecutors), AffairsRemoteDataSource.getInstance(appExecutors));
                    stuffsRepository.getStuffsFromRemoteDateSource(user.getUserId(), new StuffsDataSource.GetStuffsCallBack() {
                        @Override
                        public void onStuffsLoaded(java.util.List<Stuff> stuffs, String message) {
                            for (Stuff stuff: stuffs){
                                affairsRepository.getAffairsFromRemoteDataSource(stuff.getStuffId(), new AffairsDataSource.GetAffairsCallBack() {
                                    @Override
                                    public void onAffairsLoaded(java.util.List<Affair> affairs, String message) {

                                    }

                                    @Override
                                    public void onAffairsFail(String message) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onStuffsFail(String message) {

                        }
                    });
                    //登录成功，调转到所有材料页面
                    mLoginView.showAllStuff(user.getUserId());
                }

                @Override
                public void onDataNotAvailable(String message) {
                    Log.d(TAG, "onDataNotAvailable: 登录失败" + message);
                    mLoginView.showToast(message);
                }
            });
        }
    }

    @Override
    public void showRegister() {
        mLoginView.showRegister();
    }
}
