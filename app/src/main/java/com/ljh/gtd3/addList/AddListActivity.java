package com.ljh.gtd3.addList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ljh.gtd3.R;
import com.ljh.gtd3.allStuff.AllStuffActivity;
import com.ljh.gtd3.calendar.CalendarActivity;
import com.ljh.gtd3.data.ListGroupsSource.ListGroupsLocalDataSource;
import com.ljh.gtd3.data.ListGroupsSource.ListGroupsRepository;
import com.ljh.gtd3.data.ListGroupsSource.remote.ListGroupsRemoteDataSource;
import com.ljh.gtd3.data.ListsSource.ListsLocalDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.ListsSource.remote.ListsRemoteDataSource;
import com.ljh.gtd3.listGroup.ListGroupActivity;
import com.ljh.gtd3.notification.NotificationActivity;
import com.ljh.gtd3.util.ActivityUtils;
import com.ljh.gtd3.util.AppExecutors;
import com.ljh.gtd3.util.MyApplication;

public class AddListActivity extends AppCompatActivity {
    private AddListPresenter mAddListPresenter;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        // Set up the toolbar.
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

        //fragment
        AddListFragment addListFragment = (AddListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(addListFragment == null) {
            addListFragment = AddListFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), addListFragment, R.id.contentFrame);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String userId = sharedPreferences.getString("USERID", null);
        String listid = getIntent().getStringExtra("LISTID");
        AppExecutors appExecutors = new AppExecutors();
        //presenter
        mAddListPresenter = new AddListPresenter(
                ListsRepository.getInstance(ListsLocalDataSource.getInstance(appExecutors), ListsRemoteDataSource.getInstance(appExecutors)),
                ListGroupsRepository.getInstance(ListGroupsLocalDataSource.getInstance(appExecutors), ListGroupsRemoteDataSource.getInstance(appExecutors)),
                addListFragment,userId, listid);
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
                        Intent intent3 = new Intent(AddListActivity.this, CalendarActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.allStuff_navigation_menu_item:
                        Intent intent2 = new Intent(AddListActivity.this, AllStuffActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.listGroup_navigation_menu_item:
                        Intent intent = new Intent(AddListActivity.this, ListGroupActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.notifications_navigation_menu_item:
                        Intent intent1 = new Intent(AddListActivity.this, NotificationActivity.class);
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
