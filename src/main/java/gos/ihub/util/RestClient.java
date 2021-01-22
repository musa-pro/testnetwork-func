package gos.ihub.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RestClient {
    private String endPoint;
    private Map<String, String> headers = new HashMap<String, String>();
    private String contentType;
    public RestClient(String endPoint){
        this.endPoint = endPoint;
    }

    public void setContentType(String contentType){
        this.contentType = contentType;
        headers.put("Content-Type", contentType);
    }

    public void setHeader(String key, String value){
        headers.put(key, value);
    }

    public <T> Map<String, Object> sendPOSTRequest(T content) throws IOException {

        String responseStr;
        String bodyContent;

        if(headers != null){
            String contentType = headers.get("Content-Type");
            if(contentType != null) this.setContentType(contentType);
        }

        HttpPost postRequest = new HttpPost(endPoint);
        if(contentType != null && contentType.equalsIgnoreCase("application/json")){
            bodyContent = ((JSONObject) content).toJSONString();
        }else{
            bodyContent = (String) content;
        }
        postRequest.setEntity(new StringEntity(bodyContent));
        Map<String, Object> responseMap = sendRequest(postRequest);

        return responseMap;
    }

    public <T> Map<String, Object> sendPUTRequest(T content) throws IOException {

        String responseStr;
        String bodyContent;

        if(headers != null){
            String contentType = headers.get("Content-Type");
            if(contentType != null) this.setContentType(contentType);
        }

        HttpPut putRequest = new HttpPut(endPoint);
        if(contentType != null && contentType.equalsIgnoreCase("application/json")){
            bodyContent = ((JSONObject) content).toJSONString();
        }else{
            bodyContent = (String) content;
        }
        putRequest.setEntity(new StringEntity(bodyContent));
        Map<String, Object> responseMap = sendRequest(putRequest);

        return responseMap;
    }

    public Map<String, Object> sendGETRequest() throws IOException {

        if(headers != null){
            String contentType = headers.get("Content-Type");
            if(contentType != null) this.setContentType(contentType);
        }

        HttpGet getRequest = new HttpGet(endPoint);
        Map<String, Object> responseMap = sendRequest(getRequest);

        return responseMap;
    }

    private Map<String, Object> sendRequest(HttpRequestBase request) throws IOException {

        Map<String, Object> responseMap = new HashMap<String, Object>();


        if(contentType != null) request.addHeader("Content-Type", contentType);
        if(headers != null){
            for(String key : headers.keySet()){
                if(key.equalsIgnoreCase("Content-Type")) continue;
                request.addHeader(key, (String) headers.get(key));
            }
        }

        request.setConfig(getRequestConfig());

        try (CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
             CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(request)) {
            HttpEntity httpResponseEntity = closeableHttpResponse.getEntity();

            responseMap.put("StatusCode", closeableHttpResponse.getStatusLine().getStatusCode());
            responseMap.put("ResponseBody", EntityUtils.toString(httpResponseEntity));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return responseMap;
    }

    private RequestConfig getRequestConfig(){
        RequestConfig.Builder requestConfig = RequestConfig.custom();
        requestConfig.setConnectTimeout(15 * 1000);
        requestConfig.setConnectionRequestTimeout(15 * 1000);
        requestConfig.setSocketTimeout(15 * 1000);
        return requestConfig.build();
    }
}
