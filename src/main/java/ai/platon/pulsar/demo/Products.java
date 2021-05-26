package ai.platon.pulsar.demo;

import ai.platon.pulsar.driver.Driver;
import ai.platon.pulsar.driver.ScrapeResponse;
import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Products {

    public static void main(String[] args) throws InterruptedException, TimeoutException, ExecutionException {
        String sql =
                " select" +
                "     dom_first_text(dom, '#productTitle') as `title`," +
                "     dom_first_text(dom, '#price tr td:contains(List Price) ~ td') as `listprice`," +
                "     dom_first_text(dom, '#price tr td:matches(^Price) ~ td, #price_inside_buybox') as `price`," +
                "     array_join_to_string(dom_all_texts(dom, '#wayfinding-breadcrumbs_container ul li a'), '|') as `categories`," +
                "     dom_base_uri(dom) as `baseUri`" +
                " from" +
                "     load_and_select('https://www.amazon.com/dp/B00BTX5926 -i 1s', ':root')";

        try (Driver driver = new Driver("platonic.fun", "VQEudzEk-1-b5758d504780b7c42f43531a3a2da269")) {
            ScrapeResponse response = driver.execute(sql);
            System.out.println(new Gson().toJson(response));
        }
    }
}
