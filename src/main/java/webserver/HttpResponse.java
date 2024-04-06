package webserver;

public class HttpResponse {
    private int code;
    private byte[] responseBody;
    boolean cacheable;
    long cacheTimeout;

    public HttpResponse(int code, byte[] responseBody, boolean cacheable, long cacheTimeout) {
        this.code = code;
        this.responseBody = responseBody;
        this.cacheable = cacheable;
        this.cacheTimeout = cacheTimeout;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public byte[] getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public long getCacheTimeout() {
        return cacheTimeout;
    }

    public void setCacheTimeout(long cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
    }
}
