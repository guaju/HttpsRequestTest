package com.guaju.httpsrequesttest.http;

import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Created by guaju on 2017/11/7.
 * 不负责任的请求类
 */

public class HttpUtilsUnSafe {
    private static HttpUtilsUnSafe httpUtils;
    private HttpUtilsUnSafe(){

    }
    public static HttpUtilsUnSafe getInstance(){
        if (httpUtils==null){
            httpUtils=new HttpUtilsUnSafe();
        }
        return httpUtils;
    }
   public interface  OnRequestCallBack{
       void onSuccess(String s);
       void onFail(Exception e);
    }


    public void get(final String path,final OnRequestCallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //联网操作
                try {
                    URL url = new URL(path);
                    //1.改成s
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    //2.SSLContext 初始化
                    SSLContext tls = SSLContext.getInstance("TLS");
                    TrustManager[] trustManagers={new MyX509TrustManagerUnSafe()};
                    tls.init(null,trustManagers,new SecureRandom());
                    //3.ssl工厂
                    SSLSocketFactory factory = tls.getSocketFactory();
                    conn.setSSLSocketFactory(factory);


                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.connect();
                    InputStream inputStream = conn.getInputStream();

                    StringBuilder sb=new StringBuilder();
                    int flag;
                    byte[] buf=new byte[1024];
                    while((flag=inputStream.read(buf))!=-1){
                        sb.append(new String(buf,0,flag));
                    }
                    String s = sb.toString();
                    //调用对方传入callback完成回调操作
                    callBack.onSuccess(s);
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.onFail(e);
                }
            }
        }).start();



    }

}
