package com.ljh.gtd3.data.entity;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/6.
 */

public class List extends LitePalSupport implements Parcelable {
    @NonNull
    private int id;
    @NonNull
    private String name;
    @Nullable
    private Integer priority;
    @NonNull
    private Integer tasks;
    @NonNull
    private String  gmtCreate;
    @NonNull
    private String gmtModified;

    private java.util.List<Task> taskList = new ArrayList<Task>();

    public List() {
        tasks = taskList.size();
    }

    public List(@NonNull int id, @NonNull String name, @Nullable Integer priority, @NonNull String gmtCreate, @NonNull String gmtModified, java.util.List<Task> taskList) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.tasks = taskList.size();
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        this.taskList = taskList;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int listId) {
        this.id = listId;
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
    public Integer getTasks() {
        return tasks;
    }

    public void setTasks(@NonNull Integer tasks) {
        this.tasks = tasks;
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

    public java.util.List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(java.util.List<Task> taskList) {
        this.taskList = taskList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeValue(this.priority);
        dest.writeValue(this.tasks);
        dest.writeString(this.gmtCreate);
        dest.writeString(this.gmtModified);
        dest.writeTypedList(this.taskList);
    }

    protected List(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.priority = (Integer) in.readValue(Integer.class.getClassLoader());
        this.tasks = (Integer) in.readValue(Integer.class.getClassLoader());
        this.gmtCreate = in.readString();
        this.gmtModified = in.readString();
        this.taskList = in.createTypedArrayList(Task.CREATOR);
    }

    public static final Parcelable.Creator<List> CREATOR = new Parcelable.Creator<List>() {
        @Override
        public List createFromParcel(Parcel source) {
            return new List(source);
        }

        @Override
        public List[] newArray(int size) {
            return new List[size];
        }
    };
}
