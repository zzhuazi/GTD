package com.ljh.gtd3.data.entity;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/3/6.
 */

public class Stuff extends DataSupport {

    @NonNull
    @SerializedName("id")
    private String stuffId;
    @NonNull
    private String name;
    @Nullable
    private String introduce;
    @Nullable
    private String startTime;
    @Nullable
    private String endTime;
    @Nullable
    private Integer priority;
    @NonNull
    private Boolean isFinished;
    @NonNull
    private String userId;
    @NonNull
    private String listId;
    @NonNull
    private String gmtCreate;
    @NonNull
    private String gmtModified;

    public Stuff() {
    }

    public Stuff(@NonNull String stuffId, @NonNull String name, String introduce, String startTime, String endTime, Integer priority, @NonNull Boolean isFinished, @NonNull String userId, @NonNull String listId, @NonNull String gmtCreate, @NonNull String gmtModified) {
        this.stuffId = stuffId;
        this.name = name;
        this.introduce = introduce;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
        this.isFinished = isFinished;
        this.userId = userId;
        this.listId = listId;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
    }

    @NonNull
    public String getStuffId() {
        return stuffId;
    }

    public void setStuffId(@NonNull String stuffId) {
        this.stuffId = stuffId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(@Nullable String introduce) {
        this.introduce = introduce;
    }

    @Nullable
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(@Nullable String startTime) {
        this.startTime = startTime;
    }

    @Nullable
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(@Nullable String endTime) {
        this.endTime = endTime;
    }

    @Nullable
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(@Nullable Integer priority) {
        this.priority = priority;
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
    public String getListId() {
        return listId;
    }

    public void setListId(@NonNull String listId) {
        this.listId = listId;
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
