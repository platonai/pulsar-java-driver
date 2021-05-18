package ai.platon.pulsar.driver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Will upgrade to web socket protocol
 * */
public class Driver implements AutoCloseable {
    private String host;
    private String authToken;
    private Duration timeout = Duration.ofSeconds(120);

    private String baseUri;
    private String scrapeService;
    private String statusService;
    private ConcurrentSkipListMap<String, CompletableFuture<ScrapeResponse>> responseFutures = new ConcurrentSkipListMap<>();
    private Duration statusCheckDelay = Duration.ofSeconds(3);
    private HttpClient httpClient = HttpClient.newHttpClient();
    private Timer timer = new Timer();

    public Driver(String host, String authToken) {
        this.host = host;
        this.authToken = authToken;

        baseUri = "http://" + host + ":8182/api/x/a";
        scrapeService = baseUri + "/q";
        statusService = baseUri + "/status/batch";
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateResponses();
            }
        }, statusCheckDelay.toMillis(), statusCheckDelay.toMillis());
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public ScrapeResponse execute(String sql) throws InterruptedException, TimeoutException, ExecutionException {
        String uuid = submitTask(sql);
        CompletableFuture<ScrapeResponse> future = new CompletableFuture<>();
        responseFutures.put(uuid, future);
        return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    public List<ScrapeResponse> executeAll(Collection<String> sqls) {
        List<CompletableFuture<ScrapeResponse>> futures = sqls.stream().map(this::submitTask)
                .map(uuid -> {
                    CompletableFuture<ScrapeResponse> future = new CompletableFuture<>();
                    responseFutures.put(uuid, future);
                    return future;
                })
                .collect(Collectors.toList());

        List<ScrapeResponse> responses = new ArrayList<>();
        long i = timeout.toSeconds();
        while (i-- > 0 && !futures.isEmpty() && !Thread.currentThread().isInterrupted()) {
            Iterator<CompletableFuture<ScrapeResponse>> it = futures.iterator();
            while (it.hasNext()) {
                CompletableFuture<ScrapeResponse> future = it.next();
                try {
                    if (future.isDone()) {
                        responses.add(future.get());
                        it.remove();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return responses;
    }

    public CompletableFuture<ScrapeResponse> get(String uuid) {
        return responseFutures.get(uuid);
    }

    public List<CompletableFuture<ScrapeResponse>> getAll() {
        return new ArrayList<>(responseFutures.values());
    }

    public List<String> deserializeArray(String serializedArray) {
        if (serializedArray == null || serializedArray.isBlank()) {
            return Collections.emptyList();
        }

        List<String> links = List.of();

        String str = serializedArray.replace("(", "").replace(")", "");
        if (!str.isBlank()) {
            links = Arrays.stream(str.split(", ")).collect(Collectors.toList());
        }

        return links;
    }

    @Override
    public void close() {
        timer.cancel();
    }

    private void updateResponses() {
        try {
            doUpdateResponses();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        }
    }

    private void doUpdateResponses() throws IOException, InterruptedException, HttpException {
        List<String> keys = responseFutures.entrySet().stream()
                .filter(e -> !e.getValue().isDone())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        doUpdateResponses(keys);
    }

    private void doUpdateResponses(Iterable<String> uuids) throws IOException, InterruptedException, HttpException {
        String uuidsString = String.join(",", uuids);
        if (uuidsString.isEmpty()) {
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(statusService + "?uuids=" + uuidsString + "&authToken=" + authToken))
                .timeout(Duration.ofMinutes(2))
                .GET()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != HttpStatus.OK.value()) {
            throw new HttpException("Http failure", httpResponse.statusCode(), httpResponse.uri().toString());
        }

        Type listType = new TypeToken<ArrayList<ScrapeResponse>>(){}.getType();
        List<ScrapeResponse> responses = new Gson().fromJson(httpResponse.body(), listType);
        responses.forEach(response -> {
            if (response.getStatusCode() != HttpStatus.CREATED.value()) {
                CompletableFuture<ScrapeResponse> future = responseFutures.get(response.getUuid());
                if (future != null) {
                    future.complete(response);
                }
            }
        });
    }

    private String submitTask(String sql) {
        ScrapeRequest requestEntity = new ScrapeRequest(authToken, sql, "HIGHER2");
        HttpRequest request = post(scrapeService, requestEntity);
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpRequest post(String url, Object requestEntity) {
        String requestBody = new Gson().toJson(requestEntity);
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }
}
