Pulsar 的 java 驱动说明
===================
** 网络即数据库 **

使用简单的 SQL 将互联网转变为表格和图表.

## X-SQL

[第一个演示](src/main/java/ai/platon/pulsar/demo/News.java) 是仅使用一个 SQL 抓取一组网页：

    select
        dom_first_text(dom, '#main .news_tit_ly') as title,
        dom_first_text(dom, '#main .newsly_ly') as publish_time,
        dom_first_text(dom, '#tex') as content,
        dom_all_imgs(dom, '#tex') as content_imgs,
        dom_base_uri(dom) as URI
    from
        load_out_pages('http://gxt.jl.gov.cn/xxgk/zcwj/ -expires 1d -itemExpires 30d', '#content ul li a', 1, 100);

这个 SQL 执行如下流程：

    1. 访问入口页面，在这个案例里，是 http://gxt.jl.gov.cn/xxgk/zcwj/
    2. 使用 CSS 选择器提取外链："#content ul li a"
    3. 采集外链网页并使用 SQL 提取字段：dom_first_text, dom_all_imgs, 等等
    
我们在 URL 后加入了一些参数：

    -expires 1d -itemExpires 30d
    
这些参数控制网页抓取的整个流程，如

    -i, -expires: 入口页面的过期时间
    -ii, -itemExpires: 外链页面的过期时间

所有抓取控制参数可以在
[这里](https://github.com/platonai/pulsar/blob/master/pulsar-skeleton/src/main/kotlin/ai/platon/pulsar/common/options/LoadOptions.kt)找到。

用户自定义 SQL 函数 load_out_pages 返回一个名为 "dom" 的 ResultSet，所以我们能够使用相应的用户自定义 SQL 函数来操作这个 DOM，譬如提取所需字段：

    ...
    dom_first_text(dom, '#main .news_tit_ly') as title
    dom_all_imgs(dom, '#tex') as content_imgs
    dom_base_uri(dom) as URI
    ...

所有用户自定义 SQL 函数可以在 [这里](https://github.com/platonai/pulsar/tree/master/pulsar-ql/src/main/kotlin/ai/platon/pulsar/ql/h2/udfs) 找到。

查看 [演示](src/main/java/ai/platon/pulsar/demo) 了解更多。