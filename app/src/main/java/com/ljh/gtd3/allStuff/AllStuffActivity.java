package com.ljh.gtd3.allStuff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.ljh.gtd3.R;
import com.ljh.gtd3.calendar.CalendarActivity;
import com.ljh.gtd3.data.ListsSource.ListsLocalDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.ListsSource.remote.ListsRemoteDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsLocalDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsRepository;
import com.ljh.gtd3.data.StuffsSource.remote.StuffsRemoteDataSource;
import com.ljh.gtd3.data.UsersSource.UsersLocalDataSource;
import com.ljh.gtd3.data.UsersSource.UsersRepository;
import com.ljh.gtd3.data.UsersSource.remote.UsersRemoteDataSource;
import com.ljh.gtd3.listGroup.ListGroupActivity;
import com.ljh.gtd3.notification.NotificationActivity;
import com.ljh.gtd3.user.UserActivity;
import com.ljh.gtd3.util.ActivityUtils;
import com.ljh.gtd3.util.AppExecutors;
import com.ljh.gtd3.util.MyApplication;

public class AllStuffActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private AllStuffPresenter mAllStuffPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_stuff);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            setDrawerContent(navigationView);
            Resources resource=(Resources)getBaseContext().getResources();
            ColorStateList csl=(ColorStateList)resource.getColorStateList(R.color.navigation_menu_item_color);
            navigationView.setItemTextColor(csl);
            navigationView.getMenu().getItem(1).setChecked(true);
        }

        AllStuffFragment allStuffFragment = (AllStuffFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (allStuffFragment == null) {
            //创建fragment
            allStuffFragment = AllStuffFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), allStuffFragment, R.id.contentFrame);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String userId = sharedPreferences.getString("USERID", null);
        AppExecutors appExecutors = new AppExecutors();
        mAllStuffPresenter = new AllStuffPresenter(
                ListsRepository.getInstance(ListsLocalDataSource.getInstance(appExecutors), ListsRemoteDataSource.getInstance(appExecutors)),
                StuffsRepository.getInstance(StuffsLocalDataSource.getInstance(appExecutors), StuffsRemoteDataSource.getInstance(appExecutors)),
                UsersRepository.getInstance(UsersLocalDataSource.getInstance(appExecutors), UsersRemoteDataSource.getInstance(appExecutors),appExecutors),
                allStuffFragment,
                userId);
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
//                        Intent intent5 = new Intent(AllStuffActivity.this, UserActivity.class);
//                        startActivity(intent5);
//                        break;
                    case R.id.calendar_navigation_menu_item:
                        Intent intent2 = new Intent(AllStuffActivity.this, CalendarActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.allStuff_navigation_menu_item:
                        //已经在该页面中
                        break;
                    case R.id.listGroup_navigation_menu_item:
                        Intent intent = new Intent(AllStuffActivity.this, ListGroupActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.notifications_navigation_menu_item:
                        Intent intent1 = new Intent(AllStuffActivity.this, NotificationActivity.class);
                        startActivity(intent1);
                        break;
                }
//                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }
}
