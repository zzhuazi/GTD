package com.ljh.gtd3.addList;

import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.entity.List;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/3/17.
 */

public class AddListPresenter implements AddListContract.Presenter {
    public static final String TAG = AddListPresenter.class.getSimpleName();

    private final ListsRepository mListsRepository;

    private final AddListContract.View mAddListView;

    private List mList;

    public AddListPresenter(ListsRepository mListsRepository, AddListContract.View mAddListView, List mList) {
        this.mListsRepository = mListsRepository;
        this.mAddListView = mAddListView;
        this.mList = mList;
        mAddListView.setPresenter(this);
    }

    @Override
    public void start() {
        if (mList != null) {
            mListsRepository.GetList(mList.getId(), new ListsDataSource.GetListCallBack() {
                @Override
                public void onListLoaded(final List list, String message) {
                    mAddListView.showList(list);
                }

                @Override
                public void onListFail(String message) {

                }
            });
        }
    }

    @Override
    public void addList(List list) {
        mListsRepository.addList(list);
    }


    @Override
    public void updateList(List list) {
        mListsRepository.updateList(list);
    }
}
