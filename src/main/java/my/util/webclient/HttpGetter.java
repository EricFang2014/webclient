package my.util.webclient;

import java.util.Map;

public interface HttpGetter {
    HttpResult get(String url, Map<String, String> customHeaders) throws Exception;
    HttpResult get(String url, Map<String, String> customHeaders, int timeout) throws Exception;
}
