package com.cuber.zkweb.controller;

import com.cuber.java.zkpros.constvar.ZooKeeperConst;
import com.cuber.java.zkpros.model.*;
import com.cuber.java.zkpros.utils.Endecrypt;
import com.cuber.zkweb.model.Page;
import com.cuber.zkweb.model.ResponseMessage;
import com.cuber.zkweb.util.ZkUtils;
import com.google.gson.Gson;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cuber on 2016/10/13.
 */
@RestController
public class NodeController {
    @Autowired
    private ZkClient zkClient;

    @Value("${zookeeper.manage.spkeyInitPwd}")
    private String spKey;

    @RequestMapping("/postNode.json")
    public ResponseMessage postNode(HttpServletRequest request,ZooKeeperProsNode node){
        ResponseMessage responseMessage = new ResponseMessage();

        node = getRightZknode(request,node);

        try{
            if(!zkClient.exists(node.getParentPath() + "/" + node.getName())){
                ZkUtils.createNode(node,zkClient);
            }else{
                ZkUtils.update(node,zkClient);
            }
            responseMessage.setDone(true);
        }catch (Exception e){
            e.printStackTrace();
            responseMessage.setMessage(e.getMessage());
        }

        return responseMessage;
    }

    private ZooKeeperProsNode getRightZknode(HttpServletRequest request,ZooKeeperProsNode node){
        ZooKeeperProsNode returnObj = node;
        switch (node.getType()) {
            case "1":
                returnObj = ZkUtils.convert(ZooKeeperEnviromentNode.class, request);
                break;
            case "2":
                returnObj = ZkUtils.convert(ZooKeeperProjectNode.class, request);
                break;
            case "3":
                returnObj = ZkUtils.convert(ZooKeeperFolderNode.class, request);
                break;
            default:
                returnObj = ZkUtils.convert(ZooKeeperNode.class, request);
        }
        return returnObj;
    }

    @RequestMapping("/deleteNode.json")
    public ResponseMessage deleteNode(ZooKeeperProsNode node){
        ResponseMessage responseMessage = new ResponseMessage();
        try{
            if(zkClient.exists(node.getParentPath() + "/" + node.getName())){
                ZkUtils.deleteNode(node,zkClient );
                responseMessage.setDone(true);
            }
            responseMessage.setMessage("节点不存在，不能删除");
        }catch (Exception e){
            e.printStackTrace();
            responseMessage.setMessage(e.getMessage());
        }
        return responseMessage;
    }

    @RequestMapping("/getEncrypt.json")
    public ResponseMessage getEncrypt(String value){
        ResponseMessage responseMessage = new ResponseMessage();
        Map<String,String> map = new HashMap<>();
        String maskKey = spKey;
        try{
            if(zkClient.exists(ZooKeeperConst.ZKSPKEY)){
                maskKey = zkClient.readData(ZooKeeperConst.ZKSPKEY);
            }
            Endecrypt test = new Endecrypt();
            String mask = test.get3DESEncrypt(value, maskKey);
            map.put("maskKey",maskKey);
            map.put("mask",mask);
            Gson gson =  new Gson();
            responseMessage.setTransJsonValue(gson.toJson(map));
            responseMessage.setDone(true);
        }catch (Exception e){
            e.printStackTrace();
            responseMessage.setMessage(e.getMessage());
        }
        return responseMessage;
    }

    @RequestMapping("/getPage.json")
    public Page getChildrenNode(@RequestParam(value="path")String path, Page page){
        try{
            Page returnPage = ZkUtils.getPage(path,page,zkClient);
            return returnPage;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/postCopy.json")
    public ResponseMessage copyPath(@RequestParam(value="overWriten")boolean overWriten,
                                    ZooKeeperProsNode node, @RequestParam(value="path")String path){
        ResponseMessage responseMessage = new ResponseMessage();
        try{
            ZkUtils.copyNode(node,zkClient,path,overWriten);
            responseMessage.setDone(true);
        }catch (Exception e){
            e.printStackTrace();
            responseMessage.setMessage(e.getMessage());
        }
        return responseMessage;
    }

    public ResponseMessage getPromitionEnv(){
        return null;
    }

}
