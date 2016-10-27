package com.cuber.zkweb.config;


import com.cuber.java.zkpros.constvar.ZooKeeperConst;
import com.cuber.java.zkpros.model.ZooKeeperEnviromentNode;
import com.cuber.java.zkpros.model.ZooKeeperProjectNode;
import com.cuber.zkweb.model.EnvEnum;
import com.cuber.zkweb.util.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * Created by cuber on 2016/9/5.
 */
@Configuration
public class ZooKeeperConfig {

    @Value("${zookeeper.server.list}")
    private String zkServerList;

    @Value("${zookeeper.manage.spkeyInitPwd}")
    private String spKey;

    @Value("${zookeeper.manage.envAccKey}")
    private String accKey;

    @Bean(name = "zkClient")
    ZkClient initBean(){
        ZkClient zkClient = new ZkClient(zkServerList);
        if(!zkClient.exists(ZooKeeperConst.ZKROOT)){//创建配置中心root根路径
            zkClient.create(ZooKeeperConst.ZKROOT,"zookeeper config center", CreateMode.PERSISTENT);
        }
        ZooKeeperProjectNode node = new ZooKeeperProjectNode();
        node.setName("public_config");
        node.setDesc("zookeeper 通配中心");
        node.setParentPath(ZooKeeperConst.ZKROOT);

        if(!zkClient.exists(ZooKeeperConst.PUBLICCONFIG)){
            ZkUtils.createNode(node, zkClient);
        }else{
            ZkUtils.update(node, zkClient);
        }

        if(!zkClient.exists(ZooKeeperConst.ZKSPKEY)){//创建3D加密KEY
            zkClient.create(ZooKeeperConst.ZKSPKEY,spKey, CreateMode.PERSISTENT);
        }

        ZooKeeperEnviromentNode devNode = new ZooKeeperEnviromentNode();//
        devNode.setParentPath(ZooKeeperConst.ZKROOT);
        devNode.setAccRule("ROLE_Zkdev");
        devNode.setAccKey(accKey);
        devNode.setDesc("开发环境");
        devNode.setName("dev");

        ZkUtils.createNode(devNode,zkClient);//如果创建开发环境

        ZooKeeperEnviromentNode sitNode = new ZooKeeperEnviromentNode();
        BeanUtils.copyProperties(devNode, sitNode);
        sitNode.setAccRule("ROLE_Zksit");
        devNode.setAccKey(accKey);
        sitNode.setDesc("测试环境");
        sitNode.setName("sit");

        ZkUtils.createNode(sitNode,zkClient);//如果创建开发环境


        ZooKeeperEnviromentNode preNode = new ZooKeeperEnviromentNode();
        BeanUtils.copyProperties(devNode, preNode);
        preNode.setAccRule("ROLE_Zkpre");
        preNode.setDesc("预上线环境");
        preNode.setName("pre");

        ZkUtils.createNode(preNode,zkClient);//如果创建开发环境


        ZooKeeperEnviromentNode prodNode = new ZooKeeperEnviromentNode();
        BeanUtils.copyProperties(devNode, prodNode);
        prodNode.setAccRule("ROLE_Zkprod");
        prodNode.setDesc("生产环境");
        prodNode.setName("prod");

        ZkUtils.createNode(prodNode,zkClient);//如果创建开发环境

        return zkClient;
    }

}
