package com.ljh.gtd3.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ljh.gtd3.R;
import com.ljh.gtd3.login.LoginActivity;

public class RegisterFragment extends Fragment implements RegisterContract.View{

    private EditText mEmailEt;
    private Button mSendCodeBt;
    private EditText mPasswordEt;
    private EditText mCodeEt;
    private Button mRegisterBt;

    private RegisterContract.Presenter mPresenter;

   public static RegisterFragment newInstance(){
       RegisterFragment fragment = new RegisterFragment();
       return fragment;
   }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View root = inflater.inflate(R.layout.fragment_register, container, false);
       mEmailEt = root.findViewById(R.id.et_register_email);
       mSendCodeBt = root.findViewById(R.id.bt_register_code);
       mPasswordEt = root.findViewById(R.id.et_register_password);
       mCodeEt = root.findViewById(R.id.et_register_code);
       mRegisterBt = root.findViewById(R.id.bt_register_register);

       mSendCodeBt.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               try{
                   mPresenter.sendCode(mEmailEt.getText().toString());
                   Toast.makeText(getContext(), "正在获取验证码，请稍等..", Toast.LENGTH_LONG).show();
               }catch (Exception e){
                   e.printStackTrace();
               }

           }
       });
       mRegisterBt.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               try{
                   mPresenter.register(mEmailEt.getText().toString(), mPasswordEt.getText().toString(), mCodeEt.getText().toString());
               }catch (Exception e){
                   e.printStackTrace();
               }
           }
       });
       return root;
    }

    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        if(presenter != null) {
            mPresenter = presenter;
        }
    }

    @Override
    public void showLogin() {
        try{
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }catch (Exception e){
            e.printStackTrace();
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
}
