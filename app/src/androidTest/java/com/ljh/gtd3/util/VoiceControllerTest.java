package com.ljh.gtd3.util;

import android.support.test.runner.AndroidJUnit4;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class VoiceControllerTest {
    VoiceController voiceController;
    Context context;
    String userId;
    String text;
    @Before
    public void setUp() throws Exception {
        voiceController = new VoiceController();
        context = InstrumentationRegistry.getTargetContext();
        userId = "139f92d5-eb52-469b-9e9a-6e0862621520";
        text = "2018年5月1日劳动节";
    }

    @Test
    public void start() {
        voiceController.start(context,userId,text);
    }
}