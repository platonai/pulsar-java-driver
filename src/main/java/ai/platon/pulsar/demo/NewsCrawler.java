package ai.platon.pulsar.demo;

import ai.platon.pulsar.driver.Driver;
import ai.platon.pulsar.driver.ScrapeResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class NewsCrawler implements AutoCloseable {
    private final Driver driver;

    public NewsCrawler(String host, String authToken) {
        this.driver = new Driver(host, authToken);
    }

    public List<String> collectOutLinks(String url, String args, String outLinkCss) throws InterruptedException, TimeoutException, ExecutionException {
        // The sql to extract urls from an index page
        String sql =
                " select" +
                "     dom_all_hrefs(dom, '" + outLinkCss + "') as links" +
                " from" +
                "     load_and_select('" + url + " " + args + "', 'body')";

        ScrapeResponse response = driver.execute(sql);
        List<Map<String, Object>> resultSet = response.getResultSet();
        return getLinks(resultSet);
    }

    public List<ScrapeResponse> scrapeAll(List<String> links, String sqlTemplate) {
        Set<String> sqls = links.stream().map(link -> sqlTemplate.replace("{{url}}", link))
                .collect(Collectors.toUnmodifiableSet());
        return driver.executeAll(sqls);
    }

    private List<String> getLinks(List<Map<String, Object>> resultSet) {
        if (resultSet == null || resultSet.isEmpty()) {
            return List.of();
        }

        String linksString = resultSet.get(0).get("links").toString();
        return driver.deserializeArray(linksString);
    }

    @Override
    public void close() {
        driver.close();
    }
}
