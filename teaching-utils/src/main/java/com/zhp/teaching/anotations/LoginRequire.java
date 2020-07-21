package com.zhp.teaching.anotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Class_Name LoginRequire
 * @Author zhongping
 * @Date 2020/7/7 10:57
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequire {
    boolean loginSuccess() default true;
}
