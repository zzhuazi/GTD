package com.ljh.gtd3.voiceResult;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ljh.gtd3.R;
import com.ljh.gtd3.allTask.AllTasksActivity;

public class VoiceResultFragment extends Fragment implements VoiceResultContract.View {
    private VoiceResultContract.Presenter mPresenter;
    private EditText resultEt;
    private Button addStuffBt;

    public static VoiceResultFragment newInstance(){
        VoiceResultFragment fragment = new VoiceResultFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_voice_result, container, false);
        resultEt = root.findViewById(R.id.et_voice_result);
        addStuffBt = root.findViewById(R.id.bt_voice_result_add_stuff);
        addStuffBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toAllStuffActivity();
                getActivity().finish();
            }
        });
        mPresenter.start();
        return root;
    }

    @Override
    public void loadResult() {
        String result = getActivity().getIntent().getStringExtra("RESULT");
        resultEt.setText(result);
    }

    @Override
    public void toAllStuffActivity() {
        try{
            Intent intent = new Intent(getContext(), AllTasksActivity.class);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setPresenter(VoiceResultContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
