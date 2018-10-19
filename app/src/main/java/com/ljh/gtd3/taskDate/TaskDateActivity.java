package com.ljh.gtd3.taskDate;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.ljh.gtd3.R;
import com.ljh.gtd3.util.ActivityUtils;

public class TaskDateActivity extends AppCompatActivity {
    private TaskDatePresenter mTaskDatePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_date);
        // Set up the toolbar_add_task.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        TaskDateFragment taskDateFragment = (TaskDateFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(taskDateFragment == null) {
            taskDateFragment = TaskDateFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), taskDateFragment, R.id.contentFrame);
        }

        mTaskDatePresenter = new TaskDatePresenter(taskDateFragment);
    }

}
