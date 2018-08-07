package com.ljh.gtd3.listGroup;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import com.ljh.gtd3.R;
import com.ljh.gtd3.addList.AddListActivity;
import com.ljh.gtd3.addStuff.AddStuffActivity;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.ListGroup;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.data.entity.User;
import com.ljh.gtd3.listDetail.ListDetailActicity;
import com.ljh.gtd3.service.VoiceService;
import com.ljh.gtd3.user.UserActivity;
import com.ljh.gtd3.util.IatSettings;
import com.ljh.gtd3.util.XunfeiJsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2018/3/16.
 */

public class ListGroupFragment extends Fragment implements ListGroupContract.View {
    private static final String TAG = ListGroupFragment.class.getSimpleName();
    private ListGroupContract.Presenter mPresenter;

    private ListGroupExpandableAdapter mExpandableAdapter;
    private ExpandableListView mExpandableListView;

    private RelativeLayout mUserSettingRv;
    private TextView mUserName;
    private TextView mUserEmail;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private java.util.List<ListGroup> mListGroups;
    private java.util.List<java.util.List<List>> mLists;

    //语音听写对象
    private SpeechRecognizer mIat;
    //语音听写UI
    private RecognizerDialog mIatDialog;
    //用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    //引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private boolean mTranslateEnable = false;

    private SharedPreferences mSharedPreferences;

    public static ListGroupFragment newInstance() {
        ListGroupFragment fragment = new ListGroupFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_group, container, false);
        mSharedPreferences = getActivity().getSharedPreferences(IatSettings.PREFER_NAME, Activity.MODE_PRIVATE);
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(getContext(), mInitListener);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(getContext(), mInitListener);

