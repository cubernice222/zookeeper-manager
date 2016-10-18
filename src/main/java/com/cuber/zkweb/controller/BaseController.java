package com.cuber.zkweb.controller;

/**
 * Created by cuber on 2016/10/13.
 */

import com.cuber.java.zkpros.constvar.ZooKeeperConst;
import com.cuber.java.zkpros.model.ZooKeeperEnviromentNode;
import com.cuber.java.zkpros.model.ZooKeeperProjectNode;
import com.cuber.zkweb.model.Page;
import com.cuber.zkweb.util.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@RestController
public class BaseController {
    @Autowired
    private ZkClient zkClient;

    @GetMapping("/index")
    public ModelAndView index(Map<String, Object> model){
        List<ZooKeeperEnviromentNode> accessEnvs = ZkUtils.getCurrentUserVisualEnvNode(zkClient);
        model.put("accessEnvs",accessEnvs);
        ZooKeeperProjectNode projectNode = (ZooKeeperProjectNode)ZkUtils.getNode(ZooKeeperConst.PUBLICCONFIG,zkClient);
        Page page = new Page();
        page = ZkUtils.getPage(ZooKeeperConst.PUBLICCONFIG ,page,zkClient);
        model.put("page",page.getZooKeeperProsNodes());
        model.put("adb","adc");
        model.put("projectNode",projectNode);
        return new ModelAndView("index").addAllObjects(model);
    }

    @RequestMapping("/login.htm")
    public ModelAndView login(Map<String, Object> mode){
        return new ModelAndView("login").addObject(mode);
    }
}
