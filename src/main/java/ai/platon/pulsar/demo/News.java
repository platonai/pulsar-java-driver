package ai.platon.pulsar.demo;

import ai.platon.pulsar.driver.Driver;
import ai.platon.pulsar.driver.ScrapeResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class News {

    public static void main(String[] args) throws InterruptedException, TimeoutException, ExecutionException {
        // 1. create a driver with host and authToken
        Driver driver = new Driver("platonic.fun", "VQEudzEk-1-b5758d504780b7c42f43531a3a2da269");

        // 2. Write a SQL to scrape pages from a portal page
        String sql =
                " select" +
                "    dom_first_text(dom, '#main .news_tit_ly') as title," +
                "    dom_first_text(dom, '#main .newsly_ly') as publish_time," +
                "    dom_first_text(dom, '#tex') as content," +
                "    dom_all_imgs(dom, '#tex') as content_imgs," +
                "    dom_base_uri(dom) as URI" +
                " from" +
                "    load_out_pages('http://gxt.jl.gov.cn/xxgk/zcwj/ -expires 1d -itemExpires 30d', '#content ul li a[href~=zcwj]', 1, 100)";

        // 3. execute the SQL
        ScrapeResponse response = driver.execute(sql);

        // 4. use the response
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(response));

        // 5. close the driver
        driver.close();
    }
}
