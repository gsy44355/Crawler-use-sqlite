package com.gsy.crawler.util;

import com.gsy.crawler.crawler.CreateHeaderMap;
import com.gsy.crawler.crawler.WebCrawlerUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.io.IOException;

public class Test {
    public static void main(String[] args) {
//        TextExtract t1 = new TextExtract();
//        TextExtract t2 = new TextExtract();
//        TextExtract.setthreshold(44);
//        System.out.println(TextExtract.getThreshold());
//        System.out.println(t1.getThreshold());
//        t1.setthreshold(31);
//        System.out.println(t2.getThreshold());
//        System.out.println(TextExtract.getThreshold());
        try {
            String html = WebCrawlerUtil.getWebHtmlWithNewLine("https://www.jx.la/book/24863/8679787.html", CreateHeaderMap.getMapByName("crawler/test"),"utf-8");
//            String html1 = WebCrawlerUtil.getWebHtml("https://opinion.huanqiu.com/article/9CaKrnKpexl?from=singlemessage", CreateHeaderMap.getMapByName("crawler/test"),"utf-8");
            System.out.println(html);
//            String text = Jsoup.parseBodyFragment(html);
//            System.out.println(text);
//            TextExtract.setthreshold(10);
            System.out.println(TextExtract.parse(html));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
