package com.cuber.zkweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by cuber on 2016/10/13.
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude={WebMvcConfigurer.class})
public class ZkwebApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ZkwebApplication.class, args);
    }
}
