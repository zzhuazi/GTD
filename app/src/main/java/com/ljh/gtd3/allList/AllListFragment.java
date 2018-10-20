package com.ljh.gtd3.allList;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.listDetail.ListDetailActicity;
import com.ljh.gtd3.listTask.ListTasksActivity;

/**
 * @author Administrator
 * @date 2018/10/20
 */
public class AllListFragment extends Fragment implements AllListContract.View {
    public static final String TAG = AllListFragment.class.getSimpleName();

    private AllListContract.Presenter mPresenter;

    //view
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private View mNoListsView;
    private ImageView mNoListsIcon;
    private TextView mNoListsfMainView;

    //清单数据
    private java.util.List<List> mLists;
    private ListsAdapter mListsAdapter;

    public static AllListFragment newInstance() {
        AllListFragment allListFragment = new AllListFragment();
        return allListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_all_list, container, false);
        //侧拉导航栏
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headView = navigationView.inflateHeaderView(R.layout.nav_header);

        //view
        mRecyclerView = root.findViewById(R.id.rv_all_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mNoListsView = root.findViewById(R.id.nolist);
        mNoListsIcon = root.findViewById(R.id.nolistsIcon);
        mNoListsfMainView = root.findViewById(R.id.nolistsMain);
        mSwipeRefreshLayout = root.findViewById(R.id.swipe_refresh_all_list);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        //下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    mPresenter.loadLists();
                    mSwipeRefreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mPresenter.start();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            boolean refresh = getActivity().getIntent().getBooleanExtra("REFRESH", false);
            if (refresh) {
                Log.d(TAG, "onCreateView: 刷新刷新刷新");
                mPresenter.loadLists();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void setLoadingListsError() {
        showToast("加材清单错误");
    }

    @Override
    public void showNoLists() {
        try {
            setLoadingIndicator(false);
            mRecyclerView.setVisibility(View.GONE);
            mNoListsView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //fragment是否加载
    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showToast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void showAllLists(java.util.List<List> lists) {
        try {
            Log.d(TAG, "showAllLists: fragment中list数量");
            mLists = lists;
            mListsAdapter = new ListsAdapter(mLists);
            //设置item点击事件
            mListsAdapter.setOnItemClickListener(new ListsAdapter.ListItemListener() {
                @Override
                public void onListItemClick(View view, int pos) {
                    //跳转到该清单中的任务列表页面
                    List list = mLists.get(pos);
                    showList(list);
                }

                @Override
                public void onListItemLongCLick(View view, final int pos) {
                    //长按弹出选择对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                    builder.setTitle("设置优先级：");
                    final String[] strings = new String[]{"清单详情", "删除"};
                    builder.setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, final int i) {
                            List list = mLists.get(pos);
                            switch (i) {
                                case 1: //清单详情
                                    mPresenter.showListDetail(list);
                                    break;
                                case 2: //删除清单
                                    mPresenter.deleteList(list);
                                    mLists.remove(pos);
                                    mListsAdapter.notifyDataSetChanged();
                                    break;
                            }
                        }
                    });
                    final AlertDialog dialog = builder.create();

                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.setAdapter(mListsAdapter);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mNoListsView.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showList(List list) {
        if (list != null) {
            Intent intent = new Intent(getContext(), ListTasksActivity.class);
            intent.putExtra("LIST", list);
            startActivity(intent);
        }
    }

    @Override
    public void showListDetail(List list) {
        if (list != null) {
            Intent intent = new Intent(getContext(), ListDetailActicity.class);
            intent.putExtra("LIST", list);
            startActivity(intent);
        }
    }

    @Override
    public void setPresenter(AllListContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
