package com.ljh.gtd3.data;

/**
 * Created by Administrator on 2018/3/12.
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
