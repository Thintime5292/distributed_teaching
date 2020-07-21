package com.zhp.teaching.anotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Class_Name UserINF
 * @Author zhongping
 * @Date 2020/7/20 21:17
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserINF {
    boolean required() default true;
}
