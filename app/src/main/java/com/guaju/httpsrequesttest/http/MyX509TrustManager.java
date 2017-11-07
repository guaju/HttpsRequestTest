package com.guaju.httpsrequesttest.http;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Created by guaju on 2017/11/7.
 */

public class MyX509TrustManager implements X509TrustManager {
    //如果需要对证书进行校验，需要这里去实现，如果不实现的话是不安全 
    X509Certificate mX509Certificate;


    public MyX509TrustManager(X509Certificate mX509Certificate) {
        this.mX509Certificate = mX509Certificate;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
       for (X509Certificate certificate:chain){
           //检查证书是否有效
           certificate.checkValidity();
           try {
               certificate.verify(mX509Certificate.getPublicKey());
           } catch (NoSuchAlgorithmException e) {
               e.printStackTrace();
           } catch (InvalidKeyException e) {
               e.printStackTrace();
           } catch (NoSuchProviderException e) {
               e.printStackTrace();
           } catch (SignatureException e) {
               e.printStackTrace();
           }

       }


    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
