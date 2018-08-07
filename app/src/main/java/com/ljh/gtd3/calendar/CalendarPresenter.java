package com.ljh.gtd3.calendar;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.StuffsSource.StuffsDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsRepository;
import com.ljh.gtd3.data.UsersSource.UsersDataSource;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.data.entity.User;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2018/3/29.
 */

public class CalendarPresenter implements CalendarContract.Presenter {
    public static final String TAG = CalendarContract.class.getSimpleName();
    private UsersDataSource mUsersRepository;
    private final ListsRepository mListsRepository;
    private final StuffsRepository mStuffsRepository;
    private String mUserId;
    private CalendarContract.View mCalendarView;


    public CalendarPresenter(UsersDataSource mUsersRepository, ListsRepository mListsRepository, StuffsRepository mStuffsRepository, String mUserId, CalendarContract.View mCalendarView) {
        this.mUsersRepository = mUsersRepository;
        this.mListsRepository = mListsRepository;
        this.mStuffsRepository = mStuffsRepository;
        this.mUserId = mUserId;
        this.mCalendarView = mCalendarView;
        mCalendarView.setPresenter(this);
    }

    @Override
    public void start() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        loadStuffs(simpleDateFormat.format(new Date()));
        loadUser(mUserId);
        addDecorator();
    }

    @Override
    public void showAddStuff() {
        Map<String, String> map = new HashMap<>();
        mCalendarView.showAddStuff(map);
    }

    @Override
    public void loadStuffs(final String clickTime) {
        mStuffsRepository.getAllStuffs(mUserId, new StuffsDataSource.GetStuffsCallBack() {
            @Override
            public void onStuffsLoaded(java.util.List<Stuff> stuffs, String message){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try{
                    java.util.List<Stuff> stuffList = new ArrayList<>();
                    for (Stuff stuff : stuffs){

                        if(stuff.getStartTime() != null && stuff.getEndTime() != null && !stuff.getStartTime().equals("null") && !stuff.getEndTime().equals("null")) {   //如果有开始日期和结束日期就把这两个日期中的所有日期添加红点
                            Date start = simpleDateFormat.parse(stuff.getStartTime());
                            Date end = simpleDateFormat.parse(stuff.getEndTime());
                            Date date = simpleDateFormat.parse(clickTime);
                            if(date.after(start) && date.before(end)) {
                                stuffList.add(stuff);
                            }
                        }
                        if(stuff.getStartTime() != null && !stuff.getStartTime().equals("null")) {
                            if(stuff.getStartTime().startsWith(clickTime.substring(0,10))) {
                                stuffList.add(stuff);
                            }
                        }
                        if(stuff.getEndTime() != null && !stuff.getEndTime().equals("null")) {
                           if(stuff.getEndTime().startsWith(clickTime.substring(0,10))) {
                               stuffList.add(stuff);
                           }
                        }
                    }
                    mCalendarView.showAllStuffs(stuffList);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onStuffsFail(String message) {

            }
        });
//        mStuffsRepository.getStuffsByStartDate(mUserId, clickTime, new StuffsDataSource.GetStuffsCallBack() {
//            @Override
//            public void onStuffsLoaded(java.util.List<Stuff> stuffs, String message) {
//                if (stuffs.size() == 0) {
//                    mCalendarView.showNoStuffs();
//                }
//                mCalendarView.showAllStuffs(stuffs);
//            }
//
//            @Override
//            public void onStuffsFail(String message) {
//                mCalendarView.showNoStuffs();
//            }
//        });
    }

    @Override
    public void completeStuff(@NonNull Stuff completedStuff) {
        Log.d(TAG, "completeStuff: " + completedStuff.getFinished());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        completedStuff.setFinished(true);
        completedStuff.setGmtModified(simpleDateFormat.format(new Date()));
        try {
            mStuffsRepository.updateStuff(completedStuff);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void activateStuff(@NonNull Stuff activeStuff) {
        Log.d(TAG, "activateStuff: " + activeStuff.getFinished());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        activeStuff.setFinished(false);
        activeStuff.setGmtModified(simpleDateFormat.format(new Date()));
        try {
            mStuffsRepository.updateStuff(activeStuff);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showStuffDetail(@NonNull Stuff requestStuff) {
        mCalendarView.showStuffDetail(requestStuff.getStuffId());
    }

    @Override
    public void addStuff(final Stuff stuff) {
        mListsRepository.GetLists(mUserId, new ListsDataSource.GetListsCallBack() {
            @Override
            public void onListsLoaded(java.util.List<List> lists, String message) {
                Log.d(TAG, "onListsLoaded: list.size():" + lists.size());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                stuff.setStuffId(UUID.randomUUID().toString());
                stuff.setPriority(0);
                stuff.setUserId(mUserId);
                stuff.setListId(lists.get(0).getListId());
                stuff.setGmtCreate(simpleDateFormat.format(new Date()));
                stuff.setGmtModified(simpleDateFormat.format(new Date()));
                stuff.setFinished(false);
                mStuffsRepository.addStuff(stuff);
//                mListsRepository.updateStuffsNum(stuff.getListId());
            }

            @Override
            public void onListsFail(String message) {

            }
        });
    }

    @Override
    public void deleteStuff(final Stuff stuff) {
        Log.d(TAG, "deleteStuff: ");
        try {
            mStuffsRepository.deleteStuff(stuff.getStuffId(), new StuffsDataSource.SendRequestCallBack() {
                @Override
                public void onRequestSuccess(String message) {
                    Log.d(TAG, "onRequestSuccess: " + message);
//                    mListsRepository.subtractStuffsNum(stuff.getListId());
                }

                @Override
                public void onRequestFail(String message) {
                    Log.d(TAG, "onRequestFail: " + message);
                    mCalendarView.showToast(message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showUserSetting() {
        mCalendarView.showUserSetting();
    }

    @Override
    public void loadUser(String userId) {
        mUsersRepository.getUser(userId, new UsersDataSource.GetUserCallBack() {
            @Override
            public void onUserLoaded(User user) {
                mCalendarView.loadUser(user);
            }

            @Override
            public void onDataNotAvailable(String message) {

            }
        });
    }

    @Override
    public void addDecorator() {
        mStuffsRepository.getAllStuffs(mUserId, new StuffsDataSource.GetStuffsCallBack() {
            @Override
            public void onStuffsLoaded(java.util.List<Stuff> stuffs, String message) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Collection<CalendarDay> dates = new ArrayList<>();
                try{
                    for (Stuff stuff : stuffs){
                        Log.d(TAG, "onStuffsLoaded: start time : " + stuff.getStartTime());
                        boolean startTimeNotEmpty =  stuff.getStartTime() != null && !stuff.getStartTime().equals("null") && !stuff.getStartTime().equals("");
                        boolean endTimeNotEmpty = stuff.getEndTime() != null &&  !stuff.getEndTime().equals("null") && !stuff.getEndTime().equals("");
                        if( startTimeNotEmpty && endTimeNotEmpty) {   //如果有开始日期和结束日期就把这两个日期中的所有日期添加红点
                            Date start = simpleDateFormat.parse(stuff.getStartTime());
                            Date end = simpleDateFormat.parse(stuff.getEndTime());
                            while (start.before(end)){
                                dates.add(new CalendarDay(start));
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(start);
                                cal.add(Calendar.DATE,1);
                                start = cal.getTime();
                            }
                        }
                        if(startTimeNotEmpty) {
                            dates.add(new CalendarDay(simpleDateFormat.parse(stuff.getStartTime())));
                        }
                        if(endTimeNotEmpty) {
                            dates.add(new CalendarDay(simpleDateFormat.parse(stuff.getEndTime())));
                        }
                    }
                    mCalendarView.addDecorator(dates);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onStuffsFail(String message) {

            }
        });
    }

    @Override
    public void startVoiceService(String result) {
        mCalendarView.startVoiceService(mUserId, result);
    }
}
