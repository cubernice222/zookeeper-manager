package com.cuber.zkweb.controller;

import com.cuber.java.zkpros.constvar.ZooKeeperConst;
import com.cuber.java.zkpros.model.*;
import com.cuber.java.zkpros.utils.Endecrypt;
import com.cuber.zkweb.model.EnvEnum;
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
    public @ResponseBody ResponseMessage deletePros(HttpServletRequest request,
                                                    ZooKeeperProsNode zkpros){
        ResponseMessage responseMessage = validationDel(zkpros);
        if(null == responseMessage.getMessage()){
            if(ZkUtils.haveRightAccessNode(zkClient,zkpros)){
                if(ZkUtils.deleteNode(zkpros,zkClient)){
                    responseMessage.setDone(true);
                }else{
                    responseMessage.setMessage("节点不存在");
                }
            }else{
                responseMessage.setMessage("没有权限做此操作");
            }
        }
        return responseMessage;
    }
    private ResponseMessage validationDel(ZooKeeperProsNode zkpros){
        ResponseMessage responseMessage = new ResponseMessage();
        String path = zkpros.getParentPath() + "/" + zkpros.getName();
        if(ZooKeeperConst.PUBLICCONFIG.equals(path)){
            responseMessage.setMessage("不能删除公共配置");
        }
        zkpros = ZkUtils.getNode(path,zkClient);
        if("1".equals(zkpros.getType())){//环境
            if(EnvEnum.isExist(zkpros.getName())){
                responseMessage.setMessage("不能删除原始环境节点");
            }
        }
        return responseMessage;
    }
    @RequestMapping(value = "/modifyZkpros.json",method = RequestMethod.POST)
    public @ResponseBody ResponseMessage addPublicPros(HttpServletRequest request,
                                                       ZooKeeperNode zkpros,
                                                       @RequestParam("editType") String editType){
        Endecrypt endecrypt = new Endecrypt();
        if(zkpros.isMask()){
            String maskKey = zkClient.readData(ZooKeeperConst.ZKSPKEY);
            zkpros.setMaskKey(maskKey);
            zkpros.setValue(endecrypt.get3DESEncrypt(zkpros.getValue(),maskKey));
        }
        ResponseMessage responseMessage = ZkUtils.editNode(zkpros,zkClient,editType);
        return responseMessage;
    }

}
