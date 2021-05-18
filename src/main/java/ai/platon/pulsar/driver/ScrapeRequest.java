package ai.platon.pulsar.driver;

public class ScrapeRequest {
    private String authToken;
    private String sql;
    private String callbackUrl;
    private String priority = "HIGHER2";

    public ScrapeRequest(String authToken, String sql, String callbackUrl, String priority) {
        this.authToken = authToken;
        this.sql = sql;
        this.callbackUrl = callbackUrl;
        this.priority = priority;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
