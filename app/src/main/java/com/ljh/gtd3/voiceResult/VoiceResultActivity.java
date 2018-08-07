package com.ljh.gtd3.voiceResult;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ljh.gtd3.R;
import com.ljh.gtd3.data.StuffsSource.StuffsLocalDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsRepository;
import com.ljh.gtd3.data.StuffsSource.remote.StuffsRemoteDataSource;
import com.ljh.gtd3.util.ActivityUtils;
import com.ljh.gtd3.util.AppExecutors;
import com.ljh.gtd3.util.MyApplication;

public class VoiceResultActivity extends AppCompatActivity {
    private VoiceResultPresenter mVoiceResultPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_result);
        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        VoiceResultFragment voiceResultFragment = (VoiceResultFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(voiceResultFragment == null) {
            voiceResultFragment = VoiceResultFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), voiceResultFragment, R.id.contentFrame);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String userId = sharedPreferences.getString("USERID", null);
        String defaultListId = sharedPreferences.getString("DEFAULTLISTID", null);
        AppExecutors appExecutors = new AppExecutors();

        mVoiceResultPresenter = new VoiceResultPresenter(
                StuffsRepository.getInstance(StuffsLocalDataSource.getInstance(appExecutors), StuffsRemoteDataSource.getInstance(appExecutors)),
                userId,
                defaultListId,
                voiceResultFragment
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
