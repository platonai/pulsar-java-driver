package ai.platon.pulsar.demo;

import ai.platon.pulsar.driver.Driver;
import ai.platon.pulsar.driver.ScrapeResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class News3 implements AutoCloseable {
    private final String host = "localhost";
    private final String authToken = "tang007-1-1f0962d6717527968794596ffc1b5f56";
    private final Driver driver = new Driver(host, authToken);

    public ScrapeResponse extractArticleLinks() throws InterruptedException, TimeoutException, ExecutionException {
        // The sql to extract urls from an index page
        String sql = "select" +
                " dom_all_hrefs(dom, '#content ul li a') as links" +
                " from " +
                " load_and_select('http://gxt.jl.gov.cn/xxgk/zcwj/ -i 7d', 'body')";

        return driver.execute(sql);
    }

    private List<String> extractLinksFromRS(List<Map<String, Object>> resultSet) {
        if (resultSet != null && !resultSet.isEmpty()) {
            String linksString = resultSet.get(0).get("links").toString();
            return driver.deserializeArray(linksString);
        }

        return List.of();
    }

    public List<ScrapeResponse> extractArticles(List<String> links) {
        // The sql to extract fields from news page
        String sql = "select" +
                "    dom_first_text(doc, '#main .news_tit_ly') as title," +
                "    dom_first_text(doc, '#main .newsly_ly') as publish_time," +
                "    dom_first_text(doc, '#tex') as content," +
                "    dom_all_imgs(doc, '#tex') as content_imgs," +
                "    dom_uri(doc) as URI," +
                "    *" +
                " from" +
                "    news_load_and_extract('{{url}} -i 30d')";

        List<String> sqls = links.stream().map(link -> sql.replace("{{url}}", link)).collect(Collectors.toList());

        return driver.executeAll(sqls);
    }

    @Override
    public void close() {
        driver.close();
    }

    public static void main(String[] args) throws InterruptedException, TimeoutException, ExecutionException {
        News3 news = new News3();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ScrapeResponse response = news.extractArticleLinks();
        System.out.println(gson.toJson(response));

        List<String> links = news.extractLinksFromRS(response.getResultSet());
        List<ScrapeResponse> responses = news.extractArticles(links);
        System.out.println(gson.toJson(responses));

        news.close();
    }
}
