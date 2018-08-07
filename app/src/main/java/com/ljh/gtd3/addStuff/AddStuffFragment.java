package com.ljh.gtd3.addStuff;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ljh.gtd3.data.entity.ListGroup;
import com.ljh.gtd3.service.NotifyService;
import com.ljh.gtd3.R;
import com.ljh.gtd3.allStuff.AllStuffActivity;
import com.ljh.gtd3.data.entity.Affair;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.stuffDate.StuffDateActivity;
import com.ljh.gtd3.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2018/3/15.
 */

public class AddStuffFragment extends Fragment implements AddStuffContract.View {
    private static final String TAG = AddStuffFragment.class.getSimpleName();
    private AddStuffContract.Presenter mPresenter;

    private TextView mListNameTv;
    private EditText mNameEt;
    private TextView mStartTimeTv;
    private TextView mEndTimeTv;
    private ImageView mPriorityIv;
    private EditText mIntroduceEt;
    private Button mAddAffairBt;

    private String mStuffId;
    private String startDate = null;
    private String endDate = null;

    private RecyclerView mAffairsRv;

    private List mList = new List();
    private Integer mPriority = 0;

    private java.util.List<Affair> mAffairs;
    private AffairAdapter mAffairAdapter;

    public static AddStuffFragment newInstance() {
        AddStuffFragment fragment = new AddStuffFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //创建数据适配器
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_stuff, container, false);
        String stuffName = getActivity().getIntent().getStringExtra("STUFFNAME");
        mListNameTv = getActivity().findViewById(R.id.tv_add_stuff_list_name);
        mNameEt = root.findViewById(R.id.et_add_stuff_name);
        if (stuffName != null) {
            mNameEt.setText(stuffName);
        }
        mStartTimeTv = root.findViewById(R.id.tv_add_stuff_start_time);
        mEndTimeTv = root.findViewById(R.id.tv_add_stuff_end_time);
        mPriorityIv = root.findViewById(R.id.iv_add_stuff_priority);
        mIntroduceEt = root.findViewById(R.id.et_add_stuff_introduce);

        mAffairs = new ArrayList<>();
        //affair列表设置
        mAffairsRv = root.findViewById(R.id.rv_add_stuff_affairs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mAffairsRv.setLayoutManager(layoutManager);
        try{
            mAffairAdapter = new AffairAdapter(mAffairs);
            mAffairAdapter.setOnAffairItemListener(new AffairAdapter.OnAffairItemListener() {
                @Override
                public void onAffairClick(View view, final int pos) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addAffair(mAffairs.get(pos), pos);
                        }
                    });
                }

