package com.ljh.gtd3.allList;

import android.util.Log;

import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.entity.List;

/**
 * @author Administrator
 * @date 2018/10/20
 */
public class AllListPresenter implements AllListContract.Presenter {
    public static final String TAG = AllListPresenter.class.getSimpleName();
    private final ListsRepository mListsRepository;
    private final AllListContract.View mAllListsView;

    public AllListPresenter(ListsRepository mListsRepository, AllListContract.View mAllListsView) {
        this.mListsRepository = mListsRepository;
        this.mAllListsView = mAllListsView;
        mAllListsView.setPresenter(this);
    }

    @Override
    public void loadLists() {
        mAllListsView.setLoadingIndicator(true);
        mListsRepository.GetLists(new ListsDataSource.GetListsCallBack() {
            @Override
            public void onListsLoaded(java.util.List<List> lists, String message) {
                //成功获取所有清单
                Log.d(TAG, "onListsLoaded: 获取清单成功，清单数量为：" + lists.size());
                getListsSuccess(lists);
            }

            @Override
            public void onListsFail(String message) {
                //获取清单失败
                Log.d(TAG, "onListsFail: 获取清单失败！");
                getListsFail(message);
            }
        });
    }

    private void getListsFail(String message) {
        //关闭刷新指示
        mAllListsView.setLoadingIndicator(false);
        mAllListsView.setLoadingListsError();
        mAllListsView.showToast(message);
    }

    private void getListsSuccess(java.util.List<List> lists) {
        if(lists.isEmpty()) {
            mAllListsView.setLoadingIndicator(false);
            mAllListsView.setLoadingListsError();
        }else {
            if(!mAllListsView.isActive()) {
                return;
            }
            mAllListsView.setLoadingIndicator(false);
            mAllListsView.showAllLists(lists);
        }
    }

    @Override
    public void showListDetail(List list) {
        mAllListsView.showListDetail(list);
    }

    @Override
    public void updateList(List list) {
        mListsRepository.updateList(list);
    }

    @Override
    public void deleteList(List list) {
        try {
            mListsRepository.deleteList(list.getId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        loadLists();
    }
}
