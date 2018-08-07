package com.ljh.gtd3.data.UsersSource;

import com.ljh.gtd3.data.entity.User;
import com.ljh.gtd3.util.SingleExecutors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.litepal.crud.DataSupport;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by Administrator on 2018/3/11.
 */
public class UsersLocalDataSourceTest {
    private UsersLocalDataSource mLocalDataSource;

    @Before
    public void setup(){
        mLocalDataSource = UsersLocalDataSource.getInstance(new SingleExecutors());
    }

    @After
    public void cleanUP(){
        UsersLocalDataSource.destroyInstance();
    }

    @Test
    public void getUser() throws Exception {

    }

    @Test
    public void saveUser() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setAvatar("avatar.jpg");
        user.setEmail("7777@qq.com");
        user.setName("ha");
        user.setPassword("123");
        user.setIntroduce("12113");
        user.setGmtCreate(simpleDateFormat.format(new Date()));
        user.setGmtModified(simpleDateFormat.format(new Date()));
//        mLocalDataSource.s(user);
        assertThat(DataSupport.count(User.class), is(1));
    }

    @Test
    public void updateUser() throws Exception {
        User user = new User();
        user.setEmail("7777@qq.com");
        user.setAvatar("fuck.jpg");
        mLocalDataSource.updateUser(user);
    }

    @Test
    public void deleteUser() throws Exception {
    }

}