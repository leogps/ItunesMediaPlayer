package com.gps.imp.utils.ssl;


import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

/**
 * Created by leogps on 4/1/18.
 */
public class HttpClientUtils {

    public static Client getNewClient() {
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        return clientBuilder.build();
    }
}
