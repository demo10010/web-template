package com.huawei.springbootweb;

import io.netty.channel.epoll.Epoll;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class SpringBootWebApplication {

    public static void main(String[] args) {
        System.out.println("Epoll.isAvailable : "+ Epoll.isAvailable());
        SpringApplication.run(SpringBootWebApplication.class, args);
    }

}
