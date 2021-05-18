package ai.platon.pulsar.demo;

import ai.platon.pulsar.driver.Driver;
import ai.platon.pulsar.driver.ScrapeResponse;
import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Products {

    public static void main(String[] args) throws InterruptedException, TimeoutException, ExecutionException {
        String sql = "select\n" +
                "            dom_first_text(dom, '#productTitle') as `title`,\n" +
                "            dom_first_text(dom, '#price tr td:contains(List Price) ~ td') as `listprice`,\n" +
                "            dom_first_text(dom, '#price tr td:matches(^Price) ~ td, #price_inside_buybox') as `price`,\n" +
                "            array_join_to_string(dom_all_texts(dom, '#wayfinding-breadcrumbs_container ul li a'), '|') as `categories`,\n" +
                "            dom_base_uri(dom) as `baseUri`\n" +
                "        from\n" +
                "            load_and_select('https://www.amazon.com/dp/B00BTX5926 -i 1s -storeContent true', ':root')";

        Driver driver = new Driver("localhost", "tang007-1-1f0962d6717527968794596ffc1b5f56");
        ScrapeResponse response = driver.execute(sql);
        System.out.println(new Gson().toJson(response));

        driver.close();
    }
}
