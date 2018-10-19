package com.ljh.gtd3.data.entity;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

/**
 * Created by Administrator on 2018/3/6.
 */

public class SonTask extends LitePalSupport implements Parcelable {
    @NonNull
    private int id;
    @NonNull
    private String content;
    @NonNull
    private Boolean isFinished;
    @NonNull
    private Task task;

    private Integer task_id;

    @NonNull
    private String gmtCreate;
    @NonNull
    private String  gmtModified;

    public SonTask() {
    }

    public Integer getTask_id() {
        return task_id;
    }

    public void setTask_id(Integer task_id) {
        this.task_id = task_id;
    }

    public SonTask(@NonNull int id, @NonNull String content, @NonNull Boolean isFinished, @NonNull Task task, @NonNull String gmtCreate, @NonNull String gmtModified) {
        this.id = id;
        this.content = content;
        this.isFinished = isFinished;
        this.task = task;
        this.task_id = task.getId();

        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int sonTaskId) {
        this.id = sonTaskId;
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
    public Task getTask() {
        return task;
    }

    public void setTask(@NonNull Task task) {
        this.task = task;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.content);
        dest.writeValue(this.isFinished);
        dest.writeParcelable(this.task, flags);
        dest.writeValue(this.task_id);
        dest.writeString(this.gmtCreate);
        dest.writeString(this.gmtModified);
    }

    protected SonTask(Parcel in) {
        this.id = in.readInt();
        this.content = in.readString();
        this.isFinished = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.task = in.readParcelable(Task.class.getClassLoader());
        this.task_id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.gmtCreate = in.readString();
        this.gmtModified = in.readString();
    }

    public static final Creator<SonTask> CREATOR = new Creator<SonTask>() {
        @Override
        public SonTask createFromParcel(Parcel source) {
            return new SonTask(source);
        }

        @Override
        public SonTask[] newArray(int size) {
            return new SonTask[size];
        }
    };
}
