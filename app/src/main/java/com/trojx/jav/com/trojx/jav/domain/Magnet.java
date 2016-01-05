package com.trojx.jav.com.trojx.jav.domain;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/1/4.
 */
public class Magnet implements Serializable {
    private String name="",value="";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
