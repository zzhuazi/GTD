package com.ljh.gtd3.taskDetail;

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

import com.ljh.gtd3.addTask.SonTaskAdapter;
import com.ljh.gtd3.allTask.AllTasksActivity;
import com.ljh.gtd3.data.entity.SonTask;
import com.ljh.gtd3.data.entity.Task;
import com.ljh.gtd3.service.NotifyService;
import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.taskDate.TaskDateActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2018/3/16.
 */

public class TaskDetailFragment extends Fragment implements TaskDetailContract.View {
    public static final String TAG = TaskDetailFragment.class.getSimpleName();
    private TaskDetailContract.Presenter mPresenter;

    private TextView mListNameTv;
    private EditText mNameEt;
    private TextView mStartTimeTv;
    private TextView mEndTimeTv;
    private ImageView mPriorityIv;
    private EditText mIntroduceEt;

    private RecyclerView mSonTasksRv;
    private java.util.List<SonTask> mSonTasks;
    private SonTaskAdapter mSonTaskAdapter;
    private Button mAddSonTaskBt;

    private List mList = new List();
    private Task mTask;
    private Integer mPriority = 0;

    public static TaskDetailFragment newInstance() {
        TaskDetailFragment fragment = new TaskDetailFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_task, container, false);
        mListNameTv = getActivity().findViewById(R.id.tv_add_task_list_name);
        mNameEt = root.findViewById(R.id.et_add_task_name);
        mStartTimeTv = root.findViewById(R.id.tv_add_task_start_time);
        mEndTimeTv = root.findViewById(R.id.tv_add_task_end_time);
        mPriorityIv = root.findViewById(R.id.iv_add_task_priority);
        mIntroduceEt = root.findViewById(R.id.et_add_task_introduce);
        mSonTasksRv = root.findViewById(R.id.rv_add_task_sonTasks);
        mSonTasks = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mSonTasksRv.setLayoutManager(layoutManager);
//        mSonTaskAdapter = new SonTaskAdapter(mSonTasks);

