package com.ljh.gtd3.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 判断日期格式是否为"yyyy-MM-dd HH:mm:ss"
 */
public class DateUtil {

    private static final String TAG = DateUtil.class.getSimpleName();

    /**
     * 输入日期，判断日期格式是否为"yyyy-MM-dd HH:mm:ss"
     * @param dateStr
     * @return 返回判断成功与否
     */
    public static boolean isRightDateStr(String dateStr){
        if(dateStr.equals("") || dateStr.equals("null")) {
            return false;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //采用严格的格式解析，防止类似“2018-15-26 26:22:33”类型的字符串通过
            simpleDateFormat.setLenient(false);
            simpleDateFormat.parse(dateStr);
            Log.d(TAG, "isRightDateStr: true");
            return true;
        }catch (ParseException e){
            e.printStackTrace();
            return false;
        }
    }

    public static String formatDateStr(String dateStr){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //采用严格的格式解析，防止类似“2018-15-26 26:22:33”类型的字符串通过
            simpleDateFormat.setLenient(false);
            Date date = simpleDateFormat.parse(dateStr);
            return simpleDateFormat.format(date);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return "null";
    }
}
