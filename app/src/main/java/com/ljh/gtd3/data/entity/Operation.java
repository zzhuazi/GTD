package com.ljh.gtd3.data.entity;

import org.litepal.crud.DataSupport;

public class Operation extends DataSupport {

    private String operationId;
    private String name;

    public String getId() {
        return operationId;
    }

    public void setId(String id) {
        this.operationId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
