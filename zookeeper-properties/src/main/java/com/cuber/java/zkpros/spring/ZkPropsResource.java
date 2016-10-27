package com.cuber.java.zkpros.spring;

import com.cuber.java.zkpros.constvar.ZooKeeperConst;
import com.cuber.java.zkpros.model.ZooKeeperEnviromentNode;
import com.cuber.java.zkpros.model.ZooKeeperNode;
import com.cuber.java.zkpros.model.ZooKeeperProsNode;
import com.cuber.java.zkpros.utils.Endecrypt;
import com.google.gson.Gson;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Properties;


/**
 * Created by cuber on 2016/9/6.
 */
public class ZkPropsResource {
    protected final Log logger = LogFactory.getLog(getClass());
    /**
     * /opt/pay/config.properties
     */
    private Resource zkconfigMainPath;

    /**
     * 项目名称
     */
    private String zkprojectName;

    /**
     * zookeeper主要配置目录
     */
    private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

    public PropertiesPersister getPropertiesPersister() {
        return propertiesPersister;
    }

    public void setPropertiesPersister(PropertiesPersister propertiesPersister) {
        this.propertiesPersister = propertiesPersister;
    }

    private Properties result = new Properties();

    public Properties getResult() {
        return result;
    }

    public void setResult(Properties result) {
        this.result = result;
    }

    public ZkPropsResource() {
    }

    public ZkPropsResource(Resource zkconfigMainPath, String zkprojectName) throws Exception{
        this.zkconfigMainPath = zkconfigMainPath;
        this.zkprojectName = zkprojectName;
        EncodedResource resource = new EncodedResource(zkconfigMainPath, "UTF-8");
        try {
            InputStream stream = null;
            Reader reader = null;
            try {
                if (resource.requiresReader()) {
                    reader = resource.getReader();
                    propertiesPersister.load(result, reader);
                } else {
                    stream = resource.getInputStream();
                    propertiesPersister.load(result, stream);
                }
            }
            finally {
                if (stream != null) {
                    stream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
        } catch (IOException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not load properties from " + zkconfigMainPath + ": " + ex.getMessage());
            }
            else {
                throw ex;
            }
        }
        String zkServerLists = result.getProperty("zookeeper.server.lists");
        String envString = result.getProperty("zookeeper.manage.env");
        String accCode = result.getProperty("zookeeper.manage.acckey");
        ZkClient zkClient = new ZkClient(zkServerLists);
        if(zkClient != null){
            result = accEnv(envString,zkClient,result,zkprojectName,accCode);
        }
    }





    public Properties accEnv(String env, ZkClient zkClient, Properties properties, String projectName, String accCode){
        String envPath = ZooKeeperConst.ZKROOT + "/" + env;
        ZooKeeperEnviromentNode envObj = getZkObj(envPath,zkClient,ZooKeeperEnviromentNode.class);
        if(null!=envObj){
            String acckey = envObj.getAccKey();
            if(null != acckey && acckey.equals(accCode)){
                properties = accProject(ZooKeeperConst.PUBLICCONFIG,zkClient,properties);
                properties = accProject(envPath + "/" +  projectName,
                        zkClient,properties);
            }else{
                throw new BeanInitializationException("access key [" + acckey + "] did't match");
            }
        }else{
            throw new BeanInitializationException("can find Env [" +  env + "]");
        }
        return properties;
    }

    public Properties accProject(String projectPath, ZkClient zkClient,Properties properties){
        if(zkClient.exists(projectPath)){
            List<String> nodes = zkClient.getChildren(projectPath);
            if(nodes != null && nodes.size() > 0){
                String nodePath = null;
                for (String nodeName: nodes
                        ) {
                    nodePath = projectPath + "/" + nodeName;
                    ZooKeeperProsNode envObj = getZkObj(nodePath,zkClient,ZooKeeperProsNode.class);
                    if("3".equals(envObj.getType())){
                        properties.putAll(accProject(nodePath,zkClient,properties));
                    }else{
                        ZooKeeperNode nodeObj = getZkObj(nodePath,zkClient,ZooKeeperNode.class);
                        String value = nodeObj.getValue();
                        if(nodeObj.isMask()){
                            Endecrypt decrypt = new Endecrypt();
                            value = decrypt.get3DESDecrypt(value,nodeObj.getMaskKey());
                        }
                        properties.put(nodeName,value);
                    }
                }
            }
        }
        return properties;
    }


    private <T extends ZooKeeperProsNode> T getZkObj(String path, ZkClient zkClient, Class<T> clazz){
        if( zkClient.exists(path)){
            String value = zkClient.readData(path);
            Gson gson = new Gson();
            T zkObj= gson.fromJson(value,clazz);
            return zkObj;
        }
        return null;
    }

    public Log getLogger() {
        return logger;
    }

    public Resource getZkconfigMainPath() {
        return zkconfigMainPath;
    }

    public void setZkconfigMainPath(Resource zkconfigMainPath) {
        this.zkconfigMainPath = zkconfigMainPath;
    }

    public String getZkprojectName() {
        return zkprojectName;
    }

    public void setZkprojectName(String zkprojectName) {
        this.zkprojectName = zkprojectName;
    }

}
