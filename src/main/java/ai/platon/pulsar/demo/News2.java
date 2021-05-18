package ai.platon.pulsar.demo;

import ai.platon.pulsar.driver.Driver;
import ai.platon.pulsar.driver.ScrapeResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class News2 {

    public static void main(String[] args) throws InterruptedException, TimeoutException, ExecutionException {
        // Create a driver
        Driver driver = new Driver("localhost", "tang007-1-1f0962d6717527968794596ffc1b5f56");

        // The sql to extract out links from an index page
        String sql = "select" +
                " dom_all_hrefs(dom, '#content ul li a') as links" +
                " from " +
                " load_and_select('http://gxt.jl.gov.cn/xxgk/zcwj/ -i 7d', 'body')";

        ScrapeResponse response = driver.execute(sql);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(response));

        // The sql to extract fields from news page
        String sql2 = "select" +
                "    dom_first_text(doc, '#main .news_tit_ly') as title," +
                "    dom_first_text(doc, '#main .newsly_ly') as publish_time," +
                "    dom_first_text(doc, '#tex') as content," +
                "    dom_all_imgs(doc, '#tex') as content_imgs," +
                "    dom_uri(doc) as URI," +
                "    *" +
                " from" +
                "    news_load_and_extract('{{url}} -i 30d')";

        // Extract links from result set
        Set<String> sqls = Set.of();
        List<Map<String, Object>> resultSet = response.getResultSet();
        if (resultSet != null && !resultSet.isEmpty()) {
            String linksString = resultSet.get(0).get("links").toString();
            sqls = driver.deserializeArray(linksString).stream()
                    .map(link -> sql2.replace("{{url}}", link))
                    .collect(Collectors.toSet());
        }

        // Execute all sqls to extract articles
        List<ScrapeResponse> responses = driver.executeAll(sqls);
        System.out.println(gson.toJson(responses));

        driver.close();
    }
}
