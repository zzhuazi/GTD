package com.ljh.gtd3.allTask;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.ljh.gtd3.R;
import com.ljh.gtd3.addList.AddListActivity;
import com.ljh.gtd3.addTask.AddTaskActivity;
import com.ljh.gtd3.allList.AllListActivity;
import com.ljh.gtd3.calendar.CalendarActivity;
import com.ljh.gtd3.data.ListsSource.ListsLocalDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.tasksSource.TasksLocalDataSource;
import com.ljh.gtd3.data.tasksSource.TasksRepository;
import com.ljh.gtd3.util.ActivityUtils;
import com.ljh.gtd3.util.AppExecutors;

public class AllTasksActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private AllTasksPresenter mAllTasksPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_task);

        // Set up the toolbar_add_task.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        //抽屉菜单
        mDrawerLayout = findViewById(R.id.drawer_layout);
        //导航
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            setDrawerContent(navigationView);
            Resources resource=(Resources)getBaseContext().getResources();
            ColorStateList csl=(ColorStateList)resource.getColorStateList(R.color.navigation_menu_item_color);
            navigationView.setItemTextColor(csl);
            navigationView.getMenu().getItem(0).setChecked(true);
        }

        AllTasksFragment allTasksFragment = (AllTasksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (allTasksFragment == null) {
            //创建fragment
            allTasksFragment = AllTasksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), allTasksFragment, R.id.contentFrame);
        }

        AppExecutors appExecutors = new AppExecutors();
        mAllTasksPresenter = new AllTasksPresenter(
                ListsRepository.getInstance(ListsLocalDataSource.getInstance(appExecutors)),
                TasksRepository.getInstance(TasksLocalDataSource.getInstance(appExecutors)),
                allTasksFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
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
                        Intent intent2 = new Intent(AllTasksActivity.this, CalendarActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.alltask_navigation_menu_item:
                        //已经在该页面中
                        break;
                    case R.id.list_navigation_menu_item:
                        Intent intent = new Intent(AllTasksActivity.this, AllListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.add_list_navigation_menu_item:
                        Intent intent4 = new Intent(AllTasksActivity.this, AddListActivity.class);
                        startActivity(intent4);
                        break;
                }
//                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            long secondTime = System.currentTimeMillis();
            if(secondTime - firstTime > 2000) {
                Toast.makeText(AllTasksActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            }else {
                System.exit(0);
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
