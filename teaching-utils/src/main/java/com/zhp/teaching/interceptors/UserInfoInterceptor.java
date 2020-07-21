package com.zhp.teaching.interceptors;

import com.zhp.teaching.anotations.UserINF;
import com.zhp.teaching.utils.CookieUtil;
import com.zhp.teaching.utils.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Class_Name UserInfoIntercepter
 * @Author zhongping
 * @Date 2020/7/20 17:42
 **/
@Configuration
public class UserInfoInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        UserINF methodAnnotation = handlerMethod.getMethodAnnotation(UserINF.class);
        if (methodAnnotation == null) {
            if (modelAndView != null)
                modelAndView.addObject("user", null);
            return;
        }
        String user_info = CookieUtil.getCookieValue(request, "user_info", true);
        if (user_info == null) {
            if (modelAndView != null)
                modelAndView.addObject("user", null);
            return;
        }
        String ip = request.getHeader("x-forwarded-for");//通过nginx转发的客户端
        if (StringUtils.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.isBlank(ip)) {
            //如果前两部都没获取到ip
            ip = "127.0.0.1";
        }
        Map<String, Object> infoMap = JwtUtil.decode(user_info, "user_info", ip);
        Object user = infoMap.get("user");
        modelAndView.addObject("user", user);
    }
}
