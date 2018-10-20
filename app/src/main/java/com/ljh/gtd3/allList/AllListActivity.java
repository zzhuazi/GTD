package com.ljh.gtd3.allList;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ljh.gtd3.R;
import com.ljh.gtd3.allTask.AllTasksActivity;
import com.ljh.gtd3.allTask.AllTasksFragment;
import com.ljh.gtd3.allTask.AllTasksPresenter;
import com.ljh.gtd3.calendar.CalendarActivity;
import com.ljh.gtd3.data.ListsSource.ListsLocalDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.tasksSource.TasksLocalDataSource;
import com.ljh.gtd3.data.tasksSource.TasksRepository;
import com.ljh.gtd3.util.ActivityUtils;
import com.ljh.gtd3.util.AppExecutors;

/**
 * @author Administrator
 * @date 2018/10/20
 */
public class AllListActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private AllListPresenter mAllListPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

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

       //fragment
        AllListFragment allListFragment = (AllListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(allListFragment == null) {
            allListFragment = AllListFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), allListFragment, R.id.contentFrame);
        }
        //presenter
        AppExecutors appExecutors = new AppExecutors();
        mAllListPresenter = new AllListPresenter(
                ListsRepository.getInstance(ListsLocalDataSource.getInstance(appExecutors)),
                allListFragment);
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
                        Intent intent2 = new Intent(AllListActivity.this, CalendarActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.alltask_navigation_menu_item:
                        Intent intent = new Intent(AllListActivity.this, AllTasksActivity.class);
                        startActivity(intent);
                        break;
                }
//                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }
}
