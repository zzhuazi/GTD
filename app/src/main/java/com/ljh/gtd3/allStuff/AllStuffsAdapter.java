package com.ljh.gtd3.allStuff;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljh.gtd3.R;
import com.ljh.gtd3.data.VO.ListVO;
import com.ljh.gtd3.data.entity.Stuff;

/**
 * Created by Administrator on 2018/3/14.
 */

public class AllStuffsAdapter extends RecyclerView.Adapter<AllStuffsAdapter.ViewHolder> {
    public static final String TAG = AllStuffsAdapter.class.getSimpleName();
    private java.util.List<ListVO> listVOS;

    private AllStuffItemListener mAllStuffItemListener = null;

    public void setAllStuffItemListener(AllStuffItemListener allStuffItemListener){
        mAllStuffItemListener = allStuffItemListener;
    }

    public AllStuffsAdapter(java.util.List<ListVO> listVOS) {
        this.listVOS = listVOS;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_stuff_item, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        ListVO listVO = listVOS.get(position);
        StuffsAdapter stuffsAdapter = new StuffsAdapter(listVO.getStuffList());
        if(!listVOS.isEmpty()) {
            holder.mTextView.setText(listVO.getList().getName());
//            LinearLayoutManager layoutManager = new LinearLayoutManager(holder.mTextView.getContext());
//            holder.mRecyclerView.setLayoutManager(layoutManager);
//            holder.mRecyclerView.setAdapter(stuffsAdapter);
        }
        if(mAllStuffItemListener != null) {
//            stuffsAdapter.setOnItemClickListener(new StuffsAdapter.StuffItemListener() {
//                @Override
//                public void onStuffClick(Stuff clickedStuff) {
//                    mAllStuffItemListener.onStuffClick(clickedStuff);
//                }
//
//                @Override
//                public void onCompleteStuffClick(Stuff completedStuff) {
//                    mAllStuffItemListener.onCompleteStuffClick(completedStuff);
//                }
//
//                @Override
//                public void onActivateStuffClick(Stuff activatedStuff) {
//                    mAllStuffItemListener.onActivateStuffClick(activatedStuff);
//                }
//
//                @Override
//                public void onItemLongClick(View view, Stuff stuff) {
//                    mAllStuffItemListener.onItemLongClick(view, stuff);
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return listVOS.size();
    }

    public void replaceData(java.util.List<ListVO> listVOS) {
        Log.d(TAG, "replaceData: " + listVOS.size());
        setListVO(listVOS);
        notifyDataSetChanged();
    }

    public void setListVO(java.util.List<ListVO> listVOs) {
        listVOS = listVOs;
        Log.d(TAG, "setListVO: adapter's listVOs" + listVOS.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView mTextView;
        RecyclerView mRecyclerView;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            mTextView = itemView.findViewById(R.id.tv_all_stuff_item_list_name);
//            mRecyclerView = itemView.findViewById(R.id.rv_all_stuff_stuffs);
        }
    }

    public interface AllStuffItemListener{
        void onStuffClick(Stuff clickedStuff);
        void onCompleteStuffClick(Stuff completedStuff);
        void onActivateStuffClick(Stuff activatedStuff);
        void onItemLongClick(View view, Stuff stuff);
    }
}
