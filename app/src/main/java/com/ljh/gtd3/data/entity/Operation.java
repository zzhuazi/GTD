package com.ljh.gtd3.data.entity;


import org.litepal.crud.LitePalSupport;

public class Operation extends LitePalSupport {

    private int operationId;
    private String name;

    public int getId() {
        return operationId;
    }

    public void setId(int id) {
        this.operationId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