        mAddSonTaskBt = root.findViewById(R.id.bt_add_task_add_sonTask);
        mAddSonTaskBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSonTask(null, -1);
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
        //加载Task信息
        mPresenter.start();
        return root;
    }

    @Override
    public void setPresenter(TaskDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showTask(Task task, List list) {
        try {
            mTask = task;
            mListNameTv.setText(list.getName());
            mListNameTv.setTag(R.id.tag_listId, list.getId());
            mNameEt.setText(task.getName());
            if (task.getStartTime() != null) {
                if (!task.getStartTime().isEmpty()) {
                    if (!task.getStartTime().equals("null")) {
                        mStartTimeTv.setText(task.getStartTime());
                        mStartTimeTv.setTag(task.getStartTime());
                    }
                } else {
                    mStartTimeTv.setText(task.getStartTime());
                }
            }
            if (task.getEndTime() != null) {
                if (!task.getEndTime().isEmpty()) {
                    if (!task.getEndTime().equals("null")) {
                        mEndTimeTv.setText(task.getEndTime());
                        mEndTimeTv.setTag(task.getEndTime());
                    }
                } else {
                    mEndTimeTv.setText(task.getEndTime());
                }
            }
            if (task.getIntroduce() != null) {
                mIntroduceEt.setText(task.getIntroduce());
            }
            if (task.getPriority() == null) {
                task.setPriority(0);
            }
            mPriority = task.getPriority();
            mPriorityIv.setTag(task.getPriority());
            priorityBackGroupSelector(task.getPriority());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showLists(final java.util.List<List> lists) {
        try {
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
                            mListNameTv.setTag(R.id.tag_listId, lists.get(i + 1).getId());
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
//                                    mListNameTv.setTag(R.id.tag_listId, lists.get(i + 1).getId());
//                                    mListNameTv.setTag(R.id.tag_userId, lists.get(i + 1).getUserId());
//                                }
//                            });
//                            dialog.dismiss();
//                        }
//                    });
                    dialog.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showStartTime() {
        try {
            Intent intent = new Intent(getContext(), TaskDateActivity.class);
            intent.putExtra("DATE", mStartTimeTv.getTag().toString());
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showEndTime() {
        try {
            Intent intent = new Intent(getContext(), TaskDateActivity.class);
            intent.putExtra("DATE", mEndTimeTv.getTag().toString());
            startActivityForResult(intent, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showPriority() {
        try {
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
        } catch (Exception e) {
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
    public void showAllSonTask(java.util.List<SonTask> sonTasks) {
        try {
            mSonTasks.addAll(sonTasks);
            mSonTaskAdapter = new SonTaskAdapter(sonTasks);
            mSonTaskAdapter.setOnSonTaskItemListener(new SonTaskAdapter.OnSonTaskItemListener() {
                @Override
                public void onSonTaskItemClick(View view, int pos) {
                    addSonTask(mSonTasks.get(pos), pos);
                }

                @Override
                public void onCompleteSonTaskClick(View view, int pos) {
                    mPresenter.completeSonTask(mSonTasks.get(pos));
                    mSonTasks.get(pos).setFinished(true);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSonTaskAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onActivateSonTaskClick(View view, int pos) {
                    mPresenter.activateSonTask(mSonTasks.get(pos));
                    mSonTasks.get(pos).setFinished(false);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSonTaskAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onImageViewClick(View view, final int pos) {
                    mPresenter.deleteSonTask(mSonTasks.get(pos).getId());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSonTasks.remove(pos);
                            mSonTaskAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSonTasksRv.setAdapter(mSonTaskAdapter);
                }
            });
        } catch (Exception e) {
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
    public void showAllTasks() {
        try {
            Intent intent = new Intent(getContext(), AllTasksActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addSonTask(final SonTask requestSonTask, final int pos) {
        try {
            final AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            View view = View.inflate(getContext(), R.layout.add_sontask_name, null);
            final EditText SonTaskName = view.findViewById(R.id.add_sonTask_name);
            if (requestSonTask != null) {
                SonTaskName.setText(requestSonTask.getContent());
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
                            String name = SonTaskName.getText().toString();
                            Log.d(TAG, "onClick: name = ?" + name);
                            if (!name.isEmpty() && requestSonTask == null) {
                                SonTask sonTask = new SonTask();
                                sonTask.setFinished(false);
                                sonTask.setContent(name);
                                //setTask
                                mSonTasks.add(sonTask);
                                try {
                                    mSonTaskAdapter.replaceSonTasks(mSonTasks);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mPresenter.addSonTask(sonTask);
                                dialog.dismiss();
                            } else if (!name.isEmpty() && pos != -1) {
                                requestSonTask.setContent(name);
                                mSonTasks.remove(pos);
                                mSonTasks.add(pos, requestSonTask);
                                mPresenter.updateSonTask(requestSonTask);
                                mSonTaskAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            } else if (name.isEmpty() || name.equals("")) {
                                showToast("请输入事务内容");
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_add_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.save_task: //这里是update Task的意思
                    updateTask();
                    getActivity().finish();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateTask() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Task task = new Task();
            task.setId((Integer) mListNameTv.getTag(R.id.tag_listId));
//            task.setUserId((String) mListNameTv.getTag(R.id.tag_userId));
            task.setName(mNameEt.getText().toString());
            task.setIntroduce(mIntroduceEt.getText().toString());
            task.setPriority(mPriority);
            task.setFinished(false);
            task.setList_id((Integer) mListNameTv.getTag(R.id.tag_listId));
            task.setGmtModified(simpleDateFormat.format(new Date()));
            task.setStartTime((String) mStartTimeTv.getTag());
            task.setEndTime((String) mEndTimeTv.getTag());
            mPresenter.updateTask(task);
            Intent intent = new Intent(getContext(), NotifyService.class);
            intent.putExtra("USERID", (String) mListNameTv.getTag(R.id.tag_userId));
            getContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
