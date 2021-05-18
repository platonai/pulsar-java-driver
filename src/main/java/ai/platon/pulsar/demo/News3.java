package ai.platon.pulsar.demo;

import ai.platon.pulsar.driver.Driver;
import ai.platon.pulsar.driver.ScrapeResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class News3 {

    public static void main(String[] args) throws InterruptedException, TimeoutException, ExecutionException {
        // 1. Create a driver
        Driver driver = new Driver("platonic.fun", "tang007-1-1f0962d6717527968794596ffc1b5f56");

        // 2. Extract out links from a portal page
        String portalSQL =
                " select" +
                "     dom_all_hrefs(dom, '#content ul li a') as links" +
                "     from " +
                " load_and_select('http://gxt.jl.gov.cn/xxgk/zcwj/ -i 7d', 'body')";

        ScrapeResponse response = driver.execute(portalSQL);

        // 3. Extract links from result set
        List<Map<String, Object>> resultSet = response.getResultSet();
        Set<String> links = Set.of();
        if (resultSet != null && !resultSet.isEmpty()) {
            String linksString = resultSet.get(0).get("links").toString();
            links = new HashSet<>(driver.deserializeArray(linksString));
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(response));

        // 4. Create SQLs for article pages
        String articleSQLTemplate =
                " select" +
                "     dom_first_text(doc, '#main .news_tit_ly') as title," +
                "     dom_first_text(doc, '#main .newsly_ly') as publish_time," +
                "     dom_first_text(doc, '#tex') as content," +
                "     dom_all_imgs(doc, '#tex') as content_imgs," +
                "     dom_uri(doc) as URI," +
                "     *" +
                " from" +
                "     news_load_and_extract('{{url}} -i 30d')";
        Set<String> articleSQLs = links.stream().map(link -> articleSQLTemplate.replace("{{url}}", link))
                .collect(Collectors.toSet());

        // 5. Execute all SQLs to extract article pages
        List<ScrapeResponse> responses = driver.executeAll(articleSQLs);
        System.out.println(gson.toJson(responses));

        // 6. close the driver
        driver.close();
    }
}
