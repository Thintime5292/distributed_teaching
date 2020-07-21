package com.zhp.teaching.utils;

import io.jsonwebtoken.*;

import java.util.Map;

/**
 * @Class_Name JwtUtil
 * @Author zhongping
 * @Date 2020/7/7 10:36
 **/
public class JwtUtil {
    //key为服务器的秘钥，salt为盐值（比如当前浏览器的ip和访问时间）
    public static String encode(String key, Map<String, Object> param, String salt) {
        if (salt != null) {
            key += salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256, key);
        jwtBuilder = jwtBuilder.setClaims(param);
        String token = jwtBuilder.compact();
        return token;
    }
    public static Map<String,Object> decode(String token,String key,String salt){
        Claims claims = null;
        if(salt != null){
            key+=salt;
        }
        try {
            claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        }catch (JwtException e){

        }
        return claims;
    }
}
