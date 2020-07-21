package com.zhp.teaching.service;

import com.zhp.teaching.bean.User;

import javax.servlet.http.*;

/**
 * @Class_Name UserService
 * @Author zhongping
 * @Date 2020/7/19 20:35
 **/
public interface UserService {
    /**
     * 通过用户名查找用户
     *
     * @param username
     * @return
     */
    User getUserByUsername(String username);

    /**
     * 将已经登录的用户的token存到redis缓存里
     *
     * @param token
     * @param userLogin
     * @return
     */
    String saveToken(String token, User userLogin);

    /**
     * 用户退出登录
     *
     * @param uid
     * @param request
     * @param response
     * @return
     */
    Boolean logout(Integer uid, HttpServletRequest request, HttpServletResponse response);

    /**
     * 保存用户信息
     * @param user
     * @return
     */
    User saveUser(User user);

    /**
     * 通过用户id查询用户信息
     * @param uid
     * @return
     */
    User getUserById(Integer uid);
}
