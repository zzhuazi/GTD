package com.ljh.gtd3.allStuff;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
import com.ljh.gtd3.data.entity.User;
import com.ljh.gtd3.service.VoiceService;
import com.ljh.gtd3.addStuff.AddStuffActivity;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.stuffDetail.StuffDetailActivity;
import com.ljh.gtd3.user.UserActivity;
import com.ljh.gtd3.util.IatSettings;
import com.ljh.gtd3.util.XunfeiJsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/13.
 */

public class AllStuffFragment extends Fragment implements AllStuffContract.View {
    public static final String TAG = AllStuffFragment.class.getSimpleName();
    private AllStuffContract.Presenter mPresenter;

    private ExpandableAdapter mExpandableAdapter;
    private ExpandableListView mExpandableListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mNoStuffsView;
    private ImageView mNoStuffsIcon;
    private TextView mNoStufsfMainView;

    private RelativeLayout mUserSettingRv;
    private TextView mUserName;
    private TextView mUserEmail;

    //显示正在录音
    private AlertDialog mAlertDialog;
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

    //    private List<com.ljh.gtd3.data.entity.List> mLists;
    private List<String> mStuffDates;
    private List<List<Stuff>> mStuffs;

    public static AllStuffFragment newInstance() {
        AllStuffFragment fragment = new AllStuffFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_all_stuff, container, false);
        mSharedPreferences = getActivity().getSharedPreferences(IatSettings.PREFER_NAME, Activity.MODE_PRIVATE);
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(getContext(), mInitListener);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(getContext(), mInitListener);
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headView = navigationView.inflateHeaderView(R.layout.nav_header);
        mUserSettingRv = headView.findViewById(R.id.rv_user_setting);
        mUserEmail = headView.findViewById(R.id.email);
        mUserName = headView.findViewById(R.id.username);
        mUserSettingRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.showUserSetting();
            }
        });
        mExpandableListView = root.findViewById(R.id.el_all_stuff);
        mNoStuffsView = root.findViewById(R.id.noStuffs);
        mNoStuffsIcon = root.findViewById(R.id.noStuffsIcon);
        mNoStufsfMainView = root.findViewById(R.id.noStuffsMain);
        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_stuff);

//        fab.setImageResource(R.drawable.ic_add);
        //悬浮按钮的点击事件：跳转到添加stuff页面
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: add");
                try {
                    mPresenter.showAddStuff();
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

        mSwipeRefreshLayout = root.findViewById(R.id.swipe_refresh_all_stuff);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    mPresenter.loadStuffs(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mPresenter.start();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            boolean refresh = getActivity().getIntent().getBooleanExtra("REFRESH", false);
            if (refresh) {
                Log.d(TAG, "onCreateView: 刷新刷新刷新");
                mPresenter.loadStuffs(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int ret = 0; //函数调用返回值

    @Override
    public void setPresenter(AllStuffContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void setLoadingStuffsError() {
        showToast("加载材料错误！");
    }

    @Override
    public void showAllStuffs(List<String> stuffDates, final List<List<Stuff>> stuffs) {
        try {
            mStuffDates = stuffDates;
            mStuffs = stuffs;
            mExpandableAdapter = new ExpandableAdapter(mStuffDates, mStuffs);
            mExpandableAdapter.setOnChildClickListener(new ExpandableAdapter.OnChildClickListener() {
                @Override
                public void onChildItemClickListener(View view, final int parentPos, final int childPos) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mPresenter.showStuffDetail(mStuffs.get(parentPos).get(childPos));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildItemLongClickListener(View view, final int parentPos, final int childPos) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("删除？")
                                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                mPresenter.deleteStuff(mStuffs.get(parentPos).get(childPos));
                                                mStuffs.get(parentPos).remove(childPos);
                                                mExpandableAdapter.notifyDataSetChanged();
                                                dialogInterface.dismiss();
                                            }
                                        });
                                builder.create().show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onActivateStuffClick(View view, final int parentPos, final int childPos) {
                    try {
                        mPresenter.activateStuff(mStuffs.get(parentPos).get(childPos));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mStuffs.get(parentPos).get(childPos).setFinished(false);
                                mExpandableAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCompleteStuffClick(View view, final int parentPos, final int childPos) {
                    try {
                        mPresenter.completeStuff(mStuffs.get(parentPos).get(childPos));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mStuffs.get(parentPos).get(childPos).setFinished(true);
                                mExpandableAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mExpandableListView.setAdapter(mExpandableAdapter);
                    mExpandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView absListView, int i) {

                        }

                        @Override
                        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            View firstView = absListView.getChildAt(firstVisibleItem);
                            if (firstVisibleItem == 0 && (firstView == null || firstView.getTop() == 0)) {
                                mSwipeRefreshLayout.setEnabled(true);
                            } else {
                                mSwipeRefreshLayout.setEnabled(false);
                            }
                        }
                    });
                    int count = mExpandableListView.getCount();
                    for (int i = 0; i < count; i++) {
                        mExpandableListView.expandGroup(i);
                    }
                    mExpandableListView.setVisibility(View.VISIBLE);
                    mNoStuffsView.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showAddStuff(Map<String, String> map) {
        try {
            Log.d(TAG, "showAddStuff: showAddStuff::");
            Intent intent = new Intent(getContext(), AddStuffActivity.class);
            intent.putExtra("STUFFNAME", map.get("STUFFNAME"));
            startActivity(intent);
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showStuffDetail(String stuffId) {
        try {
            Intent intent = new Intent(getContext(), StuffDetailActivity.class);
            intent.putExtra("STUFFID", stuffId);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showNoStuffs() {
        try {
            setLoadingIndicator(false);
            mExpandableListView.setVisibility(View.GONE);
            mNoStuffsView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void showUserSetting() {
        try {
            Intent intent = new Intent(getContext(), UserActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startVoiceService(String userId, String result) {
        try {
            Intent intent = new Intent(getContext(), VoiceService.class);
            intent.putExtra("USERID", userId);
            intent.putExtra("RESULT", result);
            getContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadUser(User user) {
        try {
            mUserEmail.setText(user.getEmail());
            mUserName.setText(user.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
