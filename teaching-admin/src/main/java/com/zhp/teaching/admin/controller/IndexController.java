package com.zhp.teaching.admin.controller;

import com.zhp.teaching.anotations.LoginRequire;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Class_Name IndexController
 * @Author zhongping
 * @Date 2020/7/19 20:04
 **/
@Controller
@CrossOrigin
public class IndexController {
    @RequestMapping("/admin")
    @LoginRequire
    public String index(){
        return "index/index";
    }
}
