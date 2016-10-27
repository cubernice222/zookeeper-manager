package com.cuber.zkweb.controller;

import com.cuber.java.zkpros.constvar.ZooKeeperConst;
import com.cuber.java.zkpros.model.ZooKeeperEnviromentNode;
import com.cuber.java.zkpros.model.ZooKeeperNode;
import com.cuber.java.zkpros.utils.Endecrypt;
import com.cuber.zkweb.model.Page;
import com.cuber.zkweb.model.ResponseMessage;
import com.cuber.zkweb.model.RoleEnum;
import com.cuber.zkweb.util.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * Created by cuber on 2016/10/27.
 */
@Controller
public class EnvManagerController {

    @Autowired
    private ZkClient zkClient;

    @RequestMapping(value = "/displayEnv.htm",method = RequestMethod.GET)
    public String displayEnv(Model model){
        ZkUtils.constructMenu(model, zkClient);
        List<RoleEnum> roles = ZkUtils.getCurUserHasRole(zkClient);
        model.addAttribute("roleList",roles);
        return "environment";
    }

    @RequestMapping(value = "/getEnv.json",method = RequestMethod.POST)
    public @ResponseBody Page getEnv(Model model){//不支持分页，显示所有的节点
        Page  page = new Page();
        page.setCurPage(1);
        page.setData(ZkUtils.getCurrentUserVisualEnvNode(zkClient));
        page.setTotalRecords(page.getData().size());
        return page;
    }

    @RequestMapping(value = "/editEnv.json",method = RequestMethod.POST)
    public @ResponseBody ResponseMessage addPublicPros(HttpServletRequest request,
                                  ZooKeeperEnviromentNode zkpros,
                                  @RequestParam("editType") String editType){
        return ZkUtils.editNode(zkpros,zkClient,editType);
    }



}
