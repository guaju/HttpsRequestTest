package com.guaju.httpsrequesttest.http;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by guaju on 2017/11/7.
 * https2负责任的一个请求类
 *
 */

public class HttpUtilsSafe2 {
    private static HttpUtilsSafe2 httpUtils;
    private HttpUtilsSafe2(){

    }
    public static HttpUtilsSafe2 getInstance(){
        if (httpUtils==null){
            httpUtils=new HttpUtilsSafe2();
        }
        return httpUtils;
    }
   public interface  OnRequestCallBack{
       void onSuccess(String s);
       void onFail(Exception e);
    }


    public void get(final Context context, final String path, final OnRequestCallBack callBack){
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
                    //3.定义一个 TrustManagerFactory,让这个工厂生成TrustManager数组

                    String defaultType = KeyStore.getDefaultType();
                    KeyStore instance = KeyStore.getInstance(defaultType);
                    instance.load(null);
                    instance.setCertificateEntry("srca",getX509Certificate(context));

                    String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();//得到默认算法
                    TrustManagerFactory trustMF = TrustManagerFactory.getInstance(defaultAlgorithm);
                    trustMF.init(instance);
                    TrustManager[] trustManagers = trustMF.getTrustManagers();


                    tls.init(null,trustManagers,new SecureRandom());
                    //3.ssl工厂
                    SSLSocketFactory factory = tls.getSocketFactory();
                    //4.添加一个主机名称校验器
                    conn.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            if (hostname.equals("kyfw.12306.cn")) {
                                    return true;
                            }else{
                                    return false;
                                }

                        }
                    });



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
   //拿到自己的证书
   X509Certificate getX509Certificate(Context context) throws IOException, CertificateException {
       InputStream in = context.getAssets().open("srca.cer");
       CertificateFactory instance = CertificateFactory.getInstance("X.509");
       X509Certificate certificate = (X509Certificate) instance.generateCertificate(in);
       return certificate;
   }

}
