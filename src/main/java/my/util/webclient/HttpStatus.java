package my.util.webclient;

/**
 * Created by eric on 5/23/19.
 */
public class HttpStatus {
    private int statusCode;
    private String message;
    private String protocalVersion;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getProtocalVersion() {
        return protocalVersion;
    }

    public void setProtocalVersion(String protocalVersion) {
        this.protocalVersion = protocalVersion;
    }

    @Override
    public String toString() {
        return "HttpStatus{" +
                "message='" + message + '\'' +
                ", statusCode=" + statusCode +
                ", protocalVersion='" + protocalVersion + '\'' +
                '}';
    }
}
