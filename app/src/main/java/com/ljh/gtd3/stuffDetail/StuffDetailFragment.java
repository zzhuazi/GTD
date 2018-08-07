package com.ljh.gtd3.stuffDetail;

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

import com.ljh.gtd3.addStuff.AffairAdapter;
import com.ljh.gtd3.service.NotifyService;
import com.ljh.gtd3.R;
import com.ljh.gtd3.allStuff.AllStuffActivity;
import com.ljh.gtd3.data.entity.Affair;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.stuffDate.StuffDateActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2018/3/16.
 */

public class StuffDetailFragment extends Fragment implements StuffDetailContract.View {
    public static final String TAG = StuffDetailFragment.class.getSimpleName();
    private StuffDetailContract.Presenter mPresenter;

    private TextView mListNameTv;
    private EditText mNameEt;
    private TextView mStartTimeTv;
    private TextView mEndTimeTv;
    private ImageView mPriorityIv;
    private EditText mIntroduceEt;

    private RecyclerView mAffairsRv;
    private java.util.List<Affair> mAffairs;
    private AffairAdapter mAffairAdapter;
    private Button mAddAffairBt;

    private List mList = new List();
    private String mStuffId;
    private Integer mPriority = 0;

    public static StuffDetailFragment newInstance() {
        StuffDetailFragment fragment = new StuffDetailFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_stuff, container, false);
        mListNameTv = getActivity().findViewById(R.id.tv_add_stuff_list_name);
        mNameEt = root.findViewById(R.id.et_add_stuff_name);
        mStartTimeTv = root.findViewById(R.id.tv_add_stuff_start_time);
        mEndTimeTv = root.findViewById(R.id.tv_add_stuff_end_time);
        mPriorityIv = root.findViewById(R.id.iv_add_stuff_priority);
        mIntroduceEt = root.findViewById(R.id.et_add_stuff_introduce);
        mAffairsRv = root.findViewById(R.id.rv_add_stuff_affairs);
        mAffairs = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mAffairsRv.setLayoutManager(layoutManager);
//        mAffairAdapter = new AffairAdapter(mAffairs);

        mAddAffairBt = root.findViewById(R.id.bt_add_stuff_add_affair);
        mAddAffairBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAffair(null, -1);
            }
        });
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
        setHasOptionsMenu(true);
        //加载stuff信息
        mPresenter.start();
        return root;
    }

    @Override
    public void setPresenter(StuffDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showStuff(Stuff stuff, List list) {
        try{
            mStuffId = stuff.getStuffId();
            mListNameTv.setText(list.getName());
            mListNameTv.setTag(R.id.tag_listId, list.getListId());
            mListNameTv.setTag(R.id.tag_userId, list.getUserId());
            mNameEt.setText(stuff.getName());
            if (stuff.getStartTime() != null) {
                if (!stuff.getStartTime().isEmpty()) {
                    if (!stuff.getStartTime().equals("null")) {
                        mStartTimeTv.setText(stuff.getStartTime());
                        mStartTimeTv.setTag(stuff.getStartTime());
                    }
                } else {
                    mStartTimeTv.setText(stuff.getStartTime());
                }
            }
            if (stuff.getEndTime() != null) {
                if (!stuff.getEndTime().isEmpty()) {
                    if (!stuff.getEndTime().equals("null")) {
                        mEndTimeTv.setText(stuff.getEndTime());
                        mEndTimeTv.setTag(stuff.getEndTime());
                    }
                } else {
                    mEndTimeTv.setText(stuff.getEndTime());
                }
            }
            if (stuff.getIntroduce() != null) {
                mIntroduceEt.setText(stuff.getIntroduce());
            }
            if (stuff.getPriority() == null) {
                stuff.setPriority(0);
            }
            mPriority = stuff.getPriority();
            mPriorityIv.setTag(stuff.getPriority());
            priorityBackGroupSelector(stuff.getPriority());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showLists(final java.util.List<List> lists) {
        try{
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
                            mListNameTv.setTag(R.id.tag_listId, lists.get(i + 1).getListId());
                            mListNameTv.setTag(R.id.tag_userId, lists.get(i + 1).getUserId());
                            dialogInterface.dismiss();
                        }
                    });
                }
            });
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final AlertDialog dialog = builder.create();

