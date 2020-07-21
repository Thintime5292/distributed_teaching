package com.zhp.teaching.interceptors;

import com.alibaba.fastjson.JSON;
import com.zhp.teaching.anotations.LoginRequire;
import com.zhp.teaching.utils.CookieUtil;
import com.zhp.teaching.utils.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Class_Name AuthInterceptor
 * @Author zhongping
 * @Date 2020/7/7 11:00
 **/
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        //判断被拦截的请求的访问的方法的注解（是否需要拦截）
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LoginRequire methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequire.class);
        //判断是否拦截请求
        if (methodAnnotation == null) {
            return true;//不拦截
        }
        String token = "";
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        String newToken = request.getParameter("token");
        if (StringUtils.isNotBlank(oldToken)) {
            token = oldToken;
        }
        if (StringUtils.isNotBlank(newToken)) {
            token = newToken;
        }
        //拦截之后获得该请求是否必须登录成功
        boolean loginSuccess = methodAnnotation.loginSuccess();
        //调用认证中心进行验证
        String verify = "fail";
        Map<String, Object> verifyMap = new HashMap<>();
        if (StringUtils.isNotBlank(token)) {
            String ip = request.getHeader("x-forwarded-for");//通过nginx转发的客户端
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();
            }
            if (StringUtils.isBlank(ip)) {
                //如果前两部都没获取到ip
                ip = "127.0.0.1";
            }
            String verifyJson = HttpclientUtil.doGet("http://passport.teaching.com:9988/passport/verify?token=" + token + "&currentIp=" + ip);
            verifyMap = JSON.parseObject(verifyJson, Map.class);
            verify = (String) verifyMap.get("status");
        }
        if (loginSuccess) {//如果为true则为必须登录成功
            if (!verify.equals("success")) {
                //获取请求地址
                StringBuffer requestURL = request.getRequestURL();
                String requestPath = urlPath(request,requestURL.toString());
                //重定向到的登录中心
//                response.sendRedirect("http://passport.teaching.com:9988/passport/index?returnUrl=" + requestURL);
                response.sendRedirect("http://passport.teaching.com:9988/passport/index?returnUrl=" + requestPath);
                return false;
            } else {
                //验证通过，覆盖cookie中的token
                if (StringUtils.isNotBlank(token)) {
                    //验证通过，覆盖原cookie中的token
                    CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
                }
                request.setAttribute("uid", verifyMap.get("uid"));
                return true;
            }
        } else {//没有登录也能进行访问，但是必须验证
            if (verify.equals("success")) {
                //需要将用户携带的用户信息写入
                request.setAttribute("uid", verifyMap.get("uid"));
                if (StringUtils.isNotBlank(token)) {
                    //验证通过，覆盖原cookie中的token
                    CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
                }
            }
        }
        return true;
    }

    /**
     * 拼接参数
     * @param request
     * @param url
     * @return
     */
    private String urlPath(HttpServletRequest request, String url) {
        Enumeration<String> parameterNames = request.getParameterNames();
        String pathParam = "?";
        while (parameterNames.hasMoreElements()) {
            String param = parameterNames.nextElement();
            String paramVal = request.getParameter(param);
            url += param + "=" + paramVal + "&";
        }
        if (parameterNames.hasMoreElements())
            url += pathParam.substring(0, pathParam.length() - 1);
        return url;
    }
}
