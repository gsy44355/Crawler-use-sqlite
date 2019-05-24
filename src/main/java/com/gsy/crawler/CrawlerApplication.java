package com.gsy.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableAsync
//@EnableScheduling
@MapperScan("com.gsy.crawler.mapper")
public class CrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerApplication.class, args);

    }


}
