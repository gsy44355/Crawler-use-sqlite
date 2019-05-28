package com.gsy.crawler;

import com.gsy.crawler.crawler.CrawlerBaseService;
import com.gsy.crawler.crawler.CreateHeaderMap;
import com.gsy.crawler.crawler.SpecialCrawlerService;
import com.gsy.crawler.crawler.WebCrawlerUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * Created By Gsy on 2019/5/21
 */
@Component
public class Command implements CommandLineRunner {
    @Autowired(required = true)
//    @Qualifier("meituluCrawler")
    @Qualifier("WeiboCrawlerService")
    SpecialCrawlerService specialCrawlerService;
    @Autowired
    CrawlerBaseService crawlerBaseService;
    @Override
    public void run(String... args) throws Exception {

//        crawlerBaseService.addUrl(new TbCrawlerUrl("https://weibo.cn/mblog/picAll/HvmAs29hS?rl=1","1","0"));
//        WebCrawlerUtil.getWebPicture("https://mtl.ttsqgs.com/images/img/3292/49.jpg","1.jpg", CreateHeaderMap.getMapByNameWithRandomIp("crawler/picturea"),"");
        System.out.println("请输入start or restart：");
        Scanner sc = new Scanner(System.in);
        String s = sc.next();
        doit(s);
    }
    public void doit(String s){
        s = StringUtils.lowerCase(s);
        switch (s){
            case "start":{
                specialCrawlerService.startNew();
                break;
            }
            case "restart":{
                specialCrawlerService.reStart();
                break;
            }
            default:
                System.out.println("请输入 start 或 restart 决定你要如何爬取");
        }
    }

}
