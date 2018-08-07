package com.ljh.gtd3.addList;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.ListGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2018/3/17.
 */

public class AddListFragment extends Fragment implements AddListContract.View {
    public static final String TAG = AddListFragment.class.getSimpleName();
    private AddListContract.Presenter mPresenter;

    private EditText mListNameEt;
    private LinearLayout mPriorutyLL;
    private ImageView mPriorityIv;
    private TextView mListGroupNameTv;

    private Integer mPriority = 0;

    private String mListId;

    private ListGroup mListGroup = new ListGroup();

    public static AddListFragment newInstance() {
        AddListFragment fragment = new AddListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_list, container, false);
        mListNameEt = root.findViewById(R.id.et_add_list_name);
        mPriorutyLL = root.findViewById(R.id.ll_add_list_set_priority);
        mPriorityIv = root.findViewById(R.id.iv_add_list_priority);
        mListGroupNameTv = root.findViewById(R.id.tv_add_list_listgroup_name);
        mListGroupNameTv.setTag(R.id.tag_listGroupId, "null");
        //选择清单组名
        mListGroupNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    mPresenter.showSelectListGroups();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        mPriorutyLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    showPriority();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        setHasOptionsMenu(true);
        mPresenter.start();
        return root;
    }

    @Override
    public void setPresenter(AddListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showPriority() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("设置优先级：");
        final String[] strings = new String[]{"无", "低", "中", "高"};
        builder.setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                mPriority = i;
                priorityBackgroupSelector(i);
            }
        });
        final AlertDialog dialog = builder.create();

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void priorityBackgroupSelector(int i) {
        switch (i) {
            case 1:
                mPriorityIv.setBackgroundColor(getResources().getColor(R.color.colorBule));
                break;
            case 2:
                mPriorityIv.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                break;
            case 3:
                mPriorityIv.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                break;
        }
    }

    @Override
    public void showSelectListGroups(final List<ListGroup> listGroups) {
        try{
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle("选择所属的清单：");
            ArrayList<String> listsGroupName = new ArrayList<>();
            if (listGroups != null) {
                for (ListGroup listGroup : listGroups) {
                    listsGroupName.add(listGroup.getName());
                }
                final String[] strings = listsGroupName.toArray(new String[listsGroupName.size() + 1]);
                strings[listsGroupName.size()] = "添加文件夹";
                builder.setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        try{
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (i != listGroups.size()) {
                                        mListGroupNameTv.setText(strings[i]);
                                        mListGroupNameTv.setTag(R.id.tag_listGroupId, listGroups.get(i).getListGroupId());
                                        mListGroupNameTv.setTag(R.id.tag_userId, listGroups.get(i).getUserId());
                                    } else {
                                        String listGroupId = UUID.randomUUID().toString();
                                        mListGroupNameTv.setTag(R.id.tag_listGroupId, listGroupId);
                                        addListGroup(listGroupId);
                                    }
                                    dialogInterface.dismiss();
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                String[] strings = {"添加文件夹"};
                builder.setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        try{
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String listGroupId = UUID.randomUUID().toString();
                                    addListGroup(listGroupId);
                                    dialogInterface.dismiss();
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addListGroup(final String listGroupId) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        View view = View.inflate(getContext(), R.layout.add_list_group, null);
        final EditText listGroupName = view.findViewById(R.id.add_list_group_name);
        builder1.setTitle("新文件夹名")
                .setView(view)
                .setPositiveButton("确认", null);
        final AlertDialog dialog = builder1.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = listGroupName.getText().toString();
                if (!name.isEmpty()) {
                    ListGroup listGroup = new ListGroup();
                    listGroup.setListGroupId(listGroupId);
                    listGroup.setName(name);
                    mPresenter.addListGroup(listGroup);
                    mListGroupNameTv.setTag(R.id.tag_listGroupId, listGroupId);
                    mListGroupNameTv.setText(name);
                    dialog.dismiss();
                } else {
                    showToast("请输入文件夹名字");
                }
            }
        });
    }

    @Override
    public void showToast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void showListGroups() {

    }

    @Override
    public void showList(com.ljh.gtd3.data.entity.List list, ListGroup listGroup) {
        try{
            mListId = list.getListId();
            mListNameEt.setText(list.getName());
            if (listGroup != null) {
                mListGroupNameTv.setText(listGroup.getName());
            }
            if (list.getPriority() == null) {
                list.setPriority(0);
            }
            mPriority = list.getPriority();
            mPriorutyLL.setTag(list.getPriority());
            priorityBackgroupSelector(list.getPriority());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_stuff:
                try{
                    addList();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addList() {
        if (mListNameEt.getText().toString().equals("")) {
            showToast("清单名不能为空！");
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            com.ljh.gtd3.data.entity.List list = new com.ljh.gtd3.data.entity.List();
            if(mListId == null) {
                list.setListId(UUID.randomUUID().toString());
            }else {
                list.setListId(mListId);
            }
            list.setName(mListNameEt.getText().toString());
            list.setStuffs(0);
            list.setPriority(mPriority);
            list.setGmtCreate(simpleDateFormat.format(new Date()));
            list.setGmtModified(simpleDateFormat.format(new Date()));
            if (!mListGroupNameTv.getTag(R.id.tag_listGroupId).equals("null")) {
                list.setListGroupId((String) mListGroupNameTv.getTag(R.id.tag_listGroupId));
            }
            if(mListId == null) {
                mPresenter.addList(list);
            }else {
                mPresenter.updateList(list);
            }
            getActivity().setResult(getActivity().RESULT_OK);
            getActivity().finish();
        }
    }
}
