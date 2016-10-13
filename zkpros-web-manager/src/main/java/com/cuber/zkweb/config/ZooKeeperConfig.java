package com.cuber.zkweb.config;


import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by cuber on 2016/9/5.
 */
@Configuration
public class ZooKeeperConfig {

    @Value("${zookeeper.server.list}")
    private String zkServerList;

    @Value("${zookeeper.manage.mainpath}")
    private String mainpath;


    @Bean(name = "zkClient")
    ZkClient initBean(){
        ZkClient zkClient = new ZkClient(zkServerList);
        //checkEnv(zkClient);
        return zkClient;
    }

}
