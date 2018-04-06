package com.gps.imp.utils.ssl;



import com.gps.imp.utils.JavaVersionUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created by leogps on 4/1/18.
 */
public class HttpClientUtils {

    private static Logger LOGGER = Logger.getLogger(HttpClientUtils.class);

    public static void initBouncyCastle() {

        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);

        Security.removeProvider(BouncyCastleJsseProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleJsseProvider(), 1);

        Provider[] providers = Security.getProviders();
        LOGGER.debug("Listing security providers...");
        for (Provider provider : providers) {
            LOGGER.debug(provider);
        }
    }

    public static Client getNewClient() {
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        if(JavaVersionUtils.isGreaterThan6()) {
            return clientBuilder.build();
        }

        try {
            clientBuilder.sslContext(getBouncyCastleSslContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clientBuilder.build();
    }



    private static SSLContext getBouncyCastleSslContext() throws NoSuchProviderException, NoSuchAlgorithmException, IOException, CertificateException, KeyManagementException, KeyStoreException {

        KeyStore keyStore = KeyStore.getInstance("JKS");

        // load default jvm keystore
        keyStore.load(new FileInputStream(
                System.getProperties()
                        .getProperty("java.home") + File.separator
                        + "lib" + File.separator + "security" + File.separator
                        + "cacerts"), "changeit".toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2", BouncyCastleJsseProvider.PROVIDER_NAME);
        sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
        return sslContext;
    }
}
