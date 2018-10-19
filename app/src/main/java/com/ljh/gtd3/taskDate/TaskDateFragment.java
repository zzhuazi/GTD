package com.ljh.gtd3.taskDate;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ljh.gtd3.R;
import com.ljh.gtd3.util.DateUtil;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/3/25.
 */

public class TaskDateFragment extends Fragment implements TaskDateContract.View {
    private static final String TAG = TaskDateFragment.class.getSimpleName();
    private TaskDateContract.Presenter mPresenter;
    private MaterialCalendarView mMaterialCalendarView;
    private TextView mTimeTv;
    private String mDate = null;
    private String mDatetime = null;
    private String mTime = null;

    public static TaskDateFragment newInstance() {
        TaskDateFragment fragment = new TaskDateFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stuff_date, container, false);
        mMaterialCalendarView = view.findViewById(R.id.mcv_stuff_date);
        mMaterialCalendarView.state().edit()
                //设置周一为第一天
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        mMaterialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                mDatetime = simpleDateFormat.format(date.getDate());
            }
        });
        mTimeTv = view.findViewById(R.id.tv_stuff_time);
        mTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                //显示时间dialog
                final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        mTime = hourOfDay+":"+minute+":" + "00";
                        mTimeTv.setText("已选择时间:" + mTime);
                    }
                },calendar.get(java.util.Calendar.HOUR_OF_DAY), calendar.get(java.util.Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
        mDate = getActivity().getIntent().getStringExtra("DATE");
        mPresenter.loadDate(mDate);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_stuff_date, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            switch (item.getItemId()) {
                case R.id.cancel_stuff_date:
                    mPresenter.isSelected(false);
                    getActivity().finish();
                    break;
                case android.R.id.home:
                    mPresenter.isSelected(true);
                    getActivity().finish();
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setPresenter(TaskDateContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void getDate(String date, String time) {
        try{
            mDatetime = date;
            mTime = time;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm:ss");
            if (mDatetime != null && mTime != null) {
                try {
                    mMaterialCalendarView.setSelectedDate(simpleDateFormat.parse(mDatetime));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mTimeTv.setText("已选择时间:" + mTime);
            }else {
                mMaterialCalendarView.setSelectedDate(new Date());
                mDatetime = simpleDateFormat.format(new Date());
                mTime = "00:00:00";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setSelectedResult() {
        try{
            mDate = mDatetime + " " + mTime;
            mDate = DateUtil.formatDateStr(mDate);
            Log.d(TAG, "setSelectedResult: " + mDate);
            Intent intent = new Intent();
            intent.putExtra("DATE", mDate);
            getActivity().setResult(getActivity().RESULT_OK, intent);
            getActivity().finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setNoSelectedResult() {
        try{
            Intent intent = new Intent();
            getActivity().setResult(getActivity().RESULT_CANCELED, intent);
            getActivity().finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
