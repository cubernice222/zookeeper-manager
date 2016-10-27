package com.cuber.zkweb.model;

/**
 * Created by cuber on 2016/10/27.
 */
public enum  RoleEnum {
    DEV("ROLE_Zkdev","开发环境"),
    SIT("ROLE_Zksit","测试环境"),
    PRE("ROLE_Zkpre","联调环境"),
    PROD("ROLE_Zkprod","生产环境");

    private String accRole;
    private String desc;

    public String getAccRole() {
        return accRole;
    }

    public void setAccRole(String accRole) {
        this.accRole = accRole;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    RoleEnum(String accRole, String desc){
        this.accRole = accRole;
        this.desc = desc;
    }

    public static boolean isExist(String accRole){
        RoleEnum[] allRoles = RoleEnum.values();
        for (RoleEnum role:allRoles
             ) {
            if(role.getAccRole().equals(accRole)){
                return true;
            }
        }
        return false;
    }

    public static RoleEnum getRoleByCode(String accRole){
        RoleEnum[] allRoles = RoleEnum.values();
        for (RoleEnum role:allRoles
                ) {
            if(role.getAccRole().equals(accRole)){
                return role;
            }
        }
        return null;
    }
}
