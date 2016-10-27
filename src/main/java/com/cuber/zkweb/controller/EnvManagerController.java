package com.cuber.zkweb.controller;

import com.cuber.java.zkpros.constvar.ZooKeeperConst;
import com.cuber.java.zkpros.model.ZooKeeperEnviromentNode;
import com.cuber.zkweb.model.EnvNodeShowDTO;
import com.cuber.zkweb.model.Page;
import com.cuber.zkweb.model.ResponseMessage;
import com.cuber.zkweb.model.RoleEnum;
import com.cuber.zkweb.util.ZkUtils;
import com.google.common.collect.Lists;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        model.addAttribute("curParentPath", ZooKeeperConst.ZKROOT);
        return "environment";
    }

    @RequestMapping(value = "/getEnv.json",method = RequestMethod.POST)
    public @ResponseBody Page getEnv(Model model){//不支持分页，显示所有的节点
        Page  page = new Page();
        page.setCurPage(1);
        List<ZooKeeperEnviromentNode> envNodes = ZkUtils.getCurrentUserVisualEnvNode(zkClient);
        List<EnvNodeShowDTO> envDTOs = Lists.newArrayList();
        envNodes.stream().forEach(env->{
            EnvNodeShowDTO dto = new EnvNodeShowDTO();
            BeanUtils.copyProperties(env,dto);
            dto.setRoleName(RoleEnum.getRoleByCode(dto.getAccRule()).getDesc());
            envDTOs.add(dto);
        });
        page.setData(envDTOs);
        page.setTotalRecords(page.getData().size());
        return page;
    }

    @RequestMapping(value = "/editEnv.json",method = RequestMethod.POST)
    public @ResponseBody ResponseMessage addPublicPros(HttpServletRequest request,
                                  ZooKeeperEnviromentNode zkpros,
                                  @RequestParam("editType") String editType){
        return ZkUtils.editNode(zkpros,zkClient,editType);
    }

    @RequestMapping(value = "/env/{envName}.htm",method = RequestMethod.GET)
    public String env(@PathVariable(value="envName") String envName,Model model){
        ZkUtils.constructMenu(model, zkClient);
        String parentPath = ZooKeeperConst.ZKROOT + "/" + envName;
        model.addAttribute("curParentPath", parentPath);
        model.addAttribute("envName", envName);
        ZooKeeperEnviromentNode enviromentNode = (ZooKeeperEnviromentNode)
                ZkUtils.getNode(parentPath,zkClient);
        model.addAttribute("curEnv", enviromentNode);
        return "env";
    }

    @RequestMapping(value = "/{envName}/projects.json",method = RequestMethod.POST)
    public @ResponseBody Page getPubPros(HttpServletRequest request,
                                         @PathVariable(value="envName") String envName,
                                         @RequestParam("pq_curpage") int pq_curpage,
                                         @RequestParam("pq_rpp") int pq_rpp,
                                         @RequestParam("filterMode") boolean filterMode){
        Page page = new Page();
        page.setPageCount(pq_rpp);
        page.setCurPage(pq_curpage>0?pq_curpage:1);
        String parentPath = ZooKeeperConst.ZKROOT + "/" + envName;
        if(!filterMode){
            page = ZkUtils.getPage(parentPath,page,zkClient);
        }else{
            String filterValue = request.getParameter("filterValue");
            page = ZkUtils.getFilter(parentPath,page,zkClient,filterValue);
        }
        return page;
    }
}
