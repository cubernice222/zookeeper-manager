package com.cuber.zkweb.controller;

import com.cuber.java.zkpros.constvar.ZooKeeperConst;
import com.cuber.java.zkpros.model.ZooKeeperEnviromentNode;
import com.cuber.zkweb.model.Page;
import com.cuber.zkweb.model.ResponseMessage;
import com.cuber.zkweb.util.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by cuber on 2016/10/22.
 */
@Controller
public class SpkeyController {
    @Autowired
    private ZkClient zkClient;

    @RequestMapping(value = "/showSpkey.htm",method = RequestMethod.GET)
    public String showSpkeyValue(Model model){
        ZkUtils.constructMenu(model, zkClient);
        model.addAttribute("spkey",zkClient.readData(ZooKeeperConst.ZKSPKEY));
        return "spkey";
    }

    @RequestMapping(value = "/modifySpkey.json",method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage modifySpkeyValue(@RequestParam("spkey") String spkey) {
        ResponseMessage responseMessage = new ResponseMessage();
        zkClient.writeData(ZooKeeperConst.ZKSPKEY,spkey);
        responseMessage.setDone(true);
        responseMessage.setMessage("I LIKE  THIS SHOW");
        return responseMessage;
    }
}
