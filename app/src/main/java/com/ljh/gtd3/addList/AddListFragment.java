package com.ljh.gtd3.addList;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ljh.gtd3.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/3/17.
 */

public class AddListFragment extends Fragment implements AddListContract.View {
    public static final String TAG = AddListFragment.class.getSimpleName();
    private AddListContract.Presenter mPresenter;

    private EditText mListNameEt;
    private LinearLayout mPriorityLL;
    private ImageView mPriorityIv;

    private Integer mPriority = 0;

    private Integer mListId;

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
        mPriorityLL = root.findViewById(R.id.ll_add_list_set_priority);
        mPriorityIv = root.findViewById(R.id.iv_add_list_priority);

        mPriorityLL.setOnClickListener(new View.OnClickListener() {
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
    public void showToast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void showList(com.ljh.gtd3.data.entity.List list) {
        try{
            mListId = list.getId();
            mListNameEt.setText(list.getName());

            if (list.getPriority() == null) {
                list.setPriority(0);
            }
            mPriority = list.getPriority();
            mPriorityLL.setTag(list.getPriority());
            priorityBackgroupSelector(list.getPriority());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_add_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_task:
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
            if(mListId != null) {
                list.setId(mListId);
            }
            list.setName(mListNameEt.getText().toString());
            list.setTasks(list.getTaskList().size());
            list.setPriority(mPriority);
            list.setGmtCreate(simpleDateFormat.format(new Date()));
            list.setGmtModified(simpleDateFormat.format(new Date()));
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
