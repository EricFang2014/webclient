package my.util.webclient;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class HttpGetterImpl implements HttpGetter{
    private static Logger logger = LoggerFactory.getLogger(HttpGetterImpl.class);
    @Override
    public HttpResult get(String url, Map<String, String> customHeaders) throws Exception {
        return get(url, customHeaders, 0);
    }

    @Override
    public HttpResult get(String url, Map<String, String> customHeaders, int timeout) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpResult httpResult = new HttpResult();
        try {
            HttpGet get = new HttpGet(url);
            if (logger.isDebugEnabled()){
                logger.debug("trying to submit request {}", get.getRequestLine());
            }
            if (timeout > 0){
                logger.debug("Trying to set timeout {} seconds to access {}", timeout, url);
                RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout * 1000)
                        .setSocketTimeout(timeout * 1000).build();
                get.setConfig(requestConfig);
            }

            if (null != customHeaders && customHeaders.size() > 0){
                Set<String> keys = customHeaders.keySet();
                for(String key : keys){
                    get.addHeader(key, customHeaders.get(key));
                    logger.debug("add key[{}]-value[{}] to header"
                            , key, customHeaders.get(key));
                }
            }
            if (logger.isDebugEnabled()){
                Header[] headers = get.getAllHeaders();
                logger.debug("There are {} headers in post request", headers.length);
                for(Header header : headers){
                    logger.debug("{}:{}", header.getName(), header.getValue());
                }
            }
            CloseableHttpResponse response = httpclient.execute(get);
            StatusLine statusLine = response.getStatusLine();
            HttpStatus status = new HttpStatus();
            status.setStatusCode(statusLine.getStatusCode());
            status.setMessage(statusLine.getReasonPhrase());
            status.setProtocalVersion(statusLine.getProtocolVersion().toString());
            httpResult.setStatus(status);
            try {

                HttpEntity entity = response.getEntity();
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
