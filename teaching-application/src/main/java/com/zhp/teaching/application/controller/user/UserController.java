package com.zhp.teaching.application.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zhp.teaching.anotations.LoginRequire;
import com.zhp.teaching.anotations.UserINF;
import com.zhp.teaching.bean.User;
import com.zhp.teaching.service.UserService;
import com.zhp.teaching.utils.CookieUtil;
import com.zhp.teaching.utils.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Class_Name UserController
 * @Author zhongping
 * @Date 2020/7/20 22:14
 **/
@Controller
@RequestMapping("/user")
public class UserController {
    @Reference
    UserService userService;

    @RequestMapping("toRegister")
    public String toRegister(User user, ModelMap modelMap) {
        modelMap.put("userInfo", user);
        return "user/register";
    }

    @RequestMapping("register")
    @ResponseBody
    public Map<String, Object> register(User user) {
        Map<String, Object> resultMap = new HashMap<>();
        String username = user.getUsername();
        Map<String, Object> exist = isExist(username);
        Integer isExist = (Integer) exist.get("code");
        if (user != null) {
            if (isExist == 0)
                user = userService.saveUser(user);
            else user = null;
        }
        boolean success = user != null;
        Integer code = success ? 0 : 1;
        String msg = success ? "注册成功!" : "注册失败!";
        resultMap.put("code", code);
        resultMap.put("msg", msg);
        return resultMap;
    }

    @RequestMapping("isExist")
    @ResponseBody
    public Map<String, Object> isExist(String username) {
        User user = null;
        if (StringUtils.isNotBlank(username))
            user = userService.getUserByUsername(username);
        Integer code = user == null ? 0 : 1;
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", code);
        return resultMap;
    }

    @RequestMapping("info")
    @LoginRequire
    @UserINF
    public String info() {
        return "user/info";
    }

    @RequestMapping("save")
    @LoginRequire
    @ResponseBody
    public Map<String, Object> save(User user, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = new HashMap<>();
        if (user != null) {
            user = userService.saveUser(user);
        }
        boolean success = user != null;
        if (success) addUserCookieToClient(request, response, user);
        Integer code = success ? 0 : 1;
        String msg = success ? "保存成功!" : "保存失败!";
        resultMap.put("code", code);
        resultMap.put("msg", msg);
        return resultMap;
    }

    private void addUserCookieToClient(HttpServletRequest request, HttpServletResponse response, User user) {
        if (user != null) user.setPassword(null);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("user", user);
        String ipAddr = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ipAddr)) ipAddr = request.getRemoteAddr();
        if (StringUtils.isBlank(ipAddr)) ipAddr = "127.0.0.1";
        String user_info = JwtUtil.encode("user_info", userMap, ipAddr);
        CookieUtil.setCookie(request, response, "user_info", user_info, 60 * 60 * 2, true);
    }

    @RequestMapping("logout")
    @LoginRequire
    @ResponseBody
    public Map<String, Object> logout(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        CookieUtil.deleteCookie(request, response, "oldToken");
        CookieUtil.deleteCookie(request, response, "user_info");
        result.put("code", 0);
        result.put("msg", "退出成功!");
        return result;
    }
}
