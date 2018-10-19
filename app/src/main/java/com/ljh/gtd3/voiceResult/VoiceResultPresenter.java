package com.ljh.gtd3.voiceResult;

import com.ljh.gtd3.data.tasksSource.TasksRepository;
import com.ljh.gtd3.data.entity.Task;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VoiceResultPresenter implements VoiceResultContract.Presenter {
    private final TasksRepository mStuffsRepository;

    private String mUserId;

    private String mDefaultListId;

    private VoiceResultContract.View mVoiceResultView;

    public VoiceResultPresenter(TasksRepository mStuffsRepository, String mUserId, String mDefaultListId, VoiceResultContract.View mVoiceResultView) {
        this.mStuffsRepository = mStuffsRepository;
        this.mUserId = mUserId;
        this.mDefaultListId = mDefaultListId;
        this.mVoiceResultView = mVoiceResultView;
        mVoiceResultView.setPresenter(this);
    }

    @Override
    public void addStuff(String result) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Task task = new Task();
//        task.setUserId(mUserId);
//        task.setId(mDefaultListId);
//        task.setFinished(false);
//        task.setPriority(0);
//        task.setName(result);
//        task.setGmtCreate(simpleDateFormat.format(new Date()));
//        task.setGmtModified(simpleDateFormat.format(new Date()));
//        mStuffsRepository.addStuff(task);
//        mVoiceResultView.toAllStuffActivity();
    }

    @Override
    public void start() {
        mVoiceResultView.loadResult();
    }
}
