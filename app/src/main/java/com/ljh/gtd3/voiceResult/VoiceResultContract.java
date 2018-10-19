package com.ljh.gtd3.voiceResult;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;

public interface VoiceResultContract {
    interface View extends BaseView<Presenter>{
        void loadResult();  //加载voice返回的未处理的语音文本信息
        void toAllStuffActivity();
    }
    interface Presenter extends BasePresenter{
        void addStuff(String result);
    }
}