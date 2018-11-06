package com.ljh.gtd3.allList;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.List;

/**
 * @author Administrator
 * @date 2018/10/20
 */
public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ViewHolder> {
    public static final String TAG = ListsAdapter.class.getSimpleName();
    private java.util.List<List> mLists;
    private ListItemListener mItemListener = null;

    public ListsAdapter(java.util.List<List> mLists) {
        this.mLists = mLists;
    }

    public void setOnItemClickListener(ListItemListener listener) {
        this.mItemListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        List list = mLists.get(position);
        switch (list.getPriority()) {
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
        holder.mListName.setText(list.getName());
        holder.mTasksNum.setText(list.getTasks().toString());
        if(mItemListener != null) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mItemListener.onListItemClick(view, pos);
                }
            });
            holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mItemListener.onListItemLongCLick(view, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        View priorityColor;
        TextView mListName;
        TextView mTasksNum;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            priorityColor = itemView.findViewById(R.id.v_list_item_priority);
            mListName = itemView.findViewById(R.id.tv_list_item_list_name);
            mTasksNum = itemView.findViewById(R.id.tv_list_item_tasks);
        }
    }

    public interface ListItemListener {
        void onListItemClick(View view, int pos);

        void onListItemLongCLick(View view, int pos);
    }
}
