package com.cuber.java.zkpros.model;

/**
 * Created by cuber on 2016/9/29.
 */
public class ZooKeeperNode extends ZooKeeperProsNode {
    private String value;
    private boolean mask;
    private String maskKey;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isMask() {
        return mask;
    }

    public void setMask(boolean mask) {
        this.mask = mask;
    }

    public String getMaskKey() {
        return maskKey;
    }

    public void setMaskKey(String maskKey) {
        this.maskKey = maskKey;
    }
}
