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


/**
 * 这个作为例子放在这里，可以参照这个来写
 */
@Service("AissPicCrawlerServiceImpl")
public class AissPicCrawlerServiceImpl implements SpecialCrawlerService {
//    注入基础service，后续调用其中方法
    @Autowired
    CrawlerBaseService crawlerBaseService;
//    代码内直接给出网址主体，后续使用，如果不用可以不写
    String baseUrl = "https://www.aitaotu.com";
//    给出保存图片的主文件夹，保存图片时拼接使用
    String basedir = "D:\\图片\\aiss";
//    从0开始时使用，实现接口中需要覆盖的方法
    @Override
    public void startNew() {
        crawlerBaseService.deleteAll();//删除表中所有的网址，这个表明了不能同时开两个不同的任务，可以优化，但是单机个人认为没必要，如果一定要，考虑把数据库文件导出来。
        String startUrl = "https://www.aitaotu.com/tag/aiss/@replace@.html";
        for(int i = 1;i<=8;i++){
            String stemp = startUrl.replaceAll("@replace@",String.valueOf(i));
            crawlerBaseService.addUrl(new TbCrawlerUrl(stemp, "1","0"));//直接调用addUrl这个方法，相当于维护的URL池，type位可以自定义，但建议用1 2 3 4直接写，简单
        }
        reStart();//后续操作与重新开始一致，只不过最开始需要给出起始URL，所以只需要调用restart方法即可。
    }
//    中间暂停过，想要重新开始使用的逻辑
    @Override
    public void reStart() {
        crawlerBaseService.resetAll();//将URL池中的busy位为1的置为0，为1说明上一次正在执行，但是还没完
        crawlerBaseService.resetAllFail();//将busy位为3的置为0，为3说明上一次失败了，如果不想管失败的可以考虑注释掉
        //下面这个方法是多线程方法，使用λ表达式，异常等都已经处理，只需要自行维护一下特殊处理过程，即提取URL的过程即可
        crawlerBaseService.doCrawlerByMultiThread("1",0,8,(tbCrawlerUrl)->{
            String s = tbCrawlerUrl.getUrl();
            String html = WebCrawlerUtil.getWebHtml(s,CreateHeaderMap.getMapByName("crawler/aisspage1"),"utf-8");
            Document document = Jsoup.parse(html);
            Elements elements = document.getElementsByClass("Pli-litpic");
            for (Element e:elements) {
                String temp = baseUrl+e.attr("href");
                crawlerBaseService.addUrl(new TbCrawlerUrl(temp,"2","0"));//注意type位要与之前不一致
            }
        });
        //下面方法就是看你对于URL池需要几次更新
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

            String s = url.getUrl();
            String info = url.getInfo();
            String pathdir = info.split(";")[0];
            String picname = s.substring(s.lastIndexOf("/"));
            String picdir = basedir + "\\" + pathdir;
            //获取图片并保存
            WebCrawlerUtil.getWebPicture(s,picname,CreateHeaderMap.getMapByNameWithRandomIp("crawler/aisspagepic"),picdir);
        });
    }
}
