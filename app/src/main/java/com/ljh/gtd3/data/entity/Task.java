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

public class Task extends LitePalSupport implements Parcelable {

    @NonNull
    private int id;
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
    private List list;

    private Integer list_id;

    private java.util.List<SonTask> sonTaskList = new ArrayList<SonTask>();

    @NonNull
    private String gmtCreate;
    @NonNull
    private String gmtModified;

    public Task() {
    }

    public Task(@NonNull int id, @NonNull String name, @Nullable String introduce, @Nullable String startTime, @Nullable String endTime, @Nullable Integer priority, @NonNull Boolean isFinished, @NonNull List list, java.util.List<SonTask> sonTaskList, @NonNull String gmtCreate, @NonNull String gmtModified) {
        this.id = id;
        this.name = name;
        this.introduce = introduce;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
        this.isFinished = isFinished;
        this.list = list;
        this.list_id = list.getId();
        this.sonTaskList = sonTaskList;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int taskId) {
        this.id = taskId;
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
    public List getList() {
        return list;
    }

    public void setList(@NonNull List list) {
        this.list = list;
    }

    public Integer getList_id() {
        return list_id;
    }

    public void setList_id(Integer list_id) {
        this.list_id = list_id;
    }
    
    public java.util.List<SonTask> getSonTaskList() {
        return sonTaskList;
    }

    public void setSonTaskList(java.util.List<SonTask> sonTaskList) {
        this.sonTaskList = sonTaskList;
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

    @Override
    public String toString() {
        return "Task{id= " + id +
                ", name= " + name +
                ". introduce= " + introduce +
                ". startTime= " + startTime +
                ". endTime= " + endTime +
                ", priority= " + priority +
                ", isFinished= " + isFinished +
                ", listId= " +
                "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.introduce);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeValue(this.priority);
        dest.writeValue(this.isFinished);
        dest.writeParcelable(this.list, flags);
        dest.writeValue(this.list_id);
        dest.writeTypedList(this.sonTaskList);
        dest.writeString(this.gmtCreate);
        dest.writeString(this.gmtModified);
    }

    protected Task(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.introduce = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.priority = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isFinished = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.list = in.readParcelable(List.class.getClassLoader());
        this.list_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.sonTaskList = in.createTypedArrayList(SonTask.CREATOR);
        this.gmtCreate = in.readString();
        this.gmtModified = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
