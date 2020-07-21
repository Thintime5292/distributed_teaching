package com.zhp.teaching.application.controller.home;

import com.zhp.teaching.anotations.LoginRequire;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Class_Name HomeController
 * @Author zhongping
 * @Date 2020/7/19 14:58
 **/
@Controller
@CrossOrigin
@RequestMapping("/home")
public class HomeController {
    @RequestMapping("/index")
    @LoginRequire
    public String index(){
        return "home/home";
    }

}
