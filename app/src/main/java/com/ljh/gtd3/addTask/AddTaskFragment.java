package com.ljh.gtd3.addTask;

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

import com.ljh.gtd3.allTask.AllTasksActivity;
import com.ljh.gtd3.service.NotifyService;
import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.SonTask;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Task;
import com.ljh.gtd3.taskDate.TaskDateActivity;
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

public class AddTaskFragment extends Fragment implements AddTaskContract.View {
    private static final String TAG = AddTaskFragment.class.getSimpleName();
    private AddTaskContract.Presenter mPresenter;

    private TextView mListNameTv;
    private EditText mNameEt;
    private TextView mStartTimeTv;
    private TextView mEndTimeTv;
    private ImageView mPriorityIv;
    private EditText mIntroduceEt;
    private Button mAddSonTaskBt;

    private String mTaskId;
    private String startDate = null;
    private String endDate = null;

    private RecyclerView mSonTasksRv;

    private List mList = new List();
    private Integer mPriority = 0;

    private java.util.List<SonTask> mSonTasks;
    private SonTaskAdapter mSonTaskAdapter;

    public static AddTaskFragment newInstance() {
        AddTaskFragment fragment = new AddTaskFragment();
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
        View root = inflater.inflate(R.layout.fragment_add_task, container, false);
        String TaskName = getActivity().getIntent().getStringExtra("TASKNAME");
        mListNameTv = getActivity().findViewById(R.id.tv_add_task_list_name);
        mNameEt = root.findViewById(R.id.et_add_task_name);
        if (TaskName != null) {
            mNameEt.setText(TaskName);
        }
        mStartTimeTv = root.findViewById(R.id.tv_add_task_start_time);
        mEndTimeTv = root.findViewById(R.id.tv_add_task_end_time);
        mPriorityIv = root.findViewById(R.id.iv_add_task_priority);
        mIntroduceEt = root.findViewById(R.id.et_add_task_introduce);

        mSonTasks = new ArrayList<>();
        //SonTask列表设置
        mSonTasksRv = root.findViewById(R.id.rv_add_task_sonTasks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mSonTasksRv.setLayoutManager(layoutManager);
        try{
            mSonTaskAdapter = new SonTaskAdapter(mSonTasks);
            mSonTaskAdapter.setOnSonTaskItemListener(new SonTaskAdapter.OnSonTaskItemListener() {
                @Override
                public void onSonTaskItemClick(View view, final int pos) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addSonTask(mSonTasks.get(pos), pos);
                        }
                    });
                }

                @Override
                public void onCompleteSonTaskClick(View view, final int pos) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSonTasks.get(pos).setFinished(true);
                            mSonTaskAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onActivateSonTaskClick(View view, final int pos) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSonTasks.get(pos).setFinished(false);
                            mSonTaskAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onImageViewClick(View view, final int pos) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSonTasks.remove(pos);
                            mSonTaskAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
            mSonTasksRv.setAdapter(mSonTaskAdapter);
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
            mAddSonTaskBt = root.findViewById(R.id.bt_add_task_add_sonTask);
            mAddSonTaskBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addSonTask(null, -1);
                }
            });
            setHasOptionsMenu(true);
            mTaskId = UUID.randomUUID().toString();
            mPresenter.start();
        }catch (Exception e){
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public void setPresenter(AddTaskContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showListName(final List list) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListNameTv.setText(list.getName());
                mListNameTv.setTag(R.id.tag_listId, list.getId());
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListNameTv.setText(strings[i]);
                        mListNameTv.setTag(R.id.tag_listId, lists.get(i).getId());
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
            Intent intent = new Intent(getContext(), TaskDateActivity.class);
            intent.putExtra("START_DATE", mStartTimeTv.getTag().toString());
            startActivityForResult(intent, 1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showEndTime() {
        try{
            Intent intent = new Intent(getContext(), TaskDateActivity.class);
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
    public void showAllSonTask(java.util.List<SonTask> sonTasks) {

    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAllTasks() {
        try{

        }catch (Exception e){
            e.printStackTrace();
        }
        Intent intent = new Intent(getContext(), AllTasksActivity.class);
        startActivity(intent);
    }

    @Override
    public void addSonTask(final SonTask requestSonTask, final int pos) {
        try{
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            View view = View.inflate(getContext(), R.layout.add_sontask_name, null);
            final EditText SonTaskName = view.findViewById(R.id.add_sonTask_name);
            if (requestSonTask != null) {
                SonTaskName.setText(requestSonTask.getContent());
            }
            builder1.setTitle("子任务")
                    .setView(view)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String name = SonTaskName.getText().toString();
                            if (!name.isEmpty() && requestSonTask == null) {
                                SonTask sonTask = new SonTask();
                                sonTask.setFinished(false);
                                sonTask.setContent(name);
                                mSonTasks.add(sonTask);
                                mSonTaskAdapter.notifyDataSetChanged();
                            } else if (pos != -1 && requestSonTask != null) {
                                requestSonTask.setContent(name);
                                mSonTasks.remove(pos);
                                mSonTasks.add(pos, requestSonTask);
                                mSonTaskAdapter.notifyDataSetChanged();
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
        inflater.inflate(R.menu.toolbar_add_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_task:
                try{
                    addTask();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addTask() {
        try{
            if (mNameEt.getText().toString().equals("")) {
                showToast("材料名不能为空");
            } else {
                //添加Task
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Task task = new Task();
                task.setName(mNameEt.getText().toString());
                task.setIntroduce(mIntroduceEt.getText().toString());
                task.setPriority(mPriority);
                task.setFinished(false);
                task.setList_id((Integer) mListNameTv.getTag(R.id.tag_listId));
                task.setGmtCreate(simpleDateFormat.format(new Date()));
                task.setGmtModified(simpleDateFormat.format(new Date()));
                Log.d(TAG, "addTask: aaa" + mStartTimeTv.getTag().toString());
                //判断日期格式是否正确
                if (!mStartTimeTv.getTag().equals("null")) {
                    if(DateUtil.isRightDateStr(mStartTimeTv.getTag().toString())) {
                        Log.d(TAG, "addTask: ??????????" + mStartTimeTv.getTag().toString());
                        Date startDate = simpleDateFormat.parse((String) mStartTimeTv.getTag());
                        task.setStartTime(simpleDateFormat.format(startDate));
                    }else {
                        Log.d(TAG, "addTask: xxxxxxxxx" + mStartTimeTv.getTag().toString());
                        showToast("开始日期格式错误，添加材料失败");
                        return;
                    }
                }
                if (!mEndTimeTv.getTag().equals("null") ) {
                    if( DateUtil.isRightDateStr(mEndTimeTv.getTag().toString())) {
                        Date endTime = simpleDateFormat.parse((String) mEndTimeTv.getTag());
                        task.setEndTime(simpleDateFormat.format(endTime));
                    }else {
                        showToast("结束日期格式错误，添加材料失败");
                        return;
                    }
                }
                mPresenter.addTask(task);

                //添加SonTasks
                mPresenter.addSonTask(mSonTasks);
                //重新开启消息提示的服务。
                Intent intent = new Intent(getContext(), NotifyService.class);
                intent.putExtra("USERID", (String) mListNameTv.getTag(R.id.tag_userId));
                getContext().startService(intent);
                getActivity().finish();
                Intent intent2 = new Intent(getContext(), AllTasksActivity.class);
                startActivity(intent2);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
