package com.ljh.gtd3.listGroup;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.ListGroup;

/**
 * Created by Administrator on 2018/3/27.
 */

public class ListGroupExpandableAdapter extends BaseExpandableListAdapter {
    private static final String TAG = ListGroupExpandableAdapter.class.getSimpleName();
    private java.util.List<ListGroup> listGroups;
    private java.util.List<java.util.List<List>> lists;
    private OnChildClickListener mOnChildClickListener;
    private OnParentClickListener mOnParentClickListener;

    public ListGroupExpandableAdapter(java.util.List<ListGroup> listGroups, java.util.List<java.util.List<List>> lists) {
        this.listGroups = listGroups;
        this.lists = lists;
    }

    public void setOnChildClickListener(OnChildClickListener onChildClickListener){
        mOnChildClickListener = onChildClickListener;
    }

    public void setOnParentClickListener(OnParentClickListener onParentClickListener){
        mOnParentClickListener = onParentClickListener;
    }

    @Override
    public int getGroupCount() {
        return listGroups.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return lists.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return listGroups.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return lists.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
        GroupViewHolder groupViewHolder;
        if(view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_group_group_item, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.view = view;
            groupViewHolder.listGroupNameTv = view.findViewById(R.id.tv_item_list_group_name);
            view.setTag(groupViewHolder);
        }else {
            groupViewHolder = (GroupViewHolder) view.getTag();
        }
        Log.d(TAG, "getGroupView: " + listGroups.get(i).getName());
        groupViewHolder.listGroupNameTv.setText(listGroups.get(i).getName());
        if(mOnParentClickListener != null) {
            groupViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnParentClickListener.onParentClickListener(view, i);
                }
            });
            groupViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnParentClickListener.onParentLongClickListener(view, i);
                    return true;
                }
            });
        }
        return view;
    }

    @Override
    public View getChildView(final int i, final int i1, boolean b, View convertView, ViewGroup viewGroup) {
        ChildViewHolder childViewHolder;
        View view;
        if(convertView == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.view = view;
            childViewHolder.priorityColor = view.findViewById(R.id.v_list_item_priority);
            childViewHolder.listNameTv = view.findViewById(R.id.tv_list_item_list_name);
            childViewHolder.stuffNumTv = view.findViewById(R.id.tv_list_item_stuffs);
            view.setTag(childViewHolder);
        }else {
            view = convertView;
            childViewHolder = (ChildViewHolder) view.getTag();
        }
        switch (lists.get(i).get(i1).getPriority()) {
            case 1:
                childViewHolder.priorityColor.setBackgroundColor(childViewHolder.priorityColor.getResources().getColor(R.color.colorBule));
                break;
            case 2:
                childViewHolder.priorityColor.setBackgroundColor(childViewHolder.priorityColor.getResources().getColor(R.color.colorYellow));
                break;
            case 3:
                childViewHolder.priorityColor.setBackgroundColor(childViewHolder.priorityColor.getResources().getColor(R.color.colorAccent));
                break;
            default:
                childViewHolder.priorityColor.setBackgroundColor(childViewHolder.priorityColor.getResources().getColor(R.color.colorWhite));
                break;
        }
        childViewHolder.listNameTv.setText(lists.get(i).get(i1).getName());
        childViewHolder.stuffNumTv.setText(String.valueOf(lists.get(i).get(i1).getStuffs()));
        if(mOnChildClickListener != null) {
            childViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnChildClickListener.onChildClickListener(view, i, i1);
                }
            });
            childViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnChildClickListener.onChildLongClickListener(view, i, i1);
                    return true;
                }
            });
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public interface OnChildClickListener {
        void onChildClickListener(View view, int parentPos, int childPos);
        void onChildLongClickListener(View view, int parentPos, int childPos);
    }

    public interface OnParentClickListener{
        void onParentClickListener(View view, int parentPos);
        void onParentLongClickListener(View view, int parentPos);
    }

    class GroupViewHolder {
        View view;
        TextView listGroupNameTv;
    }

    class ChildViewHolder {
        View view;
        View priorityColor;
        TextView listNameTv;
        TextView stuffNumTv;
    }
}
