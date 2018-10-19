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
import android.view.MenuItem;

import com.ljh.gtd3.R;
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
            navigationView.getMenu().getItem(1).setChecked(true);
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
//                    case R.id.rv_user_setting:
//                        Intent intent5 = new Intent(AllTasksActivity.this, UserActivity.class);
//                        startActivity(intent5);
//                        break;
                    case R.id.calendar_navigation_menu_item:
                        Intent intent2 = new Intent(AllTasksActivity.this, CalendarActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.allStuff_navigation_menu_item:
                        //已经在该页面中
                        break;
//                    case R.id.listGroup_navigation_menu_item:
//                        Intent intent = new Intent(AllTasksActivity.this, ListGroupActivity.class);
//                        startActivity(intent);
//                        break;
//                    case R.id.notifications_navigation_menu_item:
//                        Intent intent1 = new Intent(AllTasksActivity.this, NotificationActivity.class);
//                        startActivity(intent1);
//                        break;
                }
//                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }
}
