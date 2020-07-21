package com.zhp.teaching.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @Class_Name HttpClientUtil
 * @Author zhongping
 * @Date 2020/7/7 11:03
 **/
public class HttpclientUtil {
    public static String doGet(String url){
        //创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建httpGET请求
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            //执行请求
            response = httpClient.execute(httpGet);
            //判断返回状态是否为200
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity,"UTF-8");
                EntityUtils.consume(entity);
                httpClient.close();
                return result;
            }
            httpClient.close();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return null;
    }
    public static String doPost(String url){
        //创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建httpGET请求
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            //执行请求
            response = httpClient.execute(httpPost);
            //判断返回状态是否为200
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity,"UTF-8");
                EntityUtils.consume(entity);
                httpClient.close();
                return result;
            }
            httpClient.close();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
