package com.ljh.gtd3.data.entity;


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/3/6.
 */

public class ListGroup extends DataSupport{
    @NonNull
    @SerializedName("id")
    private String listGroupId;
    @NonNull
    private String name;
    @NonNull
    private String userId;
    @NonNull
    private String  gmtCreate;
    @NonNull
    private String gmtModified;

    public ListGroup() {
    }

    public ListGroup(@NonNull String listGroupId, @NonNull String name, @NonNull String userId, @NonNull String gmtCreate, @NonNull String gmtModified) {
        this.listGroupId = listGroupId;
        this.name = name;
        this.userId = userId;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
    }

    @NonNull
    public String getListGroupId() {
        return listGroupId;
    }

    public void setListGroupId(@NonNull String listGroupId) {
        this.listGroupId = listGroupId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
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
