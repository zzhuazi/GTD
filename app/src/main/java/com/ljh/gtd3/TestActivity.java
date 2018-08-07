package com.ljh.gtd3;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ljh.gtd3.util.MyApplication;
import com.ljh.gtd3.util.VoiceController;

public class TestActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private Button button1;
    private TextView textView;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        String result = getIntent().getStringExtra("RESULT");
        String content = getIntent().getStringExtra("CONTENT");
//        Toast.makeText(this, "转换的内容为：  " + content, Toast.LENGTH_SHORT).show();
        editText = findViewById(R.id.content);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.answer);
        editText.setText(result);
        textView.setText(content);
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
//        userId = sharedPreferences.getString("USERID", null);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                VoiceController voiceController = new VoiceController();
////                String start = voiceController.start(TestActivity.this, userId, editText.getText().toString());
//                new Task().execute();
//
//            }
//        });
    }

//    class Task extends AsyncTask<Void,Void, String>{
//        @Override
//        protected String doInBackground(Void... voids) {
//            VoiceController voiceController = new VoiceController();
//            String start = voiceController.start(TestActivity.this, userId, editText.getText().toString());
//            return start;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            textView.setText(s);
//        }
//    }
}
