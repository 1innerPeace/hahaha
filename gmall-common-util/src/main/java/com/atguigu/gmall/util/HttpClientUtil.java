package com.atguigu.gmall.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class HttpClientUtil {


    public static String doGet(String url){
        //创建httpclient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建http GET 请求
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try{
            // 执行请求
            response = httpClient.execute(httpGet);

            int statusCode = response.getStatusLine().getStatusCode();
            String value = String.valueOf(statusCode);
            String value2 = String.valueOf(HttpStatus.SC_OK);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
                httpClient.close();
                return result;
            }
            httpClient.close();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        return  null;
    }



    public static void download(String url,String fileName)   {

        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();

                // String result = EntityUtils.toString(entity, "UTF-8");
                byte[] bytes = EntityUtils.toByteArray(entity);
                File file =new File(fileName);
                //  InputStream in = entity.getContent();
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(bytes);

                EntityUtils.consume(entity);

                httpclient.close();
                fout.flush();
                fout.close();
                return  ;
            }
            httpclient.close();
        }catch (IOException e){
            e.printStackTrace();
            return  ;
        }
        return   ;
    }

}
