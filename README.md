#  使用sqlite的网络爬虫框架
这个写的目的是爬取微博图片，但是微博审核越来越严格，对cookie的校验会导致爬虫无法连接到微博网站，所以最后想要把这个发展成为自己的一个框架直接使用比较好的
+  这个是一个爬虫框架，支持多线程
+  使用springboot开发
+  使用sqlite进行数据管理
+  sqlite表名为tb_crawler_url
+  busy字段 0为新建 1为正在爬取  2为已完成  3为爬取失败
+  有自带的日志框架
+  使用时主要使用CrawlerBaseServiceImpl与WebCrawlerUtil进行开发
+  目前工程有三个例子，爬取美图录  爬取微博图片 爬取aiss图片，都在custom包下，可以考虑参照使用
+  resources/crawler 目录下放的是访问用到的请求头，使用CreateHeaderMap类中静态方法可以直接生成，然后调用WebCrawlerUtil中的方法作为参数传入使用
+  数据库文件在db文件夹下
+  使用mybatis+那个自动化生成的工具- -忘了叫啥了= =
+  爬虫case by case 开发建议放在custom文件夹下，实现SpecialCrawlerService接口
+  目前最新更新是保证Aiss的这个能用，直接springboot启动就可以使用（当然也有可能cookie过期= =）



2019以前的readme
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

 