package com.zhp.teaching.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.zhp.teaching.bean.User;
import com.zhp.teaching.mapper.UserMapper;
import com.zhp.teaching.service.UserService;
import com.zhp.teaching.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * @Class_Name UserServiceImpl
 * @Author zhongping
 * @Date 2020/7/19 22:17
 **/
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    UserMapper userMapper;

    @Override
    public User getUserByUsername(String username) {
        if (username == null) return null;
        Jedis jedis = null;
        User user = null;
        try {
            jedis = redisUtil.getJedis();
            String key = "user:*:username:" + username + ":info";
            Set<String> keys = jedis.keys(key);
            if (keys != null && keys.size() == 1) {
                key = keys.toArray()[0].toString();
                String val = jedis.get(key);
                if (StringUtils.isNotBlank(val)) {
                    user = JSON.parseObject(val, User.class);
                }
            }
            if (user == null) {
                String lock = UUID.randomUUID().toString();
                String lockKey = "user:" + username + ":lock";
                //设置分布式锁，防止“雪崩”
                String OK = jedis.set(lockKey, lock, "nx", "px", 10 * 1000);
                if (StringUtils.isNotBlank(OK) && OK.equals("OK")) {
                    user = getUserByUsernameFromDB(username);
                    if (user != null) {
                        key = "user:" + user.getId() + ":username:" + username + ":info";
                        String val = JSON.toJSONString(user);
                        jedis.set(key, val);
                    } else {//不存在该user时，将null值存入redis防止缓存穿透
                        key = "user:null:username:" + username + ":info";
                        jedis.setex(key, 60 * 3, JSON.toJSONString(null));
                    }
                    String lockVal = jedis.exists(lockKey) ? jedis.get(lockKey) : "";
                    if (StringUtils.isNotBlank(lockVal) && lockVal.equals(lock)) {
                        jedis.del(lockKey);
                    }
                } else {//锁设置失败时
                    Thread.sleep(3000);
                    return getUserByUsername(username);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return user;
    }

    /**
     * 通过用户名从数据库查询用户
     *
     * @param username
     * @return
     */
    private User getUserByUsernameFromDB(String username) {
        if (username == null) return null;
        User user = new User();
        user.setUsername(username);
        return userMapper.selectOne(user);
    }

    @Override
    public String saveToken(String token, User userLogin) {
        if (StringUtils.isBlank(token) || userLogin == null) return null;
        String key = "user:" + userLogin.getId() + ":token";
        Jedis jedis = redisUtil.getJedis();
        String setex = jedis.setex(key, 60 * 60 * 2, token);
        jedis.close();
        return token;
    }

    @Override
    public Boolean logout(Integer uid, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public User saveUser(User user) {
        if (user == null) return null;
        Integer id = user.getId();
        String password = user.getPassword();
        if (StringUtils.isBlank(password)) user.setPassword(null);
        else {
            password = DigestUtils.md5DigestAsHex(password.getBytes());
            user.setPassword(password);
        }
        int result = 0;
        if (id != null) {
            result = userMapper.updateByPrimaryKeySelective(user);
        } else {
            user.setRegisterTime(new Date());
            result = userMapper.insertSelective(user);
        }
        if (result == 1) {
            user = userMapper.selectByPrimaryKey(user);
            String key = "user:" + user.getId() + ":username:"+user.getUsername()+":info";
            Jedis jedis = redisUtil.getJedis();
            String set = jedis.set(key, JSON.toJSONString(user));
            jedis.close();
        }
        return user;
    }

    @Override
    public User getUserById(Integer uid) {
        if (uid == null) return null;
        Jedis jedis = null;
        User user = null;
        try {
            jedis = redisUtil.getJedis();
            String key = "user:" + uid + ":username:*:info";
            Set<String> keys = jedis.keys(key);
            if (keys != null && keys.size() == 1) {
                key = keys.toArray()[0].toString();
                String val = jedis.get(key);
                if (StringUtils.isNotBlank(val)) {
                    user = JSON.parseObject(val, User.class);
                }
            }
            if (user == null) {
                String lock = UUID.randomUUID().toString();
                String lockKey = "user:" + uid + ":lock";
                //设置分布式锁，防止“雪崩”
                String OK = jedis.set(lockKey, lock, "nx", "px", 10 * 1000);
                if (StringUtils.isNotBlank(OK) && OK.equals("OK")) {
                    user = getUserByIdFromDB(uid);
                    if (user != null) {
                        key = "user:" + uid + ":username:" + user.getUsername() + ":info";
                        String val = JSON.toJSONString(user);
                        jedis.set(key, val);
                    } else {//不存在该user时，将null值存入redis防止缓存穿透
                        key = "user:" + uid + ":username:null:info";
                        jedis.setex(key, 60 * 3, JSON.toJSONString(null));
                    }
                    String lockVal = jedis.exists(lockKey) ? jedis.get(lockKey) : "";
                    if (StringUtils.isNotBlank(lockVal) && lockVal.equals(lock)) {
                        jedis.del(lockKey);
                    }
                } else {//锁设置失败时
                    Thread.sleep(3000);
                    return getUserById(uid);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return user;
    }

    /**
     * 通过用户id查询用户信息
     *
     * @param uid
     * @return
     */
    private User getUserByIdFromDB(Integer uid) {
        if (uid == null) return null;
        User user = new User();
        user.setId(uid);
        return userMapper.selectByPrimaryKey(user);
    }
}
