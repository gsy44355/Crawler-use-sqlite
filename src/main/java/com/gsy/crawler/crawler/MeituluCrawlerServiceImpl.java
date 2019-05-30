package com.gsy.crawler.crawler;

import com.gsy.crawler.pojo.TbCrawlerUrl;
import com.gsy.crawler.util.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;

/**
 * Created By Gsy on 2019/5/23
 */
@Service("meituluCrawler")
public class MeituluCrawlerServiceImpl implements SpecialCrawlerService {
    @Autowired
    CrawlerBaseService crawlerBaseService;
    @Override
    public void startNew() {
        crawlerBaseService.deleteAll();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("crawler/start");
        for (int i = Integer.parseInt(resourceBundle.getString("countStart") ); i <Integer.parseInt(resourceBundle.getString("countEnd") ); i++) {
            crawlerBaseService.addUrl(new TbCrawlerUrl(resourceBundle.getString("mainUrl").replace("@replace@",""+i),"1","0"));
        }
        crawlerBaseService.addUrl(new TbCrawlerUrl(resourceBundle.getString("firstUrl"),"1","0"));
        reStart();
    }

    @Override
    public void reStart() {
        crawlerBaseService.resetAll();
        getUrl();
        getPicUrl();
        getPic();

    }

    /**
     * 获取图片源链接，会有两种方式  针对的是https://weibo.cn
     */
    public void getUrl() {
        crawlerBaseService.doCrawlerByMultiThread("1",0,20,(tbCrawlerUrla) -> {
            String s = tbCrawlerUrla.getUrl();
            String html = WebCrawlerUtil.getWebHtml(s, CreateHeaderMap.getMapByName("crawler/pagea"),"utf-8");
            Document document = Jsoup.parse(html);
            Elements elements = document.getElementsByClass("img");
            Elements elements1  = elements.first().getElementsByTag("li");
            for (Element element: elements1) {
                TbCrawlerUrl tbCrawlerUrl = new TbCrawlerUrl();
                tbCrawlerUrl.setUrl(element.getElementsByTag("img").first().attr("src"));
                tbCrawlerUrl.setType("2");
                tbCrawlerUrl.setBusy("0");
                String info = "name="+element.getElementsByTag("img").first().attr("alt");
                info = info+";count="+element.getElementsByTag("p").first().text().replaceAll(".*?(\\d+) 张","$1");
                tbCrawlerUrl.setInfo(info);
                LogUtil.info(this.getClass(),"tbCrawlerUrl={}",tbCrawlerUrl);
                crawlerBaseService.addUrl(tbCrawlerUrl);
            }

        });
    }

    /**
     * 获取图片真实链接，进行了一次302跳转
     */
    public void getPicUrl(){
        crawlerBaseService.doCrawlerByMultiThread("2",0,20,(tbCrawlerUrl) -> {
            String url = tbCrawlerUrl.getUrl();
            String info = tbCrawlerUrl.getInfo();
            String dirName = info.split(";")[0].split("=")[1];
            int picNum = Integer.parseInt(info.split(";")[1].split("=")[1]);
            for (int i = 1; i <=picNum ; i++) {
                TbCrawlerUrl tbCrawlerUrl1 = new TbCrawlerUrl();
                tbCrawlerUrl1.setType("3");
                tbCrawlerUrl1.setUrl(url.replaceAll("(.*?)0([.]jpg)","$1"+i+"$2"));
                tbCrawlerUrl1.setBusy("0");
                tbCrawlerUrl1.setInfo(dirName);
                crawlerBaseService.addUrl(tbCrawlerUrl1);
                LogUtil.info(this.getClass(),"tbCrawlerUrl={}",tbCrawlerUrl.toString());
            }
        });
    }

    /**
     * 真实获取图片，这个没有session，多线程随便跑
     */
    @Async
    public void getPic() {
        crawlerBaseService.doCrawlerByMultiThread("3",0,20,tbCrawlerUrl -> {
            String url = tbCrawlerUrl.getUrl();
            WebCrawlerUtil.getWebPicture(url, url.substring(url.lastIndexOf("/")), CreateHeaderMap.getMapByNameWithRandomIp("crawler/picturea"), ResourceBundle.getBundle("crawler/start").getString("dir")+tbCrawlerUrl.getInfo());
            LogUtil.info(this.getClass(),"保存图片={}" + url);
        });
    }
}
