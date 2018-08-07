package com.ljh.gtd3.data.entity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;


/**
 * Created by Administrator on 2018/3/6.
 */

public class User extends DataSupport{

    @NonNull
    @SerializedName("id")
    private String userId;
    @Nullable
    private String name;
    @Nullable
    private String avatar;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @Nullable
    private String sex;
    @Nullable
    private String introduce;
    @Nullable
    private Integer phone;
    @NonNull
    private String gmtCreate;
    @NonNull
    private String gmtModified;

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(@Nullable String avatar) {
        this.avatar = avatar;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    @Nullable
    public String getSex() {
        return sex;
    }

    public void setSex(@Nullable String sex) {
        this.sex = sex;
    }

    @Nullable
    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(@Nullable String introduce) {
        this.introduce = introduce;
    }

    @Nullable
    public Integer getPhone() {
        return phone;
    }

    public void setPhone(@Nullable Integer phone) {
        this.phone = phone;
    }

    @NonNull
    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(@NonNull String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @NonNull
    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(@NonNull String gmtModified) {
        this.gmtModified = gmtModified;
    }
}
