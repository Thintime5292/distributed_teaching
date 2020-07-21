package com.zhp.teaching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.zhp.teaching.mapper")
public class TeachingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeachingServiceApplication.class, args);
    }

}
