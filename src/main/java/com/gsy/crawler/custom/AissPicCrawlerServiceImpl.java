package com.gsy.crawler.custom;

import com.gsy.crawler.crawler.CrawlerBaseService;
import com.gsy.crawler.crawler.CreateHeaderMap;
import com.gsy.crawler.crawler.SpecialCrawlerService;
import com.gsy.crawler.crawler.WebCrawlerUtil;
import com.gsy.crawler.pojo.TbCrawlerUrl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ResourceBundle;

@Service("AissPicCrawlerServiceImpl")
public class AissPicCrawlerServiceImpl implements SpecialCrawlerService {
    @Autowired
    CrawlerBaseService crawlerBaseService;
    String baseUrl = "https://www.aitaotu.com";
    @Override
    public void startNew() {
        crawlerBaseService.deleteAll();
        String startUrl = "https://www.aitaotu.com/tag/aiss/@replace@.html";
        for(int i = 1;i<=8;i++){
            String stemp = startUrl.replaceAll("@replace@",String.valueOf(i));
            crawlerBaseService.addUrl(new TbCrawlerUrl(stemp, "1","0"));
        }
        reStart();
    }

    @Override
    public void reStart() {
        crawlerBaseService.resetAll();
        crawlerBaseService.resetAllFail();
        crawlerBaseService.doCrawlerByMultiThread("1",0,8,(tbCrawlerUrl)->{
            String s = tbCrawlerUrl.getUrl();
            String html = WebCrawlerUtil.getWebHtml(s,CreateHeaderMap.getMapByName("crawler/aisspage1"),"utf-8");
            Document document = Jsoup.parse(html);
            Elements elements = document.getElementsByClass("Pli-litpic");
            for (Element e:elements) {
                String temp = baseUrl+e.attr("href");
                crawlerBaseService.addUrl(new TbCrawlerUrl(temp,"2","0"));
            }
        });
        getUrl();
        getPicUrl();
        getPic();
    }
    public void getUrl(){
        crawlerBaseService.doCrawlerByMultiThread("2",0,20,tbCrawlerUrl -> {
            String s = tbCrawlerUrl.getUrl();
            String html = WebCrawlerUtil.getWebHtml(s,CreateHeaderMap.getMapByName("crawler/aisspage2"),"utf-8");
            Document document = Jsoup.parse(html);
            String totalpage = document.getElementsByClass("totalpage").first().text();
            String title = document.getElementsByTag("h2").first().text().replaceAll("\\s","");
            for (int i = 1; i <= Integer.parseInt(totalpage); i++) {
                crawlerBaseService.addUrl(new TbCrawlerUrl(s.replace(".html","_"+i+".html"),"3","0",title));
            }
//            https://www.aitaotu.com/guonei/25825_4.html
//            https://www.aitaotu.com/guonei/25825.html
        });
    }
    public void getPicUrl(){
        crawlerBaseService.doCrawlerByMultiThread("3",0,20,urlPojo ->{
            String s = urlPojo.getUrl();
            String html = WebCrawlerUtil.getWebHtml(s,CreateHeaderMap.getMapByName("crawler/aisspage2"),"utf-8");
            Document document = Jsoup.parse(html);
            Element elementdiv = document.getElementById("big-pic");
            Elements elementspic = elementdiv.getElementsByTag("img");
            for (Element e:elementspic) {
                crawlerBaseService.addUrl(new TbCrawlerUrl(e.attr("src"),"4","0",urlPojo.getInfo()+";"+e.attr("alt").replaceAll("\\s","")+".jpg"));
            }
//            big-pic
        } );
    }
    public void getPic(){
        crawlerBaseService.doCrawlerByMultiThread("4",0,20,url -> {
            String basedir = "D:\\图片\\aiss";
            String s = url.getUrl();
            String info = url.getInfo();
            String pathdir = info.split(";")[0];
            String picname = s.substring(s.lastIndexOf("/"));
            String picdir = basedir + "\\" + pathdir;
            WebCrawlerUtil.getWebPicture(s,picname,CreateHeaderMap.getMapByNameWithRandomIp("crawler/aisspagepic"),picdir);
        });
    }
}
