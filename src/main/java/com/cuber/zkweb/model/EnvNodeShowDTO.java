package com.cuber.zkweb.model;

import com.cuber.java.zkpros.model.ZooKeeperEnviromentNode;

/**
 * Created by cuber on 2016/10/28.
 */
public class EnvNodeShowDTO extends ZooKeeperEnviromentNode{
    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
