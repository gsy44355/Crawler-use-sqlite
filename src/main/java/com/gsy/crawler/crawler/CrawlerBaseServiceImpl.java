package com.gsy.crawler.crawler;

import com.gsy.crawler.mapper.auto.TbCrawlerUrlMapper;
import com.gsy.crawler.mapper.custom.TbCrawlerUrlCustomMapper;
import com.gsy.crawler.pojo.TbCrawlerUrl;
import com.gsy.crawler.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By Gsy on 2019/5/18
 */
@Service
public class CrawlerBaseServiceImpl implements CrawlerBaseService {
    @Autowired
    TbCrawlerUrlMapper tbCrawlerUrlMapper;
    @Autowired
    TbCrawlerUrlCustomMapper tbCrawlerUrlCustomMapper;
    @Override
    public synchronized TbCrawlerUrl getUrl(String type) {
        TbCrawlerUrl tbCrawlerUrl = tbCrawlerUrlCustomMapper.getOneUrl(type);
        if (tbCrawlerUrl == null){
            return null;
        }
        tbCrawlerUrl.setBusy("1");
        tbCrawlerUrlMapper.updateByPrimaryKeySelective(tbCrawlerUrl);
        return tbCrawlerUrl;
    }

    @Override
    public int updateUrlToNoUse(String url) {
        return tbCrawlerUrlMapper.updateByPrimaryKeySelective(new TbCrawlerUrl(url,"0"));
    }

    @Override
    public int updateUrlToError(String url) {

        return tbCrawlerUrlMapper.updateByPrimaryKeySelective(new TbCrawlerUrl(url,"3"));
    }

    @Override
    public int resetAll() {
        tbCrawlerUrlCustomMapper.resetAll();
        return 1;
    }

    @Override
    public int addUrl(TbCrawlerUrl tbCrawlerUrl) {
        try{
            tbCrawlerUrlMapper.insertSelective(tbCrawlerUrl);
            LogUtil.info(this.getClass(),"新增URL={}",tbCrawlerUrl.toString());
            return 1;
        }catch (DuplicateKeyException e){
            LogUtil.info(this.getClass(),"Crawler获取到重复Url={}",tbCrawlerUrl.getUrl());
            return 1;
        }catch (UncategorizedSQLException e){
//            System.out.println("获取到异常");
            if (e.getMessage().contains("UNIQUE constraint failed")){
//             关键字拦截sqlite重复URL报错
                LogUtil.info(this.getClass(),"Crawler获取到重复Url={}",tbCrawlerUrl.getUrl());
            }
            return 1;
        }
    }

    @Override
    public int deleteUrl(String url) {
        return tbCrawlerUrlMapper.deleteByPrimaryKey(url);
    }

    @Override
    public int deleteAll() {
        LogUtil.info(this.getClass(),"删除所有存储的URL，重新开始");
        return tbCrawlerUrlCustomMapper.deleteAll();

    }

    @Override
    public void doCrawler(String type,long sleepTime,CrawlerSpecialFunc crawlerSpecialFunc) {
        int errorCount = 0;
        long start = System.currentTimeMillis();
        while(true){
            TbCrawlerUrl url = null;
            try {
                if(sleepTime != 0 ){
                    Thread.sleep(sleepTime);
                }
                url = this.getUrl(type);
                if(url == null){
                    break;
                }
                LogUtil.info(this.getClass(),"数据库中取出Url={}",url);
                crawlerSpecialFunc.specialFunc(url);
                this.updateUrlFinish(url.getUrl());
            }catch (Exception e){
                errorCount++;
                LogUtil.error(this.getClass(),"抓取异常，决定需要如何处理",e);
                this.updateUrlToError(url.getUrl());
                if (errorCount >100){
                    break;
                }
            }
        }
        LogUtil.info(this.getClass(),"线程爬取结束,共耗时{}s",(System.currentTimeMillis()-start)/1000);
    }

    @Override
    public void doCrawlerByMultiThread(String type, long sleepTime, int threadCounts, CrawlerSpecialFunc crawlerSpecialFunc) {
        List<Thread> list = new ArrayList<>();
        long multiStartTime = System.currentTimeMillis();
        LogUtil.info(this.getClass(),"多线程爬取开始，线程数={}",threadCounts);
        for (int i = 0; i < threadCounts; i++) {
            LogUtil.info(this.getClass(),"创建线程={}",i);
            Thread thread = new Thread(() -> {
                this.doCrawler(type, sleepTime, crawlerSpecialFunc);
            });
            list.add(thread);
            thread.start();
        }
        for (Thread thread:list) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LogUtil.info(this.getClass(),"多线程爬取结束，共耗时={}秒",(System.currentTimeMillis()-multiStartTime)/1000);
    }

    @Override
    public void updateUrlFinish(String url) {
        tbCrawlerUrlMapper.updateByPrimaryKeySelective(new TbCrawlerUrl(url,"2"));
    }

    @Override
    public void resetAllFail() {
        tbCrawlerUrlCustomMapper.resetAllFail();
    }
}
