package ai.platon.pulsar.driver;

public class HttpException extends Exception {
    private int responseCode;
    private String uri;

    public int getResponseCode() {
        return responseCode;
    }

    public String getUri() {
        return uri;
    }

    public HttpException(int responseCode, String uri) {
        this.responseCode = responseCode;
        this.uri = uri;
    }

    public HttpException(String message, int responseCode, String uri) {
        super(message);
        this.responseCode = responseCode;
        this.uri = uri;
    }

    public HttpException(String message, Throwable cause, int responseCode, String uri) {
        super(message, cause);
        this.responseCode = responseCode;
        this.uri = uri;
    }

    public HttpException(Throwable cause, int responseCode, String uri) {
        super(cause);
        this.responseCode = responseCode;
        this.uri = uri;
    }

    public HttpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int responseCode, String uri) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.responseCode = responseCode;
        this.uri = uri;
    }
}