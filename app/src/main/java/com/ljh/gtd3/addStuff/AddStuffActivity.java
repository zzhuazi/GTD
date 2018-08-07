package com.ljh.gtd3.addStuff;

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
import android.view.View;
import android.widget.TextView;

import com.ljh.gtd3.R;
import com.ljh.gtd3.addList.AddListActivity;
import com.ljh.gtd3.allStuff.AllStuffActivity;
import com.ljh.gtd3.calendar.CalendarActivity;
import com.ljh.gtd3.data.AffairSource.AffairsLocalDataSource;
import com.ljh.gtd3.data.AffairSource.AffairsRepository;
import com.ljh.gtd3.data.AffairSource.remote.AffairsRemoteDataSource;
import com.ljh.gtd3.data.ListsSource.ListsLocalDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.ListsSource.remote.ListsRemoteDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsLocalDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsRepository;
import com.ljh.gtd3.data.StuffsSource.remote.StuffsRemoteDataSource;
import com.ljh.gtd3.listGroup.ListGroupActivity;
import com.ljh.gtd3.notification.NotificationActivity;
import com.ljh.gtd3.util.ActivityUtils;
import com.ljh.gtd3.util.AppExecutors;
import com.ljh.gtd3.util.MyApplication;

public class AddStuffActivity extends AppCompatActivity {
    private AddStuffPresenter mAddStuffPresenter;
    private DrawerLayout mDrawerLayout;
    private TextView mListNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stuff);

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

        AddStuffFragment addStuffFragment = (AddStuffFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(addStuffFragment == null) {
            addStuffFragment = AddStuffFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),addStuffFragment, R.id.contentFrame);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String userId = sharedPreferences.getString("USERID", null);
        AppExecutors appExecutors = new AppExecutors();
        mAddStuffPresenter = new AddStuffPresenter(
                ListsRepository.getInstance(ListsLocalDataSource.getInstance(appExecutors), ListsRemoteDataSource.getInstance(appExecutors)),
                StuffsRepository.getInstance(StuffsLocalDataSource.getInstance(appExecutors), StuffsRemoteDataSource.getInstance(appExecutors)),
                AffairsRepository.getInstance(AffairsLocalDataSource.getInstance(appExecutors), AffairsRemoteDataSource.getInstance(appExecutors)),
                addStuffFragment,
                userId);
        mListNameTv = findViewById(R.id.tv_add_stuff_list_name);
        mListNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //加载dialog对话框
                mAddStuffPresenter.showLists();
            }
        });
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
                        Intent intent3 = new Intent(AddStuffActivity.this, CalendarActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.allStuff_navigation_menu_item:
                        Intent intent2 = new Intent(AddStuffActivity.this, AllStuffActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.listGroup_navigation_menu_item:
                        Intent intent = new Intent(AddStuffActivity.this, ListGroupActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.notifications_navigation_menu_item:
                        Intent intent1 = new Intent(AddStuffActivity.this, NotificationActivity.class);
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
