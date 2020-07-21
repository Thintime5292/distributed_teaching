package com.zhp.teaching.passport;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.zhp.teaching.bean.User;
import com.zhp.teaching.service.UserService;
import com.zhp.teaching.utils.CookieUtil;
import com.zhp.teaching.utils.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Class_Name PassportController
 * @Author zhongping
 * @Date 2020/7/19 20:30
 **/
@Controller
@CrossOrigin
@RequestMapping("passport")
public class PassportController {
    @Reference
    UserService userService;

    @RequestMapping("index")
    public String index(String returnUrl, ModelMap modelMap) {
        modelMap.put("returnUrl", returnUrl);
        return "login";
    }

    @RequestMapping("login")
    @ResponseBody
    public Map<String, Object> login(User user, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        String token = "fail";
        String password = user.getPassword();
        String username = user.getUsername();
        if (username == null || username == "") {
            result.put("code", 1);
            result.put("msg", "用户名不能为空!");
            return result;
        }
        if (password == null || password == "") {
            result.put("code", 1);
            result.put("msg", "密码不能为空!");
            return result;
        }
        User userLogin = userService.getUserByUsername(username);
        if (userLogin == null) {
            result.put("code", 1);
            result.put("msg", "用户不存在!");
            return result;
        }
        String pwd = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!pwd.equals(userLogin.getPassword())) {
            result.put("code", 1);
            result.put("msg", "密码错误!");
            return result;
        }
        Integer state = userLogin.getState();
        if (state.equals(1)) {
            result.put("code", 1);
            result.put("msg", "该用户已被禁用!");
            return result;
        }
        //用jwt制作token
        Integer uid = userLogin.getId();
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("uid", uid);
        tokenMap.put("username", username);
        String ipAddr = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ipAddr)) ipAddr = request.getRemoteAddr();
        if (StringUtils.isBlank(ipAddr)) ipAddr = "127.0.0.1";
        token = JwtUtil.encode("music_admin", tokenMap, ipAddr);
        userService.saveToken(token, userLogin);
        addUserCookieToClient(request, response, userLogin);
        result.put("code", 0);
        result.put("msg", "登录成功!");
        result.put("token", token);
        return result;
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

    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token, String currentIp) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> decode = JwtUtil.decode(token, "music_admin", currentIp);
        if (decode != null) {
            map.put("status", "success");
            map.put("uid", decode.get("uid"));
            map.put("username", decode.get("username"));
        } else {
            map.put("status", "fail");
        }
        return JSON.toJSONString(map);
    }
}
