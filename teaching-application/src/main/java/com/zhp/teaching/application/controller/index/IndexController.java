package com.zhp.teaching.application.controller.index;

import com.zhp.teaching.anotations.UserINF;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Class_Name IndexController
 * @Author zhongping
 * @Date 2020/7/18 23:19
 **/
@Controller
@CrossOrigin
public class IndexController {
    @RequestMapping("/")
    @UserINF
    public String index(){
        return "index/index";
    }
}
