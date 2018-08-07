package com.ljh.gtd3.voiceResult;

import com.ljh.gtd3.data.StuffsSource.StuffsRepository;
import com.ljh.gtd3.data.entity.Stuff;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VoiceResultPresenter implements VoiceResultContract.Presenter {
    private final StuffsRepository mStuffsRepository;

    private String mUserId;

    private String mDefaultListId;

    private VoiceResultContract.View mVoiceResultView;

    public VoiceResultPresenter(StuffsRepository mStuffsRepository, String mUserId, String mDefaultListId, VoiceResultContract.View mVoiceResultView) {
        this.mStuffsRepository = mStuffsRepository;
        this.mUserId = mUserId;
        this.mDefaultListId = mDefaultListId;
        this.mVoiceResultView = mVoiceResultView;
        mVoiceResultView.setPresenter(this);
    }

    @Override
    public void addStuff(String result) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Stuff stuff = new Stuff();
        stuff.setUserId(mUserId);
        stuff.setListId(mDefaultListId);
        stuff.setFinished(false);
        stuff.setPriority(0);
        stuff.setName(result);
        stuff.setGmtCreate(simpleDateFormat.format(new Date()));
        stuff.setGmtModified(simpleDateFormat.format(new Date()));
        mStuffsRepository.addStuff(stuff);
        mVoiceResultView.toAllStuffActivity();
    }

    @Override
    public void start() {
        mVoiceResultView.loadResult();
    }
}
