package com.ljh.gtd3.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.AlarmClock;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ljh.gtd3.allTask.AllTasksActivity;
import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsLocalDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.tasksSource.TasksDataSource;
import com.ljh.gtd3.data.tasksSource.TasksLocalDataSource;
import com.ljh.gtd3.data.tasksSource.TasksRepository;
import com.ljh.gtd3.data.entity.Operation;
import com.ljh.gtd3.data.entity.Task;
import com.ljh.gtd3.data.operationSource.OperationDataSource;
import com.ljh.gtd3.data.operationSource.OperationsRepository;
import com.ljh.gtd3.listDetail.ListDetailActicity;
import com.ljh.gtd3.taskDetail.TaskDetailActivity;
import com.ljh.gtd3.voiceResult.VoiceResultActivity;

import org.apache.lucene.analysis.Analyzer;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class VoiceController {
    private static final String TAG = VoiceController.class.getSimpleName();
    ListsRepository mListsRepository;
    TasksRepository mTasksRepository;
    private List<String> mDictionaryList;  //词库
    private List<Operation> mOperationList; //操作命令词
    private List<Object> mObjectList;  //对象命令词
    private List<String> mSegmentResults;  //分词结果列表

    private AppExecutors mAppExecutors;
    private String originalText = null;   //原始语音文本
    private String operationKey = null;   //分词中的操作命令词
    private Integer objectKey = null; //分词中的对象命令词
    private Integer objectType;    //对象命令词
    private String dateKey = "";  //分词中的时间词
    private String contentKey = "";  //分词中的其他内容
    private int defaultListId; //“收集箱id”
    private Context mContext;

    class Object {
        String name;
        Integer objectId;
        int type; //0为listGroup, 1为list, 2为Task，3为其他
    }

    public void start(Context context, String text) {
        mContext = context;
        mDictionaryList = new ArrayList<>();
        mOperationList = new ArrayList<>();
        mObjectList = new ArrayList<>();
        mAppExecutors = new AppExecutors();
        originalText = text;
        mListsRepository = ListsRepository.getInstance(ListsLocalDataSource.getInstance(mAppExecutors));
        mTasksRepository = TasksRepository.getInstance(TasksLocalDataSource.getInstance(mAppExecutors));
        try {
            loadDictionaryAndObject();  //加载词库和对象命令词
            loadOperation();   //加载操作命令词
            SystemClock.sleep(500);
            getIkAnalyzer(text);   //获得分词列表
            handleSegmentation();  //处理分词列表
            createControlOrder(); //生成控制代码
        } catch (Exception e) {
            e.printStackTrace();
            toVoiceResultActivity();
        }
    }

    //生成控制代码
    private void createControlOrder() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d(TAG, "createControlOrder: operation::" + operationKey);
        Log.d(TAG, "createControlOrder: object::" + objectKey);
        Log.d(TAG, "createControlOrder: objectType::" + objectType);
        Log.d(TAG, "createControlOrder: time::" + dateKey);
        if (operationKey != null) {
            switch (operationKey) {
                case "打开":
                    if (objectKey != null) {
                        switch (objectType) {
                            case 0:  //为listGroup，跳转到清单列表
//                                Intent listGroupIntent = new Intent(mContext, ListGroupActivity.class);
//                                listGroupIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//                                mContext.startActivity(listGroupIntent);
                                break;
                            case 1:  //为list,跳转到对应的list页面
                                Intent listIntent = new Intent(mContext, ListDetailActicity.class);
                                listIntent.putExtra("LISTID", objectKey);
                                listIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(listIntent);
                                break;
                            case 2:  //为Task, 跳转到对应的Task
                                toTaskDetailActivity();
                                break;
                            case 3:  //为闹钟，跳转到闹钟
                                Intent alarms = new Intent(AlarmClock.ACTION_SET_ALARM);
                                alarms.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                if (alarms.resolveActivity(mContext.getPackageManager()) != null) {
                                    mContext.startActivity(alarms);
                                }
                                break;
                        }
                    } else {
                        //打开命令中没有对象命令词-》打开语音转文字，让用户操作
                        toVoiceResultActivity();
                    }
                    break;
                case "关闭":
//                    android.os.Process.killProcess(android.os.Process.myPid());

                    int currentVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(startMain);
                        System.exit(0);
                    } else {// android2.1
                        ActivityManager am = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
                        am.restartPackage(mContext.getPackageName());
                    }

                    break;
                case "添加":
                case "新增":
                case "增加":
                    Task task = new Task();
                    if (contentKey != null) {
                        Log.d(TAG, "createControlOrder: content key " + contentKey);
                        task.setName(contentKey);
                    } else {
                        task.setName("未设置内容");
                    }
                    task.setFinished(false);
                    task.setGmtCreate(simpleDateFormat.format(new Date()));
                    task.setGmtModified(simpleDateFormat.format(new Date()));
                    task.setPriority(0);
                    if (objectKey != null && objectType == 1) {  //对象为列表
//                        task.setId(objectKey);
                    } else {
                        task.setId(defaultListId);   //如果未找到list对象，则添加到默认的收集箱中
                    }
                    if (dateKey != null && !dateKey.equals("")) {
                        task.setStartTime(dateKey);
                    }
                    Log.d(TAG, "createControlOrder: " + task.getId());
                    mTasksRepository.addTask(task);
                    toTaskDetailActivity(task.getId()); //跳转到对应的TaskDetail页面
                    break;
                case "完成":
                    if (objectKey != null && objectType == 2) {  //对象为Task
                        Task finishedTask = new Task();
                        finishedTask.setId(objectKey);
                        finishedTask.setFinished(true);
                        finishedTask.setGmtModified(simpleDateFormat.format(new Date()));
                        mTasksRepository.updateTask(finishedTask);
                        toAllTasksActivity();
                    } else {
                        //命令中没有对象命令词-》打开语音转文字，让用户操作
                        toVoiceResultActivity();
                    }
                    break;
                case "设置":
                case "修改":
                    if (objectKey != null && objectType == 2) { //只修改Task
                        Task updateTask = new Task();
                        updateTask.setId(objectKey);
                        if (contentKey != null) {
                            updateTask.setName(contentKey);
                        } else {
                            updateTask.setName("未设置内容");
                        }
                        if (dateKey != null && !dateKey.equals("")) {
                            updateTask.setStartTime(dateKey);
                        }
                        updateTask.setGmtModified(simpleDateFormat.format(new Date()));
                        mTasksRepository.updateTask(updateTask);
                        toTaskDetailActivity(); //跳转到对应的TaskDetail页面
                    } else {
                        //命令中没有对象命令词-》打开语音转文字，让用户操作
                        toVoiceResultActivity();
                    }
                    break;
                case "删除":  //只删除Task
                    if (objectKey != null && objectType == 2) {
                        mTasksRepository.deleteTask(objectKey);
                    } else {
                        //命令中没有对象命令词-》打开语音转文字，让用户操作
                        toVoiceResultActivity();
                    }
                    break;
            }
        } else {  // 没有操作词
            if (objectKey != null) {  //有对象命令词，跳转到对应的页面
                switch (objectType) {
//                    case 0:  //为listGroup，跳转到清单列表
//                        Intent listGroupIntent = new Intent(mContext, ListGroupActivity.class);
//                        listGroupIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//                        mContext.startActivity(listGroupIntent);
//                        break;
                    case 1:  //为list,跳转到对应的list页面
                        Intent listIntent = new Intent(mContext, ListDetailActicity.class);
                        listIntent.putExtra("LISTID", objectKey);
                        listIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(listIntent);
                        break;
                    case 2:  //为Task, 跳转到对应的Task
                        toTaskDetailActivity();
                        break;
                    case 3:  //为闹钟，跳转到闹钟
                        Intent alarms = new Intent(AlarmClock.ACTION_SET_ALARM);
                        alarms.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(alarms);
                        break;
                }
            } else {//没有操作命令词、没有对象命令词，则视为Task添加
                Task task = new Task();
                if (contentKey != null) {
                    task.setName(contentKey);
                } else {
                    task.setName("未设置内容");
                }
                task.setFinished(false);
                task.setGmtCreate(simpleDateFormat.format(new Date()));
                task.setGmtModified(simpleDateFormat.format(new Date()));
                task.setPriority(0);
                task.setId(defaultListId);   //如果未找到list对象，则添加到默认的收集箱中
                if (dateKey != null && !dateKey.equals("")) {
                    Log.d(TAG, "createControlOrder: set time ");
                    task.setStartTime(dateKey);
                }
                mTasksRepository.addTask(task);
//                mListsRepository.updateTasksNum(defaultListId);
                toTaskDetailActivity(task.getId()); //跳转到对应的TaskDetail页面
            }
        }
    }

    private void toVoiceResultActivity() {
        try {
            Intent intent = new Intent(mContext, VoiceResultActivity.class);
            intent.putExtra("RESULT", originalText);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toTaskDetailActivity() {
        try {
            Intent intent = new Intent(mContext, TaskDetailActivity.class);
            intent.putExtra("TaskID", objectKey);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toTaskDetailActivity(int TaskId) {
        try {
            Intent intent = new Intent(mContext, TaskDetailActivity.class);
            intent.putExtra("TASK", TaskId);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toAllTasksActivity() {
        try {
            Intent intent = new Intent(mContext, AllTasksActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("REFRESH", true);
            mContext.startActivity(intent);  //跳转到所有Tasks页面
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //处理分词列表
    private void handleSegmentation() {
        Log.d(TAG, "handleSegmentation: operation长度" + mOperationList.size());
        Log.d(TAG, "handleSegmentation: object长度" + mObjectList.size());
        boolean hasOperationKey = false;
        boolean hasObjectKey = false;
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < mSegmentResults.size(); i++) {
            if (!hasOperationKey && isOperation(mSegmentResults.get(i))) { //没有操作词时则寻找操作词
                hasOperationKey = true;
                continue;
            }
            if (!hasObjectKey && isObject(mSegmentResults.get(i))) {  //没有对象词时寻找对象词
                hasObjectKey = true;
                continue;
            }
            stringBuffer.append(mSegmentResults.get(i));
        }
        contentKey = stringBuffer.toString();  //其他所有词添加到content中
        //正则判断是否有时间词
        hasTime(contentKey);
    }

    private void hasTime(String contentKey) {
        Log.d(TAG, "hasTime: 返回的内容" + contentKey);
        //判断是否存在时间词
        String reg = "[(([0-9]{4}[年]){0,1}([0-1]{0,1}[0-9]{0,1}[月]){0,1}([0-9]{1,2}[日]){0,1}([0-2]{0,1}[0-9]{0,1}[点|时]){0,1}([0-5]{0,1}[0-9]{0,1}[分]){0,1})|上午|下午|晚上|傍晚]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(contentKey);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            System.out.println(matcher.group() + "  " + matcher.start() + "  " + matcher.end());
            stringBuffer.append(matcher.group());
        }
        System.out.println(stringBuffer.toString());
        if (stringBuffer.length() == 0) {  //不存在时间词，退出
            System.out.println("不存在时间词");
            return;
        }
        String year = null;
        String month = null;
        String date = null;
        String hour = null;
        String minute = null;
        //是否存在年
        String yearReg = "[0-9]{4}[年]";
        Pattern yearPattern = Pattern.compile(yearReg);
        Matcher yearMatcher = yearPattern.matcher(contentKey);
        if (yearMatcher.find()) {
            System.out.println("年份匹配");
            year = yearMatcher.group().substring(0, 4);
//            year = getNumber(yearMatcher.group());
            System.out.println(year);
        }
        //是否存在月
        String monthReg = "[0-1]{0,1}[0-9]{0,1}[月]";
        Pattern monthPattern = Pattern.compile(monthReg);
        Matcher monthMatcher = monthPattern.matcher(contentKey);
//        Log.d(TAG, "hasTime: 月匹配吗？" + monthMatcher.find());
        if (monthMatcher.find()) {
            System.out.println("月匹配");
            month = monthMatcher.group().substring(0, monthMatcher.group().length() - 1);
//            month = getNumber(monthMatcher.group());
            if (Integer.parseInt(month) > 12) {
                month = null;
            }
        }
        //是否存在日
        String dateReg = "[0-9]{1,2}[日]";
        Pattern datePattern = Pattern.compile(dateReg);
        Matcher dateMatcher = datePattern.matcher(contentKey);
        if (dateMatcher.find()) {
            System.out.println("日匹配");
            date = dateMatcher.group().substring(0, dateMatcher.group().length() - 1);  //获取“日”前的数字
//            date = dateMatcher.group();
            if (Integer.parseInt(date) > 31) { //判断是否小于31
                date = null;  //大于31，则置date = null
            }
        }
        if (contentKey.contains("明天")) {
            date = String.valueOf(new Date().getDate() + 1);
        }
        if (contentKey.contains("后天")) {
            date = String.valueOf(new Date().getDate() + 2);
        }
        String hourReg = "[0-2]{0,1}[0-9]{0,1}[点|时]";
        Pattern hourPattern = Pattern.compile(hourReg);
        Matcher hourMatcher = hourPattern.matcher(contentKey);
        if (hourMatcher.find()) {
            System.out.println("时匹配");

            hour = hourMatcher.group(); //获取“点|时”前的数字
            if (hour.length() != 1) {
                hour = hour.substring(0, hour.length() - 1);
//                hour = getNumber(hour);
                if (Integer.parseInt(hour) > 25) {
                    hour = null;
                }
            }
        }
        String hourReg1 = "[下午|傍晚|晚上][0-2]{0,1}[0-9]{0,1}[点|时]";
        Pattern hourPattern1 = Pattern.compile(hourReg1);
        Matcher hourMatcher1 = hourPattern1.matcher(contentKey);
        if (hourMatcher1.find()) {
            hour = hourMatcher1.group(); //获取“点|时”前的数字
            hour = hour.substring(0, hour.length() - 1);
//            hour = getNumber(hourMatcher1.group());
            if (Integer.parseInt(hour) > 12) {
                hour = null;
            } else {
                hour = String.valueOf(Integer.parseInt(hour) + 12);
            }
            System.out.println(hour);
        }
        String minuteReg = "[0-5]{0,1}[0-9]{0,1}[分]";
        Pattern minutePattern = Pattern.compile(minuteReg);
        Matcher minuteMatcher = minutePattern.matcher(contentKey);
        if (minuteMatcher.find()) {
            System.out.println("分匹配" + minuteMatcher.group());
            minute = minuteMatcher.group().substring(0, minuteMatcher.group().length() - 1);
//            minute = getNumber(minuteMatcher.group());
            if (Integer.parseInt(minute) > 60) {
                minute = null;
            }
        }
        if (year == null && month == null && date == null && hour == null && minute == null) {
            dateKey = null;
            System.out.println("不存在相关的时间词");
            return;
        }
        if (year != null) {
            dateKey += year;
        } else {
            dateKey += Calendar.getInstance().get(Calendar.YEAR);
            ;
        }
        if (month != null) {
            dateKey += ("-" + month);
        } else {
            dateKey += ("-" + (Calendar.getInstance().get(Calendar.MONTH) + 1));
        }
        if (date != null) {
            dateKey += ("-" + date);
        } else {
            dateKey += ("-" + Calendar.getInstance().get(Calendar.DATE));
        }
        if (hour != null) {
            dateKey += (" " + hour);
        } else {
            dateKey += (" " + Calendar.getInstance().get(Calendar.HOUR));
        }
        if (minute != null) {
            dateKey += (":" + minute);
        } else {
            dateKey += (":" + Calendar.getInstance().get(Calendar.MINUTE));
        }
        dateKey += ":00";
        Log.d(TAG, "hasTime: " + dateKey);
        //判断日期格式是否为"yyyy-MM-dd HH:mm:ss"
        if (!DateUtil.isRightDateStr(dateKey)) {
            dateKey = null;
        }
    }

    @NonNull
    private String getNumber(String matcherResult) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(matcherResult);
        String result = m.replaceAll("").trim();
        Log.d(TAG, "getNumber: " + result);
        return result;
    }

    //词是否为对象词
    private boolean isObject(String word) {
        for (Object object : mObjectList) {
            if (object.name.equals(word)) {  //第二个分词为
                objectKey = object.objectId;
                objectType = object.type;
                return true;
            }
        }
        return false;
    }

    //词是否为操作词
    private boolean isOperation(String word) {
        Log.d(TAG, "isOperation: " + word + mOperationList.size());
        for (Operation operation : mOperationList) {
            if (operation.getName().equals(word)) {  //第一个词为操作词
                operationKey = operation.getName();
                return true;
            }
        }
        return false;
    }

    //获得分词列表
    private void getIkAnalyzer(String text) {
        mSegmentResults = new ArrayList<>(); //初始化分词列表
        //创建词库
        Dictionary.initial(DefaultConfig.getInstance());
        Dictionary.getSingleton().addWords(mDictionaryList);//添加词库
        //创建分词对象
        Analyzer analyzer = new IKAnalyzer(true);
        StringReader reader = new StringReader(text);
        IKSegmenter iks = new IKSegmenter(reader, true);  //只能分词
        Lexeme t;
        try {
            while ((t = iks.next()) != null) {
                mSegmentResults.add(t.getLexemeText());
                Log.d(TAG, "getIkAnalyzer: " + t.getLexemeText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载操作词
     */
    private void loadOperation() {
        OperationsRepository operationsRepository = OperationsRepository.getInstance(mAppExecutors);
        operationsRepository.getOperations(new OperationDataSource.GetOperationsCallBack() {
            @Override
            public void onOperationsLoad(List<Operation> operations, String message) {
                Log.d(TAG, "onOperationsLoad: 加载操作词");
                mOperationList.addAll(operations);
            }

            @Override
            public void onOperationsFail(String message) {

            }
        });
    }

    //加载词库和对象命令词
    private void loadDictionaryAndObject() {

        mListsRepository.GetLists(new ListsDataSource.GetListsCallBack() {
            @Override
            public void onListsLoaded(List<com.ljh.gtd3.data.entity.List> lists, String message) {
                for (com.ljh.gtd3.data.entity.List list : lists) {
                    if (list.getName().equals("收集箱")) {
                        defaultListId = list.getId();
                    }
                    Object object = new Object();
                    object.name = list.getName();
                    object.objectId = list.getId();
                    object.type = 1;
                    mObjectList.add(object);  //添加对象命令词
                    mDictionaryList.add(list.getName());  ///添加词库
                }
            }

            @Override
            public void onListsFail(String message) {

            }
        });
        mTasksRepository.getAllTasks(new TasksDataSource.GetTasksCallBack() {
            @Override
            public void onTasksLoaded(List<Task> tasks, String message) {
                for (Task task : tasks) {
                    Object object = new Object();
                    object.name = task.getName();
                    object.objectId = task.getId();
                    object.type = 2;
                    mObjectList.add(object);  //添加对象命令词
                    mDictionaryList.add(task.getName()); //添加词库
                }
            }

            @Override
            public void onTasksFail(String message) {

            }
        });
        Object object = new Object();
//        object.objectId = UUID.randomUUID().toString();
        object.name = "闹钟";
        object.type = 3;
        mObjectList.add(object);
    }
}