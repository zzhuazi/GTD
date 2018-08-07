package com.ljh.gtd3.data.entity;


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/3/6.
 */

public class Affair extends DataSupport{
    @NonNull
    @SerializedName("id")
    private String affairId;
    @NonNull
    private String content;
    @NonNull
    private Boolean isFinished;
    @NonNull
    private String userId;
    @NonNull
    private String stuffId;
    @NonNull
    private String gmtCreate;
    @NonNull
    private String  gmtModified;

    public Affair(@NonNull String affairId, @NonNull String content, @NonNull Boolean isFinished, @NonNull String userId, @NonNull String stuffId, @NonNull String gmtCreate, @NonNull String gmtModified) {
        this.affairId = affairId;
        this.content = content;
        this.isFinished = isFinished;
        this.userId = userId;
        this.stuffId = stuffId;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
    }

    public Affair() {

    }

    @NonNull
    public String getAffairId() {
        return affairId;
    }

    public void setAffairId(@NonNull String affairId) {
        this.affairId = affairId;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @NonNull
    public Boolean getFinished() {
        return isFinished;
    }

    public void setFinished(@NonNull Boolean finished) {
        isFinished = finished;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getStuffId() {
        return stuffId;
    }

    public void setStuffId(@NonNull String stuffId) {
        this.stuffId = stuffId;
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
