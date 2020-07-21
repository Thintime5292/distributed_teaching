package com.zhp.teaching.config;

import com.zhp.teaching.interceptors.AuthInterceptor;
import com.zhp.teaching.interceptors.UserInfoInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Class_Name WebMvcConfiguration
 * @Author zhongping
 * @Date 2020/7/7 10:58
 **/
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    AuthInterceptor authInterceptor;
    @Autowired
    UserInfoInterceptor userInfoInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
        registry.addInterceptor(userInfoInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
