package com.ljh.gtd3.register;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ljh.gtd3.R;
import com.ljh.gtd3.util.ActivityUtils;
import com.ljh.gtd3.util.AppExecutors;
import com.ljh.gtd3.util.Injection;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        RegisterFragment registerFragment = (RegisterFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(registerFragment == null) {
            registerFragment = RegisterFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),registerFragment,R.id.contentFrame);
        }
        AppExecutors appExecutors = new AppExecutors();
        new RegisterPresenter(Injection.provideUsersRepository(getApplicationContext()), registerFragment);
    }

    /**
     * 设置toolbar中的返回键
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