        mExpandableListView = root.findViewById(R.id.ev_list_group);

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headView = navigationView.inflateHeaderView(R.layout.nav_header);
        mUserSettingRv = headView.findViewById(R.id.rv_user_setting);
        mUserEmail = headView.findViewById(R.id.email);
        mUserName = headView.findViewById(R.id.username);
        mUserSettingRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    mPresenter.showUserSetting();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_stuff);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    mPresenter.showAddStuff();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        //悬浮按钮长按时间，跳转到讯飞语音听写
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try{
                    if (null == mIat) {
                        // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
                        showToast("录音服务器逃走了...");
                        return true;
                    }
                    Log.d(TAG, "onLongClick: add");
                    //移动数据分析，收集开始听写事件
                    FlowerCollector.onEvent(getContext(), "iat_recognize");
                    mIatResults.clear();
                    //设置参数
                    setParam();
//                ret = mIat.startListening(mRecognizerListener);
                    //显示听写对话框
                    mIatDialog.setListener(mRecognizerDialogListener);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mIatDialog.show();
                        }
                    });
                    //获取字体所在的控件，设置为"",隐藏字体，
                    TextView txt = (TextView) mIatDialog.getWindow().getDecorView().findViewWithTag("textlink");
                    txt.setText("");
                    if (ret != ErrorCode.SUCCESS) {
                        showToast("听写失败，错误码" + ret);
                    } else {
                        showToast("请开始说话...");
                    }
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                    return true;
                }

            }
        });
        mSwipeRefreshLayout = root.findViewById(R.id.swipe_refresh_all_stuff);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try{
                    mPresenter.loadLists(true);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        mPresenter.start();
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
//        mPresenter.loadLists(false);
    }

    @Override
    public void setPresenter(ListGroupContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showListDetail(@NonNull List requestList) {
        try{
            Intent intent = new Intent(getContext(), ListDetailActicity.class);
            Log.d(TAG, "showListDetail: "+ requestList.getListId());
            intent.putExtra("LISTID", requestList.getListId());
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showListSetting(@NonNull List requestList) {
        try{
            Intent intent = new Intent(getContext(), AddListActivity.class);
            intent.putExtra("LISTID", requestList.getListId());
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showAddStuff() {
        try{
            Intent intent = new Intent(getContext(), AddStuffActivity.class);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.swipe_refresh_all_stuff);

        //当页面处理好后在执行
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void setLoadingStuffsError() {
        showToast("加载文件夹发生异常！");
    }

    @Override
    public void showAllLists(final java.util.List<ListGroup> listGroups, final java.util.List<java.util.List<List>> lists) {
        try{
            mListGroups = listGroups;
            mLists = lists;
            mExpandableAdapter = new ListGroupExpandableAdapter(mListGroups, mLists);
            mExpandableAdapter.setOnChildClickListener(new ListGroupExpandableAdapter.OnChildClickListener() {
                @Override
                public void onChildClickListener(View view, int parentPos, int childPos) {
                    try{
                        showListDetail(lists.get(parentPos).get(childPos));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildLongClickListener(View view, final int parentPos, final int childPos) {
                    try{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onChildLongClick(parentPos, childPos);
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            mExpandableAdapter.setOnParentClickListener(new ListGroupExpandableAdapter.OnParentClickListener() {
                @Override
                public void onParentClickListener(View view, final int parentPos) {
                    try{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(mExpandableListView.isGroupExpanded(parentPos)) {
                                    mExpandableListView.collapseGroup(parentPos);
                                }else {
                                    mExpandableListView.expandGroup(parentPos);
                                }
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onParentLongClickListener(View view, final int parentPos) {
                    try{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onParentLongClick(parentPos);
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mExpandableListView.setAdapter(mExpandableAdapter);
                    mExpandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView absListView, int i) {

                        }

                        @Override
                        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            View firstView = absListView.getChildAt(firstVisibleItem);
                            if (firstVisibleItem == 0 && (firstView == null || firstView.getTop() == 0)) {
                                mSwipeRefreshLayout.setEnabled(true);
                            } else {
                                mSwipeRefreshLayout.setEnabled(false);
                            }
                        }
                    });
                    int count = mExpandableListView.getCount();
                    for (int i = 0; i < count; i++) {
                        mExpandableListView.expandGroup(i);
                    }
                    mExpandableListView.setVisibility(View.VISIBLE);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void onParentLongClick(final int parentPos) {
        try {
            if(mListGroups.get(parentPos).getName().equals("其他")) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("该文件夹不能修改!");
                    }
                });
            }else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("选择操作");
                final String[] strings = {"修改名称", "删除"};
                builder.setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Log.d(TAG, "onClick: 修改名称");
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            View view = View.inflate(getContext(), R.layout.add_list_group, null);
                            final EditText listName = view.findViewById(R.id.add_list_group_name);
                            builder.setTitle("修改清单名称：")
                                    .setView(view)
                                    .setPositiveButton("确认", null);
                            final AlertDialog alertDialog = builder.create();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    alertDialog.show();
                                }
                            });
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String name = listName.getText().toString();
                                    if (!name.isEmpty()) {
                                        mListGroups.get(parentPos).setName(name);
                                        mPresenter.updateListGroup(mListGroups.get(parentPos));
                                        mExpandableAdapter.notifyDataSetChanged();
                                        alertDialog.dismiss();
                                        dialogInterface.dismiss();
                                    } else {
                                        showToast("请输入文件夹名字");
                                    }
                                }
                            });
                        } else if (i == 1) {
                            //将listGroup下的list移到最后一个“其他”二级列表中
                            mLists.get(mLists.size() - 1).addAll(mLists.get(parentPos));
//                    mLists.add(mLists.size()-1,mLists.get(parentPos));
                            mLists.remove(parentPos);
                            mPresenter.deleteListGroup(mListGroups.get(parentPos).getListGroupId());
                            mListGroups.remove(parentPos);
                            mExpandableAdapter.notifyDataSetChanged();
                            dialogInterface.dismiss();
                        }
                    }
                });
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        builder.create().show();
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void onChildLongClick(final int parentPos, final int childPos) {
        try {
            if(mLists.get(parentPos).get(childPos).getName().equals("收集箱")) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("该清单不能修改!");
                    }
                });
            }else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("选择操作");
                final String[] strings = {"清单详情", "修改名称", "删除"};
                builder.setSingleChoiceItems(strings, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Intent intent = new Intent(getContext(), AddListActivity.class);
                                intent.putExtra("LISTID", mLists.get(parentPos).get(childPos).getListId());
                                startActivity(intent);
                                getActivity().finish();
                                break;
                            case 1:
                                Log.d(TAG, "onClick: 修改名称");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                View view = View.inflate(getContext(), R.layout.edit_list_name, null);
                                final EditText listName = view.findViewById(R.id.edit_list_name);
                                builder.setTitle("修改清单名称：")
                                        .setView(view)
                                        .setPositiveButton("确认", null);
                                final AlertDialog alertDialog = builder.create();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertDialog.show();
                                    }
                                });
                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String name = listName.getText().toString();
                                        if (!name.isEmpty()) {
                                            mLists.get(parentPos).get(childPos).setName(name);
                                            mPresenter.updateList(mLists.get(parentPos).get(childPos));
                                            mExpandableAdapter.notifyDataSetChanged();
                                            alertDialog.dismiss();
                                            dialogInterface.dismiss();
                                        } else {
                                            showToast("请输入文件夹名字");
                                        }
                                    }
                                });
                                break;
                            case 2:
                                //将该list下所有stuff删除
                                String listId = mLists.get(parentPos).get(childPos).getListId();
                                mPresenter.deleteStuffByListId(listId);
                                mPresenter.deleteList(listId);
                                mLists.get(parentPos).remove(childPos);
                                mExpandableAdapter.notifyDataSetChanged();
                                dialogInterface.dismiss();
                                break;
                        }
