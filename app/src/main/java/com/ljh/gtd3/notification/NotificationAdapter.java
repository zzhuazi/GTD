package com.ljh.gtd3.notification;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.Notification;

import java.util.List;

/**
 * Created by Administrator on 2018/3/25.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> mNotifications;
    private OnItemClickListener mOnItemClickListener = null;

    public NotificationAdapter(List<Notification> mNotifications) {
        this.mNotifications = mNotifications;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Notification notification = mNotifications.get(position);
        if (!mNotifications.isEmpty()) {
            holder.mContent.setText(notification.getContent());
            holder.mDate.setText(notification.getTime());
            if(notification.getRead()) {
                holder.relativeLayout.setBackgroundColor(holder.relativeLayout.getResources().getColor(R.color.colorhui));
            }else {
                holder.relativeLayout.setBackgroundColor(holder.relativeLayout.getResources().getColor(R.color.colorWhite));
            }
        }
        if (mOnItemClickListener != null) {
            if(!notification.getRead()) {
                holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onNotificationItemClickListener(view, pos);
                    }
                });
            }
            holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onNotificationItemLongClickListener(view, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mContent;
        TextView mDate;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.rl_notification_item);
            mContent = itemView.findViewById(R.id.tv_notification_item_content);
            mDate = itemView.findViewById(R.id.tv_notification_item_date);
        }
    }

    interface OnItemClickListener {
        void onNotificationItemClickListener(View view, int pos);

        void onNotificationItemLongClickListener(View view, int pos);
    }
}
