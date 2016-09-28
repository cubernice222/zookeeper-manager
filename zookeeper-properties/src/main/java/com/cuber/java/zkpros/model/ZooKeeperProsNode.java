package com.cuber.java.zkpros.model;

/**
 * Created by cuber on 2016/9/29.
 */
public class ZooKeeperProsNode {
    private String name;
    private String fullPath;
    private String desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
