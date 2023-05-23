package com.bbudzowski.homeautoandroid.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class BaseApi<T> {
    static OkHttpClient client = null;
    static final String host = "https://weaweg.mywire.org:4433/api";
    final ObjectMapper mapper = new ObjectMapper();

    public static boolean createClient(InputStream keyFile, String username, String password) {
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.hostnameVerifier((hostname, session) -> true);
            builder.authenticator((route, response) -> {
                Response tmp = response;
                for(int i = 0; tmp != null; ++i) {
                    tmp = tmp.priorResponse();
                    if(i >=3)
                        return null;
                }
                String credential = Credentials.basic(username, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            });
            KeyStore myTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            System.out.println(KeyStore.getDefaultType());
            myTrustStore.load(keyFile, "tial2o3".toCharArray());
            keyFile.close();

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(myTrustStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, trustManager);
            builder.connectTimeout(10, TimeUnit.SECONDS);
            builder.writeTimeout(10, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            client = new OkHttpClient(builder);
            return true;
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException |
                 CertificateException | IOException e) {
            return false;
        }
    }

    Response getResponse(String url) {
        Request request = new Request.Builder().get().url(url).build();
        try {
            return client.newCall(request).execute();
        } catch (Exception e) {
            //return null;
            throw new RuntimeException(e);
        }
    }

    Response postResponse(String url, String bodyString) {
        RequestBody body = RequestBody.create(bodyString, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().post(body).url(url).build();
        try {
            return client.newCall(request).execute();
        } catch (Exception e) {
            return null;
        }
    }

    List<T> getResultList(Response res, JavaType type) {
        try {
            return mapper.readValue(res.body().string(), type);
        } catch (Exception e) {
            return null;
        }
    }

    T getSingleResult(Response res) {
        try {
            return mapper.readValue(res.body().string(), new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }
}
