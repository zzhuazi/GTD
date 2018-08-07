package com.ljh.gtd3.data.VO;

import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Stuff;

/**
 * Created by Administrator on 2018/3/14.
 */

public class ListVO  {
    private List list;
    private java.util.List<Stuff> stuffList;

    public ListVO() {
    }

    public ListVO(List list, java.util.List<Stuff> stuffList) {
        this.list = list;
        this.stuffList = stuffList;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public java.util.List<Stuff> getStuffList() {
        return stuffList;
    }

    public void setStuffList(java.util.List<Stuff> stuffList) {
        this.stuffList = stuffList;
    }
}
