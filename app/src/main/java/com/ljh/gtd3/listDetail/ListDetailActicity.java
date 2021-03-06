package com.ljh.gtd3.listDetail;

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
import com.ljh.gtd3.addStuff.AddStuffActivity;
import com.ljh.gtd3.allStuff.AllStuffActivity;
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
import com.ljh.gtd3.util.ActivityUtils;
import com.ljh.gtd3.util.AppExecutors;
import com.ljh.gtd3.util.MyApplication;

public class ListDetailActicity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ListDetailContract.Presenter mListDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail_acticity);

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
        }

        ListDetailFragment listDetailFragment = (ListDetailFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(listDetailFragment == null) {
            listDetailFragment = ListDetailFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), listDetailFragment, R.id.contentFrame);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String userId = sharedPreferences.getString("USERID", null);
        AppExecutors appExecutors = new AppExecutors();
        String listId = getIntent().getStringExtra("LISTID");

        mListDetailPresenter = new ListDetailPresenter(
                UsersRepository.getInstance(UsersLocalDataSource.getInstance(appExecutors), UsersRemoteDataSource.getInstance(appExecutors),appExecutors),
                ListsRepository.getInstance(ListsLocalDataSource.getInstance(appExecutors), ListsRemoteDataSource.getInstance(appExecutors)),
                StuffsRepository.getInstance(StuffsLocalDataSource.getInstance(appExecutors), StuffsRemoteDataSource.getInstance(appExecutors)),
                listDetailFragment,
                userId,
                listId
        );
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
                        Intent intent3 = new Intent(ListDetailActicity.this, CalendarActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.allStuff_navigation_menu_item:
                        Intent intent2 = new Intent(ListDetailActicity.this, AllStuffActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.listGroup_navigation_menu_item:
                        item.setChecked(false);
                        Intent intent = new Intent(ListDetailActicity.this, ListGroupActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.notifications_navigation_menu_item:
                        item.setChecked(false);
                        Intent intent1 = new Intent(ListDetailActicity.this, NotificationActivity.class);
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