//                if(i == 1) {
//                    Log.d(TAG, "onClick: 修改名称");
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                    View view = View.inflate(getContext(), R.layout.edit_list_name, null);
//                    final EditText listName = view.findViewById(R.id.edit_list_name);
//                    builder.setTitle("修改清单名称：")
//                            .setView(view)
//                            .setPositiveButton("确认", null);
//                    final AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            String name = listName.getText().toString();
//                            if (!name.isEmpty()) {
//                                mLists.get(parentPos).get(childPos).setName(name);
//                                mPresenter.updateList(mLists.get(parentPos).get(childPos));
//                                mExpandableAdapter.notifyDataSetChanged();
//                                alertDialog.dismiss();
//                                dialogInterface.dismiss();
//                            } else {
//                                showToast("请输入文件夹名字");
//                            }
//                        }
//                    });
//                }else if(i == 2) {
//                    //将该list下所有stuff删除
//                    String listId = mLists.get(parentPos).get(childPos).getListId();
//                    mPresenter.deleteStuffByListId(listId);
//                    mPresenter.deleteList(listId);
//                    mLists.get(parentPos).remove(childPos);
//                    mExpandableAdapter.notifyDataSetChanged();
//                    dialogInterface.dismiss();
//                }
                    }
                });
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        builder.create().show();
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
    public void startVoiceService(String userId, String result) {
        try{
            Intent intent = new Intent(getContext(), VoiceService.class);
            intent.putExtra("USERID", userId);
            intent.putExtra("RESULT", result);
            getContext().startService(intent);
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

    @Override
    public void showAddListGroup() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                View view = View.inflate(getContext(), R.layout.add_list_group, null);
                final EditText listGroupName = view.findViewById(R.id.add_list_group_name);
                builder1.setTitle("新文件夹名")
                        .setView(view)
                        .setPositiveButton("确认", null);
                final AlertDialog dialog = builder1.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = listGroupName.getText().toString();
                        if (!name.isEmpty()) {
                            ListGroup listGroup = new ListGroup();
                            listGroup.setListGroupId(UUID.randomUUID().toString());
                            listGroup.setName(name);
                            mPresenter.addListGroup(listGroup);
                            mListGroups.add(listGroup);
                            java.util.List<List> lists = new ArrayList<>();
                            mLists.add(lists);
                            mExpandableAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        } else {
                            showToast("请输入文件夹名字");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_list_group_add, menu);
    }

    //显示toorbar中的加号
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_list_group:
                showAddPopUpMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //toolbar中加号点击按钮处理
    public void showAddPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.add_list_group));
        popup.getMenuInflater().inflate(R.menu.add, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_add_list_group:
                        showAddListGroup();
                        break;
                    case R.id.menu_add_list:
                        Intent intent = new Intent(getContext(), AddListActivity.class);
                        startActivityForResult(intent,1);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            switch (requestCode){
                case 1:
                    if(resultCode == RESULT_OK) {
                        mPresenter.start();
                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    int ret = 0; //函数调用返回值
    //**********************语音部分*********************
    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showToast("初始化失败，错误码：" + code);
            }
        }
    };

    //处理返回的数据
    private String printResult(RecognizerResult results) {
//        String text = XunfeiJsonParser.parseIatResult(results.getResultString());
        String text = XunfeiJsonParser.parseIatResult(results.getResultString());
        Log.d(TAG, "printResult: text" + text);
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
            Log.d(TAG, "printResult: sn" + sn);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        //将每一段拼成一个句子
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
            Log.d(TAG, "printResult: " + mIatResults.get(key));
        }
        return resultBuffer.toString();

    }

    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        this.mTranslateEnable = mSharedPreferences.getBoolean("translate", false);
        if (mTranslateEnable) {
            Log.i(TAG, "translate enable");
            mIat.setParameter(SpeechConstant.ASR_SCH, "1");
            mIat.setParameter(SpeechConstant.ADD_CAP, "translate");
            mIat.setParameter(SpeechConstant.TRS_SRC, "its");
        }

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIat.setParameter(SpeechConstant.ACCENT, null);

            if (mTranslateEnable) {
                mIat.setParameter(SpeechConstant.ORI_LANG, "en");
                mIat.setParameter(SpeechConstant.TRANS_LANG, "cn");
            }
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);

            if (mTranslateEnable) {
                mIat.setParameter(SpeechConstant.ORI_LANG, "cn");
                mIat.setParameter(SpeechConstant.TRANS_LANG, "en");
            }
        }
        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    //听写UI监视器
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            Log.d(TAG, recognizerResult.getResultString());
            String printResult = printResult(recognizerResult);
            Log.d(TAG, "onResult: " + printResult);
            if (isLast) {
                // TODO 最后的结果
                mPresenter.startVoiceService(printResult);
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            showToast(speechError.getPlainDescription(true));
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mIat) {
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
    }

    @Override
    public void onPause() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(getContext());
        super.onPause();
    }
}
