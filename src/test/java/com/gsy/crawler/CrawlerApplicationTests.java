package com.gsy.crawler;

import com.gsy.crawler.crawler.CrawlerBaseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlerApplicationTests {
    @Autowired
    CrawlerBaseService crawlerBaseService;
    @Test
    public void contextLoads() {
        crawlerBaseService.doCrawlerByMultiThread("88",0,20,url -> {
//            System.out.println("");
        });
    }

}