                @Override
                public void onCompleteAffairClick(View view, final int pos) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAffairs.get(pos).setFinished(true);
                            mAffairAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onActivateAffairClick(View view, final int pos) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAffairs.get(pos).setFinished(false);
                            mAffairAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onImageViewClick(View view, final int pos) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAffairs.remove(pos);
                            mAffairAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
            mAffairsRv.setAdapter(mAffairAdapter);
            //选择清单名称
            mListNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.showLists();
                }
            });
            //选择材料开始时间
            mStartTimeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showStartTime();
                }
            });
            //选择材料结束时间
            mEndTimeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEndTime();
                }
            });
            //设置材料优先级
            mPriorityIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPriority();
                }
            });
            mAddAffairBt = root.findViewById(R.id.bt_add_stuff_add_affair);
            mAddAffairBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addAffair(null, -1);
                }
            });
            setHasOptionsMenu(true);
            mStuffId = UUID.randomUUID().toString();
            mPresenter.start();
        }catch (Exception e){
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public void setPresenter(AddStuffContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showListName(final List list) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListNameTv.setText(list.getName());
                mListNameTv.setTag(R.id.tag_listId, list.getListId());
                mListNameTv.setTag(R.id.tag_userId, list.getUserId());
            }
        });
    }

    @Override
    public void showLists(final java.util.List<List> lists) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择所属的清单：");
        ArrayList<String> listsName = new ArrayList<>();
        for (List list : lists) {
            listsName.add(list.getName());
        }
        final String[] strings = listsName.toArray(new String[lists.size()]);
        builder.setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, final int i) {
                mList = lists.get(i);
                Log.d(TAG, "onClick: list.getUserId == " + mList.getUserId());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListNameTv.setText(strings[i]);
                        mListNameTv.setTag(R.id.tag_listId, lists.get(i).getListId());
                        mListNameTv.setTag(R.id.tag_userId, lists.get(i).getUserId());
                        dialogInterface.dismiss();
                    }
                });
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public void showStartTime() {
        try{
            Intent intent = new Intent(getContext(), StuffDateActivity.class);
            intent.putExtra("START_DATE", mStartTimeTv.getTag().toString());
            startActivityForResult(intent, 1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showEndTime() {
        try{
            Intent intent = new Intent(getContext(), StuffDateActivity.class);
            intent.putExtra("END_DATE", mEndTimeTv.getTag().toString());
            startActivityForResult(intent, 2);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    mStartTimeTv.setTag(data.getStringExtra("DATE"));
                    mStartTimeTv.setText(mStartTimeTv.getTag().toString());
                } else if (resultCode == RESULT_CANCELED) {
                    mStartTimeTv.setTag("null");
                    mStartTimeTv.setText("开始时间");
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    mEndTimeTv.setTag(data.getStringExtra("DATE"));
                    mEndTimeTv.setText(mEndTimeTv.getTag().toString());
                } else if (resultCode == RESULT_CANCELED) {
                    mEndTimeTv.setTag("null");
                    mEndTimeTv.setText("结束时间");
                }
                break;
        }
    }

    @Override
    public void showPriority() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("设置优先级：");
        final String[] strings = new String[]{"无", "低", "中", "高"};
        builder.setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                mPriority = i;
                switch (i) {
                    case 1:
                        mPriorityIv.setBackgroundColor(getResources().getColor(R.color.colorBule));
                        break;
                    case 2:
                        mPriorityIv.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                        break;
                    case 3:
                        mPriorityIv.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        break;
                }
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
    }

    @Override
    public void showAllAffair(java.util.List<Affair> affairs) {

    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAllStuffs() {
        try{

        }catch (Exception e){
            e.printStackTrace();
        }
        Intent intent = new Intent(getContext(), AllStuffActivity.class);
        startActivity(intent);
    }

    @Override
    public void addAffair(final Affair requestAffair, final int pos) {
        try{
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            View view = View.inflate(getContext(), R.layout.add_affair_name, null);
            final EditText affairName = view.findViewById(R.id.add_affair_name);
            if (requestAffair != null) {
                affairName.setText(requestAffair.getContent());
            }
            builder1.setTitle("新事务")
                    .setView(view)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String name = affairName.getText().toString();
                            if (!name.isEmpty() && requestAffair == null) {
                                Affair affair = new Affair();
                                affair.setFinished(false);
                                affair.setContent(name);
                                affair.setAffairId(UUID.randomUUID().toString());
                                affair.setStuffId(mStuffId);
                                mAffairs.add(affair);
                                mAffairAdapter.notifyDataSetChanged();
                            } else if (pos != -1 && requestAffair != null) {
                                requestAffair.setContent(name);
                                mAffairs.remove(pos);
                                mAffairs.add(pos, requestAffair);
                                mAffairAdapter.notifyDataSetChanged();
                            }
                            dialogInterface.dismiss();
                        }
                    });
            builder1.create().show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_stuff:
                try{
                    addStuff();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addStuff() {
        try{
            if (mNameEt.getText().toString().equals("")) {
                showToast("材料名不能为空");
            } else {
                //添加stuff
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Stuff stuff = new Stuff();
                stuff.setStuffId(mStuffId);
                stuff.setName(mNameEt.getText().toString());
                stuff.setIntroduce(mIntroduceEt.getText().toString());
                stuff.setPriority(mPriority);
                stuff.setFinished(false);
                stuff.setUserId((String) mListNameTv.getTag(R.id.tag_userId));
                stuff.setListId((String) mListNameTv.getTag(R.id.tag_listId));
                stuff.setGmtCreate(simpleDateFormat.format(new Date()));
                stuff.setGmtModified(simpleDateFormat.format(new Date()));
                Log.d(TAG, "addStuff: aaa" + mStartTimeTv.getTag().toString());
                //判断日期格式是否正确
                if (!mStartTimeTv.getTag().equals("null")) {
                    if(DateUtil.isRightDateStr(mStartTimeTv.getTag().toString())) {
                        Log.d(TAG, "addStuff: ??????????" + mStartTimeTv.getTag().toString());
                        Date startDate = simpleDateFormat.parse((String) mStartTimeTv.getTag());
                        stuff.setStartTime(simpleDateFormat.format(startDate));
                    }else {
                        Log.d(TAG, "addStuff: xxxxxxxxx" + mStartTimeTv.getTag().toString());
                        showToast("开始日期格式错误，添加材料失败");
                        return;
                    }
                }
                if (!mEndTimeTv.getTag().equals("null") ) {
                    if( DateUtil.isRightDateStr(mEndTimeTv.getTag().toString())) {
                        Date endTime = simpleDateFormat.parse((String) mEndTimeTv.getTag());
                        stuff.setEndTime(simpleDateFormat.format(endTime));
                    }else {
                        showToast("结束日期格式错误，添加材料失败");
                        return;
                    }
                }
                mPresenter.addStuff(stuff);

                //添加Affairs
                mPresenter.addAffair(mAffairs);
                //重新开启消息提示的服务。
                Intent intent = new Intent(getContext(), NotifyService.class);
                intent.putExtra("USERID", (String) mListNameTv.getTag(R.id.tag_userId));
                getContext().startService(intent);
                getActivity().finish();
                Intent intent2 = new Intent(getContext(), AllStuffActivity.class);
                startActivity(intent2);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
