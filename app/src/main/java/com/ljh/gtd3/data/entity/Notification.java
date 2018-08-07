package com.ljh.gtd3.data.entity;


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/3/6.
 */

public class Notification extends DataSupport{
    @NonNull
    @SerializedName("id")
    private Integer id;
    @NonNull
    private String content;
    @NonNull
    private String time;
    @NonNull
    private Boolean isRead;
    @NonNull
    private String userId;
    @NonNull
    private String gmtCreate;
    @NonNull
    private String gmtModified;

    public Notification() {
    }

    public Notification(@NonNull Integer id, @NonNull String content, @NonNull String time, @NonNull Boolean isRead, @NonNull String userId, @NonNull String gmtCreate, @NonNull String gmtModified) {
        this.id = id;
        this.content = content;
        this.time = time;
        this.isRead = isRead;
        this.userId = userId;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @NonNull
    public String getTime() {
        return time;
    }

    public void setTime(@NonNull String time) {
        this.time = time;
    }

    @NonNull
    public Boolean getRead() {
        return isRead;
    }

    public void setRead(@NonNull Boolean read) {
        isRead = read;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
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
