package ai.platon.pulsar.driver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ScrapeResponse {
    private int statusCode = HttpStatus.CREATED.value();
    private int pageStatusCode = HttpStatus.CREATED.value();
    private String status = HttpStatus.CREATED.getReasonPhrase();
    private String pageStatus = HttpStatus.CREATED.getReasonPhrase();
    private int pageContentBytes = 0;
    private String createdAt;
    private String timestamp;
    private String uuid;
    private String version;
    private List<Map<String, Object>> resultSet;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPageStatus() {
        return pageStatus;
    }

    public void setPageStatus(String pageStatus) {
        this.pageStatus = pageStatus;
    }

    public int getPageContentBytes() {
        return pageContentBytes;
    }

    public void setPageContentBytes(int pageContentBytes) {
        this.pageContentBytes = pageContentBytes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getPageStatusCode() {
        return pageStatusCode;
    }

    public void setPageStatusCode(int pageStatusCode) {
        this.pageStatusCode = pageStatusCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Map<String, Object>> getResultSet() {
        return resultSet;
    }

    public void setResultSet(List<Map<String, Object>> resultSet) {
        this.resultSet = resultSet;
    }
}
