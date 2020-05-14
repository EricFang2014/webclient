package my.util.webclient;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

/**
 * Created by eric on 5/23/19.
 */
public class HttpPostRequester implements HttpRequester {
    private static Logger logger = LoggerFactory.getLogger(HttpPostRequester.class);

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    private int timeout;

    private ContentType contentType;

    public org.apache.http.entity.ContentType getContentType() {
        return contentType;
    }

    public void setContentType(org.apache.http.entity.ContentType contentType) {
        this.contentType = contentType;
    }

    @Override
    public HttpResult submit(String url, String body, Map<String, String> customHeaders) throws Exception {
        return submit(url, body, customHeaders, timeout);
    }

    @Override
    public HttpResult submit(String url, String body, Map<String, String> customHeaders, int timeout) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpResult httpResult = new HttpResult();
        try {
            HttpPost post = new HttpPost(url);
            if (logger.isDebugEnabled()){
                logger.debug("trying to submit request {}", post.getRequestLine());
            }
            if (timeout > 0){
                logger.debug("Trying to set timeout {} seconds to access {}", timeout, url);
                RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout * 1000)
                        .setSocketTimeout(timeout * 1000).build();
                post.setConfig(requestConfig);
            }

            if (!StringUtils.isBlank(body)) {
                org.apache.http.entity.ContentType contentType = getContentType() != null ? getContentType() : org.apache.http.entity.ContentType.APPLICATION_JSON;
                StringEntity postEntity = new StringEntity(body, contentType);
                post.setEntity(postEntity);
            }
            if (null != customHeaders && customHeaders.size() > 0){
                Set<String> keys = customHeaders.keySet();
                for(String key : keys){
                    post.addHeader(key, customHeaders.get(key));
                    logger.debug("add key[{}]-value[{}] to header"
                            , key, customHeaders.get(key));
                }
            }
            if (logger.isDebugEnabled()){
                Header[] headers = post.getAllHeaders();
                logger.debug("There are {} headers in post request", headers.length);
                for(Header header : headers){
                    logger.debug("{}:{}", header.getName(), header.getValue());
                }
            }
            CloseableHttpResponse response = httpclient.execute(post);
            StatusLine statusLine = response.getStatusLine();
            HttpStatus status = new HttpStatus();
            status.setStatusCode(statusLine.getStatusCode());
            status.setMessage(statusLine.getReasonPhrase());
            status.setProtocalVersion(statusLine.getProtocolVersion().toString());
            httpResult.setStatus(status);
            //httpResult.setStatus(response.getStatusLine());
            try {

                HttpEntity entity = response.getEntity();
                //System.out.println(response.getStatusLine());
                //EntityUtils.consume(entity);

                httpResult.setResponse(EntityUtils.toString(entity));
                logger.debug("The response from {} is {}", url, httpResult.getResponse());

            }catch (Exception ex) {
                logger.error(String.format("Error occurs when submitting request %s, message: %s"
                        , url, ex.getMessage()), ex);
                httpResult.setResponse(response.toString());
                httpResult.setError(ex.getMessage());
            }
            finally{
                response.close();
            }
        }catch(Exception ex){
            logger.error(String.format("Error occurs when submitting request %s, message: %s"
                    , url, ex.getMessage()), ex);

            httpResult.setError(ex.getMessage());
        }
        finally {
            httpclient.close();
        }
        return httpResult;
    }
}
