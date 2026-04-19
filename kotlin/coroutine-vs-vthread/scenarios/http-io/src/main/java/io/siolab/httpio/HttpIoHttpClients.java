package io.siolab.httpio;

import java.net.http.HttpClient;
import java.time.Duration;

public final class HttpIoHttpClients {
    private static final int CONNECTION_POOL_SIZE = 1_000;
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);

    static {
        System.setProperty("jdk.httpclient.connectionPoolSize", String.valueOf(CONNECTION_POOL_SIZE));
    }

    private HttpIoHttpClients() {
    }

    public static HttpClient newHttpClient() {
        return HttpClient.newBuilder()
            .connectTimeout(CONNECT_TIMEOUT)
            .followRedirects(HttpClient.Redirect.NEVER)
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    }
}
