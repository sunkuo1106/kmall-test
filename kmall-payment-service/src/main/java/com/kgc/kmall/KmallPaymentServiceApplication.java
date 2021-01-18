package com.kgc.kmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.kgc.kmall.kmallpaymentservice.mapper")
@SpringBootApplication
public class KmallPaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KmallPaymentServiceApplication.class, args);
    }

}
