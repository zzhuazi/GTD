package com.ljh.gtd3.listGroup;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.List;

/**
 * Created by Administrator on 2018/3/16.
 */

public class ListGroupAdapter extends RecyclerView.Adapter<ListGroupAdapter.ViewHolder>{
    private java.util.List<List> mLists;
    private ListItemListener mListItemListener = null;

    public ListGroupAdapter(java.util.List<List> mLists) {
        this.mLists = mLists;
    }

    public void setOnItemClickListener(ListItemListener listener){
        this.mListItemListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final List list = mLists.get(position);
        if(mLists.size() != 0) {
            holder.mListName.setText(list.getName());
            holder.mStuffs.setText(list.getStuffs().toString());
        }
        if(mListItemListener != null) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListItemListener.onListItemClick(list);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public void replaceData(java.util.List<List> lists) {
        setListVO(lists);
        notifyDataSetChanged();
    }

    private void setListVO(java.util.List<List> lists) {
        mLists = lists;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView mListName;
        TextView mStuffs;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            mListName = itemView.findViewById(R.id.tv_list_item_list_name);
            mStuffs = itemView.findViewById(R.id.tv_list_item_stuffs);
        }
    }

    public interface ListItemListener{
        void onListItemClick(List list);
    }
}
