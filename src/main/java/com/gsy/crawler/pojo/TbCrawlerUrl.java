package com.gsy.crawler.pojo;

import javax.persistence.*;

@Table(name = "tb_crawler_url")
public class TbCrawlerUrl {
    @Id
    private String url;

    private String type;

    private String busy;

    private String info;

    /**
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return busy
     */
    public String getBusy() {
        return busy;
    }

    /**
     * @param busy
     */
    public void setBusy(String busy) {
        this.busy = busy;
    }

    /**
     * @return info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info
     */
    public void setInfo(String info) {
        this.info = info;
    }
    public TbCrawlerUrl(){

    }

    public TbCrawlerUrl(String url, String type, String busy) {
        this.url = url;
        this.type = type;
        this.busy = busy;
    }

    public TbCrawlerUrl(String url, String busy) {
        this.url = url;
        this.busy = busy;
    }
}