package com.cuber.zkweb.controller;

import com.cuber.java.zkpros.constvar.ZooKeeperConst;
import com.cuber.java.zkpros.model.ZooKeeperEnviromentNode;
import com.cuber.zkweb.model.Page;
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
import java.util.List;

/**
 * Created by cuber on 2016/10/24.
 */
@Controller
public class PublicConfController {
    @Autowired
    private ZkClient zkClient;

    @RequestMapping(value = "/showpubconf.htm",method = RequestMethod.GET)
    public String index(Model model){
        ZkUtils.constructMenu(model, zkClient);
        model.addAttribute("curParentPath", ZooKeeperConst.PUBLICCONFIG);
        return "pubconf";
    }
    @RequestMapping(value = "/showAllPros.json",method = RequestMethod.POST)
    public @ResponseBody Page getPubPros(HttpServletRequest request,
                                         @RequestParam("pq_curpage") int pq_curpage,
                                         @RequestParam("pq_rpp") int pq_rpp,
                                         @RequestParam("filterMode") boolean filterMode
                                         ){
        Page page = new Page();
        page.setPageCount(pq_rpp);
        page.setCurPage(pq_curpage>0?pq_curpage:1);
        if(!filterMode){
            page = ZkUtils.getPage(ZooKeeperConst.PUBLICCONFIG,page,zkClient);
        }else{
            String filterValue = request.getParameter("filterValue");
            page = ZkUtils.getFilter(ZooKeeperConst.PUBLICCONFIG,page,zkClient,filterValue);
        }
        return page;
    }
}
