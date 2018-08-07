package com.ljh.gtd3.allStuff;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.StuffsSource.StuffsDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsRepository;
import com.ljh.gtd3.data.UsersSource.UsersDataSource;
import com.ljh.gtd3.data.UsersSource.UsersRepository;
import com.ljh.gtd3.data.VO.ListVO;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.data.entity.User;
import com.ljh.gtd3.util.MyApplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2018/3/13.
 */

public class AllStuffPresenter implements AllStuffContract.Presenter {
    public static final String TAG = AllStuffPresenter.class.getSimpleName();
    private final ListsRepository mListsRepository;
    private final StuffsRepository mStuffsRepository;
    private final UsersRepository mUsersRepository;
    private final AllStuffContract.View mAllStuffView;
    private boolean mFirstLoad = false;
    private String mUserId;

    public AllStuffPresenter(ListsRepository mListsRepository, StuffsRepository mStuffsRepository, UsersRepository mUsersRepository, AllStuffContract.View mAllStuffView, String mUserId) {
        this.mListsRepository = mListsRepository;
        this.mStuffsRepository = mStuffsRepository;
        this.mUsersRepository = mUsersRepository;
        this.mAllStuffView = mAllStuffView;
        this.mUserId = mUserId;
        mAllStuffView.setPresenter(this);
    }

    @Override
    public void start() {
        loadStuffs(false);
        loadUser(mUserId);
    }

    @Override
    public void showAddStuff() {
        Map<String, String> map = new HashMap<>();
        mAllStuffView.showAddStuff(map);
    }

    @Override
    public void loadStuffs(boolean forceUpdate) {
        loadStuffs(forceUpdate, true);
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
        mAllStuffView.showStuffDetail(requestStuff.getStuffId());
    }


    @Override
    public void addStuff(final Stuff stuff) {
        mListsRepository.GetLists(mUserId, new ListsDataSource.GetListsCallBack() {
            @Override
            public void onListsLoaded(List<com.ljh.gtd3.data.entity.List> lists, String message) {
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
                    mAllStuffView.showToast(message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showUserSetting() {
        mAllStuffView.showUserSetting();
    }

    @Override
    public void startVoiceService(String result) {
        mAllStuffView.startVoiceService(mUserId, result);
    }

    @Override
    public void loadUser(String userId) {
        mUsersRepository.getUser(userId, new UsersDataSource.GetUserCallBack() {
            @Override
            public void onUserLoaded(User user) {
                mAllStuffView.loadUser(user);
            }

            @Override
            public void onDataNotAvailable(String message) {

            }
        });
    }

    /**
     * @param forceUpdate   是否强制更新（是否在服务器中请求数据）
     * @param showLoadingUI 是否显示ui
     */
    private void loadStuffs(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mAllStuffView.setLoadingIndicator(true);
        }
        if(forceUpdate) {  //强制刷新时到服务器取数据
            mStuffsRepository.getStuffsFromRemoteDateSource(mUserId, new StuffsDataSource.GetStuffsCallBack() {
                @Override
                public void onStuffsLoaded(List<Stuff> stuffs, String message) {
                    getStuffsSuccess(stuffs);
                }

                @Override
                public void onStuffsFail(String message) {
                    getStuffsFail(message);
                }
            });
        }else {
            mStuffsRepository.getAllStuffs(mUserId, new StuffsDataSource.GetStuffsCallBack() {
                @Override
                public void onStuffsLoaded(List<Stuff> stuffs, String message) {
                    getStuffsSuccess(stuffs);
                }

                @Override
                public void onStuffsFail(String message) {
                    getStuffsFail(message);
                }
            });
        }
    }

    private void getStuffsFail(String message) {
        mAllStuffView.setLoadingIndicator(false);
        mAllStuffView.setLoadingStuffsError();
        mAllStuffView.showToast(message);
    }

    private void getStuffsSuccess(List<Stuff> stuffs) {
        if(stuffs.size() == 0) {
            mAllStuffView.setLoadingIndicator(false);
            mAllStuffView.setLoadingStuffsError();
        }else {
            Map<String, String> stuffDatesTemp = new HashMap<>();  //存放日期
            List<List<Stuff>> stuffList = new ArrayList<>();  //存放以日期分组的stuffs
            stuffDatesTemp.put(stuffs.get(0).getGmtCreate().substring(0, 7),stuffs.get(0).getGmtCreate().substring(0, 7));  //将第一个stuff的日期为一个分组
            List<Stuff> stuffsTemp = new ArrayList<>();
            stuffsTemp.addAll(stuffs);
            //取出材料中的日期（月份），将月份存储在stuffDatesTemp中。
            for (int i = 0; i < stuffDatesTemp.size(); i++) {
                for (Iterator iterator = stuffsTemp.iterator(); iterator.hasNext();){
                    Stuff stuff = (Stuff) iterator.next();
                    if (stuffDatesTemp.get(stuff.getGmtCreate().substring(0, 7)) != null) {  //如果日期相同，则添加到该分组
                        iterator.remove();
                    } else {  //否则多添加一个日期分组stuffDate
                        stuffDatesTemp.put(stuff.getGmtCreate().substring(0, 7),stuff.getGmtCreate().substring(0, 7));
                    }
                }
            }
            //将stuffDatesTemp中的日期取出，并放入到stuffDates中（作为最终的日期列表传参）
            List<String> stuffDates = new ArrayList<>();
            Iterator iterator1 = stuffDatesTemp.keySet().iterator();
            while (iterator1.hasNext()){   //将日期map转为list
                stuffDates.add(iterator1.next().toString());
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
            try{
                boolean exchange ;
                String stuffDate;
                for (int i = 0; i< stuffDates.size(); i++){   //冒泡排序
                    exchange = false;
                    for (int j = stuffDates.size()-1;j>i; j--){
                        Date date = simpleDateFormat.parse(stuffDates.get(j));
                        Date date1 = simpleDateFormat.parse(stuffDates.get(j-1));
                        if(date.after(date1)) {
                            stuffDate = stuffDates.get(j);
                            stuffDates.set(j, stuffDates.get(j-1));
                            stuffDates.set(j-1, stuffDate);
                            exchange = true;
                        }
                    }
                    if(!exchange) {
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            //将stuff按照stuffDates中的日期进行分组
            for (int i = 0; i < stuffDates.size(); i++) {
                List<Stuff> stuffTemp = new ArrayList<>();   //单个日期分组的stuffs
                for (Iterator iterator = stuffs.iterator();iterator.hasNext();){
                    Stuff stuff = (Stuff) iterator.next();
                    if (stuff.getGmtCreate().startsWith(stuffDates.get(i))) {  //如果日期相同，则添加到该分组
                        stuffTemp.add(stuff);
                        iterator.remove();
                    }
                }
                stuffList.add(stuffTemp);
            }
            if (!mAllStuffView.isActive()) {
                return;
            }
            mAllStuffView.setLoadingIndicator(false);
            mAllStuffView.showAllStuffs(stuffDates, stuffList);
        }
    }
}
