package com.ljh.gtd3.taskDetail;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ljh.gtd3.R;
import com.ljh.gtd3.addList.AddListActivity;
import com.ljh.gtd3.allList.AllListActivity;
import com.ljh.gtd3.allTask.AllTasksActivity;
import com.ljh.gtd3.calendar.CalendarActivity;
import com.ljh.gtd3.data.entity.Task;
import com.ljh.gtd3.data.sonTaskSource.SonTasksLocalDataSource;
import com.ljh.gtd3.data.sonTaskSource.SonTasksRepository;
import com.ljh.gtd3.data.ListsSource.ListsLocalDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.tasksSource.TasksLocalDataSource;
import com.ljh.gtd3.data.tasksSource.TasksRepository;
import com.ljh.gtd3.listDetail.ListDetailActicity;
import com.ljh.gtd3.util.ActivityUtils;
import com.ljh.gtd3.util.AppExecutors;

public class TaskDetailActivity extends AppCompatActivity {
    public static final String TAG = TaskDetailActivity.class.getSimpleName();
    private TaskDetailPresenter mTaskDetailPresenter;
    private DrawerLayout mDrawerLayout;
    private TextView mListNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        // Set up the toolbar_add_task.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            setDrawerContent(navigationView);
        }

        //Fragment
        TaskDetailFragment mTaskDetailFragment;
        mTaskDetailFragment = (TaskDetailFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(mTaskDetailFragment == null) {
            mTaskDetailFragment = TaskDetailFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mTaskDetailFragment, R.id.contentFrame);
        }

        AppExecutors appExecutors = new AppExecutors();

        Task task = getIntent().getParcelableExtra("TASK");
        //presenter
        mTaskDetailPresenter = new TaskDetailPresenter(
                ListsRepository.getInstance(ListsLocalDataSource.getInstance(appExecutors)),
                TasksRepository.getInstance(TasksLocalDataSource.getInstance(appExecutors)),
                SonTasksRepository.getInstance(SonTasksLocalDataSource.getInstance(appExecutors)),
                mTaskDetailFragment, task);

        //清单名点击事件
        mListNameTv = findViewById(R.id.tv_add_task_list_name);
        mListNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //加载dialog对话框
                mTaskDetailPresenter.showLists();
            }
        });
    }

    //通知栏多个任务点击时，修改task的内容
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        String taskString = (String) bundle.get("TASK");
        Task task = new Gson().fromJson(taskString, Task.class);
        Log.d(TAG, "onNewIntent: task.id = " + task.getId() );
        mTaskDetailPresenter.replaceTask(task);
        mTaskDetailPresenter.start();
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

    private void setDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.calendar_navigation_menu_item:
                        Intent intent3 = new Intent(TaskDetailActivity.this, CalendarActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.alltask_navigation_menu_item:
                        Intent intent2 = new Intent(TaskDetailActivity.this, AllTasksActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.list_navigation_menu_item:
                        Intent intent = new Intent(TaskDetailActivity.this, AllListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.add_list_navigation_menu_item:
                        Intent intent4 = new Intent(TaskDetailActivity.this, AddListActivity.class);
                        startActivity(intent4);
                        break;
                }
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }
}
