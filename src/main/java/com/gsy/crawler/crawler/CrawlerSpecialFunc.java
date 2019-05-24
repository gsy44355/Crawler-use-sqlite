package com.gsy.crawler.crawler;

import com.gsy.crawler.pojo.TbCrawlerUrl;

/**
 * Created By Gsy on 2019/5/18
 * 在doCrawler方法中作为参数传入，实现对于这个Url的定制化操作
 */
@FunctionalInterface
public interface CrawlerSpecialFunc {
    void specialFunc(TbCrawlerUrl url) throws Exception;
}
