package com.ctsi;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hello world!
 */
@SpringBootApplication(scanBasePackages={"com.ctsi.*"})
@MapperScan(basePackages = "com.ctsi", annotationClass = Mapper.class)
@EnableScheduling
@Configuration
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
