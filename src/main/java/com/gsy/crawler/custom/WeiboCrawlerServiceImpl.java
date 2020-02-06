package com.gsy.crawler.custom;

import com.gsy.crawler.crawler.CrawlerBaseService;
import com.gsy.crawler.crawler.CreateHeaderMap;
import com.gsy.crawler.crawler.SpecialCrawlerService;
import com.gsy.crawler.crawler.WebCrawlerUtil;
import com.gsy.crawler.pojo.TbCrawlerUrl;
import com.gsy.crawler.util.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created By Gsy on 2019/5/18
 */
@Service("WeiboCrawlerService")
@EnableAsync
public class WeiboCrawlerServiceImpl implements SpecialCrawlerService {
    @Autowired
    CrawlerBaseService crawlerBaseService;
    @Override
    public void startNew() {
        crawlerBaseService.deleteAll();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("crawler/start");
        for (int i = Integer.parseInt(resourceBundle.getString("countStart") ); i <Integer.parseInt(resourceBundle.getString("countEnd") ); i++) {
            crawlerBaseService.addUrl(new TbCrawlerUrl(resourceBundle.getString("mainUrl").replace("@replace@",""+i),"1","0"));
        }
        reStart();
    }

    @Override
    public void reStart() {
        crawlerBaseService.resetAll();
        crawlerBaseService.resetAllFail();
        getUrl();
        getPicUrl();
        getPic();

    }

    /**
     * 获取图片源链接，会有两种方式  针对的是https://weibo.cn
     */
    public void getUrl() {
        crawlerBaseService.doCrawler("1",1000,(tbCrawlerUrl) -> {
            String s = tbCrawlerUrl.getUrl();
            String html = WebCrawlerUtil.getWebHtml(s, CreateHeaderMap.getMapByName("crawler/page"),"utf-8");
            Document document = Jsoup.parse(html);
            Elements elements = document.getElementsByTag("a");
            for (Element e : elements) {
                String url = e.attr("href");
                if (url.matches(".*?picAll.*?")) {
                    crawlerBaseService.addUrl(new TbCrawlerUrl(url,"1","0"));
                    LogUtil.info(this.getClass(),"存入的AllUr={}" + url);
                } else if (url.matches(".*?oripic.*?")) {
                    if(!s.matches(ResourceBundle.getBundle("crawler/start").getString("regexStr"))){
                        url = "https://weibo.cn"+url;
                    }
                    crawlerBaseService.addUrl(new TbCrawlerUrl(url,"2","0"));
                    LogUtil.info(this.getClass(),"存入的Url={}" + url);
                }
            }
        });
    }

    /**
     * 获取图片真实链接，进行了一次302跳转
     */
    public void getPicUrl(){
        crawlerBaseService.doCrawler("2",1000,(tbCrawlerUrl) -> {
            String url = tbCrawlerUrl.getUrl();
            String picUrl = null;
            try{
                picUrl = WebCrawlerUtil.get302Location(url,CreateHeaderMap.getMapByName("crawler/yuantu"));
            }catch (IOException e){
                if (("获取不到location地址").equals(e.getMessage())){
                    LogUtil.info(this.getClass(),"休息一下");
                    Thread.sleep(1000*60*5);
                }else {
                    throw e;
                }
            }
            if(StringUtils.isNotEmpty(picUrl)){
                crawlerBaseService.addUrl(new TbCrawlerUrl(picUrl,"3","0"));
            }
        });
    }

    /**
     * 真实获取图片，这个没有session，多线程随便跑
     */
    @Async
    public void getPic() {
        crawlerBaseService.doCrawlerByMultiThread("3",0,20,url -> {
            WebCrawlerUtil.getWebPicture(url.getUrl(), url.getUrl().substring(url.getUrl().lastIndexOf("/")), CreateHeaderMap.getMapByNameWithRandomIp("crawler/picture"), ResourceBundle.getBundle("crawler/start").getString("dir"));
            LogUtil.info(this.getClass(),"保存图片={}" + url);
        });
    }
}
