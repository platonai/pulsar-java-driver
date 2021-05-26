package ai.platon.pulsar.demo;

import ai.platon.pulsar.driver.ScrapeResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class News2 {

    public static void main(String[] args) throws InterruptedException, TimeoutException, ExecutionException {
        try (NewsCrawler crawler = new NewsCrawler("platonic.fun", "VQEudzEk-1-b5758d504780b7c42f43531a3a2da269")) {
            List<String> links = crawler
                    .collectOutLinks("http://gxt.jl.gov.cn/xxgk/zcwj/", "-expires 7d", "#content ul li a")
                    .stream()
                    .filter(link -> link.contains("zcwj"))
                    .collect(Collectors.toList());
            System.out.println(String.join("\n", links));

            // Extract fields from news pages
            String articleSQLTemplate =
                    " select" +
                            "    dom_first_text(doc, '#main .news_tit_ly') as title," +
                            "    dom_first_text(doc, '#main .newsly_ly') as publish_time," +
                            "    dom_first_text(doc, '#tex') as content," +
                            "    dom_all_imgs(doc, '#tex') as content_imgs," +
                            "    dom_uri(doc) as URI," +
                            "    *" +
                            " from" +
                            "    news_load_and_extract('{{url}} -expires 30d')";

            List<ScrapeResponse> responses = crawler.scrapeAll(links, articleSQLTemplate);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(responses));
        }
    }
}
