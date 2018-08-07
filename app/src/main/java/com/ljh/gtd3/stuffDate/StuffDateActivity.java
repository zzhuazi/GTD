package com.ljh.gtd3.stuffDate;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ljh.gtd3.R;
import com.ljh.gtd3.util.ActivityUtils;

public class StuffDateActivity extends AppCompatActivity {
    private StuffDatePresenter mStuffDatePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuff_date);
        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        StuffDateFragment stuffDateFragment = (StuffDateFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(stuffDateFragment == null) {
            stuffDateFragment = StuffDateFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), stuffDateFragment, R.id.contentFrame);
        }

        mStuffDatePresenter = new StuffDatePresenter(stuffDateFragment);
    }

}
