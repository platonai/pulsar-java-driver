package ai.platon.pulsar.demo;

import ai.platon.pulsar.driver.Driver;
import ai.platon.pulsar.driver.ScrapeResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class News {

    public static void main(String[] args) throws InterruptedException, TimeoutException, ExecutionException {
        // The sql to extract fields from news page
        String sql = "select" +
                "    dom_first_text(dom, '#main .news_tit_ly') as title," +
                "    dom_first_text(dom, '#main .newsly_ly') as publish_time," +
                "    dom_first_text(dom, '#tex') as content," +
                "    dom_all_imgs(dom, '#tex') as content_imgs," +
                "    dom_base_uri(dom) as URI" +
                " from" +
                "    load_out_pages('http://gxt.jl.gov.cn/xxgk/zcwj/ -i 1d -ii 30d', '#content ul li a', 1, 100)";

        Driver driver = new Driver("localhost", "tang007-1-1f0962d6717527968794596ffc1b5f56");
        ScrapeResponse response = driver.execute(sql);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(response));

        driver.close();
    }
}
