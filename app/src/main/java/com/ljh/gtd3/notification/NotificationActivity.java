package com.ljh.gtd3.notification;

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

import com.ljh.gtd3.R;
import com.ljh.gtd3.allStuff.AllStuffActivity;
import com.ljh.gtd3.calendar.CalendarActivity;
import com.ljh.gtd3.data.NotificationsSource.NotificationsLocalDataSource;
import com.ljh.gtd3.data.NotificationsSource.NotificationsRepository;
import com.ljh.gtd3.data.NotificationsSource.remote.NotificationsRemoteDataSource;
import com.ljh.gtd3.data.UsersSource.UsersLocalDataSource;
import com.ljh.gtd3.data.UsersSource.UsersRepository;
import com.ljh.gtd3.data.UsersSource.remote.UsersRemoteDataSource;
import com.ljh.gtd3.listGroup.ListGroupActivity;
import com.ljh.gtd3.util.ActivityUtils;
import com.ljh.gtd3.util.AppExecutors;
import com.ljh.gtd3.util.MyApplication;

public class NotificationActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private NotificationPresenter mNotificationPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

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
            navigationView.getMenu().getItem(3).setChecked(true);
        }

        NotificationFragment notificationFragment = (NotificationFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(notificationFragment == null) {
            notificationFragment = NotificationFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), notificationFragment, R.id.contentFrame);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String userId = sharedPreferences.getString("USERID", null);
        AppExecutors appExecutors = new AppExecutors();
        mNotificationPresenter = new NotificationPresenter(
                UsersRepository.getInstance(UsersLocalDataSource.getInstance(appExecutors), UsersRemoteDataSource.getInstance(appExecutors),appExecutors),
                NotificationsRepository.getInstance(NotificationsLocalDataSource.getInstance(appExecutors), NotificationsRemoteDataSource.getInstance(appExecutors)),
                userId,
                notificationFragment);
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
                        Intent intent3 = new Intent(NotificationActivity.this, CalendarActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.allStuff_navigation_menu_item:
                        //已经在该页面中
                        item.setChecked(false);
                        Intent intent1 = new Intent(NotificationActivity.this, AllStuffActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.listGroup_navigation_menu_item:
                        item.setChecked(false);
                        Intent intent = new Intent(NotificationActivity.this, ListGroupActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.notifications_navigation_menu_item:
                        item.setChecked(false);
                        //已经在该页面中
                        break;
                }
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }
}
