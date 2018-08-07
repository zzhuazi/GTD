package com.ljh.gtd3.data.entity;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/3/6.
 */

public class List extends DataSupport{
    @NonNull
    @SerializedName("id")
    private String listId;
    @NonNull
    private String name;
    @Nullable
    private Integer priority;
    @NonNull
    private Integer stuffs;
    @NonNull
    private String userId;
    @Nullable
    private String listGroupId;
    @NonNull
    private String  gmtCreate;
    @NonNull
    private String gmtModified;

    public List(@NonNull String listId, @NonNull String name, Integer priority, @NonNull Integer stuffs, @NonNull String userId, String listGroupId, @NonNull String gmtCreate, @NonNull String gmtModified) {
        this.listId = listId;
        this.name = name;
        this.priority = priority;
        this.stuffs = stuffs;
        this.userId = userId;
        this.listGroupId = listGroupId;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
    }

    public List() {

    }

    @NonNull
    public String getListId() {
        return listId;
    }

    public void setListId(@NonNull String listId) {
        this.listId = listId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(@Nullable Integer priority) {
        this.priority = priority;
    }

    @NonNull
    public Integer getStuffs() {
        return stuffs;
    }

    public void setStuffs(@NonNull Integer stuffs) {
        this.stuffs = stuffs;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @Nullable
    public String getListGroupId() {
        return listGroupId;
    }

    public void setListGroupId(@Nullable String listGroupId) {
        this.listGroupId = listGroupId;
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
