package com.ljh.gtd3.allTask;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.Task;

import java.util.List;

/**
 * Created by Administrator on 2018/3/14.
 */

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
    public static final String TAG = TasksAdapter.class.getSimpleName();
    private List<Task> mTasks;
    private TaskItemListener mItemListener = null;

    public TasksAdapter(List<Task> mTasks) {
        this.mTasks = mTasks;
    }

    public void setOnItemClickListener(TaskItemListener taskItemListener) {
        this.mItemListener = taskItemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stuff_item, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Task task = mTasks.get(position);
        switch (task.getPriority()) {
            case 1:
                holder.priorityColor.setBackgroundColor(holder.priorityColor.getResources().getColor(R.color.colorBule));
                break;
            case 2:
                holder.priorityColor.setBackgroundColor(holder.priorityColor.getResources().getColor(R.color.colorYellow));
                break;
            case 3:
                holder.priorityColor.setBackgroundColor(holder.priorityColor.getResources().getColor(R.color.colorAccent));
                break;
            default:
                holder.priorityColor.setBackgroundColor(holder.priorityColor.getResources().getColor(R.color.colorWhite));
                break;
        }
        holder.mTextView.setText(task.getName());
        holder.mCheckBox.setChecked(task.getFinished());
        if (task.getStartTime() != null && !task.getStartTime().equals("null")) {
            holder.mTaskStartTimeTv.setText(task.getStartTime().substring(5, 10));
        } else {
            holder.mTaskStartTimeTv.setText(" ");
        }
        if (task.getFinished()) {
            holder.mRelativeLayout.setBackgroundDrawable(holder.view.getResources().getDrawable(R.drawable.list_completed_touch_feedback));
        } else {
            holder.mRelativeLayout.setBackgroundDrawable(holder.view.getResources().getDrawable(R.drawable.touch_feedback));
        }
        if (mItemListener != null) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mItemListener.onTaskItemClick(view, pos);
                }
            });
            holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mItemListener.onTaskItemLongClick(view, pos);
                    return false;
                }
            });
            holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    if (task.getFinished()) { //完成
                        mItemListener.onActivateTaskClick(view, pos);
                    } else {
                        mItemListener.onCompleteTaskClick(view, pos);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View priorityColor;
        CheckBox mCheckBox;
        TextView mTextView;
        TextView mTaskStartTimeTv;
        RelativeLayout mRelativeLayout;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            mRelativeLayout = itemView.findViewById(R.id.rl_task_item);
            priorityColor = itemView.findViewById(R.id.v_task_item_priority);
            mCheckBox = itemView.findViewById(R.id.cb_task_item);
            mTextView = itemView.findViewById(R.id.tv_task_item_title);
            mTaskStartTimeTv = itemView.findViewById(R.id.tv_task_item_starttime);
        }
    }

    public interface TaskItemListener {
        void onTaskItemClick(View view, int pos);

        void onCompleteTaskClick(View view, int pos);

        void onActivateTaskClick(View view, int pos);

        void onTaskItemLongClick(View view, int pos);
    }
}
