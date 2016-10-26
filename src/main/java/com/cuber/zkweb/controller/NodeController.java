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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by cuber on 2016/10/13.
 */
@RestController
public class NodeController {
    @Autowired
    private ZkClient zkClient;

    @Value("${zookeeper.manage.spkeyInitPwd}")
    private String spKey;


    @RequestMapping(value = "/deletePros.json",method = RequestMethod.POST)
    public @ResponseBody
    ResponseMessage deletePros(HttpServletRequest request,
                               ZooKeeperNode zkpros){
        ResponseMessage responseMessage = new ResponseMessage();
        if(ZkUtils.haveRightAccessNode(zkClient,zkpros)){
            if(ZkUtils.deleteNode(zkpros,zkClient)){
                responseMessage.setDone(true);
            }else{
                responseMessage.setMessage("节点不存在");
            }
        }else{
            responseMessage.setMessage("没有权限做此操作");
        }
        return responseMessage;
    }

    @RequestMapping(value = "/modifyZkpros.json",method = RequestMethod.POST)
    public @ResponseBody ResponseMessage addPublicPros(HttpServletRequest request,
                                                       ZooKeeperNode zkpros,
                                                       @RequestParam("editType") String editType){
        ZkUtils.haveRightAccessNode(zkClient,zkpros);
        Endecrypt endecrypt = new Endecrypt();
        ResponseMessage responseMessage = new ResponseMessage();
        if(zkpros.isMask()){
            String maskKey = zkClient.readData(ZooKeeperConst.ZKSPKEY);
            zkpros.setMaskKey(maskKey);
            zkpros.setValue(endecrypt.get3DESEncrypt(zkpros.getValue(),maskKey));
        }
        String message = null;
        if(ZkUtils.haveRightAccessNode(zkClient,zkpros)){
            if("add".equals(editType)){
                responseMessage.setDone(ZkUtils.createNode(zkpros,zkClient));
                message = "节点已存在";
            }else{
                responseMessage.setDone(ZkUtils.update(zkpros,zkClient));
                message = "节点不存在";
            }
            if(responseMessage.isDone()){
                responseMessage.setMessage("成功");
            }else{
                responseMessage.setMessage(message);
            }
        }else{
            responseMessage.setMessage("没有权限做此操作");
        }

        return responseMessage;
    }

}
