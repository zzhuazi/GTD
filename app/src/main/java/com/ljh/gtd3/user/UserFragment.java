package com.ljh.gtd3.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.User;
import com.ljh.gtd3.login.LoginActivity;
import com.ljh.gtd3.util.MyApplication;

/**
 * Created by Administrator on 2018/4/2.
 */

public class UserFragment extends Fragment implements UserContract.View {
    private UserContract.Presenter mPresenter;
    private ImageView mUserAvatarIv;
    private TextView mUserNameTv;
    private TextView mUserEmailTv;
    private TextView mUserPhoneTv;
    private TextView mUserSexTv;
    private TextView mUserIntroduceTv;

    private RelativeLayout mUserNameRv;
    private RelativeLayout mUserPhoneRv;
    private RelativeLayout mUserSexRv;
    private RelativeLayout mUserIntroduceRv;
    private RelativeLayout mLogoutRv;

    private RelativeLayout mUserSettingRv;
    private TextView mUserName;
    private TextView mUserEmail;

    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public void setPresenter(UserContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);
//        mUserAvatarIv = root.findViewById(R.id.iv_user_avatar);
        mUserNameTv = root.findViewById(R.id.tv_user_name);
        mUserEmailTv = root.findViewById(R.id.tv_user_email);
        mUserPhoneTv = root.findViewById(R.id.tv_user_phone);
        mUserSexTv = root.findViewById(R.id.tv_user_sex);
        mUserIntroduceTv = root.findViewById(R.id.tv_user_introduce);

        mLogoutRv = root.findViewById(R.id.rv_user_logout);
        mUserNameRv = root.findViewById(R.id.rv_user_name);
        mUserPhoneRv = root.findViewById(R.id.rv_user_phone);
        mUserSexRv = root.findViewById(R.id.rv_user_sex);
        mUserIntroduceRv = root.findViewById(R.id.rv_user_introduce);
        mLogoutRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        mUserNameRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserName();
            }
        });
        mUserPhoneRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePhone();
            }
        });
        mUserSexRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSex();
            }
        });
        mUserIntroduceRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateIntroduce();
            }
        });

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headView = navigationView.inflateHeaderView(R.layout.nav_header);
        mUserSettingRv = headView.findViewById(R.id.rv_user_setting);
        mUserEmail = headView.findViewById(R.id.email);
        mUserName = headView.findViewById(R.id.username);

//        mPresenter.start();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void showUser(User user) {
        try{
            mUserEmailTv.setText(user.getEmail());
            if (user.getName() != null) {
                mUserNameTv.setText(user.getName());
            }
            if (user.getPhone() != null) {
                mUserPhoneTv.setText(String.valueOf(user.getPhone()));
            }
            if (user.getSex() != null) {
                mUserSexTv.setText(user.getSex());
            }
            if (user.getIntroduce() != null) {
                mUserIntroduceTv.setText(user.getIntroduce());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateUserName() {
        try{
            final User user = new User();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final EditText editText = new EditText(getContext());
                    editText.setHint("请输入昵称");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("修改昵称")
                            .setView(editText)
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (editText.getText().equals("")) {
                                        Toast.makeText(getContext(), "请输入昵称！", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mUserNameTv.setText(editText.getText());
                                        user.setName(editText.getText().toString());
                                        mPresenter.updateUser(user);
                                        dialogInterface.dismiss();
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.create().show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updatePhone() {
        try{
            final User user = new User();
            final EditText editText = new EditText(getContext());
            editText.setHint("请输入电话号码");
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("修改电话号码")
                    .setView(editText)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (editText.getText().equals("")) {
                                Toast.makeText(getContext(), "请输入电话号码", Toast.LENGTH_SHORT).show();
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mUserPhoneTv.setText(editText.getText());
                                    }
                                });
                                user.setPhone(Integer.getInteger(editText.getText().toString()));
                                mPresenter.updateUser(user);
                                dialogInterface.dismiss();
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.create().show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateSex() {
        try{
            final User user = new User();
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final String[] strings = new String[]{"保密", "女", "男"};
            builder.setTitle("修改电话号码")
                    .setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, final int i) {
                            user.setSex(strings[i]);
                            mPresenter.updateUser(user);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mUserSexTv.setText(strings[i]);
                                    dialogInterface.dismiss();
                                }
                            });
                        }
                    });
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.create().show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateIntroduce() {
        try{
            final User user = new User();
            final EditText editText = new EditText(getContext());
            editText.setHint("请输入个人简介");
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("修改个人简介")
                    .setView(editText)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mUserIntroduceTv.setText(editText.getText());
                                }
                            });
                            if(editText.getText() != null) {
                                user.setIntroduce(editText.getText().toString());
                                mPresenter.updateUser(user);
                            }
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.create().show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void logout() {
        try{
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear().apply();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void loadUser(User user) {
        mUserEmail.setText(user.getEmail());
        mUserName.setText(user.getName());
    }
}
