package com.ljh.gtd3.addStuff;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.Affair;

import java.util.List;

public class AffairAdapter extends RecyclerView.Adapter<AffairAdapter.ViewHolder>{
    private List<Affair> mAffairs;
    private OnAffairItemListener mOnAffairItemListener = null;

    public AffairAdapter(List<Affair> mAffairs) {
        this.mAffairs = mAffairs;
    }

    public void setOnAffairItemListener(OnAffairItemListener onAffairItemListener){
        this.mOnAffairItemListener = onAffairItemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.affair_item, parent, false);
        final AffairAdapter.ViewHolder holder = new AffairAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Affair affair = mAffairs.get(position);
       holder.mCheckBox.setChecked(affair.getFinished());
       holder.mTextView.setText(affair.getContent());
       if(mOnAffairItemListener != null) {
           holder.mTextView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   int pos = holder.getLayoutPosition();
                   mOnAffairItemListener.onAffairClick(view, pos);
               }
           });
           holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   int pos = holder.getLayoutPosition();
                   if(affair.getFinished()) { //完成
                       mOnAffairItemListener.onActivateAffairClick(view, pos);
                   }else {
                       mOnAffairItemListener.onCompleteAffairClick(view, pos);
                   }
               }
           });
           holder.mImageView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   int pos = holder.getLayoutPosition();
                   mOnAffairItemListener.onImageViewClick(view, pos);
               }
           });
       }
    }

    @Override
    public int getItemCount() {
        return mAffairs.size();
    }

    public void replaceAffairs(List<Affair> mAffairs) {
        this.mAffairs = mAffairs;
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

    public interface OnAffairItemListener{
        void onAffairClick(View view, int pos);
        void onCompleteAffairClick(View view, int pos);
        void onActivateAffairClick(View view, int pos);
        void onImageViewClick(View view, int pos);
    }
}
