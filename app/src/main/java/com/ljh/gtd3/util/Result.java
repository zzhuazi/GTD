package com.ljh.gtd3.util;

/**
 * Created by Administrator on 2018/3/11.
 */

public class Result<T> {

    public int code;
    public String msg;
    public T data;

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
