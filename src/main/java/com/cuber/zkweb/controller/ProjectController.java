package com.cuber.zkweb.controller;

import com.cuber.java.zkpros.constvar.ZooKeeperConst;
import com.cuber.java.zkpros.model.ZooKeeperEnviromentNode;
import com.cuber.java.zkpros.model.ZooKeeperProjectNode;
import com.cuber.zkweb.model.Page;
import com.cuber.zkweb.util.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by cuber on 2016/10/28.
 */
@Controller
public class ProjectController {
    @Autowired
    private ZkClient zkClient;

    @RequestMapping(value = "/project/{envName}/{projectName}.htm",method = RequestMethod.GET)
    public String index(Model model,
                        @PathVariable(value="envName") String envName,
                        @PathVariable(value="projectName") String projectName){
        ZkUtils.constructMenu(model, zkClient);
        String envPath = ZooKeeperConst.ZKROOT + "/" + envName;
        String parentPath = envPath + "/" + projectName;
        model.addAttribute("curParentPath", parentPath);
        model.addAttribute("envName", envName);
        model.addAttribute("projectName", projectName);
        ZooKeeperEnviromentNode enviromentNode = (ZooKeeperEnviromentNode)
                ZkUtils.getNode(envPath,zkClient);
        model.addAttribute("curEnv", enviromentNode);
        ZooKeeperProjectNode projectNode = (ZooKeeperProjectNode)
                ZkUtils.getNode(parentPath,zkClient);
        model.addAttribute("curProject", projectNode);
        if(ZkUtils.haveRightAccessNode(zkClient,enviromentNode)){
            return "projectconf";
        }else{
            return "404";
        }
    }

    @RequestMapping(value = "/project/{envName}/{projectName}.json",method = RequestMethod.POST)
    public @ResponseBody Page getProjectPros(@PathVariable(value="envName") String envName,
                        @PathVariable(value="projectName") String projectName,
                        HttpServletRequest request,
                        @RequestParam("pq_curpage") int pq_curpage,
                        @RequestParam("pq_rpp") int pq_rpp,
                        @RequestParam("filterMode") boolean filterMode){
        Page page = new Page();
        page.setPageCount(pq_rpp);
        page.setCurPage(pq_curpage>0?pq_curpage:1);
        String envPath = ZooKeeperConst.ZKROOT + "/" + envName;
        String parentPath = envPath + "/" + projectName;
        ZooKeeperEnviromentNode enviromentNode = (ZooKeeperEnviromentNode)
                ZkUtils.getNode(envPath,zkClient);
        if(ZkUtils.haveRightAccessNode(zkClient,enviromentNode)){
            if(!filterMode){
                page = ZkUtils.getPage(parentPath,page,zkClient);
            }else{
                String filterValue = request.getParameter("filterValue");
                page = ZkUtils.getFilter(parentPath,page,zkClient,filterValue);
            }
        }
        return page;
    }
}
