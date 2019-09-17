package my.util.webclient;

import java.util.Map;

/**
 * A utility class to submit request and get String response
 * Created by eric on 5/23/19.
 */
public interface HttpRequester {
    String ContentType = "Content-Type";
    String JSONType = "application/json";
    HttpResult submit(String url, String body, Map<String, String> customHeaders) throws Exception;
    HttpResult submit(String url, String body, Map<String, String> customHeaders, int timeout) throws Exception;
}
