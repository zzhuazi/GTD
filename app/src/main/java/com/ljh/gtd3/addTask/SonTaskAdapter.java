package com.ljh.gtd3.addTask;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.SonTask;

import java.util.List;

public class SonTaskAdapter extends RecyclerView.Adapter<SonTaskAdapter.ViewHolder>{
    private List<SonTask> mSonTasks;
    private OnSonTaskItemListener mOnSonTaskItemListener = null;

    public SonTaskAdapter(List<SonTask> mSonTasks) {
        this.mSonTasks = mSonTasks;
    }

    public void setOnSonTaskItemListener(OnSonTaskItemListener mOnSonTaskItemListener){
        this.mOnSonTaskItemListener = mOnSonTaskItemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.affair_item, parent, false);
        final SonTaskAdapter.ViewHolder holder = new SonTaskAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SonTask sonTask = mSonTasks.get(position);
       holder.mCheckBox.setChecked(sonTask.getFinished());
       holder.mTextView.setText(sonTask.getContent());
       if(mOnSonTaskItemListener != null) {
           holder.mTextView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   int pos = holder.getLayoutPosition();
                   mOnSonTaskItemListener.onSonTaskItemClick(view, pos);
               }
           });
           holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   int pos = holder.getLayoutPosition();
                   if(sonTask.getFinished()) { //完成
                       mOnSonTaskItemListener.onActivateSonTaskClick(view, pos);
                   }else {
                       mOnSonTaskItemListener.onCompleteSonTaskClick(view, pos);
                   }
               }
           });
           holder.mImageView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   int pos = holder.getLayoutPosition();
                   mOnSonTaskItemListener.onImageViewClick(view, pos);
               }
           });
       }
    }

    @Override
    public int getItemCount() {
        return mSonTasks.size();
    }

    public void replaceSonTasks(List<SonTask> mSonTasks) {
        this.mSonTasks = mSonTasks;
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox mCheckBox;
        TextView mTextView;
        ImageView mImageView;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            mCheckBox = itemView.findViewById(R.id.cb_affair_item);
            mTextView = itemView.findViewById(R.id.tv_affair_name);
            mImageView = itemView.findViewById(R.id.iv_affair_delete);
        }
    }

    public interface OnSonTaskItemListener{
        void onSonTaskItemClick(View view, int pos);
        void onCompleteSonTaskClick(View view, int pos);
        void onActivateSonTaskClick(View view, int pos);
        void onImageViewClick(View view, int pos);
    }
}
