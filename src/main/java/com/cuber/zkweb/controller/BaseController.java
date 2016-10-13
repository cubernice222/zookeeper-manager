package com.cuber.zkweb.controller;

/**
 * Created by cuber on 2016/10/13.
 */

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
public class BaseController {
    @RequestMapping("/index.htm")
    public ModelAndView index(Map<String, Object> mode){
        return new ModelAndView("index").addObject(mode);
    }

    @RequestMapping("/login.htm")
    public ModelAndView login(Map<String, Object> mode){
        return new ModelAndView("login").addObject(mode);
    }
}
