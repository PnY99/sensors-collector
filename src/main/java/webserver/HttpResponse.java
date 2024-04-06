package webserver;

public class HttpResponse {
    private int code;
    private byte[] responseBody;

    public HttpResponse(int code, byte[] responseBody) {
        this.code = code;
        this.responseBody = responseBody;
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
}