//                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, final int i) {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Log.d(TAG, "run: i == " + i + 1);
//                                    mListNameTv.setText(strings[i + 1]);
//                                    mListNameTv.setTag(R.id.tag_listId, lists.get(i + 1).getListId());
//                                    mListNameTv.setTag(R.id.tag_userId, lists.get(i + 1).getUserId());
//                                }
//                            });
//                            dialog.dismiss();
//                        }
//                    });
                    dialog.show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showStartTime() {
        try{
            Intent intent = new Intent(getContext(), StuffDateActivity.class);
            intent.putExtra("DATE", mStartTimeTv.getTag().toString());
            startActivityForResult(intent, 1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showEndTime() {
        try{
            Intent intent = new Intent(getContext(), StuffDateActivity.class);
            intent.putExtra("DATE", mEndTimeTv.getTag().toString());
            startActivityForResult(intent, 2);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showPriority() {
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("设置优先级：");
            final String[] strings = new String[]{"无", "低", "中", "高"};
            builder.setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, final int i) {
                    mPriority = i;
                    priorityBackGroupSelector(i);
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void priorityBackGroupSelector(int i) {
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

    @Override
    public void showAllAffair(java.util.List<Affair> affairs) {
        try{
            mAffairs.addAll(affairs);
            mAffairAdapter = new AffairAdapter(affairs);
            mAffairAdapter.setOnAffairItemListener(new AffairAdapter.OnAffairItemListener() {
                @Override
                public void onAffairClick(View view, int pos) {
                    addAffair(mAffairs.get(pos), pos);
                }

                @Override
                public void onCompleteAffairClick(View view, int pos) {
                    mPresenter.completeAffair(mAffairs.get(pos));
                    mAffairs.get(pos).setFinished(true);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAffairAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onActivateAffairClick(View view, int pos) {
                    mPresenter.activateAffair(mAffairs.get(pos));
                    mAffairs.get(pos).setFinished(false);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAffairAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onImageViewClick(View view, final int pos) {
                    mPresenter.deleteAffair(mAffairs.get(pos).getAffairId());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAffairs.remove(pos);
                            mAffairAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAffairsRv.setAdapter(mAffairAdapter);
                }
            });
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
    public void showAllStuffs() {
        try{
            Intent intent = new Intent(getContext(), AllStuffActivity.class);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void addAffair(final Affair requestAffair, final int pos) {
        try{
            final AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            View view = View.inflate(getContext(), R.layout.add_affair_name, null);
            final EditText affairName = view.findViewById(R.id.add_affair_name);
            if (requestAffair != null) {
                affairName.setText(requestAffair.getContent());
            }
            builder1.setTitle("新事务")
                    .setView(view)
                    .setPositiveButton("确认", null);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final AlertDialog dialog = builder1.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String name = affairName.getText().toString();
                            Log.d(TAG, "onClick: name = ?" + name);
                            if (!name.isEmpty() && requestAffair == null) {
                                Affair affair = new Affair();
                                affair.setFinished(false);
                                affair.setContent(name);
                                affair.setAffairId(UUID.randomUUID().toString());
                                affair.setStuffId(mStuffId);
                                mAffairs.add(affair);
                                try{
                                    mAffairAdapter.replaceAffairs(mAffairs);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                mPresenter.addAffair(affair);
                                dialog.dismiss();
                            } else if (!name.isEmpty() && pos != -1) {
                                requestAffair.setContent(name);
                                mAffairs.remove(pos);
                                mAffairs.add(pos, requestAffair);
                                mPresenter.updateAffair(requestAffair);
                                mAffairAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            } else if (name.isEmpty() || name.equals("")) {
                                showToast("请输入事务内容");
                            }
                        }
                    });
                }
            });
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
        try{
            switch (item.getItemId()) {
                case R.id.save_stuff: //这里是update stuff的意思
                    updateStuff();
                    getActivity().finish();
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateStuff() {
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Stuff stuff = new Stuff();
            stuff.setListId((String) mListNameTv.getTag(R.id.tag_listId));
            stuff.setUserId((String) mListNameTv.getTag(R.id.tag_userId));
            stuff.setName(mNameEt.getText().toString());
            stuff.setIntroduce(mIntroduceEt.getText().toString());
            stuff.setPriority(mPriority);
            stuff.setFinished(false);
            stuff.setGmtModified(simpleDateFormat.format(new Date()));
            stuff.setStartTime((String) mStartTimeTv.getTag());
            stuff.setEndTime((String) mEndTimeTv.getTag());
            mPresenter.updateStuff(stuff);
            Intent intent = new Intent(getContext(), NotifyService.class);
            intent.putExtra("USERID", (String) mListNameTv.getTag(R.id.tag_userId));
            getContext().startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
