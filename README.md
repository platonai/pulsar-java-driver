Java driver for pulsar
===================
** Network As A Database **

Turn the Web into tables and charts using simple SQLs.

[中文文档](README.zh.md)

## X-SQL

The [very first demo](src/main/java/ai/platon/pulsar/demo/News.java) is to scrape a set of webpages using just one SQL:

    select
        dom_first_text(dom, '#main .news_tit_ly') as title,
        dom_first_text(dom, '#main .newsly_ly') as publish_time,
        dom_first_text(dom, '#tex') as content,
        dom_all_imgs(dom, '#tex') as content_imgs,
        dom_base_uri(dom) as URI
    from
        load_out_pages('http://gxt.jl.gov.cn/xxgk/zcwj/ -expires 1d -itemExpires 30d', '#content ul li a', 1, 100);

The SQL works as the following:

    1. visit the portal page, in this example, it is http://gxt.jl.gov.cn/xxgk/zcwj/
    2. find out the out links in portal page, using the css query: "#content ul li a"
    3. fetch each out pages and scrape fields using the SQL functions: dom_first_text, dom_all_imgs, etc
    
We appended several parameters to the url:

    -expires 1d -itemExpires 30d
    
The parameters control the entire scraping process of the url,

    -i, -expires: the expire time of the portal page
    -ii, -itemExpires: the expire time of the out pages (item pages)

All scrape control parameters can be found [here](https://github.com/platonai/pulsar/blob/master/pulsar-skeleton/src/main/kotlin/ai/platon/pulsar/common/options/LoadOptions.kt).

The use defined SQL function load_out_pages returns a ResultSet named as "dom", so we can use SQL udfs to manipulate the
DOM: 

    ...
    dom_first_text(dom, '#main .news_tit_ly') as title
    dom_all_imgs(dom, '#tex') as content_imgs
    dom_base_uri(dom) as URI
    ...

All user defined functions can be found [here](https://github.com/platonai/pulsar/tree/master/pulsar-ql/src/main/kotlin/ai/platon/pulsar/ql/h2/udfs).

Check out the [demos](src/main/java/ai/platon/pulsar/demo) to see more examples.
Check out [The Complete Amazon Data Model](https://github.com/platonai/pulsar/blob/master/pulsar-app/pulsar-sites-support/pulsar-site-amazon/src/main/resources/config/sites/amazon/crawl/parse/sql) to see X-SQL examples.

## Scraping methods
The most useful scraping functions are:
    
    load_and_select                   # load a page and do something on the DOM
    load_out_pages                    # load a portal page, extract out links, load the out pages and do things on the DOM for each out page
    news_load_and_extract             # load a news page, automatically extract the news content using the famous boilerpile algorithm, and we also can manually extract fields from the page

The most useful DOM functions are:

    dom_(uri, base_uri, text, ...)     # query a attribute of the DOM element
    dom_first_(text, attr, href, ...)  # query a attribute of the first matching DOM element
    dom_all_(text, attr, href, ...)    # query a attribute of the all matching DOM elements

We also have lot of utility functions, such as string functions, data time functions, etc.
