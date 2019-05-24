#  Java 微博相册爬虫
+  使用sqlite数据库，Java版本11
+  无需安装数据库，clone工程就可以直接使用
+  支持随时停止，断点重传
+  支持自行扩展
--------------------------------------
##   使用方式
+  下载工程
+  修改page.properties，里面是用自己账号访问weibo.cn的headers
+  修改start.properties，按照start.properties中注释进行修改
+  修改command.java  @Qualifier("meituluCrawler")，修改为@Qualifier("WeiboCrawlerService")

    