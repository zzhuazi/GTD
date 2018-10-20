package com.ljh.gtd3.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import com.ljh.gtd3.R;
import com.ljh.gtd3.addTask.AddTaskActivity;
import com.ljh.gtd3.allTask.TasksAdapter;
import com.ljh.gtd3.data.entity.Task;
import com.ljh.gtd3.service.VoiceService;
import com.ljh.gtd3.taskDetail.TaskDetailActivity;
import com.ljh.gtd3.util.IatSettings;
import com.ljh.gtd3.util.XunfeiJsonParser;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/29.
 */

public class CalendarFragment extends Fragment implements CalendarContract.View {
    public static final String TAG = CalendarContract.class.getSimpleName();
    private CalendarContract.Presenter mPresenter;
    private MaterialCalendarView mMaterialCalendarView;
    private RecyclerView mRecyclerView;
    private TasksAdapter mTasksAdapter;
    private View mNoTasksView;
    private TextView mTaskStartDate;
    

    //语音听写对象
    private SpeechRecognizer mIat;
    //语音听写UI
    private RecognizerDialog mIatDialog;
    //用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    //引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private boolean mTranslateEnable = false;
    private SharedPreferences mSharedPreferences;

    private List<Task> mTasks = new ArrayList<>();

    public static CalendarFragment newInstance() {
        CalendarFragment fragment = new CalendarFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        mSharedPreferences = getActivity().getSharedPreferences(IatSettings.PREFER_NAME, Activity.MODE_PRIVATE);
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(getContext(), mInitListener);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(getContext(), mInitListener);

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headView = navigationView.inflateHeaderView(R.layout.nav_header);
       
        mTaskStartDate = getActivity().findViewById(R.id.title_text);
        mMaterialCalendarView = root.findViewById(R.id.mcv_calendar);
        mMaterialCalendarView.state().edit()
                //设置周一为第一天
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        mMaterialCalendarView.setSelectionColor(getResources().getColor(R.color.colorGreen));
        mMaterialCalendarView.setSelectedDate(new Date());
        mMaterialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull final CalendarDay date, boolean selected) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mTasks.clear();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Log.d(TAG, "onDateSelected: " + simpleDateFormat.format(date.getDate()));
                            mPresenter.loadTasks(simpleDateFormat.format(date.getDate()));
                            showAllTasks(mTasks);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = root.findViewById(R.id.rv_calender);
        mRecyclerView.setLayoutManager(layoutManager);
        mNoTasksView = root.findViewById(R.id.notasks);
        // Set up floating action button
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_add_task);

