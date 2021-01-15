package com.kgc.kmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.kgc.kmall.kmallorderservice.mapper")
@SpringBootApplication
public class KmallOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KmallOrderServiceApplication.class, args);
    }

}
