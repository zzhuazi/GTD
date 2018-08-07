package com.ljh.gtd3.notification;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.Notification;
import com.ljh.gtd3.data.entity.User;
import com.ljh.gtd3.user.UserActivity;

import java.util.List;

/**
 * Created by Administrator on 2018/3/25.
 */

public class NotificationFragment extends Fragment implements NotificationContact.View {

    public static final String TAG = NotificationFragment.class.getSimpleName();
    private NotificationContact.Presenter mPresenter;

    private RecyclerView mRecyclerView;
    private NotificationAdapter mNotificationAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mNoNotificationsView;
    private ImageView mNoNotificationsIcon;
    private TextView mNoNotificationMainView;

    private RelativeLayout mUserSettingRv;
    private TextView mUserName;
    private TextView mUserEmail;

    private List<Notification> mNotifications;

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = view.findViewById(R.id.rv_notification);
        mRecyclerView.setLayoutManager(layoutManager);
        mNoNotificationsView = view.findViewById(R.id.noNotifications);
        mNoNotificationsIcon = view.findViewById(R.id.noNotificationsIcon);
        mNoNotificationMainView = view.findViewById(R.id.noNotificationsMain);

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headView = navigationView.inflateHeaderView(R.layout.nav_header);
        mUserSettingRv = headView.findViewById(R.id.rv_user_setting);
        mUserEmail = headView.findViewById(R.id.email);
        mUserName = headView.findViewById(R.id.username);
        mUserSettingRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.showUserSetting();
            }
        });

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_all_notification);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadNotifications(true, true);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mPresenter.start();
        return view;
    }

    @Override
    public void setPresenter(NotificationContact.Presenter presenter) {
        mPresenter = presenter;
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
    public void setLoadingNotificationError() {
        showToast("没有消息！");
    }

    @Override
    public void showAllNotification(List<Notification> notifications) {
        try{
            Log.d(TAG, "showAllNotification: " + notifications.size());
            mNotifications = notifications;
            mNotificationAdapter = new NotificationAdapter(mNotifications);
            mNotificationAdapter.setOnItemClickListener(new NotificationAdapter.OnItemClickListener() {
                @Override
                public void onNotificationItemClickListener(final View view, final int pos) {
                    mPresenter.readNotification(mNotifications.get(pos).getId());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mNotifications.get(pos).setRead(true);
                            mNotificationAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onNotificationItemLongClickListener(View view, final int pos) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("删除？")
                                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mPresenter.deleteNotification(mNotifications.get(pos).getId());
                                            mNotifications.remove(pos);
                                            mNotificationAdapter.notifyDataSetChanged();
                                            dialogInterface.dismiss();
                                        }
                                    });
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    builder.create().show();
                                }
                            });
                        }
                    });
                }
            });
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.setAdapter(mNotificationAdapter);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mNoNotificationsView.setVisibility(View.GONE);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showNoNotification() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoadingIndicator(false);
                mRecyclerView.setVisibility(View.GONE);
                mNoNotificationsView.setVisibility(View.VISIBLE);
            }
        });

    }

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
    public void showUserSetting() {
        try{
            Intent intent = new Intent(getContext(), UserActivity.class);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void loadUser(User user) {
        try{
            mUserEmail.setText(user.getEmail());
            mUserName.setText(user.getName());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
