package com.cuber.java.zkpros.model;

/**
 * Created by cuber on 2016/9/29.
 */
public class ZooKeeperProsNode {
    private String name;
    private String parentPath;
    private String desc;
    private String type;

    public ZooKeeperProsNode(){

    }


    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
