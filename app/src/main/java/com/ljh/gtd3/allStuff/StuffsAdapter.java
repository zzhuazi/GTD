package com.ljh.gtd3.allStuff;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.Stuff;

import java.util.List;

/**
 * Created by Administrator on 2018/3/14.
 */

public class StuffsAdapter extends RecyclerView.Adapter<StuffsAdapter.ViewHolder>{

    private List<Stuff> mStuffs;
    private StuffItemListener mItemListener = null;

    public StuffsAdapter(List<Stuff> mStuffs) {
        this.mStuffs = mStuffs;
    }

    public void setOnItemClickListener(StuffItemListener stuffItemListener){
        this.mItemListener = stuffItemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stuff_item, parent,false);
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stuff_item, null);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Stuff stuff = mStuffs.get(position);
        if(mStuffs.size() != 0) {
            switch (stuff.getPriority()) {
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
            holder.mTextView.setText(stuff.getName());
            holder.mCheckBox.setChecked(stuff.getFinished());
            if(stuff.getFinished()) {
                holder.view.setBackgroundDrawable(holder.view.getResources().getDrawable(R.drawable.list_completed_touch_feedback));
            }else{
                holder.view.setBackgroundDrawable(holder.view.getResources().getDrawable(R.drawable.touch_feedback));
            }
        }
        if(mItemListener!= null) {
            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mItemListener.onStuffClick(view, pos);
                }
            });
            holder.mTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mItemListener.onItemLongClick(view, pos);
                    return false;
                }
            });
            holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    if(stuff.getFinished()) { //完成
                        mItemListener.onActivateStuffClick(view, pos);
                    }else {
                        mItemListener.onCompleteStuffClick(view, pos);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mStuffs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View priorityColor;
        CheckBox mCheckBox;
        TextView mTextView;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            priorityColor = itemView.findViewById(R.id.v_stuff_item_priority);
            mCheckBox = itemView.findViewById(R.id.cb_stuff_item);
            mTextView = itemView.findViewById(R.id.tv_stuff_item_title);
        }
    }

    public interface StuffItemListener{
        void onStuffClick(View view, int pos);
        void onCompleteStuffClick(View view, int pos);
        void onActivateStuffClick(View view, int pos);
        void onItemLongClick(View view, int pos);
    }
}
