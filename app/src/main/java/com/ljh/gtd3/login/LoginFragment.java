package com.ljh.gtd3.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ljh.gtd3.R;
import com.ljh.gtd3.allStuff.AllStuffActivity;
import com.ljh.gtd3.register.RegisterActivity;

/**
 * Created by Administrator on 2018/3/12.
 */

public class LoginFragment extends Fragment implements LoginContract.View{
    private static final String TAG = LoginFragment.class.getSimpleName();
    private EditText mEmailEt;
    private EditText mPasswordEt;
    private Button mLoginBt;
    private TextView mToRegisterTv;

    private LoginContract.Presenter mPresenter;

    public static LoginFragment newInstance(){
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
       // mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        mEmailEt = root.findViewById(R.id.et_login_email);
        mPasswordEt = root.findViewById(R.id.et_login_password);
        mLoginBt = root.findViewById(R.id.bt_login_login);
        mToRegisterTv = root.findViewById(R.id.tv_login_toregister);

        mLoginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    mPresenter.login(mEmailEt.getText().toString(), mPasswordEt.getText().toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        mToRegisterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.showRegister();
            }
        });
        return root;
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    @Override
    public void showRegister() {
        try{
            Intent intent = new Intent(getContext(), RegisterActivity.class);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showAllStuff(String userId) {
        try{
            Intent intent = new Intent(getContext(), AllStuffActivity.class);
            intent.putExtra("USERID", userId);
            startActivity(intent);
            getActivity().finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showToast(final String message) {
        Log.d(TAG, "showToast: " + message);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
