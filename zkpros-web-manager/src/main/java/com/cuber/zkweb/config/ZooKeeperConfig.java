package com.ipaylinks.project.config;

import com.ipaylinks.project.server.ZkManagerService;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;


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
