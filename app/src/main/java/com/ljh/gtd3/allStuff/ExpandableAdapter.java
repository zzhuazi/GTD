package com.ljh.gtd3.allStuff;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Stuff;

/**
 * Created by Administrator on 2018/3/24.
 */

public class ExpandableAdapter extends BaseExpandableListAdapter {
    private static final String TAG = ExpandableAdapter.class.getSimpleName();
    //    private java.util.List<List> lists;
    private java.util.List<String> stuffDates;
    private java.util.List<java.util.List<Stuff>> stuffs;
    private OnChildClickListener mOnChildClickListener;

//    public ExpandableAdapter(java.util.List<List> lists, java.util.List<java.util.List<Stuff>> stuffs) {
//        this.lists = lists;
//        this.stuffs = stuffs;
//    }


    public ExpandableAdapter(java.util.List<String> stuffDates, java.util.List<java.util.List<Stuff>> stuffs) {
        this.stuffDates = stuffDates;
        this.stuffs = stuffs;
    }

    public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
        mOnChildClickListener = onChildClickListener;
    }

    @Override
    public int getGroupCount() {
//        return lists.size();
        return stuffDates.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return stuffs.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
//        return lists.get(i);
        return stuffDates.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return stuffs.get(i).get(i1);
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
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        ViewHolderGroup viewHolderGroup;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_stuff_item, null);
            viewHolderGroup = new ViewHolderGroup();
            viewHolderGroup.textView = view.findViewById(R.id.tv_all_stuff_item_list_name);
            view.setTag(viewHolderGroup);
        } else {
            viewHolderGroup = (ViewHolderGroup) view.getTag();
        }
//        viewHolderGroup.textView.setText(lists.get(i).getName());
        viewHolderGroup.textView.setText(stuffDates.get(i));
        return view;
    }

    @Override
    public View getChildView(final int i, final int i1, boolean b, View convertView, ViewGroup viewGroup) {
        Stuff stuff = stuffs.get(i).get(i1);
        View view;
        ViewHolderChild viewHolderChild;
        if (convertView == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stuff_item, null);
            viewHolderChild = new ViewHolderChild();
            viewHolderChild.checkBox = view.findViewById(R.id.cb_stuff_item);
            viewHolderChild.priorityColor = view.findViewById(R.id.v_stuff_item_priority);
            viewHolderChild.textView = view.findViewById(R.id.tv_stuff_item_title);
            view.setTag(viewHolderChild);   //将viewHolder存储在view中
        } else {
            view = convertView;
            viewHolderChild = (ViewHolderChild) view.getTag();
        }
        viewHolderChild.checkBox.setChecked(stuff.getFinished());
        switch (stuff.getPriority()) {
            case 1:
                viewHolderChild.priorityColor.setBackgroundColor(viewHolderChild.priorityColor.getResources().getColor(R.color.colorBule));
                break;
            case 2:
                viewHolderChild.priorityColor.setBackgroundColor(viewHolderChild.priorityColor.getResources().getColor(R.color.colorYellow));
                break;
            case 3:
                viewHolderChild.priorityColor.setBackgroundColor(viewHolderChild.priorityColor.getResources().getColor(R.color.colorAccent));
                break;
            default:
                viewHolderChild.priorityColor.setBackgroundColor(viewHolderChild.priorityColor.getResources().getColor(R.color.colorWhite));
                break;
        }
        viewHolderChild.textView.setText(stuff.getName());
        if (mOnChildClickListener != null) {
            viewHolderChild.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnChildClickListener.onChildItemClickListener(view, i, i1);
                }
            });
            viewHolderChild.textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnChildClickListener.onChildItemLongClickListener(view, i, i1);
                    return false;
                }
            });
            viewHolderChild.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (stuffs.get(i).get(i1).getFinished()) {  //完成
                        mOnChildClickListener.onActivateStuffClick(view, i, i1);
                    } else {
                        mOnChildClickListener.onCompleteStuffClick(view, i, i1);
                    }
                }
            });
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    interface OnChildClickListener {
        void onChildItemClickListener(View view, int parentPos, int childPos);

        void onChildItemLongClickListener(View view, int parentPos, int childPos);

        void onActivateStuffClick(View view, int parentPos, int childPos);  //将已完成的材料变为未完成

        void onCompleteStuffClick(View view, int parentPos, int childPos);  //将未完成的材料变为已完成
    }

    class ViewHolderGroup {
        TextView textView;
    }

    class ViewHolderChild {
        CheckBox checkBox;
        View priorityColor;
        TextView textView;
    }
}
