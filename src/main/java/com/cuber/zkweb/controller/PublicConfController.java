package com.cuber.zkweb.controller;

import com.cuber.java.zkpros.constvar.ZooKeeperConst;
import com.cuber.java.zkpros.model.ZooKeeperEnviromentNode;
import com.cuber.java.zkpros.model.ZooKeeperNode;
import com.cuber.java.zkpros.model.ZooKeeperProsNode;
import com.cuber.java.zkpros.utils.Endecrypt;
import com.cuber.zkweb.model.Page;
import com.cuber.zkweb.model.ResponseMessage;
import com.cuber.zkweb.model.ZkProsDTO;
import com.cuber.zkweb.util.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cuber on 2016/10/24.
 */
@Controller
public class PublicConfController {
    @Autowired
    private ZkClient zkClient;

    @RequestMapping(value = "/showpubconf.htm",method = RequestMethod.GET)
    public String index(Model model){
        List<ZooKeeperEnviromentNode> accessEnvs = ZkUtils.getCurrentUserVisualEnvNode(zkClient);
        model.addAttribute("accessEnvs",accessEnvs);
        model.addAttribute("curParentPath", ZooKeeperConst.PUBLICCONFIG);
        return "pubconf";
    }
    @RequestMapping(value = "/showAllPros.json",method = RequestMethod.POST)
    public @ResponseBody Page getPubPros(HttpServletRequest request,
                                         @RequestParam("pq_curpage") int pq_curpage,
                                         @RequestParam("pq_rpp") int pq_rpp,
                                         @RequestParam("pq_datatype") String pq_datatype){
        Page page = new Page();
        page.setPageCount(pq_rpp);
        page.setCurPage(pq_curpage);
        System.out.println(request.getParameterMap());
        List<ZooKeeperProsNode> data = new ArrayList<>();
        page = ZkUtils.getPage(ZooKeeperConst.PUBLICCONFIG,page,zkClient);
        return page;
    }


    @RequestMapping(value = "/modifypubConf.json",method = RequestMethod.POST)
    public @ResponseBody ResponseMessage addPublicPros(HttpServletRequest request,
                                         @RequestParam("editType") String editType){
        Endecrypt endecrypt = new Endecrypt();
        ResponseMessage responseMessage = new ResponseMessage();
        ZooKeeperNode zkpros =  new ZooKeeperNode();
        Map map = request.getParameterMap();
        System.out.println(request.getParameterMap());
        if(zkpros.isMask()){
            zkpros.setValue(endecrypt.get3DESEncrypt(zkpros.getValue(),zkClient.readData(ZooKeeperConst.ZKSPKEY)));
        }
        String message = null;
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
        return responseMessage;
    }
}
