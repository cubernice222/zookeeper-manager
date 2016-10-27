package com.cuber.zkweb.model;

/**
 * Created by cuber on 2016/10/27.
 */
public enum EnvEnum {
    DEV("dev","开发环境"),
    SIT("sit","测试环境"),
    PRE("pre","联调环境"),
    PROD("prod","生产环境");

    private String envName;
    private String desc;

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    EnvEnum(String envName, String desc){
        this.envName = envName;
        this.desc = desc;
    }

    public static boolean isExist(String envName){
        EnvEnum[] envNames = EnvEnum.values();
        for (EnvEnum role:envNames
                ) {
            if(role.getEnvName().equals(envName)){
                return true;
            }
        }
        return false;
    }
}
