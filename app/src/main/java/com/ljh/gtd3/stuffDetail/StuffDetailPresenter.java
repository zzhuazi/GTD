package com.ljh.gtd3.stuffDetail;

import android.widget.Toast;

import com.ljh.gtd3.data.AffairSource.AffairsDataSource;
import com.ljh.gtd3.data.AffairSource.AffairsRepository;
import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.StuffsSource.StuffsDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsRepository;
import com.ljh.gtd3.data.entity.Affair;
import com.ljh.gtd3.data.entity.Stuff;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/3/16.
 */

public class StuffDetailPresenter implements StuffDetailContract.Presenter{
    public static final String TAG = StuffDetailPresenter.class.getSimpleName();

    private final ListsRepository mListsRepository;

    private final StuffsRepository mStuffsRepository;

    private final AffairsRepository mAffairsRepository;

    private StuffDetailContract.View mStuffDetailView;

    private String mUserId;

    private String mStuffId;

    public StuffDetailPresenter(ListsRepository mListsRepository, StuffsRepository mStuffsRepository, AffairsRepository mAffairsRepository, StuffDetailContract.View mStuffDetailView, String mUserId, String mStuffId) {
        this.mListsRepository = mListsRepository;
        this.mStuffsRepository = mStuffsRepository;
        this.mAffairsRepository = mAffairsRepository;
        this.mStuffDetailView = mStuffDetailView;
        this.mUserId = mUserId;
        this.mStuffId = mStuffId;
        this.mStuffDetailView.setPresenter(this);
    }

    //加载stuff的数据
    @Override
    public void start() {
        mStuffsRepository.getStuff(mStuffId, new StuffsDataSource.GetStuffCallBack() {
            @Override
            public void onStuffLoaded(final Stuff stuff, String message) {
                mListsRepository.GetList(stuff.getListId(), new ListsDataSource.GetListCallBack() {
                    @Override
                    public void onListLoaded(com.ljh.gtd3.data.entity.List list, String message) {
                        mStuffDetailView.showStuff(stuff, list);
                    }

                    @Override
                    public void onListFail(String message) {

                    }
                });

            }

            @Override
            public void onStuffFail(String message) {

            }
        });
        showAllAffair();
    }

    @Override
    public void showLists() {
        mListsRepository.GetLists(mUserId, new ListsDataSource.GetListsCallBack() {
            @Override
            public void onListsLoaded(List<com.ljh.gtd3.data.entity.List> lists, String message) {
                mStuffDetailView.showLists(lists);
            }

            @Override
            public void onListsFail(String message) {
                mStuffDetailView.showToast(message);
            }
        });
    }

    @Override
    public void showAllStuffs() {
        mStuffDetailView.showAllStuffs();
    }

    @Override
    public void updateStuff(Stuff stuff) {
        stuff.setStuffId(mStuffId);
        mStuffsRepository.updateStuff(stuff);
        mStuffDetailView.showAllStuffs();
    }

    @Override
    public void showAllAffair() {
        mAffairsRepository.getAffairs(mStuffId, new AffairsDataSource.GetAffairsCallBack() {
            @Override
            public void onAffairsLoaded(List<Affair> affairs, String message) {
                mStuffDetailView.showAllAffair(affairs);
            }

            @Override
            public void onAffairsFail(String message) {
                List<Affair> affairs = new ArrayList<>();
                mStuffDetailView.showAllAffair(affairs);
            }
        });
    }

    @Override
    public void addAffair(Affair affair) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        affair.setUserId(mUserId);
        affair.setGmtCreate(simpleDateFormat.format(new Date()));
        affair.setGmtModified(simpleDateFormat.format(new Date()));
        try {
            mAffairsRepository.addAffair(affair);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void completeAffair(Affair affair) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        affair.setFinished(true);
        affair.setGmtModified(simpleDateFormat.format(new Date()));
        try{
            mAffairsRepository.updateAffair(affair);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void activateAffair(Affair affair) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        affair.setFinished(false);
        affair.setGmtModified(simpleDateFormat.format(new Date()));
        try{
            mAffairsRepository.updateAffair(affair);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateAffair(Affair affair) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        affair.setGmtModified(simpleDateFormat.format(new Date()));
        try{
            mAffairsRepository.updateAffair(affair);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAffair(String affairId) {
        try{
            mAffairsRepository.deleteAffair(affairId, new AffairsDataSource.SendRequestCallBack() {
                @Override
                public void onRequestSuccess(String message) {
                    mStuffDetailView.showToast("删除成功");
                }

                @Override
                public void onRequestFail(String message) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
