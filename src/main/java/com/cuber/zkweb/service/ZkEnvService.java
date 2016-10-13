package com.cuber.zkweb.service;

import com.cuber.java.zkpros.model.ZooKeeperEnviromentNode;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by cuber on 2016/10/13.
 */
public class ZkEnvService {

    @Autowired
    private ZkClient zkClient;
    public boolean createEnv(ZooKeeperEnviromentNode zkenv){
        if(zkenv != null){

        }
        return false;
    }
}
