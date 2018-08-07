package com.ljh.gtd3.addStuff;

import com.ljh.gtd3.data.AffairSource.AffairsRepository;
import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.StuffsSource.StuffsRepository;
import com.ljh.gtd3.data.entity.Affair;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/3/15.
 */

public class AddStuffPresenter implements AddStuffContract.Presenter{
    public static final String TAG = AddStuffPresenter.class.getSimpleName();

    private final ListsRepository mListsRepository;

    private final StuffsRepository mStuffsRepository;

    private final AffairsRepository mAffairsRepository;

    private final AddStuffContract.View mAddStuffView;

    private String mUserId;

    public AddStuffPresenter(ListsRepository mListsRepository, StuffsRepository mStuffsRepository, AffairsRepository mAffairsRepository, AddStuffContract.View mAddStuffView, String mUserId) {
        this.mListsRepository = mListsRepository;
        this.mStuffsRepository = mStuffsRepository;
        this.mAffairsRepository = mAffairsRepository;
        this.mAddStuffView = mAddStuffView;
        this.mUserId = mUserId;
        this.mAddStuffView.setPresenter(this);
    }

    @Override
    public void start() {
        mListsRepository.GetLists(mUserId, new ListsDataSource.GetListsCallBack() {
            @Override
            public void onListsLoaded(List<com.ljh.gtd3.data.entity.List> lists, String message) {
                com.ljh.gtd3.data.entity.List list = lists.get(lists.size()-1);
                mAddStuffView.showListName(list);
            }

            @Override
            public void onListsFail(String message) {

            }
        });
    }

    @Override
    public void showLists() {
        mListsRepository.GetLists(mUserId, new ListsDataSource.GetListsCallBack() {
            @Override
            public void onListsLoaded(List<com.ljh.gtd3.data.entity.List> lists, String message) {
                mAddStuffView.showLists(lists);
            }

            @Override
            public void onListsFail(String message) {
                mAddStuffView.showToast(message);
            }
        });
    }

    @Override
    public void showAllStuffs() {
        //显示所有stuff页面
        mAddStuffView.showAllStuffs();
    }

    /**
     * 添加材料
     * @param stuff
     */
    @Override
    public void addStuff(Stuff stuff) {
        try{
            //默认startTime and endTime 格式都是正确的
            boolean isRightStartTime = true;
            boolean isRightEndTime = true;
            //如果startTime不为空，且格式不正确，则将isRightStartTime置为错误
            if(stuff.getStartTime() != null && !DateUtil.isRightDateStr(stuff.getStartTime())) {
                isRightStartTime = false;
            }
            //如果endTime不为空，且格式不正确，则将isRightEndTime置为错误
            if(stuff.getEndTime() != null && !DateUtil.isRightDateStr(stuff.getEndTime())) {
                isRightEndTime = false;
            }
            //当startTime and EndTime格式都正确的时候，添加材料
            if(isRightStartTime && isRightEndTime) {
                mStuffsRepository.addStuff(stuff);
//                mListsRepository.updateStuffsNum(stuff.getListId());
            }else { //否则提示添加失败
                mAddStuffView.showToast("日期格式错误，添加材料失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            mAddStuffView.showToast("日期格式错误，添加材料失败");
        }
    }

    @Override
    public void addAffair(List<Affair> affairs) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Affair affair : affairs){
            affair.setUserId(mUserId);
            affair.setGmtCreate(simpleDateFormat.format(new Date()));
            affair.setGmtModified(simpleDateFormat.format(new Date()));
            mAffairsRepository.addAffair(affair);
        }
    }
}
