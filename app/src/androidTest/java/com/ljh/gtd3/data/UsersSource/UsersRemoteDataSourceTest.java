package com.ljh.gtd3.data.UsersSource;

import com.ljh.gtd3.data.UsersSource.remote.UsersRemoteDataSource;
import com.ljh.gtd3.util.SingleExecutors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

/**
 * Created by Administrator on 2018/3/12.
 */
public class UsersRemoteDataSourceTest {

    private UsersRemoteDataSource usersRemoteDataSource;

    @Before
    public void setup(){
        usersRemoteDataSource = UsersRemoteDataSource.getInstance(new SingleExecutors());
    }

    @After
    public void cleanUP(){
        UsersRemoteDataSource.destroyInstance();
    }

    @Test
    public void getUser() throws Exception {
//        String userId = "123";
//        usersRemoteDataSource.getUser(userId, new UsersDataSource.GetUserCallBack() {
//            @Override
//            public void onUserLoaded(User user) {
//                assertNull(user);
//                assertThat(user.getUserId(), is("134"));
//            }
//
//            @Override
//            public void onDataNotAvailable() {
//                fail("网络错误");
//            }
//        });
    }

}