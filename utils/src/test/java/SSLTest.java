import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.net.ssl.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by leogps on 4/1/18.
 */
public class SSLTest {

    @Test
    public void bouncyCastleTLSV12PlainSocketTest()
            throws Exception
    {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);

        Security.removeProvider(BouncyCastleJsseProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleJsseProvider(), 2);

        /*
         * TEST CODE ONLY. If writing your own code based on this test case, you should configure
         * your trust manager(s) using a proper TrustManagerFactory, or else the server will be
         * completely unauthenticated.
         */
        TrustManager tm = new X509TrustManager()
        {
            public X509Certificate[] getAcceptedIssuers()
            {
                return new X509Certificate[0];
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
            {
                if (chain == null || chain.length < 1 || authType == null || authType.length() < 1)
                {
                    throw new IllegalArgumentException();
                }

                String subject = chain[0].getSubjectX500Principal().getName();
                System.out.println("Auto-trusted server certificate chain for: " + subject);
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
            {
            }
        };

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2", BouncyCastleJsseProvider.PROVIDER_NAME);
        sslContext.init(null, new TrustManager[]{ tm }, new SecureRandom());

        String host = "api.github.com";///repos/rg3/youtube-dl/releases/latest
        int port = 443;

        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        SSLSocket sslSocket = (SSLSocket)sslSocketFactory.createSocket(host, port);

        System.out.println("---");

        InputStream input = sslSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        String line;
        while ((line = reader.readLine()) != null)
        {
            System.out.println("<<< " + line);

        }

        System.out.flush();

        sslSocket.close();
    }

    @Test
    public void bouncyCastleTLSV12JerseyTest() throws KeyManagementException, NoSuchProviderException, NoSuchAlgorithmException, IOException, CertificateException, KeyStoreException {

        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);

        Security.removeProvider(BouncyCastleJsseProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleJsseProvider(), 2);

        for (Provider p : Security.getProviders()) {
            System.out.println(p);
        }

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2", BouncyCastleJsseProvider.PROVIDER_NAME);

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
        sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());

        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        clientBuilder.sslContext(sslContext);

        Client client = clientBuilder.build();
        Response response = client.target("https://api.github.com/repos/rg3/youtube-dl/releases/latest")
                .request()
                .get();
        if(response != null && response.getStatus() == Response.Status.OK.getStatusCode()) {
            String content = response.readEntity(String.class);
            System.out.println(content);
        } else {
            Assert.fail("Response NOT OK");
        }

    }

}