//        fab.setImageResource(R.drawable.ic_add);
        //悬浮按钮的点击事件：跳转到添加Task页面
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: add");
                try {
                    mPresenter.showAddTask();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //悬浮按钮长按时间，跳转到讯飞语音听写
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    if (null == mIat) {
                        // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
                        showToast("创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化");
                        return true;
                    }
                    Log.d(TAG, "onLongClick: add");
                    //移动数据分析，收集开始听写事件
                    FlowerCollector.onEvent(getContext(), "iat_recognize");
                    mIatResults.clear();
                    //设置参数
                    setParam();
//                ret = mIat.startListening(mRecognizerListener);
                    //显示听写对话框
                    mIatDialog.setListener(mRecognizerDialogListener);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mIatDialog.show();
                        }
                    });
                    //获取字体所在的控件，设置为"",隐藏字体，
                    TextView txt = (TextView) mIatDialog.getWindow().getDecorView().findViewWithTag("textlink");
                    txt.setText("");
                    if (ret != ErrorCode.SUCCESS) {
                        showToast("听写失败，错误码" + ret);
                    } else {
                        showToast("请开始说话...");
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
            }
        });
        mPresenter.start();
        return root;
    }

    int ret = 0; //函数调用返回值

    @Override
    public void setPresenter(CalendarContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setLoadingTasksError() {
        showToast("加载材料异常");
    }

    @Override
    public void showAllTasks(List<Task> tasks) {
        try {
            mTasks = tasks;
            mTasksAdapter = new TasksAdapter(mTasks);
            mTasksAdapter.setOnItemClickListener(new TasksAdapter.TaskItemListener() {
                @Override
                public void onTaskItemClick(View view, final int pos) {
                    try{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mPresenter.showTaskDetail(mTasks.get(pos));
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCompleteTaskClick(View view, final int pos) {
                    try{
                        mPresenter.completeTask(mTasks.get(pos));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTasks.get(pos).setFinished(true);
                                mPresenter.completeTask(mTasks.get(pos));
                                mTasksAdapter.notifyDataSetChanged();
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onActivateTaskClick(View view, final int pos) {
                    try{
                        mPresenter.activateTask(mTasks.get(pos));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTasks.get(pos).setFinished(false);
                                mPresenter.activateTask(mTasks.get(pos));
                                mTasksAdapter.notifyDataSetChanged();
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onTaskItemLongClick(View view, final int pos) {
                    try{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("删除？")
                                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                mPresenter.deleteTask(mTasks.get(pos));
                                                mTasks.remove(pos);
                                                mTasksAdapter.notifyDataSetChanged();
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
            });
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.setAdapter(mTasksAdapter);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mNoTasksView.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showAddTask(Map<String, String> map) {
        Log.d(TAG, "showAddTask: showAddTask::");
        Intent intent = new Intent(getContext(), AddTaskActivity.class);
        intent.putExtra("TASKNAME", map.get("TASKNAME"));
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void showTaskDetail(Task task) {
        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        intent.putExtra("TASK", task);
        startActivity(intent);
    }

    @Override
    public void showNoTasks() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setVisibility(View.GONE);
                mNoTasksView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean isActive() {
        return isAdded();
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
    public void addDecorator(Collection<CalendarDay> calendarDays) {
        try {
            mMaterialCalendarView.addDecorator(new EventDecorator(Color.BLUE, calendarDays));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startVoiceService(String result) {
        try {
            Intent intent = new Intent(getContext(), VoiceService.class);
            intent.putExtra("RESULT", result);
            getContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //**********************语音部分*********************
    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showToast("初始化失败，错误码：" + code);
            }
        }
    };

    //处理返回的数据
    private String printResult(RecognizerResult results) {
//        String text = XunfeiJsonParser.parseIatResult(results.getResultString());
        String text = XunfeiJsonParser.parseIatResult(results.getResultString());
        Log.d(TAG, "printResult: text" + text);
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
            Log.d(TAG, "printResult: sn" + sn);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        //将每一段拼成一个句子
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
            Log.d(TAG, "printResult: " + mIatResults.get(key));
        }
        return resultBuffer.toString();

    }

    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        this.mTranslateEnable = mSharedPreferences.getBoolean("translate", false);
        if (mTranslateEnable) {
            Log.i(TAG, "translate enable");
            mIat.setParameter(SpeechConstant.ASR_SCH, "1");
            mIat.setParameter(SpeechConstant.ADD_CAP, "translate");
            mIat.setParameter(SpeechConstant.TRS_SRC, "its");
        }

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIat.setParameter(SpeechConstant.ACCENT, null);

            if (mTranslateEnable) {
                mIat.setParameter(SpeechConstant.ORI_LANG, "en");
                mIat.setParameter(SpeechConstant.TRANS_LANG, "cn");
            }
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);

            if (mTranslateEnable) {
                mIat.setParameter(SpeechConstant.ORI_LANG, "cn");
                mIat.setParameter(SpeechConstant.TRANS_LANG, "en");
            }
        }
        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    //听写UI监视器
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            Log.d(TAG, recognizerResult.getResultString());
            String printResult = printResult(recognizerResult);
            Log.d(TAG, "onResult: " + printResult);
            if (isLast) {
                // TODO 最后的结果
                mPresenter.startVoiceService(printResult);
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            showToast(speechError.getPlainDescription(true));
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mIat) {
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
    }

    @Override
    public void onPause() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(getContext());
        super.onPause();
    }
}
